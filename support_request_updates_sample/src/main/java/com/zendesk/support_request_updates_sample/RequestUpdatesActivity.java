package com.zendesk.support_request_updates_sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zendesk.logger.Logger;
import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;

import zendesk.support.RequestUpdates;
import zendesk.support.Support;
import zendesk.support.requestlist.RequestListActivity;

public class RequestUpdatesActivity extends AppCompatActivity {

    private final static String LOG_TAG = "RequestUpdatesActivity";

    private final static String TICKET_LIST_BUTTON_TEXT = "Ticket List";

    private Button ticketListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Global.isMissingCredentials()) {
            setContentView(R.layout.missing_credentials);
            return;
        }
        setContentView(R.layout.activity_request_updates);

        ticketListButton = findViewById(R.id.ticket_list_button);
        ticketListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestListActivity.builder().show(RequestUpdatesActivity.this);
            }
        });

        findViewById(R.id.request_updates_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Support.INSTANCE.provider().requestProvider().getUpdatesForDevice(new ZendeskCallback<RequestUpdates>() {
                    @Override
                    public void onSuccess(RequestUpdates requestUpdates) {
                        if (requestUpdates != null && requestUpdates.hasUpdatedRequests()) {
                            ticketListButton.setText(TICKET_LIST_BUTTON_TEXT + " (" + requestUpdates.getRequestUpdates().size() + " new)");
                        } else {
                            ticketListButton.setText(TICKET_LIST_BUTTON_TEXT);
                        }
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        Logger.d(LOG_TAG, "Error getting updates for device " + errorResponse);
                    }
                });
            }
        });
    }
}
