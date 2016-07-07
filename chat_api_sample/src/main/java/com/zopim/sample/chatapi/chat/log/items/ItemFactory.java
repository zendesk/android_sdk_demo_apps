package com.zopim.sample.chatapi.chat.log.items;

import com.zopim.android.sdk.model.Agent;
import com.zopim.android.sdk.model.items.AgentAttachment;
import com.zopim.android.sdk.model.items.AgentMessage;
import com.zopim.android.sdk.model.items.RowItem;
import com.zopim.android.sdk.model.items.VisitorAttachment;
import com.zopim.android.sdk.model.items.VisitorMessage;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import static com.zopim.android.sdk.model.items.RowItem.Type;
import static com.zopim.android.sdk.model.items.RowItem.Type.AGENT_ATTACHMENT;

public class ItemFactory {

    private static final List<RowItem.Type> USED_MESSAGES = Arrays.asList(
            Type.AGENT_MESSAGE, AGENT_ATTACHMENT, Type.VISITOR_ATTACHMENT, Type.VISITOR_MESSAGE
    );

    public static int countUsedMessages(Collection<RowItem> chatLogs) {
        int count = 0;
        for(RowItem rowItem: chatLogs) {
            if(USED_MESSAGES.contains(rowItem.getType())){
                count++;
            }
        }
        return count;
    }

    public static ViewHolderWrapper get(String id, RowItem rowItem, final LinkedHashMap<String, Agent> agents) {
        switch (rowItem.getType()) {
            case AGENT_MESSAGE: {
                return agentMessage(id, (AgentMessage) rowItem, findAgent(agents, rowItem));
            }

            case AGENT_ATTACHMENT: {
                return agentAttachment(id, (AgentAttachment) rowItem, findAgent(agents, rowItem));
            }

            case VISITOR_ATTACHMENT: {
                return visitorAttachment(id, (VisitorAttachment) rowItem);
            }

            case VISITOR_MESSAGE: {
                return visitorMessage(id, (VisitorMessage) rowItem);
            }
        }
        return null;
    }


    static Agent findAgent(LinkedHashMap<String, Agent> agents, RowItem rowItem) {
        return agents.get(rowItem.getParticipantId());
    }

    static AgentMessageWrapper agentMessage(String id, AgentMessage rowItem, Agent agent) {
        return new AgentMessageWrapper(id, rowItem, agent);
    }

    static VisitorMessageWrapper visitorMessage(String id, VisitorMessage rowItem) {
        return new VisitorMessageWrapper(id, rowItem);
    }

    static AgentAttachmentWrapper agentAttachment(String id, AgentAttachment rowItem, Agent agent) {
        return new AgentAttachmentWrapper(id, rowItem, agent);
    }

    static VisitorAttachmentWrapper visitorAttachment(String id, VisitorAttachment rowItem) {
        return new VisitorAttachmentWrapper(id, rowItem);
    }
}