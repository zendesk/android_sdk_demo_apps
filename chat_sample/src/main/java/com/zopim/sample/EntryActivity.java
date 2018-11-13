package com.zopim.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.PreChatForm;
import com.zopim.android.sdk.prechat.ZopimChatActivity;
import com.zopim.android.sdk.prechat.ZopimPreChatFragment;

public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Global.isMissingCredentials()) {
            setContentView(R.layout.missing_credentials);
            return;
        }
        setContentView(R.layout.entry_activity);
    }

    /**
     * Starts the chat with global config that was provided at init state via {@link com.zopim.android.sdk.api.ZopimChatApi#init(String)}
     *
     * @see Global
     */
    public void buttonNoConfig(View view) {
        startActivity(new Intent(this, ZopimChatActivity.class));

        // Sample breadcrumb
        ZopimChat.trackEvent("Started chat without config");
    }

    /**
     * Starts the chat with all pre chat form fields set as {@link PreChatForm.Field#OPTIONAL} optional
     */
    public void buttonOptionalPreChat(View view) {
        // set pre chat fields as optional
        PreChatForm preChatConfig = new PreChatForm.Builder()
                .name(PreChatForm.Field.OPTIONAL_EDITABLE)
                .email(PreChatForm.Field.OPTIONAL_EDITABLE)
                .phoneNumber(PreChatForm.Field.OPTIONAL_EDITABLE)
                .department(PreChatForm.Field.OPTIONAL_EDITABLE)
                .message(PreChatForm.Field.OPTIONAL_EDITABLE)
                .build();

        // build chat config
        ZopimChat.SessionConfig config = new ZopimChat.SessionConfig().preChatForm(preChatConfig);

        // start chat activity with config
        ZopimChatActivity.startActivity(this, config);

        // Sample breadcrumb
        ZopimChat.trackEvent("Started chat with optional pre-chat form");
    }

    /**
     * Starts the chat with all pre chat form fields set as {@link PreChatForm.Field#REQUIRED} mandatory
     */
    public void buttonMandatoryPreChat(View view) {
        // set pre chat fields as mandatory
        PreChatForm preChatForm = new PreChatForm.Builder()
                .name(PreChatForm.Field.REQUIRED_EDITABLE)
                .email(PreChatForm.Field.REQUIRED_EDITABLE)
                .phoneNumber(PreChatForm.Field.REQUIRED_EDITABLE)
                .department(PreChatForm.Field.REQUIRED_EDITABLE)
                .message(PreChatForm.Field.REQUIRED_EDITABLE)
                .build();

        // build chat config
        ZopimChat.SessionConfig config = new ZopimChat.SessionConfig().preChatForm(preChatForm);

        // start chat activity with config
        ZopimChatActivity.startActivity(this, config);

        // Sample breadcrumb
        ZopimChat.trackEvent("Started chat with mandatory pre-chat form");
    }

    /**
     * Starts the chat all pre chat form fields set as {@link PreChatForm.Field#NOT_REQUIRED} hidden
     */
    public void buttonNoPreChat(View view) {
        // set pre chat fields as hidden
        PreChatForm preChatForm = new PreChatForm.Builder()
                .name(PreChatForm.Field.NOT_REQUIRED)
                .email(PreChatForm.Field.NOT_REQUIRED)
                .phoneNumber(PreChatForm.Field.NOT_REQUIRED)
                .department(PreChatForm.Field.NOT_REQUIRED)
                .message(PreChatForm.Field.NOT_REQUIRED)
                .build();

        // build chat config
        ZopimChat.SessionConfig config = new ZopimChat.SessionConfig().preChatForm(preChatForm);

        // start chat activity with config
        ZopimChatActivity.startActivity(this, config);

        // Sample breadcrumb
        ZopimChat.trackEvent("Started chat without pre-chat form");
    }

    /**
     * Pre-sets {@link com.zopim.android.sdk.model.VisitorInfo} data in the chat config and starts the new chat
     */
    public void buttonPreSetData(View view) {
        // build and set visitor info
        VisitorInfo visitorInfo = new VisitorInfo.Builder()
                .phoneNumber("+1800111222333")
                .email("visitor@example.com")
                .name("Sample Visitor")
                .build();

        // visitor info can be set at any point when that information becomes available
        ZopimChat.setVisitorInfo(visitorInfo);

        // set pre chat fields as mandatory
        PreChatForm preChatForm = new PreChatForm.Builder()
                .name(PreChatForm.Field.REQUIRED_EDITABLE)
                .email(PreChatForm.Field.REQUIRED_EDITABLE)
                .phoneNumber(PreChatForm.Field.REQUIRED_EDITABLE)
                .department(PreChatForm.Field.REQUIRED_EDITABLE)
                .message(PreChatForm.Field.REQUIRED_EDITABLE)
                .build();

        // build chat config
        ZopimChat.SessionConfig config = new ZopimChat.SessionConfig().preChatForm(preChatForm).department("My memory");

        // start chat activity with config
        ZopimChatActivity.startActivity(EntryActivity.this, config);

        // Sample breadcrumb
        ZopimChat.trackEvent("Started chat with pre-set visitor information");
    }

    /**
     * Configures the chat and loads up the {@link ZopimPreChatFragment} pre chat fragment
     */
    public void buttonChatFragment(View view) {
        startActivity(new Intent(this, SampleChatActivity.class));

        // Sample breadcrumb
        ZopimChat.trackEvent("Started chat via sample fragment integration");
    }

    /**
     * Initializes chat with the account key provided in the input field
     */
    public void buttonAccountKey(View view) {
        EditText accountKeyInput = (EditText) findViewById(R.id.account_key_input);
        String accountKey = accountKeyInput.getText().toString().trim();
        if (accountKey.isEmpty()) {
            accountKeyInput.setError(getResources().getString(R.string.error_account_key));
            return;
        }
        Toast.makeText(this, R.string.account_key_confirm, Toast.LENGTH_SHORT).show();
        // re-initialize chat with new account key
        ZopimChat.init(accountKey);
        // Sample breadcrumb
        ZopimChat.trackEvent("Updated Zopim account key");
    }
}
