package com.zendesk.sample.chatproviders.chat.log.items;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import com.zendesk.sample.chatproviders.R;

import zendesk.belvedere.Belvedere;
import zendesk.belvedere.MediaResult;
import zendesk.chat.Agent;
import zendesk.chat.ChatLog;

/**
 * Class used to display a {@link ChatLog.AttachmentMessage} as an item in a {@link RecyclerView}.
 * <p>
 * Responsible for binding values to inflated views and to determine if the item needs to be updated.
 */
class AgentAttachmentWrapper extends ViewHolderWrapper<ChatLog.AttachmentMessage> {

    private final Agent agent;

    AgentAttachmentWrapper(final ChatLog.AttachmentMessage chatLog, final Agent agent) {
        super(ItemType.AGENT_ATTACHMENT, chatLog);
        this.agent = agent;
    }

    @Override
    public void bind(final RecyclerView.ViewHolder holder) {
        BinderHelper.displayTimeStamp(holder.itemView, getChatLog());
        BinderHelper.displayAgentAvatar(holder.itemView, agent);

        final ImageView imageView = holder.itemView.findViewById(R.id.chat_log_agent_attachment_imageview);
        final Uri parse = Uri.parse(getChatLog().getAttachment().getUrl());
        PicassoHelper.loadImage(imageView, parse, null);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(getChatLog().getAttachment().getFile() != null) {
                    final Belvedere belvedere = Belvedere.from(view.getContext());
                    final MediaResult fileRepresentation = belvedere
                            .getFile(null, getChatLog().getAttachment().getName());

                    if (fileRepresentation != null) {
                        final Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(fileRepresentation.getUri(), "image/*");
                        belvedere.grantPermissionsForUri(intent, fileRepresentation.getUri());
                        view.getContext().startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public boolean isUpdated(final ChatLog.AttachmentMessage chatLog) {
        return false;
    }

}