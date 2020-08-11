package com.zendesk.sample.chatproviders.chat.log.items;

import androidx.recyclerview.widget.RecyclerView;

import zendesk.chat.ChatLog;

/**
 * Super class every item in the {@link RecyclerView} inherits from.
 *
 * @param <T> specific type of the {@link ChatLog} this class should hold.
 */
public abstract class ViewHolderWrapper<T extends ChatLog> {

    private final ItemType itemType;
    private final T chatLog;
    private final long id;

    ViewHolderWrapper(final ItemType type, final T chatLog) {
        this.itemType = type;
        this.chatLog = chatLog;
        this.id = chatLog.getId().hashCode();
    }

    public String getMessageId() {
        return getChatLog().getId();
    }

    T getChatLog() {
        return chatLog;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public long getId() {
        return id;
    }

    public abstract void bind(RecyclerView.ViewHolder holder);

    public abstract boolean isUpdated(T rowItem);
}