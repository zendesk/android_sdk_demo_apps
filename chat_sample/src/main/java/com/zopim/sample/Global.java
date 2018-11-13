package com.zopim.sample;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.zendesk.logger.Logger;
import com.zendesk.util.StringUtils;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.model.VisitorInfo;

public class Global extends Application {

    private final static String LOG_TAG = "ChatSample";

    /**
     * Account config needed to initialize {@link com.zopim.android.sdk.api.ZopimChatApi#init(String)}
     * <p/>
     * Account key can be found in Zopim Dashboard at the <a href="https://dashboard.zopim.com/#widget/getting_started">Getting Started Page</a>
     */
    private final static String ACCOUNT_KEY = ""; // NB: Replace this key with your Zopim account key
    private final static String PROJECT_ID = ""; // Firebase console -> Project settings -> General -> Project Id
    private final static String API_KEY = ""; // Firebase console -> Project settings -> Cloud messaging -> Server Key
    private final static String FCM_SENDER_ID = ""; // Firebase console -> Project settings -> Cloud messaging -> Sender ID

    private static boolean missingCredentials = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable logging
        Logger.setLoggable(true);

        if (StringUtils.isEmpty(ACCOUNT_KEY)) {
            missingCredentials = true;
            return;
        }

        // Sample breadcrumb
        ZopimChat.trackEvent("Application created");

        /**
         * Minimum chat configuration. Chat must be initialization before starting the chat.
         */
        ZopimChat.init(ACCOUNT_KEY);

        // Uncomment to see how global configuration is set during initialization. This will be the default chat configuration for all chat sessions.
        {
//            /**
//             * Define default pre-chat form requirements for all of chat. You will have a chance to provide special case requirements when starting a chat.
//             */
//            PreChatForm defaultPreChat = new PreChatForm.Builder()
//                    .email(PreChatForm.Field.OPTIONAL)
//                    .department(PreChatForm.Field.OPTIONAL)
//                    .phoneNumber(PreChatForm.Field.OPTIONAL)
//                    .build();
//
//            /**
//             * Global chat configuration
//             */
//            ZopimChat.init(AccountKey.ACCOUNT_KEY)
//                    .preChatForm(defaultPreChat)
//                    .department("The date")
//                    .tags("sample")
//                    .disableVisitorInfoStorage();
//
//            /**
//             * Specify visitor data. This can be done at any point but it will apply at every chat startup.
//             */
//            VisitorInfo visitorData = new VisitorInfo.Builder()
//                    .name("Sample Visitor")
//                    .phoneNumber("+1800111222333")
//                    .email("visitor@example.com")
//                    .build();
//
//            ZopimChat.setVisitorInfo(visitorData);
        }

        // Initialise Firebase app to receive push notifications
        initFirebase();

        // clear visitor info. Visitor info storage can be disabled at chat initialization
        VisitorInfo emptyVisitorInfo = new VisitorInfo.Builder().build();
        ZopimChat.setVisitorInfo(emptyVisitorInfo);
        Log.v("Zopim Chat Sample", "Visitor info erased.");
    }

    private void initFirebase() {
        if (PROJECT_ID.isEmpty() || API_KEY.isEmpty() || FCM_SENDER_ID.isEmpty()) {
            missingCredentials = true;
            return;
        }

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId(PROJECT_ID)
                .setApiKey(API_KEY)
                .setGcmSenderId(FCM_SENDER_ID)
                .build();

        FirebaseApp.initializeApp(this, options);

        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            if (token != null) {
                Log.d(LOG_TAG, "Obtained FCM token");
                ZopimChat.setPushToken(token);
            }
        } catch (IllegalStateException e) {
            Log.d(LOG_TAG, "Error requesting FCM token");
        }
    }

    static boolean isMissingCredentials() {
        return missingCredentials;
    }
}
