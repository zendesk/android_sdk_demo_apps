package com.zendesk.ratemyapp;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.Serializable;

/**
 * This is a {@link android.support.v4.app.DialogFragment DialogFragment} that allows the user to
 * send feedback.  It is designed to be used by the {@link com.zendesk.ratemyapp.RateMyAppDialog}
 * component.
 * <p>
 *     The dialog contains a text entry area, a cancel button and a send button and the layout
 *     is defined by fragment_send_feedback.xml
 * </p>
 * <p>
 *     The text entry area controls the state of the send button. If there is no text entered it
 *     will be disabled and if there is at least one character it will be enabled.
 * </p>
 * <p>
 *     The actual sending of feedback must be implemented by the client. The provided {@link DialogActionListener}
 *     will receive a call to {@link DialogActionListener#onSendButtonClicked(String)} with the user's
 *     feedback when the "Send" button is clicked. This is the point at which the feedback should
 *     be recorded/sent.
 * </p>
 *
 */
public class FeedbackDialog extends DialogFragment implements Serializable {

    private static final String LOG_TAG = FeedbackDialog.class.getSimpleName();

    /**
     * This is the name of the tag that will be set on the
     * {@link FeedbackDialog} that will be created by this button.
     */
    public static final String FEEDBACK_DIALOG_TAG = "feedback";

    private DialogActionListener feedbackListener;

    public static FeedbackDialog newInstance(DialogActionListener feedbackListener) {

        final FeedbackDialog feedbackDialog = new FeedbackDialog();

        if (feedbackListener != null) {
            feedbackDialog.feedbackListener = feedbackListener;
        }

        return feedbackDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        setStyle(STYLE_NO_TITLE, getTheme());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View dialog = inflater.inflate(R.layout.fragment_send_feedback, container, false);

        final View cancelButton = dialog.findViewById(R.id.rma_feedback_issue_cancel_button);
        final View sendButton = dialog.findViewById(R.id.rma_feedback_issue_send_button);
        final EditText editText = (EditText) dialog.findViewById(R.id.rma_feedback_issue_edittext);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.rma_feedback_issue_progress);

        if (cancelButton != null) {
            Log.d(LOG_TAG, "Inflating cancel button...");

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOG_TAG, "Cancel button was clicked...");

                    dismiss();

                    if (feedbackListener != null) {
                        Log.d(LOG_TAG, "Notifying feedback listener of submission cancellation");
                        feedbackListener.cancelled();
                    }
                }
            });
        }

        if (sendButton == null || editText == null) {
            Log.w(LOG_TAG, "Could not setOnClickListener because views were null");
        } else {
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (cancelButton != null) {
                        cancelButton.setEnabled(false);
                    }

                    sendButton.setEnabled(false);
                    editText.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);

                    final String feedback = editText.getText().toString();

                    feedbackListener.onSendButtonClicked(feedback);

                    dismiss();
                }
            });
        }

        if (editText != null) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable editable) {

                    if (sendButton == null) {
                        Log.e(LOG_TAG, "ignoring afterTextChanged() because sendButton is null");
                        return;
                    }

                    if (editable == null || editable.length() == 0) {
                        sendButton.setEnabled(false);
                    } else {
                        sendButton.setEnabled(true);
                    }
                }

                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });
        }

        return dialog;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
