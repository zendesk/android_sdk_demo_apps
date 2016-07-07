package com.zopim.sample.chatapi.chat.log.items;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.zopim.android.sdk.model.items.RowItem;
import com.zopim.sample.chatapi.R;

class VisitorMessageWrapper extends ViewHolderWrapper<com.zopim.android.sdk.model.items.VisitorMessage> {

    VisitorMessageWrapper(final String messageId, final com.zopim.android.sdk.model.items.VisitorMessage rowItem) {
        super(ItemType.VISITOR_MESSAGE, messageId, rowItem);
    }

    @Override
    public void bind(final RecyclerView.ViewHolder holder) {
        BinderHelper.displayTimeStamp(holder.itemView, getRowItem());
        BinderHelper.displayVisitorVerified(holder.itemView, true);

        final TextView textView = (TextView) holder.itemView.findViewById(R.id.chat_log_visitor_message_textview);
        textView.setText(getRowItem().getMessage());
    }

    @Override
    public boolean isUpdated(final RowItem rowItem) {
        return false;
    }

}