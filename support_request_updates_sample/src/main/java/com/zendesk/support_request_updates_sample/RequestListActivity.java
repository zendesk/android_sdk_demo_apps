package com.zendesk.support_request_updates_sample;

import android.view.Menu;
import android.view.MenuItem;

import com.zendesk.sdk.feedback.ui.ContactZendeskActivity;
import com.zendesk.sdk.requests.RequestActivity;

public class RequestListActivity extends RequestActivity {

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
}
