package com.zendesk.sample.customfields;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zendesk.sdk.model.access.AnonymousIdentity;
import com.zendesk.sdk.model.access.JwtIdentity;
import com.zendesk.sdk.model.request.CreateRequest;
import com.zendesk.sdk.model.request.CustomField;
import com.zendesk.sdk.network.RequestProvider;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;

import java.util.ArrayList;
import java.util.List;

public class CustomFieldsActivity extends AppCompatActivity {

    private static final String TAG = "CustomFieldsActivity";

    // EditText Views for text input for custom fields
    private EditText customInput1;
    private EditText customInput2;
    private EditText customInput3;
    private EditText customInput4;
    private Button submitButton;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Zendesk SDK and set Anonymous or JWT authentication details
        initializeZendesk();

        // Uninteresting method of findViewById calls
        captureViews();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show friendly ProgressDialog with indeterminate progress spinner, non-cancelable.
                progressDialog = ProgressDialog.show(CustomFieldsActivity.this, "Sending Request", "Aligning 1s and 0s...", true, false);

                // Get an instance of the RequestProvider from the ZendeskConfig
                RequestProvider provider = ZendeskConfig.INSTANCE.provider().requestProvider();

                // Build your CreateRequest object, which includes the custom field data
                CreateRequest request = buildCreateRequest();

                // Optionally, create a ZendeskCallback. This can be null.
                ZendeskCallback<CreateRequest> callback = buildCallback();

                // Call the provider method
                provider.createRequest(request, callback);
            }
        });
    }

    private void initializeZendesk() {
        // Initialize the SDK with your Zendesk subdomain, mobile SDK app ID, and client ID.
        // Get these details from your Zendesk dashboard: Admin -> Channels -> MobileSDK
        ZendeskConfig.INSTANCE.init(getApplicationContext(),
                "https://[subdomain].zendesk.com",
                "[app ID]",
                "[client ID]");

        // Set either Anonymous or JWT identity, as below:

        // 1. Anonymous (All fields are optional.)
        ZendeskConfig.INSTANCE.setIdentity(
                new AnonymousIdentity.Builder()
                        .withNameIdentifier("[optional name")
                        .withEmailIdentifier("[optional email]")
                        .withExternalIdentifier("[optional external identifier]")
                        .build()
        );

        // 2. JWT (Must be initialized with your JWT identifier)
        ZendeskConfig.INSTANCE.setIdentity(new JwtIdentity("[JWT identifier]"));
    }

    private CreateRequest buildCreateRequest() {
        // Create an instance of CreateRequest
        CreateRequest request = new CreateRequest();

        // Set any details on it you want
        request.setSubject("Test Custom Fields Ticket");
        request.setDescription("We should see custom fields on this ticket!");

        // Build CustomField objects as desired, using the Custom Field IDs from your Zendesk dashboard.
        request.setCustomFields(buildCustomFieldsList());

        return request;
    }

    private List<CustomField> buildCustomFieldsList() {
        List<CustomField> list = new ArrayList<>();

        // Make sure to use the Custom Field IDs from your Zendesk dashboard!
        list.add(new CustomField(1L, customInput1.getText().toString()));
        list.add(new CustomField(2L, customInput2.getText().toString()));
        list.add(new CustomField(3L, customInput3.getText().toString()));
        list.add(new CustomField(4L, customInput4.getText().toString()));

        return list;
    }

    private ZendeskCallback<CreateRequest> buildCallback() {
        // Build the optional callback to handle success/error
      return new ZendeskCallback<CreateRequest>() {
          @Override
          public void onSuccess(CreateRequest createRequest) {
              progressDialog.dismiss();
              Log.d(TAG, "onSuccess: Ticket created!");
              Toast.makeText(getApplicationContext(), "Success! Ticket created!", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onError(ErrorResponse errorResponse) {
              progressDialog.dismiss();
              Log.d(TAG, "onError: " + errorResponse.getReason());
              Toast.makeText(getApplicationContext(), "Error! " + errorResponse.getReason(), Toast.LENGTH_SHORT).show();
          }
      };
    }

    private void captureViews() {
        customInput1 = (EditText) findViewById(R.id.customInput1);
        customInput2 = (EditText) findViewById(R.id.customInput2);
        customInput3 = (EditText) findViewById(R.id.customInput3);
        customInput4 = (EditText) findViewById(R.id.customInput4);
        submitButton = (Button) findViewById(R.id.btnSubmit);
    }
}
