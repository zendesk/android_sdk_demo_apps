package com.zendesk.sample.chatproviders.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.lang.ref.WeakReference;

import zendesk.chat.Chat;
import zendesk.chat.ChatProvider;
import zendesk.chat.ChatSessionStatus;
import zendesk.chat.ChatState;
import zendesk.chat.ConnectionProvider;
import zendesk.chat.ConnectionStatus;
import zendesk.chat.ObservationScope;
import zendesk.chat.Observer;

/**
 * Model class that's responsible for interacting with Zendesk Chat API.
 */
class ChatModel implements ChatMvp.Model {

    private final ChatProvider chatProvider;
    private final ConnectionProvider connectionProvider;
    private final Context context;
    private BroadcastReceiver timeoutReceiver;
    private Handler mainHandler;

    private WeakReference<ChatListener> chatListener;
    private ObservationScope chatStateObservationScope;
    private ObservationScope connectionObservationScope;

    ChatModel(ChatProvider chatProvider, ConnectionProvider connectionProvider, Context context) {
        this.chatProvider = chatProvider;
        this.connectionProvider = connectionProvider;
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void sendMessage(final String message) {
        chatProvider.sendMessage(message);
    }

    @Override
    public void sendAttachment(final File file) {
        chatProvider.sendFile(file, null);
    }

    @Override
    public void registerChatListener(final ChatListener chatListener) {
        if(this.chatListener != null) {
            unregisterChatListener();
        }

        this.chatListener = new WeakReference<>(chatListener);
        bindChatListener();
    }

    @Override
    public void unregisterChatListener() {
        chatListener = null;
        unbindChatListener();
    }

    @Override
    public void clearChatIfEnded() {
        if (chatProvider.getChatState() != null
                && chatProvider.getChatState().getChatSessionStatus() == ChatSessionStatus.ENDED) {
            Chat.INSTANCE.clearCache();
        }
    }

    private void unbindChatListener() {
        if (chatStateObservationScope != null) {
            chatStateObservationScope.cancel();
        }

        if (connectionObservationScope != null) {
            connectionObservationScope.cancel();
        }

        if (timeoutReceiver != null) {
            context.unregisterReceiver(timeoutReceiver);
            timeoutReceiver = null;
        }
    }

    private void bindChatListener() {
        chatStateObservationScope = new ObservationScope();
        Observer<ChatState> chatStateObserver = new Observer<ChatState>() {
            @Override
            public void update(final ChatState chatState) {
                updateChatListener(new UpdateChatLogListener() {
                    @Override
                    public void update(ChatListener chatListener) {
                        chatListener.onUpdateChatState(chatState);
                    }
                });
            }
        };

        connectionObservationScope = new ObservationScope();
        Observer<ConnectionStatus> connectionStatusObserver = new Observer<ConnectionStatus>() {
            @Override
            public void update(final ConnectionStatus connectionStatus) {
                updateChatListener(new UpdateChatLogListener() {
                    @Override
                    public void update(ChatListener chatListener) {
                        chatListener.onUpdateConnection(connectionStatus);
                    }
                });
            }
        };

        chatProvider.observeChatState(chatStateObservationScope, chatStateObserver);
        connectionProvider.observeConnectionStatus(connectionObservationScope, connectionStatusObserver);

    }

    private void updateChatListener(final UpdateChatLogListener updater) {
        if(chatListener != null && chatListener.get() != null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (chatListener != null && chatListener.get() != null) {
                        updater.update(chatListener.get());
                    }
                }
            });
        }
    }

    private interface UpdateChatLogListener {
        void update(ChatListener chatListener);
    }
}
