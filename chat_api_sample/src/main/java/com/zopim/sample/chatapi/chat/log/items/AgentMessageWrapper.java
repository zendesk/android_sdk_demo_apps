package com.zopim.sample.chatapi.chat.log.items;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.zopim.android.sdk.model.Agent;
import com.zopim.android.sdk.model.ChatLog;
import com.zopim.android.sdk.model.items.RowItem;
import com.zopim.sample.chatapi.R;

class AgentMessageWrapper extends ViewHolderWrapper<com.zopim.android.sdk.model.items.AgentMessage> {

    private final Agent agent;

    AgentMessageWrapper(final String messageId, final com.zopim.android.sdk.model.items.AgentMessage chatLog, final Agent agent) {
        super(ItemType.AGENT_MESSAGE, messageId, chatLog);
        this.agent = agent;
    }

    @Override
    public void bind(final RecyclerView.ViewHolder holder) {
        BinderHelper.displayTimeStamp(holder.itemView, getRowItem());
        BinderHelper.displayAgentAvatar(holder.itemView, agent);

        final TextView textView = (TextView) holder.itemView.findViewById(R.id.chat_log_agent_message_textview);
        textView.setText(getRowItem().getMessage());
    }

    @Override
    public boolean isUpdated(final RowItem chatLog) {
        return false;
    }
}