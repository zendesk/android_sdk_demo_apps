package com.zendesk.sample.chatproviders.chat.log;

import com.zendesk.sample.chatproviders.chat.log.items.ViewHolderWrapper;

import java.util.List;

import zendesk.chat.Agent;
import zendesk.chat.ChatLog;

/**
 * Presenter class used as a bridge between {@link ChatLogModel} and {@link ChatLogView}.
 */
public class ChatLogPresenter implements ChatLogMvp.Presenter {

    private final ChatLogMvp.View view;
    private final ChatLogMvp.Model model;

    public ChatLogPresenter(ChatLogMvp.View view, ChatLogMvp.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public ViewHolderWrapper getViewHolderWrapperForPos(int position) {
        return model.getViewHolderWrapperForPos(position);
    }

    @Override
    public int getItemCount() {
        return model.getItemCount();
    }

    @Override
    public void updateChatLog(List<ChatLog> chatLogs, List<Agent> agents) {
        model.updateChatLog(chatLogs, agents);
        view.refreshWholeList();
        view.scrollToLastMessage();
    }

}
