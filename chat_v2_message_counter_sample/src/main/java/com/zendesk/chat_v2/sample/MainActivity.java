package com.zendesk.chat_v2.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import zendesk.chat.ChatConfiguration;
import zendesk.chat.ChatEngine;
import zendesk.messaging.MessagingActivity;

public class MainActivity extends AppCompatActivity implements UnreadMessageCounter.UnreadMessageCounterListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Global.isMissingCredentials()) {
            setContentView(R.layout.missing_credentials);
            return;
        }
        setContentView(R.layout.activity_main);
        final UnreadMessageCounter unreadMessageCounter = new UnreadMessageCounter(this);

        findViewById(R.id.btnChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unreadMessageCounter.stopCounter();
                MessagingActivity.builder()
                        .withEngines(ChatEngine.engine())
                        .show(MainActivity.this);

            }
        });

        findViewById(R.id.btnCounter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unreadMessageCounter.startCounter();
            }
        });
    }

    @Override
    public void onUnreadCountUpdated(int unreadCount) {
        ((TextView) findViewById(R.id.tvMessageCounter)).setText(Integer.toString(unreadCount));
    }
}