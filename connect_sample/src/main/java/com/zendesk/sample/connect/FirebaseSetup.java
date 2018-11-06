package com.zendesk.sample.connect;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

class FirebaseSetup {

    private static String PROJECT_ID = ""; // Firebase console -> Project settings -> General -> Project Id
    private static String API_KEY = ""; // Firebase console -> Project settings -> Cloud messaging -> Server Key
    private static String FCM_SENDER_ID = ""; // Firebase console -> Project settings -> Cloud messaging -> Sender ID

    static void initFirebase(Application app) {

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

    }
}
