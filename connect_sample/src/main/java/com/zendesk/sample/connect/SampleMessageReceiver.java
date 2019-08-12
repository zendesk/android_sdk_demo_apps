package com.zendesk.sample.connect;

import android.app.Service;

import com.google.firebase.messaging.RemoteMessage;
import com.zendesk.connect.MessageReceiver;
import com.zendesk.logger.Logger;

/**
 * You can optionally provide an implementation of {@link MessageReceiver} to react to Firebase
 * events like message received or new token as well.
 * <p>
 * If you do so then you must register this class by calling
 * {@link com.zendesk.connect.ConnectMessagingService#setMessageReceiver}.
 */
public class SampleMessageReceiver implements MessageReceiver {

    private final static String LOG_TAG = "SampleMessageReceiver";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage, Service service) {
        Logger.d(LOG_TAG, "Firebase message was received");
    }

    @Override
    public void onDeletedMessages(Service service) {
        Logger.d(LOG_TAG, "Firebase message was deleted");
    }

    @Override
    public void onMessageSent(String messageId, Service service) {
        Logger.d(LOG_TAG, "Firebase message was sent");
    }

    @Override
    public void onSendError(String messageId, Exception exception, Service service) {
        Logger.d(LOG_TAG, "An error occurred when sending a Firebase message");
    }

    @Override
    public void onNewToken(String token, Service service) {
        Logger.d(LOG_TAG, "A new Firebase token was received ");
    }

}
