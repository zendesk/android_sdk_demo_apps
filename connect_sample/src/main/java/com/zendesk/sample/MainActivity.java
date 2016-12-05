package com.zendesk.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import zendesk.connect.ConnectSDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectSDK.INSTANCE.show();
            }
        });

        findViewById(R.id.btn_abort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectSDK.INSTANCE.abort();
            }
        });

        final long delay = 1000L;

        findViewById(R.id.btn_preview_cta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectSDK.INSTANCE.preview(/* buttonUrl */ "https://www.zendesk.com/", /* delay */ delay);
            }
        });

        findViewById(R.id.btn_preview_no_cta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectSDK.INSTANCE.preview(/* buttonUrl */ null, /* delay */ delay);
            }
        });
    }
}
