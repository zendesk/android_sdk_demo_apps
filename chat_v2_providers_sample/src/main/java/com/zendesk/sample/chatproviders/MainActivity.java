package com.zendesk.sample.chatproviders;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.zendesk.sample.chatproviders.chat.ChatActivity;
import com.zendesk.sample.chatproviders.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Global.isMissingCredentials()) {
            setContentView(R.layout.missing_credentials);
            return;
        }

        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }
}
