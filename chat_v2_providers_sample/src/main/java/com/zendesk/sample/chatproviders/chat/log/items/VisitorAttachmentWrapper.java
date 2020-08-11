package com.zendesk.sample.chatproviders.chat.log.items;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.zendesk.sample.chatproviders.R;

import java.io.File;

import zendesk.belvedere.Belvedere;
import zendesk.chat.Attachment;
import zendesk.chat.ChatLog;
import zendesk.chat.DeliveryStatus;

/**
 * Class used to display a {@link ChatLog.AttachmentMessage} as an item in a {@link RecyclerView}.
 * <p>
 * Responsible for binding values to inflated views and to determine if the item needs to be updated.
 */
class VisitorAttachmentWrapper extends ViewHolderWrapper<ChatLog.AttachmentMessage> {

    VisitorAttachmentWrapper(final ChatLog.AttachmentMessage chatLog) {
        super(ItemType.VISITOR_ATTACHMENT, chatLog);
    }

    @Override
    public void bind(final RecyclerView.ViewHolder holder) {
        BinderHelper.displayTimeStamp(holder.itemView, getChatLog());
        BinderHelper.displayVisitorVerified(holder.itemView,
                getChatLog().getDeliveryStatus() == DeliveryStatus.DELIVERED);

        final ImageView imageView = holder.itemView.findViewById(R.id.chat_log_visitor_attachment_imageview);

        final Attachment attachment = getChatLog().getAttachment();
        final File file = attachment.getFile();
        if(file != null && file.exists()) {
            PicassoHelper.loadImage(imageView, file, null);
        } else {
            PicassoHelper.loadImage(imageView, Uri.parse(attachment.getUrl()), null);
        }

        if (getChatLog().getDeliveryStatus() == DeliveryStatus.DELIVERED) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if(file != null && file.exists()) {
                        final Uri uri = Uri.parse(getChatLog().getAttachment().getUrl());
                        Intent viewIntent = Belvedere.from(view.getContext()).getViewIntent(uri, attachment.getMimeType());
                        view.getContext().startActivity(viewIntent);
                    }
                }
            });
        }
    }

    @Override
    public boolean isUpdated(final ChatLog.AttachmentMessage chatLog) {
        return chatLog.getDeliveryStatus() != getChatLog().getDeliveryStatus();
    }
}