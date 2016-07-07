package com.zopim.sample.chatapi.chat.log.items;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.zendesk.belvedere.Belvedere;
import com.zendesk.belvedere.BelvedereFileProvider;
import com.zopim.android.sdk.model.ChatLog;
import com.zopim.android.sdk.model.items.RowItem;
import com.zopim.android.sdk.util.BelvedereProvider;
import com.zopim.sample.chatapi.R;

import java.util.Locale;

class VisitorAttachmentWrapper extends ViewHolderWrapper<com.zopim.android.sdk.model.items.VisitorAttachment> {

    private final int progress;

    VisitorAttachmentWrapper(final String messageId, final com.zopim.android.sdk.model.items.VisitorAttachment visitorAttachment) {
        super(ItemType.VISITOR_ATTACHMENT, messageId, visitorAttachment);
        progress = visitorAttachment.getUploadProgress();
    }

    @Override
    public void bind(final RecyclerView.ViewHolder holder) {
        BinderHelper.displayTimeStamp(holder.itemView, getRowItem());
        BinderHelper.displayVisitorVerified(holder.itemView, progress == 100);

        final ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.chat_log_visitor_attachment_imageview);

        if(getRowItem().getFile() != null && getRowItem().getFile().exists()) {
            PicassoHelper.loadImage(imageView, getRowItem().getFile(), null);
        } else {
            PicassoHelper.loadImage(imageView, Uri.parse(getRowItem().getUploadUrl().toString()), null);
        }

        if (progress == 100) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if(getRowItem().getFile() != null && getRowItem().getFile().exists()) {
                        final String suffix = imageView.getContext().getString(R.string.belvedere_sdk_fpa_suffix);
                        final String authority = String.format(Locale.US, "%s%s", imageView.getContext().getPackageName(), suffix);
                        final Uri uri = BelvedereFileProvider.getUriForFile(imageView.getContext(), authority, getRowItem().getFile());

                        final Belvedere belvedere = BelvedereProvider.INSTANCE.getInstance(imageView.getContext());
                        final Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "image/*");
                        belvedere.grantPermissionsForUri(intent, uri);
                        view.getContext().startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public boolean isUpdated(final RowItem chatLog) {
        return chatLog instanceof com.zopim.android.sdk.model.items.VisitorAttachment
                && ((com.zopim.android.sdk.model.items.VisitorAttachment)chatLog).getUploadProgress() != progress;
    }
}