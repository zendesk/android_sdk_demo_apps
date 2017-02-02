package com.zendesk.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zendesk.util.StringUtils;

import java.util.Locale;
import java.util.UUID;

import zendesk.connect.ConnectSDK;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etIpmUrl;
    private SeekBar sbDelay;
    private TextView tvDelay;
    private TextView tvUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();

        Identity identity = Zendesk.INSTANCE.coreModule().getIdentityManager().getIdentity();
        if(identity instanceof AnonymousIdentity) {
            final AnonymousIdentity anonymousIdentity = (AnonymousIdentity) identity;
            showUserInfo(anonymousIdentity.getEmail(),  anonymousIdentity.getName());
        } else if(identity == null) {
            installIdentity();
        }
    }

    private void setupViews() {
        etIpmUrl = (EditText) findViewById(R.id.et_ipm_url);
        sbDelay = (SeekBar) findViewById(R.id.sb_delay);
        tvDelay = (TextView) findViewById(R.id.tv_delay);
        tvUserInfo = (TextView) findViewById(R.id.tv_user_info);

        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.btn_new_id).setOnClickListener(this);
        findViewById(R.id.btn_abort).setOnClickListener(this);
        findViewById(R.id.btn_preview).setOnClickListener(this);

        sbDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int delay, boolean b) {
                tvDelay.setText(String.format(Locale.US, "Delay: %sms", delay));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tvDelay.setText(String.format(Locale.US, "Delay: %sms", sbDelay.getProgress()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_show:
                ConnectSDK.INSTANCE.show();
                return;
            case R.id.btn_abort:
                ConnectSDK.INSTANCE.abort();
                return;
            case R.id.btn_new_id:
                installIdentity();
                return;
            case R.id.btn_preview:
                ConnectSDK.INSTANCE.preview(getIpmUrl(etIpmUrl), sbDelay.getProgress());
        }
    }

    private void installIdentity() {
        final String[] randomStuff = UUID.randomUUID().toString().split("-");
        final String email = String.format(Locale.US, "%s@example.com", randomStuff[0]);
        final String name = String.format(Locale.US, "SDK User %s", randomStuff[1]);
        final Identity identity = new AnonymousIdentity.Builder()
                .withNameIdentifier(name)
                .withEmailIdentifier(email)
                .build();

        Zendesk.INSTANCE.setIdentity(identity);
        showUserInfo(name, email);
    }

    private void showUserInfo(@Nullable String name, @Nullable String email) {
        final String text = String.format(Locale.US, "Name: '%s'\nE-mail: '%s'", name, email);
        tvUserInfo.setText(text);
    }

    @Nullable
    private String getIpmUrl(@NonNull EditText editText) {
        String url = editText.getText().toString();
        if (StringUtils.hasLength(url) && !url.startsWith("http")) {
            return "https://" + url;
        }
        return null;
    }
}
