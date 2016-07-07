package com.zopim.sample.chatapi.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import com.zopim.android.sdk.api.ChatApi;
import com.zopim.android.sdk.api.ChatSession;
import com.zopim.android.sdk.data.ConnectionPath;
import com.zopim.android.sdk.data.DataSource;
import com.zopim.android.sdk.data.LivechatChatLogPath;
import com.zopim.android.sdk.data.observers.ChatItemsObserver;
import com.zopim.android.sdk.data.observers.ChatLogObserver;
import com.zopim.android.sdk.data.observers.ConnectionObserver;
import com.zopim.android.sdk.model.ChatLog;
import com.zopim.android.sdk.model.Connection;
import com.zopim.android.sdk.model.items.RowItem;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.TreeMap;

class ChatModel implements ChatMvp.Model {

    private final DataSource dataSource;
    private final ChatApi chatApi;
    private final Context context;
    private BroadcastReceiver timeoutReceiver;
    private Handler mainHandler;

    private WeakReference<ChatListener> chatListener;

    ChatModel(ChatApi chatApi, DataSource dataSource, Context context) {
        this.chatApi = chatApi;
        this.dataSource = dataSource;
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void sendMessage(final String message) {
        chatApi.send(message);
    }

    @Override
    public void sendAttachment(final File file) {
        chatApi.send(file);
    }

    @Override
    public void registerChatListener(final ChatListener chatListener) {
        if(this.chatListener != null) {
            unregisterChatListener();
        }

        this.chatListener = new WeakReference<>(chatListener);
        bindZopimListener();
    }

    @Override
    public void unregisterChatListener() {
        chatListener = null;
        unbindZopimListener();
    }

    @Override
    public void clearChatIfEnded() {
        if (chatApi.hasEnded()) {
            dataSource.clear();
        }
    }

    private void unbindZopimListener() {
        dataSource.deleteObservers();

        if (timeoutReceiver != null) {
            context.unregisterReceiver(timeoutReceiver);
            timeoutReceiver = null;
        }
    }

    private void bindZopimListener() {
        dataSource.addChatLogObserver(new ChatItemsObserver(context) {
            @Override
            protected void updateChatItems(final TreeMap<String, RowItem> treeMap) {
                updateChatListener(new UpdateChatLogListener() {
                    @Override
                    public void update(final ChatListener chatListener) {
                        chatListener.updateChatLog(treeMap);
                    }
                });
            }
        }).trigger();

        dataSource.addConnectionObserver(new ConnectionObserver() {
            @Override
            public void update(final Connection connection) {
                updateChatListener(new UpdateChatLogListener() {
                    @Override
                    public void update(final ChatListener chatListener) {
                        if(!chatApi.hasEnded()) {
                            chatListener.updateConnection(connection);
                        } else {
                            chatListener.timeout();
                        }
                    }
                });
            }
        }).trigger();

        timeoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                if (intent != null && ChatSession.ACTION_CHAT_SESSION_TIMEOUT.equals(intent.getAction())) {
                    updateChatListener(new UpdateChatLogListener() {
                        @Override
                        public void update(final ChatListener chatListener) {
                            chatListener.timeout();
                        }
                    });
                }
            }
        };

        context.registerReceiver(timeoutReceiver, new IntentFilter(ChatSession.ACTION_CHAT_SESSION_TIMEOUT));
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
