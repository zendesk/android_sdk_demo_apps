package com.zendesk.sample.chatproviders.chat;

import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import com.zendesk.util.CollectionUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import zendesk.belvedere.Belvedere;
import zendesk.belvedere.Callback;
import zendesk.belvedere.MediaResult;
import zendesk.chat.ChatState;
import zendesk.chat.ConnectionStatus;

/**
 * Bridge code between {@link ChatModel} and {@link ChatView}.
 */
class ChatPresenter implements ChatMvp.Presenter {

    private final ChatMvp.Model model;
    private final ChatMvp.View view;
    private final Belvedere belvedere;

    // keep a strong reference
    @SuppressWarnings("FieldCanBeLocal")
    private ChatMvp.Model.ChatListener chatListener;

    ChatPresenter(final ChatMvp.Model model, final ChatMvp.View view, Belvedere belvedere) {
        this.model = model;
        this.view = view;
        this.belvedere = belvedere;
    }

    @Override
    public void install(AppCompatActivity appCompatActivity) {
        chatListener = new MyChatListener();
        model.registerChatListener(chatListener);

        view.initChatUi(appCompatActivity);
        view.setInputEnabled(false);
        view.showLoading(true);
    }

    @Override
    public void sendMessage(final String message) {
        model.sendMessage(message);
    }

    @Override
    public void sendFile(final List<MediaResult> mediaResults) {
        if (CollectionUtils.isNotEmpty(mediaResults)) {
            List<Uri> fileUris = new ArrayList<>(mediaResults.size());
            for (MediaResult mediaResult : mediaResults) {
                fileUris.add(mediaResult.getUri());
            }

            belvedere.resolveUris(fileUris, "images", new Callback<List<MediaResult>>() {
                @Override
                public void success(List<MediaResult> result) {
                    for (MediaResult mediaResult : result) {
                        model.sendAttachment(mediaResult.getFile());
                    }
                }
            });
        }
    }

    @Override
    public void sendFile(File localFile) {
        model.sendAttachment(localFile);
    }

    @Override
    public void onDestroy() {
        model.unregisterChatListener();
    }

    @Override
    public void chatDismissed() {
        model.unregisterChatListener();
        model.clearChatIfEnded();
    }

    private class MyChatListener implements ChatMvp.Model.ChatListener {

        @Override
        public void onUpdateChatState(ChatState chatState) {
            view.updateChatLogs(chatState.getChatLogs(), chatState.getAgents());
        }

        @Override
        public void onUpdateConnection(final ConnectionStatus connection) {
            switch (connection) {
                case DISCONNECTED:
                case FAILED:
                case UNREACHABLE: {
                    view.connectionChanged(false);
                    break;
                }
                case CONNECTED: {
                    view.connectionChanged(true);
                    view.showLoading(false);
                    break;
                }
            }
        }
    }
}
