package com.zendesk.ratemyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
public class RateMyAppDialog extends DialogFragment implements Serializable {

    /**
     * This is the name of the tag that will be set on the
     * RateMyAppDialog that will be created by this button.
     */
    public static final String RMA_DIALOG_TAG = "rma_dialog";

    private static final String LOG_TAG = RateMyAppDialog.class.getSimpleName();

    private static final String PREFS_FILE = "rateMyApp";
    private static final String PREFS_DONT_ASK_VERSION_KEY = "appVersion";

    private String storeUrl;

    private List<RateMyAppRule> rules;

    private String appVersion;

    private DialogActionListener dialogActionListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_TITLE, getTheme());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup dialogViewGroup = (ViewGroup) inflater.inflate(R.layout.rma_dialog, container, false);

        if (storeUrl != null && !storeUrl.isEmpty()) {
            addStoreButton(inflater, dialogViewGroup);
        }

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

                final Intent storeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl));

                Log.i(LOG_TAG, "Using store URL: " + storeUrl);

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

                if (getActivity() != null && appVersion != null) {
                    SharedPreferences.Editor sharedPrefsEditor = getActivity()
                            .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE).edit();
                    sharedPrefsEditor.putString(PREFS_DONT_ASK_VERSION_KEY, appVersion);
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

    public void show(FragmentActivity fragmentActivity) {
        if (canShow()) {
            showAlways(fragmentActivity);
        }
    }

    private boolean canShow() {
        boolean canShow = true;
        for (RateMyAppRule rule: rules) {
            canShow &= rule.permitsShowOfDialog();
        }
        return canShow;
    }

    /**
     * This method can be used to always show the dialog. This doesn't respect whether the user has
     * already clicked a button to not display the dialog again. To
     *
     * @param fragmentActivity The FragmentActivity to which this Fragment will be attached to.
     */
    public void showAlways(FragmentActivity fragmentActivity) {
        if (fragmentActivity == null || fragmentActivity.isFinishing()) {
            Log.d(LOG_TAG, "Cannot show RateMyAppDialog, Activity is null or finishing.");
            return;
        }

        final FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        final DialogFragment prev = (DialogFragment) fragmentManager.findFragmentByTag(RMA_DIALOG_TAG);
        final FragmentTransaction ft = fragmentManager.beginTransaction();

        if (prev != null) {
            if(prev.getDialog() != null) {
                prev.getDialog().dismiss();
            }
            ft.remove(prev);
        }

        try {
            /*
              The dialog can fail to show here if the parent activity is in a paused state.
              We defer creation of the dialog until after the configured delay period has
              elapsed so we don't get the lifecycle methods to help us.
             */
            this.show(ft, RMA_DIALOG_TAG);
            getFragmentManager().executePendingTransactions();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private void setDialogActionListener(DialogActionListener dialogActionListener){
        this.dialogActionListener = dialogActionListener;
    }

    private void setStoreUrl(String storeUrl) {
        this.storeUrl = storeUrl;
    }

    private void setRules(List<RateMyAppRule> rules) {
        this.rules = rules;
    }

    private void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
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

    /**
     * Builder class used to instantiate a {@link RateMyAppDialog}
     * <p>
     *     Buttons should be added in the order that you want them to display.
     * </p>
     */
    public static class Builder {

        private final FragmentActivity activity;

        private String storeUrl;

        private String appVersion;

        private List<RateMyAppRule> rules = new ArrayList<>();

        private DialogActionListener dialogActionListener;

        public Builder(FragmentActivity activity) {
            if (activity == null) {
                Log.w(LOG_TAG, "Activity is null, many things will not work");
                this.activity = null;
            } else {
                this.activity = activity;
            }
        }

        /**
         * Adds a button which will link to an android store when tapped.
         * <p>
         *     Note that if the activity supplied in the builder is null then
         *     no button will be added.  It will also not be added if the
         *     server settings did not supply a store URL.
         * </p>
         * <p>
         *     As the android store url will be supplied by the server this
         *     could link to Google Play, Amazon etc.
         * </p>
         *
         * @return the builder
         */
        public Builder withAndroidStoreRatingButton(String storeUrl) {
            this.storeUrl = storeUrl;

            return this;
        }

        /**
         * Adds a button which will dismiss the dialog and never show it again.
         * <p>
         *     Note that if the activity supplied in the builder is null then
         *     no button will be added.
         * </p>
         *
         * @return the builder
         */
        public Builder withVersion(final String version) {
            this.appVersion = version;

            this.rules.add(new RateMyAppRule() {
                @Override
                public boolean permitsShowOfDialog() {
                    String storedVersion = activity.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
                            .getString(PREFS_DONT_ASK_VERSION_KEY, "");

                    boolean canShow = !storedVersion.equals(version);

                    if (!canShow) {
                        Log.d(LOG_TAG, "Cannot show RateMyAppDialog, user has selected not to show again for this version");
                    }
                    return canShow;
                }
            });

            return this;
        }

        public Builder withRule(RateMyAppRule rule) {
            this.rules.add(rule);

            return this;
        }

        /**
         * Includes a {@link DialogActionListener} which
         * can be used to subscribe to button click events. This can be useful for tracking button clicks.
         *
         * @param selectionListener The selection listener which the events will be sent to.
         * @return the builder
         */
        public Builder withDialogActionListener(DialogActionListener selectionListener) {
            this.dialogActionListener = selectionListener;
            return this;
        }

        /**
         * Creates the instance of {@link RateMyAppDialog}
         *
         * @return an instance of the dialog
         */
        public RateMyAppDialog build() {

            RateMyAppDialog dialog = new RateMyAppDialog();

            dialog.setStoreUrl(storeUrl);
            dialog.setAppVersion(appVersion);
            dialog.setRules(rules);
            dialog.setDialogActionListener(dialogActionListener);

            dialog.setRetainInstance(true);

            return dialog;
        }
    }
}
