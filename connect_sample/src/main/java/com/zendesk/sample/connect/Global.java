package com.zendesk.sample.connect;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.zendesk.connect.Connect;
import com.zendesk.logger.Logger;

public class Global extends Application {

    private final static String CONNECT_PRIVATE_KEY = ""; // Replace this with your Connect private API key
    private final static String PROJECT_ID = ""; // Firebase console -> Project settings -> General -> Project Id
    private final static String API_KEY = ""; // Firebase console -> Project settings -> Cloud messaging -> Server Key
    private final static String FCM_SENDER_ID = ""; // Firebase console -> Project settings -> Cloud messaging -> Sender ID

    private static boolean missingCredentials = false;

    @Override
    public void onCreate() {
        super.onCreate();

        if (CONNECT_PRIVATE_KEY.isEmpty() || PROJECT_ID.isEmpty() || API_KEY.isEmpty() || FCM_SENDER_ID.isEmpty()) {
            missingCredentials = true;
            return;
        }

        Logger.setLoggable(true);

        // Manually initialising the Firebase app
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId(PROJECT_ID)
                .setApiKey(API_KEY)
                .setGcmSenderId(FCM_SENDER_ID)
                .build();

        FirebaseApp.initializeApp(this, options);

        // Need to handle notification channels for Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel defaultChannel = createDefaultNotificationChannel();
            NotificationChannel silentChannel = createSilentNotificationChannel();

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(defaultChannel);
                manager.createNotificationChannel(silentChannel);
            }
        }

        Connect.INSTANCE.init(this, CONNECT_PRIVATE_KEY);
    }

    static boolean isMissingCredentials() {
        return missingCredentials;
    }

    /**
     * Creates a {@link NotificationChannel} that will play a sound when a notification is received
     * from a campaign where `sound` is set to default
     *
     * @return the created the {@link NotificationChannel}
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private NotificationChannel createDefaultNotificationChannel() {
        String channelId = getString(R.string.connect_notification_channel_id);
        String channelName = getString(R.string.connect_notification_channel_name);

        NotificationChannel channel = new NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableVibration(true);

        return channel;
    }

    /**
     * Creates a {@link NotificationChannel} that will remain silent when a notification is received
     * from a campaign where `sound` is set to silent
     *
     * @return the created the {@link NotificationChannel}
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private NotificationChannel createSilentNotificationChannel() {
        String channelId = getString(R.string.connect_silent_notification_channel_id);
        String channelName = getString(R.string.connect_silent_notification_channel_name);

        NotificationChannel channel = new NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT);

        channel.setSound(null, null);

        return channel;
    }

}
