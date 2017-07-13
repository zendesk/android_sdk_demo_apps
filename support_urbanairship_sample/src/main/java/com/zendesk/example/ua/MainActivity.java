package com.zendesk.example.ua;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.urbanairship.UAirship;
import com.urbanairship.google.PlayServicesUtils;
import com.zendesk.logger.Logger;
import com.zendesk.sdk.feedback.ui.ContactZendeskActivity;
import com.zendesk.sdk.model.access.Identity;
import com.zendesk.sdk.model.push.PushRegistrationResponse;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.sdk.requests.RequestActivity;
import com.zendesk.sdk.support.SupportActivity;
import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;

import java.util.Locale;

/**
 * This activity is a springboard that you can use to launch various parts of the Zendesk SDK.
 */
public class MainActivity extends FragmentActivity {

    private static final String LOG_TAG = "MainActivity";

    private Identity getZendeskIdentity(){
        Identity user = null;

        // Anonymous:
        // user = new AnonymousIdentity.Builder()
        //      .build();

        // JWT:
        // user = new JwtIdentity("<jwt_id>");

        return user;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (PlayServicesUtils.isGooglePlayStoreAvailable(this)) {
            PlayServicesUtils.handleAnyPlayServicesError(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * This will make a full-screen feedback screen appear. It is very similar to how
         * the feedback dialog works but it is hosted in an activity.
         */
        findViewById(R.id.main_btn_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactZendeskActivity.class);
                startActivity(intent);
            }
        });

        /**
         * This will launch an Activity that will show the current Requests that a
         * user has opened.
         */
        findViewById(R.id.main_btn_request_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RequestActivity.class));
            }
        });

        findViewById(R.id.main_btn_support).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SupportActivity.Builder().show(MainActivity.this);
            }
        });

        final EditText devicePushToken = (EditText) findViewById(R.id.main_edittext_push);

        findViewById(R.id.main_btn_push_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZendeskConfig.INSTANCE.enablePushWithUAChannelId(devicePushToken.getText().toString(), new ZendeskCallback<PushRegistrationResponse>() {
                    @Override
                    public void onSuccess(PushRegistrationResponse result) {
                        Toast.makeText(getApplicationContext(), "Registration success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ErrorResponse error) {
                        Toast.makeText(getApplicationContext(), "Registration failure: " + error.getReason(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.main_btn_push_unregister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZendeskConfig.INSTANCE.disablePush(devicePushToken.getText().toString(), new ZendeskCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(getApplicationContext(), "Deregistration success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ErrorResponse error) {
                        Toast.makeText(getApplicationContext(), "Deregistration failure: " + error.getReason(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        /*
            The channelId could be null on initial app start:

            According to the Urban Airship docs:
                > The Channel ID is your application’s unique push identifier, and
                > is required in order to target a specific device when sending a push notification.
                > Don’t worry if this value initially comes back as null on your app’s first run
                > (or after clearing the application data),
                > as the Channel ID will be created and persisted during registration.
                > (http://docs.urbanairship.com/platform/android.html)
         */

        final String channelId = UAirship.shared().getPushManager().getChannelId();
        Logger.d(LOG_TAG, String.format(Locale.US, "Urban Airship - Channel Id %s", channelId));
        ((EditText) findViewById(R.id.main_edittext_push)).setText(channelId);
    }
}