package com.zendesk.sample.connect;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.zendesk.logger.Logger;

import io.outbound.sdk.OutboundMessagingService;
import io.outbound.sdk.PushNotification;

/**
 * You can optionally extend {@link OutboundMessagingService} to handle building
 * notifications yourself, or handle events like message received or notification displayed.
 */
public class SampleMessagingService extends OutboundMessagingService {

    private final static String LOG_TAG = "SampleMessagingService";

    @Override
    public void onReceivedMessage(RemoteMessage message) {
        super.onReceivedMessage(message);
        Logger.d(LOG_TAG, "Non-Connect notification was received");
    }

    @Override
    public void onNotificationReceived(PushNotification notification) {
        super.onNotificationReceived(notification);
        Logger.d(LOG_TAG, "Connect notification was received");
    }

    @Override
    public void onNotificationDisplayed(PushNotification notification) {
        super.onNotificationDisplayed(notification);
        Logger.d(LOG_TAG, "Connect notification was displayed");
    }

    @Override
    public Notification buildNotification(PushNotification pushNotification) {
        Logger.d(LOG_TAG, "Building a custom  notification");

        NotificationCompat.Builder notificationBuilder =
                pushNotification.createNotificationBuilder(this);

        notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));

        return notificationBuilder.build();
    }
}
