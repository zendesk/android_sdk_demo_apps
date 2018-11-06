package com.zendesk.sample.connect;

import android.content.Intent;
import android.net.Uri;

import com.zendesk.logger.Logger;

import io.outbound.sdk.OutboundService;
import io.outbound.sdk.PushNotification;

/**
 * You can optionally extend {@link OutboundService} to handle what happens when a user
 * taps on a push notification delivered by Connect.
 */
public class SampleNotificationService extends OutboundService {

    private final static String LOG_TAG = "SampleNotificationService";

    @Override
    public void onOpenNotification(PushNotification notification) {
        super.onOpenNotification(notification);
        Logger.d(LOG_TAG, "User opened notification and was directed to: "
                + notification.getDeeplink());

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(notification.getDeeplink()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
