package com.zendesk.sample;

import android.app.Application;

import com.zendesk.logger.Logger;

import zendesk.connect.ConnectSDK;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;

public class Global extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable logging
        Logger.setLoggable(true);

        /**
         * Initialize Zendesk SDK with your Zendesk subdomain, mobile SDK app ID, and client ID.
         *
         * Get these details from your Zendesk dashboard: Admin -> Channels -> MobileSDK
         */
        Zendesk.INSTANCE.init(this,
                "https://{subdomain}.zendesk.com",
                "{applicationId}",
                "{oauthClientId}");

        /**
         * Authenticate using an anonymous identity (with details).
         */
        Identity anon = new AnonymousIdentity.Builder()
                .withExternalIdentifier("{optional external identifier}")
                .withEmailIdentifier("{optional email}")
                .withNameIdentifier("{optional name}")
                .build();
        Zendesk.INSTANCE.setIdentity(anon);

        /**
         * Initialize Connect SDK
         */
        ConnectSDK.INSTANCE.init(Zendesk.INSTANCE);
    }
}
