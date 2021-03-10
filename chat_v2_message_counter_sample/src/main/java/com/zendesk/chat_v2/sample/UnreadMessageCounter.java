package com.zendesk.chat_v2.sample;

import java.util.ArrayList;
import java.util.List;

import zendesk.chat.Chat;
import zendesk.chat.ChatLog;
import zendesk.chat.ChatState;
import zendesk.chat.ObservationScope;
import zendesk.chat.Observer;

class UnreadMessageCounter {

    private final UnreadMessageCounterListener unreadMessageCounterListener;

    private boolean shouldCount;
    private String lastChatLogId;
    private String lastReadChatLogId;

    public UnreadMessageCounter(UnreadMessageCounterListener unreadMessageCounterListener) {
        this.unreadMessageCounterListener = unreadMessageCounterListener;

        Chat.INSTANCE.providers().chatProvider().observeChatState(new ObservationScope(), new Observer<ChatState>() {
            @Override
            public void update(ChatState chatState) {
                if (chatState != null && !chatState.getChatLogs().isEmpty()) {
                    if (shouldCount && lastReadChatLogId !=  null) {
                        updateCounter(chatState.getChatLogs(), lastReadChatLogId);
                    }
                    lastChatLogId = chatState.getChatLogs().get(
                            chatState.getChatLogs().size() - 1
                    ).getId();
                }
            }
        });
    }


    // Determines whether or not the chat websocket should be re-connected to.
    // A connected, open websocket will disable push notifications and receive messages as normal.
    public void startCounter() {
        shouldCount = true;
        lastReadChatLogId = lastChatLogId;
        Chat.INSTANCE.providers().connectionProvider().connect();
    }

    public void stopCounter() {
        lastReadChatLogId = null;
        shouldCount = false;
        unreadMessageCounterListener.onUnreadCountUpdated(0);
    }

    // Increment the counter and send an update to the listener
    synchronized private void updateCounter(List<ChatLog> chatLogs, String lastReadId) {
        for (ChatLog chatLog : chatLogs) {
            if (chatLog.getId().equals(lastReadId)) {
                int lastReadIndex = chatLogs.indexOf(chatLog);
                List<ChatLog> unreadLogs = chatLogs.subList(lastReadIndex, chatLogs.size() - 1);
                unreadMessageCounterListener.onUnreadCountUpdated(unreadLogs.size());
                break;
            }
        }

    }

    interface UnreadMessageCounterListener {
        void onUnreadCountUpdated(int unreadCount);
    }

}
