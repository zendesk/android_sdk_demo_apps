package com.zendesk.answerbot_sample;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import zendesk.answerbot.AnswerBotEngine;
import zendesk.messaging.MessagingActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Global.isMissingCredentials()) {
            setContentView(com.zendesk.demo_apps_commons.R.layout.missing_credentials);
            return;
        }
        MessagingActivity.builder()
                .withEngines(AnswerBotEngine.engine())
                .show(this);
    }
}
