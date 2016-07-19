package com.zopim.sample.chatapi.chat;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zendesk.belvedere.BelvedereCallback;
import com.zendesk.belvedere.BelvedereResult;
import com.zopim.android.sdk.api.ChatApi;
import com.zopim.android.sdk.api.ZopimChatApi;
import com.zopim.android.sdk.util.BelvedereProvider;
import com.zopim.sample.chatapi.R;
import com.zopim.sample.chatapi.databinding.ActivityChatBinding;

import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private ChatMvp.Presenter presenter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChatApi chat = ZopimChatApi.resume(this);
        if (chat.hasEnded()) {
            chat = ZopimChatApi.start(this);
        }

        final ActivityChatBinding viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        final ChatMvp.Model model = new ChatModel(chat, ZopimChatApi.getDataSource(), getApplicationContext());
        final ChatMvp.View view = new ChatView(viewDataBinding, getApplicationContext());

        presenter = new ChatPresenter(model, view);
        view.setPresenter(presenter);
        presenter.install(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BelvedereProvider
                .INSTANCE
                .getInstance(this)
                .getFilesFromActivityOnResult(requestCode, resultCode, data, new BelvedereCallback<List<BelvedereResult>>() {
                    @Override
                    public void success(final List<BelvedereResult> belvedereResults) {
                        presenter.sendFile(belvedereResults);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        presenter.chatDismissed();
    }
}
