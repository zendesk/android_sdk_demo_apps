package com.zendesk.ratemyapp.sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zendesk.logger.Logger;
import com.zendesk.ratemyapp.DialogActionListener;
import com.zendesk.ratemyapp.RateMyAppConfig;
import com.zendesk.ratemyapp.RateMyAppDialog;
import com.zendesk.sdk.model.access.AnonymousIdentity;
import com.zendesk.sdk.model.access.Identity;
import com.zendesk.sdk.model.request.CreateRequest;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;
import com.zendesk.util.StringUtils;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.zendesk.android";
    private static final String ZENDESK_SUBDOMAIN = "https://mxssl98.zd-master.com";
    private static final String ZENDESK_APP_ID = "8f10e8cab14b26fd665c4afb9213809d9ba39750b72a1119";
    private static final String ZENDESK_OAUTH_CLIENT_ID = "mobile_sdk_client_ec5abaf13f66cb3fe72d";

    private RateMyAppConfig config;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable logging for debug purposes
        Logger.setLoggable(true);
        // and initialise the Zendesk Support SDK
        initialiseZendesk();

        // Instantiate the demo RateMyAppConfig object we'll be using for our buttons.
        config = new RateMyAppConfig.Builder()
                .withAndroidStoreRatingButton(PLAY_STORE_URL)
                .withVersion(getApplicationContext(), BuildConfig.VERSION_NAME)
                .build();

        // Show dialog that respects the "Don't Ask Again" behaviour
        setupShowButton();
        // Show dialog that doesn't respect the "Don't Ask Again" behaviour, and always shows
        setupShowAlwaysButton();
        // Show dialog that "sends" feedback by Android's ACTION_SEND Intent, instead of via Zendesk.
        setupShowOtherButton();
    }

    private void setupShowButton() {
        Button showBtn = (Button) findViewById(R.id.buttonShow);

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateMyAppDialog.show(MainActivity.this, config, zendeskActionListener);
            }
        });
    }

    private void setupShowAlwaysButton() {
        Button showAlwaysButton = (Button) findViewById(R.id.buttonShowAlways);

        showAlwaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateMyAppDialog.showAlways(MainActivity.this, config, zendeskActionListener);
            }
        });
    }

    private void setupShowOtherButton() {
        Button showOtherBtn = (Button) findViewById(R.id.buttonShowOther);

        showOtherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateMyAppDialog.show(MainActivity.this, config, otherActionListener);
            }
        });
    }

    private void initialiseZendesk() {
        // Initialise the Support SDK with subdomain, app ID, and OAuth client ID from the Zendesk console
        ZendeskConfig.INSTANCE.init(this, ZENDESK_SUBDOMAIN, ZENDESK_APP_ID, ZENDESK_OAUTH_CLIENT_ID);
        // Set an Identity, using either Anonymous or JWT authentication.
        ZendeskConfig.INSTANCE.setIdentity(new AnonymousIdentity.Builder().build());
        // ZendeskConfig.INSTANCE.setIdentity(new JwtIdentity("jwt token goes here"));
    }

    /**
     * This DialogActionListener is a simple example of how the {@link RateMyAppDialog} can be used
     * without using Zendesk. The {@link DialogActionListener#onSendButtonClicked(String)} method
     * can be used to do whatever you want. In this case, it simply sends the user's feedback as an
     * {@link Intent#ACTION_SEND}.
     */
    private DialogActionListener otherActionListener = new DialogActionListener() {
        @Override public void storeButtonClicked() {}
        @Override public void feedbackButtonClicked() {}
        @Override public void dontAskAgainClicked() {}
        @Override public void cancelled() {}

        @Override
        public void onSendButtonClicked(String feedback) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, feedback);
            intent.setType("text/plain");
            startActivity(intent);
        }
    };

    /**
     * This DialogActionListener is a simple example of using the Zendesk Support SDK's
     * {@link com.zendesk.sdk.network.RequestProvider} to create a new request from the feedback
     * entered by the user.
     */
    private DialogActionListener zendeskActionListener = new DialogActionListener() {
        @Override public void storeButtonClicked() {}
        @Override public void feedbackButtonClicked() {}
        @Override public void dontAskAgainClicked() {}
        @Override public void cancelled() {}

        @Override
        public void onSendButtonClicked(String feedback) {
            progressDialog = ProgressDialog.show(MainActivity.this, "Sending feedback", "Sending...", true, false);

            CreateRequest request = buildCreateRequest(feedback);

            //noinspection unchecked
            ZendeskConfig.INSTANCE.provider().requestProvider().createRequest(
                    request, new ZendeskCallback<CreateRequest>() {
                        @Override
                        public void onSuccess(CreateRequest createRequest) {
                            progressDialog.dismiss();

                            Log.d(LOG_TAG, "Feedback was submitted successfully.");
                            ZendeskConfig.INSTANCE.getTracker().rateMyAppFeedbackSent();

                            Toast.makeText(
                                    MainActivity.this,
                                    getString(R.string.rate_my_app_dialog_feedback_send_success_toast),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        @Override
                        public void onError(ErrorResponse error) {
                            progressDialog.dismiss();

                            Log.e(LOG_TAG, error.getReason());

                            if (error.isNetworkError()) {
                                Toast.makeText(
                                        MainActivity.this,
                                        getString(R.string.rate_my_app_dialog_feedback_send_error_no_connectivity_toast),
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Toast.makeText(
                                        MainActivity.this,
                                        getString(R.string.rate_my_app_dialog_feedback_send_error_toast),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    });
        }

        private CreateRequest buildCreateRequest(String feedback) {
            CreateRequest request = new CreateRequest();

            request.setSubject("Sample Open Source RMA integration");
            request.setDescription(feedback);
            request.setEmail(getRequestEmail());
            request.setTicketFormId(ZendeskConfig.INSTANCE.getTicketFormId());
            request.setCustomFields(ZendeskConfig.INSTANCE.getCustomFields());

            return request;
        }

        private String getRequestEmail() {
            Identity identity = ZendeskConfig.INSTANCE.storage().identityStorage().getIdentity();

            if (identity instanceof AnonymousIdentity) {
                AnonymousIdentity anonymousIdentity = (AnonymousIdentity) identity;

                if (StringUtils.hasLength(anonymousIdentity.getEmail())) {
                    return anonymousIdentity.getEmail();
                }
            }
            return null;
        }
    };
}
