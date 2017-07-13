package com.zendesk.example.ua;

import android.app.Application;

import com.urbanairship.UAirship;
import com.urbanairship.push.notifications.DefaultNotificationFactory;
import com.zendesk.logger.Logger;
import com.zendesk.sdk.network.impl.ZendeskConfig;


public class Global extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.setLoggable(true);

        // Initialises the SDK
        ZendeskConfig.INSTANCE.init(
                this,
                getString(R.string.zd_url),
                getString(R.string.zd_appid),
                getString(R.string.zd_oauth)
        );

        initUrbanAirship();
    }

    private void initUrbanAirship() {

        // Initialize Urban Airship
        UAirship.takeOff(this);

        // Enable push notification
        UAirship.shared().getPushManager().setUserNotificationsEnabled(true);

        // 'ic_chat_bubble_outline_black_24dp' should be displayed as notification icon
        final DefaultNotificationFactory defaultNotificationFactory = new DefaultNotificationFactory(getApplicationContext());
        defaultNotificationFactory.setSmallIconId(R.drawable.ic_chat_bubble_outline_black_24dp);
        UAirship.shared().getPushManager().setNotificationFactory(defaultNotificationFactory);
    }

}