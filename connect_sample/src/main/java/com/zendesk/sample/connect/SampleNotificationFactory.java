package com.zendesk.sample.connect;

import android.app.Notification;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.zendesk.connect.NotificationFactory;
import com.zendesk.connect.SystemPushPayload;
import com.zendesk.logger.Logger;

/**
 * You can optionally provide an implementation of {@link NotificationFactory} to create your own
 * custom {@link Notification}.
 * <p>
 * If you do so, then you must register this class by calling
 * {@link com.zendesk.connect.ConnectMessagingService#setNotificationFactory}.
 */
public class SampleNotificationFactory implements NotificationFactory {

    private final static String LOG_TAG = "SampleNotificationFactory";

    private final Context context;

    SampleNotificationFactory(Context context) {
        this.context = context;
    }

    @Override
    public Notification create(SystemPushPayload payload) {
        Logger.d(LOG_TAG, "Building a custom  notification");

        String notificationChannelId = context.getString(R.string.connect_notification_channel_id);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, notificationChannelId);

        notificationBuilder.setContentTitle(payload.getTitle());
        notificationBuilder.setContentText(payload.getBody());
        notificationBuilder.setColor(context.getResources().getColor(R.color.colorAccent));
        notificationBuilder.setSmallIcon(R.drawable.ic_connect_notification_icon);

        return notificationBuilder.build();
    }

}
