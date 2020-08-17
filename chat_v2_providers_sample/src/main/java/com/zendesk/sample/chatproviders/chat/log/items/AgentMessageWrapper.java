package com.zendesk.sample.chatproviders.chat.log.items;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.zendesk.sample.chatproviders.R;

import zendesk.chat.Agent;
import zendesk.chat.ChatLog;

/**
 * Class used to display a {@link ChatLog.Message} as an item in a {@link RecyclerView}.
 * <p>
 * Responsible for binding values to inflated views and to determine if the item needs to be updated.
 */
class AgentMessageWrapper extends ViewHolderWrapper<ChatLog.Message> {

    private final Agent agent;

    AgentMessageWrapper(ChatLog.Message chatLog, final Agent agent) {
        super(ItemType.AGENT_MESSAGE, chatLog);
        this.agent = agent;
    }

    @Override
    public void bind(final RecyclerView.ViewHolder holder) {
        BinderHelper.displayTimeStamp(holder.itemView, getChatLog());
        BinderHelper.displayAgentAvatar(holder.itemView, agent);

        final TextView textView = holder.itemView.findViewById(R.id.chat_log_agent_message_textview);
        textView.setText(getChatLog().getMessage());
    }

    @Override
    public boolean isUpdated(final ChatLog.Message chatLog) {
        return false;
    }
}