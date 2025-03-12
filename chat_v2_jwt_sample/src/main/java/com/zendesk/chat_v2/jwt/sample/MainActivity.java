package com.zendesk.chat_v2.jwt.sample;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.zendesk.chat_v2.sample.R;

import zendesk.chat.Chat;
import zendesk.chat.ChatConfiguration;
import zendesk.chat.ChatEngine;
import zendesk.chat.CompletionCallback;
import zendesk.chat.JwtAuthenticator;
import zendesk.messaging.MessagingActivity;

public class MainActivity extends AppCompatActivity {

    AppCompatButton btnSet;
    AppCompatButton btnReset;
    AppCompatButton btnStart;

    JwtAuthenticator jwtAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Global.isMissingCredentials()) {
            setContentView(com.zendesk.demo_apps_commons.R.layout.missing_credentials);
            return;
        }
        setContentView(R.layout.jwt_layout);

        btnSet = findViewById(R.id.btnSet);
        btnReset = findViewById(R.id.btnReset);
        btnStart = findViewById(R.id.btnStart);

        jwtAuthenticator = new JwtAuthenticator() {
            @Override
            public void getToken(JwtCompletion jwtCompletion) {
                //Fetch or generate the JWT token at this point
                //OnSuccess
                jwtCompletion.onTokenLoaded("TOKEN-HERE");
                //OnError
                jwtCompletion.onError();
            }
        };

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chat.INSTANCE.setIdentity(jwtAuthenticator);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chat.INSTANCE.resetIdentity();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatConfiguration chatConfiguration = ChatConfiguration.builder().build();

                MessagingActivity.builder()
                        .withEngines(ChatEngine.engine())
                        .show(MainActivity.this, chatConfiguration);
            }
        });
    }
}