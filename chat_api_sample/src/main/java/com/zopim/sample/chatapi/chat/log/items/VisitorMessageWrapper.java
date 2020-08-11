package com.zopim.sample.chatapi.chat.log.items;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.zopim.android.sdk.model.items.RowItem;
import com.zopim.android.sdk.model.items.VisitorAttachment;
import com.zopim.android.sdk.model.items.VisitorMessage;
import com.zopim.sample.chatapi.R;

/**
 * Class used to display a {@link VisitorMessage} as an item in a {@link RecyclerView}.
 * <p>
 * Responsible for binding values to inflated views and to determine if the item needs to be updated.
 */
class VisitorMessageWrapper extends ViewHolderWrapper<VisitorMessage> {

    VisitorMessageWrapper(final String messageId, final VisitorMessage rowItem) {
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