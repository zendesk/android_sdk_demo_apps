package com.zendesk.example.ua;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.urbanairship.AirshipReceiver;
import com.urbanairship.push.PushMessage;
import com.zendesk.logger.Logger;
import com.zendesk.sdk.deeplinking.ZendeskDeepLinking;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class IntentReceiver extends AirshipReceiver {

    private final static String LOG_TAG = IntentReceiver.class.getSimpleName();

    @Override
    protected void onNotificationPosted(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {

        PushMessage message = notificationInfo.getMessage();

        final String tid = message.getPushBundle().getString("tid");

        // Check if ticket id is valid
        if(!StringUtils.hasLength(tid)){
            Logger.e(LOG_TAG, String.format(Locale.US, "No valid ticket id found: '%s'", tid));
            return;
        }

        // Try to refresh the comment stream if visible
        final boolean refreshed = ZendeskDeepLinking.INSTANCE.refreshComments(tid);

        // If stream was successfully refreshed, we can cancel the notification
        if(refreshed){
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
        if(!StringUtils.hasLength(tid)){
            Logger.e(LOG_TAG, String.format(Locale.US, "No valid ticket id found: '%s'", tid));
            return false;
        }

        Logger.d(LOG_TAG, String.format(Locale.US, "Ticket Id found: %s", tid));

        // Create an Intent for showing RequestActivity
        final Intent zendeskDeepLinkIntent = getZendeskDeepLinkIntent(context.getApplicationContext(), tid);

        // Check if Intent is valid
        if(zendeskDeepLinkIntent == null){
            Logger.e(LOG_TAG, "Error zendeskDeepLinkIntent is 'null'");
            return false;
        }

        // Show RequestActivity
        context.sendBroadcast(zendeskDeepLinkIntent);

        return true;
    }

    @Nullable
    private Intent getZendeskDeepLinkIntent(Context context, String ticketId){

        // Make sure that Zendesk is initialized
        if(!ZendeskConfig.INSTANCE.isInitialized()) {
            ZendeskConfig.INSTANCE.init(
                context,
                context.getString(R.string.zd_url),
                context.getString(R.string.zd_appid),
                context.getString(R.string.zd_oauth)
            );
        }

        // Initialize a back stack
        final Intent mainActivity = new Intent(context, MainActivity.class);
        final ArrayList<Intent> backStack = new ArrayList<>(Arrays.asList(mainActivity));

        return ZendeskDeepLinking.INSTANCE.getRequestIntent(
                context, ticketId, null, backStack, mainActivity
        );
    }
}