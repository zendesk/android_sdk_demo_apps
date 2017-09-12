package com.zendesk.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zendesk.logger.Logger;
import com.zendesk.sdk.feedback.ui.ContactZendeskActivity;
import com.zendesk.sdk.model.push.PushRegistrationResponse;
import com.zendesk.sdk.network.UserProvider;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.sdk.requests.RequestActivity;
import com.zendesk.sdk.support.SupportActivity;
import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;
import com.zendesk.util.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * This activity is a springboard that you can use to launch various parts of the Zendesk SDK.
 */
public class MainActivity extends FragmentActivity {

    private static final String LOG_TAG = "MainActivity";

    @SuppressWarnings("FieldCanBeLocal")
    private final int CONTACT_ZENDESK_ACTIVITY_ID = 123;

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
                ContactZendeskActivity.startActivity(MainActivity.this, null);
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

        final EditText supportEdittext = (EditText) findViewById(R.id.main_edittext_support);

        findViewById(R.id.main_btn_support).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String labels = supportEdittext.getText().toString();
                String[] labelsArray = null;

                if (StringUtils.hasLength(labels)) {
                    labelsArray = labels.split(",");
                }

                new SupportActivity.Builder()
                        .withLabelNames(labelsArray)
                        .show(MainActivity.this);
            }
        });

        final EditText devicePushToken = (EditText) findViewById(R.id.main_edittext_push);

        findViewById(R.id.main_btn_push_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZendeskConfig.INSTANCE.enablePushWithIdentifier(devicePushToken.getText().toString(), new ZendeskCallback<PushRegistrationResponse>() {
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

        findViewById(R.id.main_btn_push_register_ua).setOnClickListener(new View.OnClickListener() {
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

        final ZendeskCallback<List<String>> userTagsCallback = new ZendeskCallback<List<String>>() {
            @Override
            public void onSuccess(final List<String> tags) {
                Toast.makeText(MainActivity.this, String.format(Locale.US, "User tags: %s", tags), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(final ErrorResponse error) {
                Toast.makeText(getApplicationContext(), "Usertag failure: " + error.getReason(), Toast.LENGTH_SHORT).show();
            }
        };

        findViewById(R.id.main_btn_user_tags_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String input = ((EditText) findViewById(R.id.main_edittext_user_tags)).getText().toString();
                final UserProvider userProvider = ZendeskConfig.INSTANCE.provider().userProvider();
                userProvider.addTags(StringUtils.fromCsv(input), userTagsCallback);
            }
        });

        findViewById(R.id.main_btn_user_tags_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String input = ((EditText) findViewById(R.id.main_edittext_user_tags)).getText().toString();
                final UserProvider userProvider = ZendeskConfig.INSTANCE.provider().userProvider();
                userProvider.deleteTags(StringUtils.fromCsv(input), userTagsCallback);
            }
        });

        findViewById(R.id.main_btn_clear_storage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZendeskConfig.INSTANCE.storage().sdkSettingsStorage().deleteStoredSettings();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CONTACT_ZENDESK_ACTIVITY_ID){

            if(resultCode == Activity.RESULT_OK){
                Logger.d(LOG_TAG, "ContactZendeskActivity - Request submitted");
            } else if(resultCode == Activity.RESULT_CANCELED){

                if(data != null){
                    final String reason = data.getStringExtra(ContactZendeskActivity.RESULT_ERROR_REASON);
                    Logger.d(LOG_TAG, "ContactZendeskActivity - Error: '" + reason + "'");

                } else {
                    Logger.d(LOG_TAG, "ContactZendeskActivity - OnBackPressed User cancelled");

                }
            }
        }
    }
}
