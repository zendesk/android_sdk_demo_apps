package com.zopim.sample.chatapi.chat.log.items;

import com.zopim.sample.chatapi.R;

public enum ItemType {

    AGENT_MESSAGE(1, R.layout.chat_log_agent_message, true),
    VISITOR_MESSAGE(2, R.layout.chat_log_vistor_message, false),

    AGENT_ATTACHMENT(3, R.layout.chat_log_agent_attachment, true),
    VISITOR_ATTACHMENT(4, R.layout.chat_log_visitor_attachment, false);

    final int viewType, layout;
    final boolean isAgent;

    ItemType(int viewType, int layout, boolean isAgent) {
        this.viewType = viewType;
        this.layout = layout;
        this.isAgent = isAgent;
    }

    public static ItemType forViewType(int viewType) {
        for (ItemType type : ItemType.values()) {
            if (type.viewType == viewType) {
                return type;
            }
        }
        return null;
    }

    public int getViewType() {
        return viewType;
    }

    public int getLayout() {
        return layout;
    }

    public boolean isAgent() {
        return isAgent;
    }
}