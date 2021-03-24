package com.zendesk.sample.chatproviders.chat;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.List;

import zendesk.belvedere.MediaResult;
import zendesk.chat.Agent;
import zendesk.chat.ChatLog;
import zendesk.chat.ChatState;
import zendesk.chat.ConnectionStatus;

interface ChatMvp {

    interface Model {

        void sendMessage(String message);

        void sendAttachment(File file);

        void registerChatListener(ChatListener chatListener);

        void unregisterChatListener();

        void clearChatIfEnded();

        interface ChatListener {

            void onUpdateChatState(ChatState chatState);

            void onUpdateConnection(ConnectionStatus connection);

        }
    }

    interface View {

        void initChatUi(AppCompatActivity activity);

        <E extends Presenter> void setPresenter(E presenter);

        void updateChatLogs(List<ChatLog> chatLogs, List<Agent> agents);

        void setInputEnabled(boolean enabled);

        void showLoading(boolean loading);

        void connectionChanged(boolean connected);

    }

    interface Presenter {

        void install(AppCompatActivity activity);

        void sendMessage(String message);

        void sendFile(List<MediaResult> mediaResults);

        void sendFile(File localFile);

        void onDestroy();

        void chatDismissed();
    }
}
