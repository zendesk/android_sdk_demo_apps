package com.zendesk.support_request_updates_sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zendesk.logger.Logger;
import com.zendesk.sdk.model.request.RequestUpdates;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;

public class RequestUpdatesActivity extends AppCompatActivity {

    private final static String LOG_TAG = "RequestUpdatesActivity";

    private final static String TICKET_LIST_BUTTON_TEXT = "Ticket List";

    private Button ticketListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_updates);

        ticketListButton = (Button) findViewById(R.id.ticket_list_button);
        ticketListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestUpdatesActivity.this, RequestListActivity.class));
            }
        });

        findViewById(R.id.request_updates_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZendeskConfig.INSTANCE.provider().requestProvider().getUpdatesForDevice(new ZendeskCallback<RequestUpdates>() {
                    @Override
                    public void onSuccess(RequestUpdates requestUpdates) {
                        if (requestUpdates != null && requestUpdates.hasUpdates()) {
                            ticketListButton.setText(TICKET_LIST_BUTTON_TEXT + " (" + requestUpdates.getUpdateCount() + " new)");
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
