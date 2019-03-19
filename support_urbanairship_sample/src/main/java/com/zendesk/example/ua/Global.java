package com.zendesk.example.ua;

import android.app.Application;

import com.urbanairship.UAirship;
import com.urbanairship.push.notifications.DefaultNotificationFactory;
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

        // Initialize Urban Airship
        UAirship.takeOff(this);

        // Enable push notification
        UAirship.shared().getPushManager().setUserNotificationsEnabled(true);

        // 'ic_chat_bubble_outline_black_24dp' should be displayed as notification icon
        final DefaultNotificationFactory defaultNotificationFactory = new DefaultNotificationFactory(getApplicationContext());
        defaultNotificationFactory.setSmallIconId(R.drawable.ic_chat_bubble);
        UAirship.shared().getPushManager().setNotificationFactory(defaultNotificationFactory);
    }

    static boolean isMissingCredentials() {
        return missingCredentials;
    }

}