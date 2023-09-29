package com.zendesk.example.ua;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.notifications.AirshipNotificationProvider;
import com.urbanairship.push.notifications.NotificationArguments;
import com.zendesk.logger.Logger;
import com.zendesk.util.StringUtils;

import zendesk.core.AnonymousIdentity;
import zendesk.core.Zendesk;
import zendesk.support.Support;


public class Global extends Application {

    static final String SUBDOMAIN_URL = "";
    static final String APPLICATION_ID = "";
    static final String OAUTH_CLIENT_ID = "";

    private static boolean missingCredentials = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.setLoggable(true);

        if (StringUtils.isEmpty(SUBDOMAIN_URL)
                || StringUtils.isEmpty(APPLICATION_ID)
                || StringUtils.isEmpty(OAUTH_CLIENT_ID)) {
            missingCredentials = true;
            return;
        }

        Zendesk.INSTANCE.init(this,
                SUBDOMAIN_URL,
                APPLICATION_ID,
                OAUTH_CLIENT_ID);

        Zendesk.INSTANCE.setIdentity(
                new AnonymousIdentity.Builder()
                        .withNameIdentifier("{Optional name}")
                        .withEmailIdentifier("{Optional email}")
                        .build()
        );

        Support.INSTANCE.init(Zendesk.INSTANCE);
        initUrbanAirship();
    }

    private void initUrbanAirship() {
        // Start Firebase Messaging
        FirebaseApp.initializeApp(getApplicationContext());

        // Initialize Urban Airship
        UAirship.takeOff(this);

        // Enable push notification
        UAirship.shared().getPushManager().setUserNotificationsEnabled(true);

        // 'ic_chat_bubble_outline_black_24dp' should be displayed as notification icon
        UAirship.shared().getPushManager().setNotificationProvider(new CustomNotificationFactory(getApplicationContext(), UAirship.shared().getAirshipConfigOptions()));

        UAirship.shared().getPushManager().setNotificationListener(new CustomNotificationListener(getApplicationContext()));
    }

    static boolean isMissingCredentials() {
        return missingCredentials;
    }

}

class CustomNotificationFactory extends AirshipNotificationProvider {

    public CustomNotificationFactory(
            @NonNull Context context,
            @NonNull AirshipConfigOptions configOptions
    ) {
        super(context, configOptions);
    }

    @WorkerThread
    @NonNull
    @Override
    protected NotificationCompat.Builder onExtendBuilder(
            @NonNull Context context,
            @NonNull NotificationCompat.Builder builder,
            @NonNull NotificationArguments arguments
    ) {
        builder = super.onExtendBuilder(context, builder, arguments);

        // Apply any defaults to the builder
        builder.setSmallIcon(R.drawable.ic_chat_bubble);
        return builder;
    }
}