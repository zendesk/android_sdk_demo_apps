package com.zendesk.sample.connect;

import com.zendesk.connect.NotificationEventListener;
import com.zendesk.connect.SystemPushPayload;
import com.zendesk.logger.Logger;

/**
 * You can optionally provide an implementation of {@link NotificationEventListener} to react to
 * when notifications are received or displayed.
 * <p>
 * If you do so, then you must register this class by calling
 * {@link com.zendesk.connect.ConnectMessagingService#setNotificationEventListener}.
 */
public class SampleNotificationEventListener implements NotificationEventListener {

    private final static String LOG_TAG = "SampleNotificationEventListener";

    @Override
    public void onNotificationReceived(SystemPushPayload payload) {
        Logger.d(LOG_TAG, "Connect notification was received");
    }

    @Override
    public void onNotificationDisplayed(SystemPushPayload payload) {
        Logger.d(LOG_TAG, "Connect notification was displayed");
    }

}
