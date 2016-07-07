package com.zopim.sample.chatapi.chat;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.zendesk.belvedere.BelvedereIntent;
import com.zendesk.belvedere.BelvedereSource;
import com.zendesk.util.StringUtils;
import com.zopim.android.sdk.api.ZopimChatApi;
import com.zopim.android.sdk.model.ChatLog;
import com.zopim.android.sdk.model.items.RowItem;
import com.zopim.android.sdk.util.BelvedereProvider;
import com.zopim.sample.chatapi.R;
import com.zopim.sample.chatapi.chat.log.ChatLogModel;
import com.zopim.sample.chatapi.chat.log.ChatLogMvp;
import com.zopim.sample.chatapi.chat.log.ChatLogPresenter;
import com.zopim.sample.chatapi.chat.log.ChatLogView;
import com.zopim.sample.chatapi.databinding.ActivityChatBinding;

import java.util.List;
import java.util.Map;

class ChatView implements ChatMvp.View {

    private ChatMvp.Presenter presenter;

    private final ActivityChatBinding views;
    private final Context context;

    private final Snackbar connectionSnackbar;
    private final Snackbar timeoutSnackbar;

    private ChatLogMvp.Presenter chatLogPresenter;

    ChatView(final ActivityChatBinding views, Context context) {
        this.views = views;
        this.context = context;

        this.connectionSnackbar = Snackbar.make(views.chatRootContainer, "No Connection", Snackbar.LENGTH_INDEFINITE);
        this.connectionSnackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.color_snackbar_connection));
        this.timeoutSnackbar = Snackbar.make(views.chatRootContainer, "Timeout", Snackbar.LENGTH_INDEFINITE);
        this.timeoutSnackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.color_snackbar_connection));
    }

    @Override
    public void initChatLog(final FragmentActivity activity) {
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
    public void updateChatLog(final Map<String, RowItem> chatLogMap) {
        chatLogPresenter.updateChatLog(chatLogMap);
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

    @Override
    public void timeout() {
        setInputEnabled(false);
        timeoutSnackbar.show();
    }

    private void initChatAttachmentButtons(final FragmentActivity activity) {
        views.chatCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final List<BelvedereIntent> belvedereIntents = BelvedereProvider.INSTANCE
                        .getInstance(context)
                        .getBelvedereIntents();

                for (BelvedereIntent intent : belvedereIntents) {
                    if (intent.getSource() == BelvedereSource.Camera) {
                        intent.open(activity);
                    }
                }
            }
        });

        views.chatGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final List<BelvedereIntent> belvedereIntents = BelvedereProvider.INSTANCE
                        .getInstance(context)
                        .getBelvedereIntents();

                for (BelvedereIntent intent : belvedereIntents) {
                    if (intent.getSource() == BelvedereSource.Gallery) {
                        intent.open(activity);
                    }
                }
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
        final ChatLogMvp.Model model = new ChatLogModel(ZopimChatApi.getDataSource()); // model
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
}
