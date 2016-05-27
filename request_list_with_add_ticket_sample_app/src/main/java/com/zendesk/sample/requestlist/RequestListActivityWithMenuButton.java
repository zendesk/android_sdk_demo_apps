package com.zendesk.sample.requestlist;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.zendesk.sdk.feedback.ui.ContactZendeskActivity;
import com.zendesk.sdk.model.access.AnonymousIdentity;
import com.zendesk.sdk.model.access.JwtIdentity;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.sdk.requests.RequestActivity;

public class RequestListActivityWithMenuButton extends RequestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeZendesk();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate your menu file which contains the button(s) you want to add.
        getMenuInflater().inflate(R.menu.request_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Switch on the selected item ID
        switch (item.getItemId()) {
            case R.id.action_create_ticket:
                // Start the ContactZendeskActivity with a Context and an optional ZendeskFeedbackConfiguration
                ContactZendeskActivity.startActivity(this, null);
        }
        return true;
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
}
