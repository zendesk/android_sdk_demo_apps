package com.zendesk.sample.connect;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.zendesk.connect.ConnectMessagingService;
import com.zendesk.connect.NotificationPayload;
import com.zendesk.logger.Logger;

/**
 * You can optionally extend {@link ConnectMessagingService} to handle building
 * notifications yourself, or handle events like message received or notification
 * displayed. This service must be registered in your manifest.
 *
 * If you don't want to handle push notifications yourself you can leave out this
 * class and register {@link ConnectMessagingService} in your manifest.
 */
public class SampleMessagingService extends ConnectMessagingService {

    private final static String LOG_TAG = "SampleMessagingService";

    @Override
    public void handleNonConnectNotification(RemoteMessage message) {
        Logger.d(LOG_TAG, "Non-Connect notification was received");
    }

    @Override
    public void onNotificationReceived(NotificationPayload payload) {
        Logger.d(LOG_TAG, "Connect notification was received");
    }

    @Override
    public void onNotificationDisplayed(NotificationPayload payload) {
        Logger.d(LOG_TAG, "Connect notification was displayed");
    }

    @Override
    public Notification provideCustomNotification(NotificationPayload payload) {
        Logger.d(LOG_TAG, "Building a custom  notification");

        String notificationChannelId = getString(R.string.connect_notification_channel_id);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, notificationChannelId);

        notificationBuilder.setContentTitle(payload.getTitle());
        notificationBuilder.setContentText(payload.getBody());
        notificationBuilder.setColor(getResources().getColor(R.color.colorAccent));
        notificationBuilder.setSmallIcon(R.drawable.ic_connect_notification_icon);

        return notificationBuilder.build();
    }
}
