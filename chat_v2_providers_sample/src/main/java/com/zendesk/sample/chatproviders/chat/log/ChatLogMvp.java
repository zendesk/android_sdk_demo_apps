package com.zendesk.sample.chatproviders.chat.log;

import com.zendesk.sample.chatproviders.chat.log.items.ViewHolderWrapper;

import java.util.List;

import zendesk.chat.Agent;
import zendesk.chat.ChatLog;

public interface ChatLogMvp {

    interface Presenter {

        void updateChatLog(List<ChatLog> chatLogs, List<Agent> agents);

        int getItemCount();

        ViewHolderWrapper<?> getViewHolderWrapperForPos(int position);
    }

    interface View {

        void refreshWholeList();

        <E extends ChatLogMvp.Presenter> void setPresenter(E presenter);

        void scrollToLastMessage();
    }

    interface Model {

        ViewHolderWrapper<?> getViewHolderWrapperForPos(int position);

        int getItemCount();

        void updateChatLog(List<ChatLog> chatLogs, List<Agent> agents);
    }

}
