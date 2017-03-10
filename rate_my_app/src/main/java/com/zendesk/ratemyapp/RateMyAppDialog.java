package com.zendesk.ratemyapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private DialogActionListener dialogActionListener;

    private RateMyAppConfig config;

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

        setStyle(STYLE_NO_TITLE, getTheme());
    }

    @Override
    public void onDetach() {
        super.onDetach();

        dialogActionListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup dialogViewGroup = (ViewGroup) inflater.inflate(R.layout.rma_dialog, container, false);

        addStoreButton(inflater, dialogViewGroup);

        addFeedbackButton(inflater, dialogViewGroup);

        addDontRemindMeAgainButton(inflater, dialogViewGroup);

        return dialogViewGroup;
    }

    private void addStoreButton(LayoutInflater inflater, ViewGroup container) {
        addDivider(inflater, container);

        TextView buttonTextView = (TextView) inflater.inflate(R.layout.rma_button, container, false);
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
        container.addView(buttonTextView);
    }

    private void addFeedbackButton(LayoutInflater inflater, ViewGroup container) {
        addDivider(inflater, container);

        TextView buttonTextView = (TextView) inflater.inflate(R.layout.rma_button, container, false);
        buttonTextView.setText(R.string.rate_my_app_dialog_negative_action_label);
        buttonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogActionListener != null) {
                    dialogActionListener.feedbackButtonClicked();
                }

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Fragment rmaDialog = fragmentManager.findFragmentByTag(RMA_DIALOG_TAG);

                if (rmaDialog != null) {
                    fragmentTransaction.remove(rmaDialog);
                }

                fragmentTransaction.commit();
                fragmentManager.popBackStackImmediate();

                fragmentTransaction = fragmentManager.beginTransaction();
                Fragment feedbackDialogFragment = fragmentManager.findFragmentByTag(FeedbackDialog.FEEDBACK_DIALOG_TAG);

                if (feedbackDialogFragment != null) {
                    fragmentTransaction.remove(feedbackDialogFragment);
                }

                fragmentTransaction.addToBackStack(null);

                FeedbackDialog feedbackDialog = FeedbackDialog.newInstance(dialogActionListener);
                feedbackDialog.show(fragmentTransaction, FeedbackDialog.FEEDBACK_DIALOG_TAG);
            }
        });

        container.addView(buttonTextView);
    }

    private void addDontRemindMeAgainButton(LayoutInflater inflater, ViewGroup container) {
        addDivider(inflater, container);

        TextView buttonTextView = (TextView) inflater.inflate(R.layout.rma_button, container, false);
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

        container.addView(buttonTextView);
    }

    private void addDivider(LayoutInflater inflater, ViewGroup container) {
        View divider = inflater.inflate(R.layout.rma_divider, container, false);
        container.addView(divider);
    }

    /**
     * This method can be used to tear down the RateMyAppDialog if it has been shown.
     *
     * @param fragmentManager The fragment manager in which transactions will run to remove the fragments
     */
    public void tearDownDialog(FragmentManager fragmentManager) {

        if (fragmentManager == null) {
            Log.d(LOG_TAG, "Supplied FragmentManager was null, cannot continue...");
            return;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment feedbackDialogFragment = fragmentManager.findFragmentByTag(FeedbackDialog.FEEDBACK_DIALOG_TAG);

        if (feedbackDialogFragment != null) {
            Log.d(LOG_TAG, "feedback dialog found for removal");
            fragmentTransaction.remove(feedbackDialogFragment);
        }

        fragmentTransaction.commit();

        fragmentTransaction = fragmentManager.beginTransaction();
        Fragment rmaDialog = fragmentManager.findFragmentByTag(RateMyAppDialog.RMA_DIALOG_TAG);

        if (rmaDialog != null) {
            Log.d(LOG_TAG, "RateMyApp dialog found for removal");
            fragmentTransaction.remove(rmaDialog);
        }

        fragmentTransaction.commit();
        fragmentManager.popBackStackImmediate();

        fragmentManager.executePendingTransactions();
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
