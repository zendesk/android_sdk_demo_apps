package com.zendesk.sample.chatproviders.chat;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.zendesk.sample.chatproviders.R;
import com.zendesk.sample.chatproviders.chat.log.ChatLogModel;
import com.zendesk.sample.chatproviders.chat.log.ChatLogMvp;
import com.zendesk.sample.chatproviders.chat.log.ChatLogPresenter;
import com.zendesk.sample.chatproviders.databinding.ActivityChatBinding;
import com.zendesk.util.StringUtils;
import com.zendesk.sample.chatproviders.chat.log.ChatLogView;

import java.util.List;

import zendesk.belvedere.BelvedereUi;
import zendesk.belvedere.ImageStream;
import zendesk.belvedere.MediaResult;
import zendesk.chat.Agent;
import zendesk.chat.ChatLog;

/**
 * View class that's responsible for interacting and creating the views needed
 * to display the chat.
 */
class ChatView implements ChatMvp.View {

    private ChatMvp.Presenter presenter;

    private final ActivityChatBinding views;
    private final Context context;

    private final Snackbar connectionSnackbar;

    private ImageStream imageStream;

    private ChatLogMvp.Presenter chatLogPresenter;
    private BelvedereListener belvedereListener = new BelvedereListener();

    ChatView(final ActivityChatBinding views, Context context) {
        this.views = views;
        this.context = context;

        this.connectionSnackbar = createSnackback(views.chatRootContainer, R.string.snackbar_connection);
    }

    @Override
    public void initChatUi(final AppCompatActivity activity) {
        initChatLogRecycler();
        initChatSendButton();
        initChatInput();
        initChatAttachmentButtons(activity);
    }

    @Override
    public <E extends ChatMvp.Presenter> void setPresenter(final E presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateChatLogs(List<ChatLog> chatLogs, List<Agent> agents) {
        chatLogPresenter.updateChatLog(chatLogs, agents);
    }

    @Override
    public void setInputEnabled(final boolean enabled) {
        views.chatSendBtn.setEnabled(enabled);
        views.chatInput.setEnabled(enabled);
        views.chatCameraBtn.setEnabled(enabled);
        views.chatGalleryBtn.setEnabled(enabled);
    }

    @Override
    public void showLoading(final boolean loading) {
        views.chatProgressbar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void connectionChanged(final boolean connected) {
        setInputEnabled(connected);

        if (connected) {
            connectionSnackbar.dismiss();
        } else {
            connectionSnackbar.show();
        }
    }

    private void initChatAttachmentButtons(final AppCompatActivity activity) {

        imageStream = BelvedereUi.install(activity);

        imageStream.addListener(belvedereListener);

        views.chatCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                BelvedereUi.imageStream(activity)
                        .withCameraIntent()
                        .showPopup(activity);
            }
        });

        views.chatGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                BelvedereUi.imageStream(activity.getApplicationContext())
                        .withDocumentIntent("*/*", true)
                        .showPopup(activity);
            }
        });
    }

    private void initChatInput() {
        views.chatInput.setEnabled(false);
        views.chatInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                // intentionally empty
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                if (StringUtils.isEmpty(views.chatInput.getText().toString())) {
                    views.chatSendBtn.hide();
                } else {
                    views.chatSendBtn.show();
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                // intentionally empty
            }
        });
    }

    private void initChatSendButton() {
        views.chatSendBtn.hide();
        views.chatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                presenter.sendMessage(views.chatInput.getText().toString());
                views.chatInput.setText("");
            }
        });
    }

    private void initChatLogRecycler() {
        final RecyclerView recyclerView = views.chatRecycler;

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        final ChatLogView chatLogAdapter = new ChatLogView(layoutManager); // view
        final ChatLogMvp.Model model = new ChatLogModel(); // model
        chatLogPresenter = new ChatLogPresenter(chatLogAdapter, model); // presenter
        chatLogAdapter.setPresenter(chatLogPresenter);

        recyclerView.setAdapter(chatLogAdapter);

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            chatLogAdapter.scrollToLastMessage();
                        }
                    }, 100);
                }
            }
        });
    }

    private Snackbar createSnackback(ViewGroup container, int titleId) {
        final String title = container.getContext().getString(titleId);
        final Snackbar snackbar = Snackbar.make(views.chatRootContainer, title, Snackbar.LENGTH_INDEFINITE);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.color_snackbar_connection));
        return snackbar;
    }

    private class BelvedereListener implements ImageStream.Listener {

        @Override
        public void onDismissed() {
            Log.d("TAG", "onDismissed: ");
        }

        @Override
        public void onVisible() {
            Log.d("TAG", "onVisible: ");
        }

        @Override
        public void onMediaSelected(List<MediaResult> mediaResults) {
            presenter.sendFile(mediaResults);
        }

        @Override
        public void onMediaDeselected(List<MediaResult> mediaResults) {
            Log.d("TAG", "onMediaDeselected: ");
        }
    }
}
