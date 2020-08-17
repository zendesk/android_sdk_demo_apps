package com.zendesk.sample.chatproviders.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.zendesk.logger.Logger;
import com.zendesk.sample.chatproviders.R;
import com.zendesk.sample.chatproviders.databinding.ActivityChatBinding;

import zendesk.belvedere.Belvedere;
import zendesk.chat.Chat;
import zendesk.chat.Providers;

/**
 * {@link Activity} that hosts a chat.
 */
public class ChatActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ChatActivity";

    private ChatMvp.Presenter presenter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        final ActivityChatBinding viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        Providers providers = Chat.INSTANCE.providers();
        if (providers == null) {
            Logger.w(LOG_TAG, "You must initialise Chat before using it. Try calling zendesk.chat.Chat.INSTANCE.init()");
            return;
        }
        providers.connectionProvider().connect();
        final ChatMvp.Model model = new ChatModel(providers.chatProvider(),
                providers.connectionProvider(),
                getApplicationContext());
        final ChatMvp.View view = new ChatView(viewDataBinding, getApplicationContext());

        presenter = new ChatPresenter(model, view, Belvedere.from(this));
        view.setPresenter(presenter);
        presenter.install(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        presenter.chatDismissed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
