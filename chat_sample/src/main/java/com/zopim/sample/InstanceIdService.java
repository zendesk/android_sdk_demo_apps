package com.zopim.sample;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.zendesk.logger.Logger;
import com.zopim.android.sdk.api.ZopimChat;

public class InstanceIdService extends FirebaseInstanceIdService {

    private static final String LOG_TAG = "InstanceIdService";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Logger.d(LOG_TAG, "Retrieving push token");
        ZopimChat.setPushToken(FirebaseInstanceId.getInstance().getToken());
    }
}
