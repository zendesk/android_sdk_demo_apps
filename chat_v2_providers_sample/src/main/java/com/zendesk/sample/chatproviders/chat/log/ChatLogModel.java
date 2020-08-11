package com.zendesk.sample.chatproviders.chat.log;

import androidx.recyclerview.widget.RecyclerView;

import com.zendesk.sample.chatproviders.chat.log.items.ItemFactory;
import com.zendesk.sample.chatproviders.chat.log.items.ViewHolderWrapper;

import java.util.ArrayList;
import java.util.List;

import zendesk.chat.Agent;
import zendesk.chat.ChatLog;

/**
 * Model class for representing, updating and holding the internal state of the {@link RecyclerView}.
 */
public class ChatLogModel implements ChatLogMvp.Model {

    private final List<ViewHolderWrapper> listItems;


    public ChatLogModel() {
        this.listItems = new ArrayList<>();
    }

    @Override
    public ViewHolderWrapper getViewHolderWrapperForPos(final int position) {
        return listItems.get(position);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public void updateChatLog(final List<ChatLog> chatLogs, List<Agent> agents) {
        final List<ViewHolderWrapper> viewHolderWrappers = new ArrayList<>(listItems);
        final List<ViewHolderWrapper> updateHolderWrapper = new ArrayList<>();

        if (ItemFactory.countUsedMessages(chatLogs) < viewHolderWrappers.size()) {
            viewHolderWrappers.clear();
        }

        for (ChatLog chatLog : chatLogs) {
            final ViewHolderWrapper holderById = findHolderById(viewHolderWrappers, chatLog.getId());

            if (holderById != null && holderById.isUpdated(chatLog)) {
                // Update an existing item
                final ViewHolderWrapper wrapper = ItemFactory.get(chatLog, agents);

                if (wrapper != null) {
                    updateHolderWrapper.add(wrapper);
                }

            } else if (holderById != null) {
                // Carry over an existing item
                updateHolderWrapper.add(holderById);

            } else {
                // New item, create and insert
                final ViewHolderWrapper wrapper = ItemFactory.get(chatLog, agents);
                if (wrapper != null) {
                    updateHolderWrapper.add(wrapper);
                }
            }
        }

        listItems.clear();
        listItems.addAll(updateHolderWrapper);
    }

    private ViewHolderWrapper findHolderById(List<ViewHolderWrapper> list, String id) {
        for (ViewHolderWrapper wrapper : list) {
            if (wrapper.getMessageId().equals(id)) {
                return wrapper;
            }
        }
        return null;
    }

}
