package com.zopim.sample.chatapi;

import android.app.Application;

import com.zendesk.logger.Logger;
import com.zendesk.util.StringUtils;
import com.zopim.android.sdk.api.ZopimChatApi;

public class Global extends Application {

    private static final String LOG_TAG = "Global";
    private final static String ACCOUNT_KEY = "";

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable logging
        Logger.setLoggable(true);

        if(StringUtils.isEmpty(ACCOUNT_KEY)) {
            Logger.e(LOG_TAG, "============================");
            Logger.e(LOG_TAG, "== No Account Key defined ==");
            Logger.e(LOG_TAG, "============================");
        }

        // Initialize Chat SDK
        ZopimChatApi.init(ACCOUNT_KEY);
    }
}
