package com.zopim.sample.chatapi;

import android.app.Application;

import com.zendesk.logger.Logger;
import com.zopim.android.sdk.api.ZopimChatApi;

import java.util.concurrent.TimeUnit;

public class Global extends Application {

    private final static String ACCOUNT_KEY = "";

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable logging
        Logger.setLoggable(true);

        // Initialize Chat SDK
        ZopimChatApi.init(ACCOUNT_KEY);
    }

}
