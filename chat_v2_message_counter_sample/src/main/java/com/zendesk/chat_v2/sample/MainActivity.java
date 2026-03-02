package com.zendesk.chat_v2.sample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;

import zendesk.chat.Account;
import zendesk.chat.Chat;
import zendesk.chat.ChatConfiguration;
import zendesk.chat.ChatEngine;
import zendesk.chat.ChatInfo;
import zendesk.chat.ChatProvider;
import zendesk.chat.ChatSettings;
import zendesk.chat.ChatState;
import zendesk.chat.ConnectionStatus;
import zendesk.chat.ObservationScope;
import zendesk.chat.Observer;
import zendesk.chat.ProfileProvider;
import zendesk.chat.VisitorInfo;
import zendesk.classic.messaging.MessagingActivity;
import zendesk.classic.messaging.MessagingConfiguration;
import zendesk.classic.messaging.MessagingViewModel;
import zendesk.configurations.ConfigurationHelper;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Global.isMissingCredentials()) {
            setContentView(com.zendesk.demo_apps_commons.R.layout.missing_credentials);
            return;
        }
        setContentView(R.layout.activity_main);
//        final UnreadMessageCounter unreadMessageCounter = new UnreadMessageCounter(this);

        findViewById(R.id.btnChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                unreadMessageCounter.stopCounter();
//                MessagingActivity.builder()
//                        .withEngines(ChatEngine.engine())
//                        .show(MainActivity.this);
                Chat.INSTANCE.providers().chatProvider().setDepartment("Test",null);
                Chat.INSTANCE.providers().chatProvider().sendMessage("Test");
                Chat.INSTANCE.providers().accountProvider().getAccount(new ZendeskCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        Log.d("T", account.getStatus() + "");
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        // Handle error in getting Account here
                    }
                });
                ChatState chatState = Chat.INSTANCE.providers().chatProvider().getChatState();
                if (chatState != null) {
                    Log.d("T", chatState.getChatLogs().size() + " ");
                }

            }
        });

//        findViewById(R.id.btnChat2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                unreadMessageCounter.stopCounter();
////                MessagingActivity.builder()
////                        .withEngines(ChatEngine.engine())
////                        .show(MainActivity.this);
//
//            }
//        });

        findViewById(R.id.btnCounter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                unreadMessageCounter.startCounter();
//                @SuppressLint("RestrictedApi")
//                ConfigurationHelper configurationHelper = new ConfigurationHelper();
//                @SuppressLint("RestrictedApi")
//                final MessagingConfiguration messagingConfiguration =
//                        configurationHelper.fromBundle(getIntent().getExtras(), MessagingConfiguration.class);

                ProfileProvider profileProvider = Chat.INSTANCE.providers().profileProvider();
                ChatProvider chatProvider = Chat.INSTANCE.providers().chatProvider();
                VisitorInfo visitorInfo = VisitorInfo.builder()
                        .withPhoneNumber("570123123")
                        .withEmail("test@gmail.com")
                        .withName("Test")
                        .build();


                profileProvider.setVisitorInfo(visitorInfo, null);

                chatProvider.observeChatState(new ObservationScope(), new Observer<ChatState>() {
                    @Override
                    public void update(ChatState chatState) {
                        if (chatState != null && !chatState.getChatLogs().isEmpty()) {
                            Log.d("TEst","" + chatState.getChatLogs().size());
                        }
                    }
                });



                Chat.INSTANCE.providers().settingsProvider().observeChatSettings(new ObservationScope(), new Observer<ChatSettings>() {
                    @Override
                    public void update(ChatSettings chatState) {
                        if (chatState != null ) {
                            Log.d("TEst","" + chatState.getAllowedFileTypes());
                        }
                    }
                });
                Chat.INSTANCE.providers().connectionProvider().observeConnectionStatus(new ObservationScope(),
                        new Observer<ConnectionStatus>() {
                            @Override
                            public void update(final ConnectionStatus status) {
                                Log.d("TEST", "Processing StatusSSS: " + status.name());
                            }
                        });
                Chat.INSTANCE.providers().connectionProvider().connect();
            }
        });
    }
}