package com.zendesk.sample.uploadattachment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zendesk.belvedere.Belvedere;
import com.zendesk.belvedere.BelvedereCallback;
import com.zendesk.belvedere.BelvedereResult;
import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;

import java.util.ArrayList;
import java.util.List;

import zendesk.support.CreateRequest;
import zendesk.support.Request;
import zendesk.support.RequestProvider;
import zendesk.support.Support;
import zendesk.support.UploadProvider;
import zendesk.support.UploadResponse;

public class UploadAttachmentActivity extends AppCompatActivity {

    private static final String LOG_TAG = "UploadAttachmentActivit";
    private static final String DEFAULT_MIMETYPE = "application/octet-stream";

    private FloatingActionButton fab;
    private EditText subjectEditText;
    private EditText descriptionEditText;
    private ProgressDialog progressDialog;

    private Belvedere belvedere;
    private UploadProvider uploadProvider;
    private RequestProvider requestProvider;

    private int uploadRequestsInProgress;
    private List<String> attachmentsUploaded = new ArrayList<>();
    private boolean createRequestInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Global.isMissingCredentials()) {
            setContentView(R.layout.missing_credentials);
            return;
        }
        setContentView(R.layout.activity_upload_attachment);

        // Initialize the Zendesk Providers used to upload files and submit requests.
        initializeZendeskProviders();

        // Initialize the Belvedere library for retrieving files from the framework, no permissions needed
        initializeBelvedereFilePicker();

        // Uninteresting method of findViewById calls
        captureViews();

        // Add Belvedere file picker to FloatingActionButton
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                belvedere.showDialog(fragmentManager);
            }
        });

        // Add TextWatchers to Subject and Description fields for enabling/disabling "Send" button
        addTextWatchersToForm();
    }

    // Implement onActivityResult to catch the files from Belvedere. For each file returned, upload
    // to Zendesk using the UploadProvider
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        belvedere.getFilesFromActivityOnResult(requestCode, resultCode, data, new BelvedereCallback<List<BelvedereResult>>() {
            @Override
            public void success(List<BelvedereResult> belvedereResults) {

                if (belvedereResults != null && belvedereResults.size() > 0) {
                    progressDialog("Uploading your attachments...").show();
                } else {
                    return;
                }

                for (int i = 0, limit = belvedereResults.size(); i < limit; i++) {

                    BelvedereResult file = belvedereResults.get(i);

                    uploadProvider.uploadAttachment(
                            file.getFile().getName(),
                            file.getFile(),
                            getMimeType(getApplicationContext(), file.getUri()),
                            new ZendeskCallback<UploadResponse>() {
                                @Override
                                public void onSuccess(UploadResponse uploadResponse) {
                                    if (uploadResponse != null && uploadResponse.getAttachment() != null) {
                                        attachmentsUploaded.add(uploadResponse.getToken());
                                        Log.d(LOG_TAG, String.format("onSuccess: Image successfully uploaded: %s",
                                                uploadResponse.getAttachment().getContentUrl()));
                                    }
                                    // Make sure to keep track of how many requests are in progress
                                    uploadRequestsInProgress--;

                                    checkUploadRequestsInProgress();
                                }

                                @Override
                                public void onError(ErrorResponse errorResponse) {
                                    // Make sure to keep track of how many requests are in progress
                                    uploadRequestsInProgress--;

                                    checkUploadRequestsInProgress();
                                }
                            });

                    uploadRequestsInProgress++;
                }
            }
        });
    }

    private void checkUploadRequestsInProgress() {
        if (noUploadRequestsInProgress() && progressDialog().isShowing()) {
            progressDialog().dismiss();
        }
    }

    private boolean noUploadRequestsInProgress() {
        return uploadRequestsInProgress == 0;
    }

    private static String getMimeType(Context context, Uri file) {
        final ContentResolver cr = context.getContentResolver();
        return (file != null) ? cr.getType(file) : DEFAULT_MIMETYPE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_attachment, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_send).setEnabled(isRequestFormValid());
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean isRequestFormValid() {
        return !subjectEditText.getText().toString().isEmpty()
                && !descriptionEditText.getText().toString().isEmpty();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {

            progressDialog("Creating your request...").show();

            createRequestInProgress = true;

            CreateRequest request = buildCreateRequest();

            requestProvider.createRequest(request, new ZendeskCallback<Request>() {
                @Override
                public void onSuccess(Request request) {
                    invalidateOptionsMenu();
                    progressDialog().dismiss();

                    // Clear form
                    subjectEditText.setText("");
                    descriptionEditText.setText("");

                    // Clear list of uploaded attachments
                    attachmentsUploaded.clear();

                    createRequestInProgress = false;
                }

                @Override
                public void onError(ErrorResponse errorResponse) {
                    progressDialog().dismiss();

                    Snackbar.make(fab, "Request creation failed: " + errorResponse.getReason(), Snackbar.LENGTH_LONG).show();

                    createRequestInProgress = false;
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private CreateRequest buildCreateRequest() {
        CreateRequest request = new CreateRequest();
        request.setDescription(descriptionEditText.getText().toString());
        request.setSubject(subjectEditText.getText().toString());
        request.setAttachments(attachmentsUploaded);
        return request;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!createRequestInProgress) {
            for (String attachment : attachmentsUploaded) {
                uploadProvider.deleteAttachment(attachment, null);
            }
        }
    }


    private void initializeZendeskProviders() {
        uploadProvider = Support.INSTANCE.provider().uploadProvider();
        requestProvider = Support.INSTANCE.provider().requestProvider();
    }

    private void initializeBelvedereFilePicker() {
        belvedere = Belvedere.from(this)
                .withContentType("image/*")
                .withAllowMultiple(true)
                .build();
    }

    private void captureViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        subjectEditText = (EditText) findViewById(R.id.requestSubject);
        descriptionEditText = (EditText) findViewById(R.id.requestDescription);
    }

    private void addTextWatchersToForm() {
        TextWatcher notEmptyTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        subjectEditText.addTextChangedListener(notEmptyTextWatcher);
        descriptionEditText.addTextChangedListener(notEmptyTextWatcher);
    }

    private ProgressDialog progressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }

        return progressDialog;
    }

    private ProgressDialog progressDialog(String dialogText) {
        progressDialog().setMessage(dialogText);
        return progressDialog();
    }
}
