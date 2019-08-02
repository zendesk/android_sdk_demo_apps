package com.zendesk.sample.connect;

import android.content.Intent;
import android.net.Uri;

import com.zendesk.connect.ConnectActionService;
import com.zendesk.connect.SystemPushPayload;
import com.zendesk.logger.Logger;

/**
 * You can optionally extend {@link ConnectActionService} to handle what happens when
 * a user taps on a push notification delivered by Connect. This service must be
 * registered in your manifest.
 * <p>
 * If you don't want to handle push actions yourself then you can leave out this
 * class and register {@link ConnectActionService} in your manifest.
 */
public class SampleActionService extends ConnectActionService {

    private final static String LOG_TAG = "SampleActionService";

    @Override
    public void onOpenNotification(SystemPushPayload payload) {
        Logger.d(LOG_TAG,
                "User opened notification and was directed to: %s",
                payload.getDeeplinkUrl());

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(payload.getDeeplinkUrl()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
