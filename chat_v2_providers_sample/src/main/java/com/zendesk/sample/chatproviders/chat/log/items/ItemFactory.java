package com.zendesk.sample.chatproviders.chat.log.items;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import zendesk.chat.Agent;
import zendesk.chat.ChatLog;
import zendesk.chat.ChatParticipant;

import static zendesk.chat.ChatLog.Type.ATTACHMENT_MESSAGE;
import static zendesk.chat.ChatLog.Type.MESSAGE;

/**
 * Factory for creating {@link ViewHolderWrapper} out of {@link ChatLog}.
 */
public class ItemFactory {

    private static final List<ChatLog.Type> USED_MESSAGES = Arrays.asList(
            MESSAGE, ATTACHMENT_MESSAGE
    );

    public static int countUsedMessages(Collection<ChatLog> chatLogs) {
        int count = 0;
        for (ChatLog chatLog : chatLogs) {
            if (USED_MESSAGES.contains(chatLog.getType())) {
                count++;
            }
        }
        return count;
    }

    public static ViewHolderWrapper get(final ChatLog chatLog, final List<Agent> agents) {
        switch (chatLog.getType()) {
            case MESSAGE: {
                if (chatLog.getChatParticipant() == ChatParticipant.AGENT) {
                    return agentMessage((ChatLog.Message) chatLog, findAgent(agents, chatLog));
                } else if (chatLog.getChatParticipant() == ChatParticipant.VISITOR) {
                    return visitorMessage((ChatLog.Message) chatLog);
                }
                break;
            }
            case ATTACHMENT_MESSAGE: {
                if (chatLog.getChatParticipant() == ChatParticipant.AGENT) {
                    return agentAttachment((ChatLog.AttachmentMessage) chatLog, findAgent(agents, chatLog));
                } else if (chatLog.getChatParticipant() == ChatParticipant.VISITOR) {
                    return visitorAttachment((ChatLog.AttachmentMessage) chatLog);
                }
                break;
            }
        }
        return null;
    }

    @Nullable
    static Agent findAgent(List<Agent> agents, ChatLog chatLog) {
        for (Agent agent : agents) {
            if (agent.getNick().equals(chatLog.getNick())) {
                return agent;
            }
        }
        return null;
    }

    static AgentMessageWrapper agentMessage(ChatLog.Message chatLog, Agent agent) {
        return new AgentMessageWrapper(chatLog, agent);
    }

    static VisitorMessageWrapper visitorMessage(ChatLog.Message chatLog) {
        return new VisitorMessageWrapper(chatLog);
    }

    static AgentAttachmentWrapper agentAttachment(ChatLog.AttachmentMessage chatLog, Agent agent) {
        return new AgentAttachmentWrapper(chatLog, agent);
    }

    static VisitorAttachmentWrapper visitorAttachment(ChatLog.AttachmentMessage chatLog) {
        return new VisitorAttachmentWrapper(chatLog);
    }
}