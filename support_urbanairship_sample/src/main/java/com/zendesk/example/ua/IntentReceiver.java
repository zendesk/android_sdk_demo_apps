package com.zendesk.example.ua;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.urbanairship.AirshipReceiver;
import com.urbanairship.push.PushMessage;
import com.zendesk.logger.Logger;
import com.zendesk.util.StringUtils;

import java.util.Locale;

import zendesk.core.Zendesk;
import zendesk.support.Support;
import zendesk.support.request.RequestActivity;

public class IntentReceiver extends AirshipReceiver {

    private final static String LOG_TAG = IntentReceiver.class.getSimpleName();

    @Override
    protected void onNotificationPosted(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {

        PushMessage message = notificationInfo.getMessage();

        final String tid = message.getPushBundle().getString("tid");

        // Check if ticket id is valid
        if (!StringUtils.hasLength(tid)) {
            Logger.e(LOG_TAG, String.format(Locale.US, "No valid ticket id found: '%s'", tid));
            return;
        }

        // Try to refresh the comment stream if visible
        final boolean refreshed = Support.INSTANCE.refreshRequest(tid, context);

        // If stream was successfully refreshed, we can cancel the notification
        if (refreshed) {
            final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(notificationInfo.getNotificationId());
        }

    }

    @Override
    protected boolean onNotificationOpened(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        // Return false here to allow Urban Airship to auto launch the launcher activity

        // Extract ticket id
        final String tid = notificationInfo.getMessage().getPushBundle().getString("tid");

        // Check if ticket id is valid
        if (!StringUtils.hasLength(tid)) {
            Logger.e(LOG_TAG, String.format(Locale.US, "No valid ticket id found: '%s'", tid));
            return false;
        }

        Logger.d(LOG_TAG, String.format(Locale.US, "Ticket Id found: %s", tid));

        // Create an Intent for showing RequestActivity
        final Intent zendeskDeepLinkIntent = getZendeskDeepLinkIntent(context.getApplicationContext(), tid);

        // Check if Intent is valid
        if (zendeskDeepLinkIntent == null) {
            Logger.e(LOG_TAG, "Error zendeskDeepLinkIntent is 'null'");
            return false;
        }

        // Show RequestActivity
        context.sendBroadcast(zendeskDeepLinkIntent);

        return true;
    }

    @Nullable
    private Intent getZendeskDeepLinkIntent(Context context, String ticketId) {

        // Make sure that Zendesk is initialized
        if (!Zendesk.INSTANCE.isInitialized()) {
            Zendesk.INSTANCE.init(
                    context,
                    Global.SUBDOMAIN_URL,
                    Global.APPLICATION_ID,
                    Global.OAUTH_CLIENT_ID
            );
            Support.INSTANCE.init(Zendesk.INSTANCE);
        }

        // Initialize a back stack
        final Intent mainActivity = new Intent(context, MainActivity.class);

        return RequestActivity.builder().withRequestId(ticketId).deepLinkIntent(context, mainActivity);
    }
}