package com.zendesk.sample.connect;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.zendesk.logger.Logger;

import io.outbound.sdk.Outbound;

public class Global extends Application {

    private final static String CONNECT_API_KEY = ""; // Replace this with your Connect API key
    private final static String PROJECT_ID = ""; // Firebase console -> Project settings -> General -> Project Id
    private final static String API_KEY = ""; // Firebase console -> Project settings -> Cloud messaging -> Server Key
    private final static String FCM_SENDER_ID = ""; // Firebase console -> Project settings -> Cloud messaging -> Sender ID

    private final static String NOTIFICATION_CHANNEL_ID = "connect_demo_notification_channel";
    private final static String NOTIFICATION_CHANNEL_NAME = "Connect Demo Notifications";

    @Override
    public void onCreate() {
        super.onCreate();

        if (CONNECT_API_KEY.isEmpty() || PROJECT_ID.isEmpty() || API_KEY.isEmpty() || FCM_SENDER_ID.isEmpty()) {
            throw new RuntimeException("You must provide a Connect API key and valid Firebase project credentials");
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
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME, importance);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }

            Outbound.init(this, CONNECT_API_KEY, NOTIFICATION_CHANNEL_ID);
        } else {
            Outbound.init(this, CONNECT_API_KEY);
        }

    }
}
