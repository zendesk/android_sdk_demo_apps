package com.zendesk.sample.uploadattachment;

import android.app.Application;

import com.zendesk.logger.Logger;
import com.zendesk.sdk.model.access.AnonymousIdentity;
import com.zendesk.sdk.model.access.JwtIdentity;
import com.zendesk.sdk.network.impl.ZendeskConfig;

public class Global extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable logging
        Logger.setLoggable(true);

        /**
         * Initialize the SDK with your Zendesk subdomain, mobile SDK app ID, and client ID.
         *
         * Get these details from your Zendesk dashboard: Admin -> Channels -> MobileSDK
         */
        ZendeskConfig.INSTANCE.init(this,
                "https://{subdomain}.zendesk.com",
                "{applicationId}",
                "{oauthClientId}");

        /**
         * Set an identity (authentication).
         *
         * Set either Anonymous or JWT identity, as below:
         */

        // a). Anonymous (All fields are optional.)
        ZendeskConfig.INSTANCE.setIdentity(
                new AnonymousIdentity.Builder()
                        .withNameIdentifier("{optional name}")
                        .withEmailIdentifier("{optional email}")
                        .build()
        );

        // b). JWT (Must be initialized with your JWT identifier)
        ZendeskConfig.INSTANCE.setIdentity(new JwtIdentity("{JWT User Identifier}"));
    }
}
