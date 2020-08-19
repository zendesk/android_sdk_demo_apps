package com.zendesk.chat_v2.jwt.sample;

import android.app.Application;

import com.zendesk.logger.Logger;
import com.zendesk.util.StringUtils;

import zendesk.chat.Chat;

public class Global extends Application {

    private static final String CHAT_ACCOUNT_KEY = "";

    private static boolean missingCredentials = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable logging
        Logger.setLoggable(true);

        if (StringUtils.isEmpty(CHAT_ACCOUNT_KEY)) {
            missingCredentials = true;
            return;
        }

        Chat.INSTANCE.init(this, CHAT_ACCOUNT_KEY);
    }


    static boolean isMissingCredentials() {
        return missingCredentials;
    }
}
