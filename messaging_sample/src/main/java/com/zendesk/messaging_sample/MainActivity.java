package com.zendesk.messaging_sample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import zendesk.answerbot.AnswerBotEngine;
import zendesk.chat.ChatEngine;
import zendesk.messaging.MessagingActivity;
import zendesk.support.SupportEngine;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Global.isMissingCredentials()) {
            setContentView(R.layout.missing_credentials);
            return;
        }

        MessagingActivity.builder()
                .withEngines(AnswerBotEngine.engine(), SupportEngine.engine(), ChatEngine.engine())
                .show(this);
    }
}
