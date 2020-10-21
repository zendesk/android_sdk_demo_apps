package com.zendesk.chat_v2.sample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import zendesk.chat.ChatConfiguration;
import zendesk.messaging.MessagingActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Global.isMissingCredentials()) {
            setContentView(R.layout.missing_credentials);
            return;
        }

        ChatConfiguration chatConfiguration = ChatConfiguration.builder().withPreChatFormEnabled(false).build();

        MessagingActivity.builder()
                .withEngines(ChatInterceptorEngine.engine(getApplicationContext()))
                .show(this, chatConfiguration);
    }
}
