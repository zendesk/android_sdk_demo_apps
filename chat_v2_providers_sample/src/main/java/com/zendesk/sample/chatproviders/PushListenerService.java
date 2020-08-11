package com.zendesk.sample.chatproviders;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zendesk.logger.Logger;

import zendesk.chat.Chat;
import zendesk.chat.PushData;
import zendesk.chat.PushNotificationsProvider;

public class PushListenerService extends FirebaseMessagingService {

    private static final String LOG_TAG = "PushListenerService";

    private static final int NOTIFICATION_ID = (int) System.currentTimeMillis();

    private static final String NOTIFICATION_CHANNEL_ID = "ChatApiSampleAppChannelId";
    private static final String NOTIFICATION_CHANNEL_NAME = "ChatApiSampleAppChannelId";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager == null) {
            Log.d(LOG_TAG, "Notification manager not found");
            return;
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_image_black_24dp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[] {0, 200, 100, 200});

            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
            manager.createNotificationChannel(channel);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        PushNotificationsProvider pushNotificationsProvider = Chat.INSTANCE.providers().pushNotificationsProvider();
        PushData pushData = pushNotificationsProvider.processPushNotification(remoteMessage.getData());

        if (pushData == null) {
            Logger.d(LOG_TAG, "Error extracting push notification.");
            return;
        }

        switch(pushData.getType()) {
            case END:
                builder.setContentTitle("Chat ended");
                builder.setContentText("The chat has ended!");
                break;
            case MESSAGE:
                builder.setContentTitle("Chat message");
                builder.setContentText("New chat message!");
                break;
        }

        manager.notify(NOTIFICATION_ID, builder.build());
    }
}
