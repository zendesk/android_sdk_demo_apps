package com.zopim.sample.chatapi.chat.log.items;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zopim.android.sdk.model.Agent;
import com.zopim.android.sdk.model.items.RowItem;
import com.zopim.sample.chatapi.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class containing a bunch of helper methods to bind often occurring values to known views.
 * <p>
 * Like binding a timestamp to a {@link TextView} or an avatar to a {@link ImageView}.
 */
class BinderHelper {

    private static int CHAT_LOG_TIMESTAMP = R.id.chat_log_holder_timestamp;
    private static int CHAT_LOG_AGENT_AVATAR = R.id.chat_log_holder_avatar;
    private static int CHAT_LOG_VISITOR_VERIFIED = R.id.chat_log_holder_verified;

    private static SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());

    static void displayTimeStamp(View itemView, RowItem rowItem) {
        final View view = itemView.findViewById(CHAT_LOG_TIMESTAMP);
        if (view instanceof TextView) {
            Date date = new Date(rowItem.getTimestamp());
            String format = DATE_FORMAT.format(date);
            ((TextView) view).setText(format);
        }
    }

    static void displayAgentAvatar(View itemView, Agent agent) {
        final View avatar = itemView.findViewById(CHAT_LOG_AGENT_AVATAR);
        if (agent != null && avatar instanceof ImageView) {
            PicassoHelper.loadAvatarImage((ImageView) avatar, agent.getAvatarUri());
        }
    }

    static void displayVisitorVerified(View itemView, boolean verified) {
        final View verifiedView = itemView.findViewById(CHAT_LOG_VISITOR_VERIFIED);
        if (verifiedView instanceof ImageView) {

            final int drawable;
            if (verified) {
                drawable = R.drawable.ic_check_black_18dp;
            } else {
                drawable = R.drawable.ic_sync_black_18dp;
            }

            ((ImageView) verifiedView)
                    .setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), drawable));
        }
    }
}