package com.zopim.sample.chatapi;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.zopim.android.sdk.api.ZopimChatApi;

public class FirebasePushSetup {

    private static final String LOG_TAG = "ChatPushSetup";

    private static String PROJECT_ID = ""; // Firebase console -> Project settings -> General -> Project Id
    private static String API_KEY = ""; // Firebase console -> Project settings -> Cloud messaging -> Server Key
    private static String FCM_SENDER_ID = ""; // Firebase console -> Project settings -> Cloud messaging -> Sender ID

    static void initPush(Application app) {

        if (PROJECT_ID.isEmpty() || API_KEY.isEmpty() || FCM_SENDER_ID.isEmpty()) {
            throw new RuntimeException("To use push in the sample app you must configure " +
                    "the credentials");
        }

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId(PROJECT_ID)
                .setApiKey(API_KEY)
                .setGcmSenderId(FCM_SENDER_ID)
                .build();

        FirebaseApp.initializeApp(app, options);

        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            if (token != null) {
                Log.d(LOG_TAG, "Obtained FCM token");
                ZopimChatApi.setPushToken(token);
            }
        } catch (IllegalStateException e) {
            Log.d(LOG_TAG, "Error requesting FCM token");
        }
    }
}
