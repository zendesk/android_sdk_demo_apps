package com.zopim.sample.chatapi.chat.log.items;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.zendesk.belvedere.Belvedere;
import com.zendesk.belvedere.BelvedereResult;
import com.zopim.android.sdk.model.Agent;
import com.zopim.android.sdk.model.items.AgentAttachment;
import com.zopim.android.sdk.model.items.RowItem;
import com.zopim.android.sdk.util.BelvedereProvider;
import com.zopim.sample.chatapi.R;

/**
 * Class used to display a {@link AgentAttachment} as an item in a {@link RecyclerView}.
 * <p>
 * Responsible for binding values to inflated views and to determine if the item needs to be updated.
 */
class AgentAttachmentWrapper extends ViewHolderWrapper<AgentAttachment> {

    private final Agent agent;

    AgentAttachmentWrapper(final String messageId, final AgentAttachment rowItem, final Agent agent) {
        super(ItemType.AGENT_ATTACHMENT, messageId, rowItem);
        this.agent = agent;
    }

    @Override
    public void bind(final RecyclerView.ViewHolder holder) {
        BinderHelper.displayTimeStamp(holder.itemView, getRowItem());
        BinderHelper.displayAgentAvatar(holder.itemView, agent);

        final ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.chat_log_agent_attachment_imageview);
        final Uri parse = Uri.parse(getRowItem().getAttachmentUrl().toString());
        PicassoHelper.loadImage(imageView, parse, null);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(getRowItem().getAttachmentFile() != null) {
                    final Belvedere belvedere = BelvedereProvider.INSTANCE
                            .getInstance(view.getContext());
                    final BelvedereResult fileRepresentation = belvedere
                            .getFileRepresentation(getRowItem().getAttachmentFile().getName());

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
    public boolean isUpdated(final RowItem rowItem) {
        return false;
    }

}