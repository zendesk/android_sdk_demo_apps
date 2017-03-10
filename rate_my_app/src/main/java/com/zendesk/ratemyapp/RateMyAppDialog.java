package com.zendesk.ratemyapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

/**
 * This is a {@link android.support.v4.app.DialogFragment DialogFragment} that will show a "Rate
 * My App" dialog, which steers positive reviews to the provided Play Store listing, and deflects
 * negative reviews to the feedback mechanism of your choice. It was written with the
 * Zendesk Support SDK in mind, but it does not actually send feedback anywhere. It delivers the
 * feedback to the provided {@link DialogActionListener}, which is then responsible for doing
 * something with it.
 *
 * As such, RateMyAppDialog is not dependent on Zendesk and can be used to direct feedback wherever
 * is desired.
 * <p>
 *     A full example is available in the rate_my_app_sample app, which
 *     shows how to use the dialog to create tickets with Zendesk, and how to use the dialog to
 *     send feedback via a {@link Intent#ACTION_SEND} Intent.
 * </p>
 *
 */
@SuppressWarnings("unused")
public class RateMyAppDialog extends DialogFragment {

    /**
     * This is the name of the tag that will be set on the
     * RateMyAppDialog that will be created by this button.
     */
    public static final String RMA_DIALOG_TAG = "rma_dialog";

    private static final String LOG_TAG = RateMyAppDialog.class.getSimpleName();

    static final String PREFS_FILE = "rateMyApp";
    static final String PREFS_DONT_ASK_VERSION_KEY = "appVersion";

    private ViewGroup deflectionGroup;

    private ViewGroup feedbackGroup;

    private DialogActionListener dialogActionListener;

    private RateMyAppConfig config;

    private ViewState viewState;

    private enum ViewState {
        DEFLECTION,
        FEEDBACK
    }

    public static void show(AppCompatActivity activity, RateMyAppConfig config, DialogActionListener actionListener) {
        if (!config.canShow()) {
            Log.d(RMA_DIALOG_TAG, "Can't show RateMyAppDialog due to configured rules.");
            return;
        }

        showAlways(activity, config, actionListener);
    }

    public static void showAlways(AppCompatActivity activity, RateMyAppConfig config, DialogActionListener actionListener) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        RateMyAppDialog fragment;
        if (fragmentManager.findFragmentByTag(RMA_DIALOG_TAG) == null) {
            fragment = new RateMyAppDialog();
            fragment.config = config;
        } else {
            fragment = (RateMyAppDialog) fragmentManager.findFragmentByTag(RMA_DIALOG_TAG);
        }
        fragment.dialogActionListener = actionListener;

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);

        fragment.show(transaction, RMA_DIALOG_TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && ViewState.FEEDBACK.name()
                .equals(savedInstanceState.getString(ViewState.class.getName()))) {
            viewState = ViewState.FEEDBACK;
        } else {
            viewState = ViewState.DEFLECTION;
        }

        setRetainInstance(true);

        setStyle(STYLE_NO_TITLE, getTheme());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup dialogViewGroup = (ViewGroup) inflater.inflate(R.layout.rma_dialog, container, false);

        deflectionGroup = (ViewGroup) dialogViewGroup.findViewById(R.id.rma_deflection_group);
        feedbackGroup = (ViewGroup) dialogViewGroup.findViewById(R.id.rma_feedback_group);

        setupDeflectionGroup(inflater);
        setupFeedbackGroup();

        if (viewState == ViewState.FEEDBACK) {
            toggleViewSwitch();
        }

        return dialogViewGroup;
    }

    private void toggleViewSwitch() {
        deflectionGroup.setVisibility(viewState == ViewState.DEFLECTION ? View.VISIBLE : View.GONE);
        feedbackGroup.setVisibility(viewState == ViewState.FEEDBACK ? View.VISIBLE : View.GONE);
    }

    private void setupDeflectionGroup(LayoutInflater inflater) {
        addStoreButton(inflater);
        addFeedbackButton(inflater);
        addDontRemindMeAgainButton(inflater);
    }

    private void addStoreButton(LayoutInflater inflater) {
        addDivider(inflater, deflectionGroup);

        TextView buttonTextView = (TextView) inflater.inflate(R.layout.rma_button, deflectionGroup, false);
        buttonTextView.setText(R.string.rate_my_app_dialog_positive_action_label);
        buttonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogActionListener != null) {
                    dialogActionListener.storeButtonClicked();
                }

                final Intent storeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(config.getStoreUrl()));

                Log.i(LOG_TAG, String.format(Locale.getDefault(), "Using store URL: %s", config.getStoreUrl()));

                startActivity(storeIntent);
            }
        });
        deflectionGroup.addView(buttonTextView);
    }

    private void addFeedbackButton(LayoutInflater inflater) {
        addDivider(inflater, deflectionGroup);

        TextView buttonTextView = (TextView) inflater.inflate(R.layout.rma_button, deflectionGroup, false);
        buttonTextView.setText(R.string.rate_my_app_dialog_negative_action_label);
        buttonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewState = ViewState.FEEDBACK;
                toggleViewSwitch();

                if (dialogActionListener != null) {
                    dialogActionListener.feedbackButtonClicked();
                }
            }
        });

        deflectionGroup.addView(buttonTextView);
    }

    private void addDontRemindMeAgainButton(LayoutInflater inflater) {
        addDivider(inflater, deflectionGroup);

        TextView buttonTextView = (TextView) inflater.inflate(R.layout.rma_button, deflectionGroup, false);
        buttonTextView.setText(R.string.rate_my_app_dialog_dismiss_action_label);
        buttonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogActionListener != null) {
                    dialogActionListener.dontAskAgainClicked();
                }

                dismiss();

                if (getActivity() != null && config.getAppVersion() != null) {
                    SharedPreferences.Editor sharedPrefsEditor = getActivity()
                            .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE).edit();
                    sharedPrefsEditor.putString(PREFS_DONT_ASK_VERSION_KEY, config.getAppVersion());
                    sharedPrefsEditor.apply();
                }
            }
        });

        deflectionGroup.addView(buttonTextView);
    }

    private void addDivider(LayoutInflater inflater, ViewGroup container) {
        View divider = inflater.inflate(R.layout.rma_divider, container, false);
        container.addView(divider);
    }

    private void setupFeedbackGroup() {
        final View cancelButton = feedbackGroup.findViewById(R.id.rma_feedback_issue_cancel_button);
        final View sendButton = feedbackGroup.findViewById(R.id.rma_feedback_issue_send_button);
        final EditText editText = (EditText) feedbackGroup.findViewById(R.id.rma_feedback_issue_edittext);

        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();

                    if (dialogActionListener!= null) {
                        Log.d(LOG_TAG, "Notifying feedback listener of submission cancellation");
                        dialogActionListener.cancelled();
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

                    final String feedback = editText.getText().toString();

                    if (dialogActionListener != null) {
                        dialogActionListener.onSendButtonClicked(feedback);
                    }

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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ViewState.class.getName(), viewState.name());
    }

    @Override
    public void onDetach() {
        super.onDetach();

        dialogActionListener = null;
    }

    /**
     * This is a hacky workaround as per https://code.google.com/p/android/issues/detail?id=17423
     * to retain the Fragment on configuration change.
     */
    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
