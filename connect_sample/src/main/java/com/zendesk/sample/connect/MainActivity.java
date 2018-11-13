package com.zendesk.sample.connect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zendesk.logger.Logger;

import io.outbound.sdk.Event;
import io.outbound.sdk.Outbound;
import io.outbound.sdk.User;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = "ConnectSampleActivity";

    private User sampleUser;
    private Event sampleEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sampleUser = new User.Builder()
                .setUserId("Y_E_E_e_e_s")
                .setFirstName("Steven")
                .setLastName("Toast")
                .build();

        sampleEvent = new Event("Sample Event");

        findViewById(R.id.identify_user_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                identifyUser(sampleUser);
            }
        });

        findViewById(R.id.track_event_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackEvent(sampleEvent);
            }
        });

        findViewById(R.id.push_register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerForPush();
            }
        });

        findViewById(R.id.push_disable_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disablePush();
            }
        });

        findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    /**
     * Identifies a user with Connect
     * @param user the user to be identified
     */
    private void identifyUser(User user) {
        Logger.d(LOG_TAG, "Identifying user");
        Outbound.identify(user);
    }

    /**
     * Tracks an event with Connect
     * @param event the event to be tracked
     */
    private void trackEvent(Event event) {
        Logger.d(LOG_TAG, "Tracking event");
        Outbound.track(event);
    }

    /**
     * Registers the current user for push notifications
     */
    private void registerForPush() {
        Logger.d(LOG_TAG, "Registering user for push");
        Outbound.register();
    }

    /**
     * Disables push notifications for the current user
     */
    private void disablePush() {
        Logger.d(LOG_TAG, "Disabling push for user");
        Outbound.disable();
    }

    /**
     * Logs out the current user by disabling push and clearing storage
     */
    private void logout() {
        Logger.d(LOG_TAG, "Logging out user");
        Outbound.logout();
    }
}
