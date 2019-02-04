package com.zendesk.sample.connect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zendesk.connect.Connect;
import com.zendesk.connect.Event;
import com.zendesk.connect.EventFactory;
import com.zendesk.connect.User;
import com.zendesk.connect.UserBuilder;
import com.zendesk.logger.Logger;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = "ConnectSampleActivity";

    private User sampleUser;
    private Event sampleEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Global.isMissingCredentials()) {
            setContentView(R.layout.missing_credentials);
            return;
        }
        setContentView(R.layout.activity_main);

        sampleUser = new UserBuilder("Y_E_E_e_e_s")
                .setFirstName("Steven")
                .setLastName("Toast")
                .build();

        sampleEvent = EventFactory.createEvent("Sample Event");

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
        Connect.INSTANCE.identifyUser(user);
    }

    /**
     * Tracks an event with Connect
     * @param event the event to be tracked
     */
    private void trackEvent(Event event) {
        Logger.d(LOG_TAG, "Tracking event");
        Connect.INSTANCE.trackEvent(event);
    }

    /**
     * Registers the current user for push notifications
     */
    private void registerForPush() {
        Logger.d(LOG_TAG, "Registering user for push");
        Connect.INSTANCE.registerForPush();
    }

    /**
     * Disables push notifications for the current user
     */
    private void disablePush() {
        Logger.d(LOG_TAG, "Disabling push for user");
        Connect.INSTANCE.disablePush();
    }

    /**
     * Logs out the current user by disabling push and clearing storage
     */
    private void logout() {
        Logger.d(LOG_TAG, "Logging out user");
        Connect.INSTANCE.logoutUser();
    }
}
