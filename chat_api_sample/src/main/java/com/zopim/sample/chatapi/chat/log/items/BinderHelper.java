package com.zopim.sample.chatapi.chat.log.items;

import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zopim.android.sdk.model.Agent;
import com.zopim.android.sdk.model.ChatLog;
import com.zopim.android.sdk.model.items.RowItem;
import com.zopim.sample.chatapi.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class BinderHelper {

    private static int CHAT_LOG_TIMESTAMP = R.id.chat_log_holder_timestamp;
    private static int CHAT_LOG_AGENT_AVATAR = R.id.chat_log_holder_avatar;
    private static int CHAT_LOG_VISITOR_VERIFIED = R.id.chat_log_holder_verified;

    static void displayTimeStamp(View itemView, RowItem rowItem) {
        final TextView timestamp = (TextView) itemView.findViewById(CHAT_LOG_TIMESTAMP);
        if(timestamp != null) {
            final Date date = new Date(rowItem.getTimestamp() != null ? rowItem.getTimestamp() : 698241591L);
            final String format = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(date);
            timestamp.setText(format);
        }
    }

    static void displayAgentAvatar(View itemView, Agent agent) {
        final ImageView avatar = (ImageView) itemView.findViewById(CHAT_LOG_AGENT_AVATAR);
        if (agent != null) {
            PicassoHelper.loadAvatarImage(avatar, agent.getAvatarUri());
        }
    }

    static void displayVisitorVerified(View itemView, boolean verified) {
        final ImageView verifiedView = (ImageView) itemView.findViewById(CHAT_LOG_VISITOR_VERIFIED);
        if(verifiedView != null) {
            final int drawable = verified ? R.drawable.ic_check_black_18dp : R.drawable.ic_sync_black_18dp;
            verifiedView.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), drawable));
        }
    }
}