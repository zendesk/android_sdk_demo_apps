package com.zendesk.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;
import com.zendesk.util.StringUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import zendesk.core.UserProvider;
import zendesk.core.Zendesk;
import zendesk.support.guide.HelpCenterActivity;
import zendesk.support.request.RequestActivity;
import zendesk.support.requestlist.RequestListActivity;

/**
 * This activity is a springboard that you can use to launch various parts of the Zendesk SDK.
 */
public class MainActivity extends FragmentActivity {

    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Global.isMissingCredentials()) {
            setContentView(R.layout.missing_credentials);
            return;
        }
        setContentView(R.layout.activity_main);

        /*
         * This will make a full-screen feedback screen appear. It is very similar to how
         * the feedback dialog works but it is hosted in an activity.
         */
        findViewById(R.id.main_btn_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File tempFile = createTempFile(".png", getCacheDir());
                            InputStream inputStream = getAssets().open("zenchat.png");
                            OutputStream outputStream = new FileOutputStream(tempFile.getPath());
                            if (inputStream != null) {
                                syncIoStream(inputStream, outputStream);
                                RequestActivity.builder()
                                        .withFiles(tempFile)
                                        .show(MainActivity.this);
                            }
                        } catch (IOException ex) {
                            if (ex.getMessage() != null) {
                                Log.e(LOG_TAG, ex.getMessage());
                            }
                        } finally {
                            handler.removeCallbacks(this);
                        }
                    }
                });
            }
        });

        /*
         * This will launch an Activity that will show the current Requests that a
         * user has opened.
         */
        findViewById(R.id.main_btn_request_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestListActivity.builder()
                        .show(MainActivity.this);
            }
        });

        final EditText supportEdittext = (EditText) findViewById(R.id.main_edittext_support);

        findViewById(R.id.main_btn_support).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String labels = supportEdittext.getText().toString();
                String[] labelsArray = null;

                if (StringUtils.hasLength(labels)) {
                    labelsArray = labels.split(",");
                }

                HelpCenterActivity.builder()
                        .withLabelNames(labelsArray)
                        .show(MainActivity.this);
            }
        });

        final EditText devicePushToken = (EditText) findViewById(R.id.main_edittext_push);

        findViewById(R.id.main_btn_push_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Zendesk.INSTANCE.provider().pushRegistrationProvider()
                        .registerWithDeviceIdentifier(devicePushToken.getText().toString(),
                                new ZendeskCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Toast.makeText(getApplicationContext(), "Registration success",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(ErrorResponse error) {
                                        Toast.makeText(getApplicationContext(),
                                                "Registration failure: " + error.getReason(), Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
            }
        });

        findViewById(R.id.main_btn_push_register_ua).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Zendesk.INSTANCE.provider().pushRegistrationProvider()
                        .registerWithUAChannelId(devicePushToken.getText().toString(), new ZendeskCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                Toast.makeText(getApplicationContext(), "Registration success", Toast.LENGTH_SHORT)
                                        .show();
                            }

                            @Override
                            public void onError(ErrorResponse error) {
                                Toast.makeText(getApplicationContext(), "Registration failure: " + error.getReason(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        findViewById(R.id.main_btn_push_unregister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Zendesk.INSTANCE.provider().pushRegistrationProvider().unregisterDevice(new ZendeskCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(getApplicationContext(), "Deregistration success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ErrorResponse error) {
                        Toast.makeText(getApplicationContext(), "Deregistration failure: " + error.getReason(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        final ZendeskCallback<List<String>> userTagsCallback = new ZendeskCallback<List<String>>() {
            @Override
            public void onSuccess(final List<String> tags) {
                Toast.makeText(MainActivity.this, String.format(Locale.US, "User tags: %s", tags), Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onError(final ErrorResponse error) {
                Toast.makeText(getApplicationContext(), "Usertag failure: " + error.getReason(), Toast.LENGTH_SHORT)
                        .show();
            }
        };

        findViewById(R.id.main_btn_user_tags_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String input = ((EditText) findViewById(R.id.main_edittext_user_tags)).getText().toString();
                final UserProvider userProvider = Zendesk.INSTANCE.provider().userProvider();
                userProvider.addTags(StringUtils.fromCsv(input), userTagsCallback);
            }
        });

        findViewById(R.id.main_btn_user_tags_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String input = ((EditText) findViewById(R.id.main_edittext_user_tags)).getText().toString();
                final UserProvider userProvider = Zendesk.INSTANCE.provider().userProvider();
                userProvider.deleteTags(StringUtils.fromCsv(input), userTagsCallback);
            }
        });
    }

    private File createTempFile(String extension, File directory) throws IOException {
        return File.createTempFile("temp-" + extension.replace(".", ""), extension, directory);
    }

    private void syncIoStream(InputStream inStream, OutputStream outStream) throws IOException {
        byte[] b = new byte[inStream.available()];
        inStream.read(b);
        outStream.write(b);
        inStream.close();
        outStream.close();
    }
}
