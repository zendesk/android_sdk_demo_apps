package com.zopim.sample.chatapi;

import android.app.Application;

import com.zendesk.logger.Logger;
import com.zendesk.util.StringUtils;
import com.zopim.android.sdk.api.ZopimChatApi;

public class Global extends Application {

    /**
     * Account config needed to initialize {@link com.zopim.android.sdk.api.ZopimChatApi#init(String)}
     * <p/>
     * Account key can be found in Zopim Dashboard at the <a href="https://dashboard.zopim.com/#widget/getting_started">Getting Started Page</a>
     */
    private final static String ACCOUNT_KEY = ""; // NB: Replace this key with your Zopim account key

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable logging
        Logger.setLoggable(true);

        if (StringUtils.isEmpty(ACCOUNT_KEY)) {
            throw new IllegalStateException("No Account Key defined");
        }

        // Initialise Firebase app to receive push notifications
        FirebasePushSetup.initPush(this);

        // Initialize Chat SDK
        ZopimChatApi.init(ACCOUNT_KEY);
    }
}
