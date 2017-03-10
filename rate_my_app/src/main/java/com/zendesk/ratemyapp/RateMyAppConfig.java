package com.zendesk.ratemyapp;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RateMyAppConfig {

    private static final String LOG_TAG = "RateMyAppConfig";

    private String storeUrl;

    private List<RateMyAppRule> rules;

    private String appVersion;

    public RateMyAppConfig(Builder builder) {
        this.storeUrl = builder.storeUrl;
        this.rules = builder.rules;
        this.appVersion = builder.appVersion;
    }

    boolean canShow() {
        boolean canShow = true;
        for (RateMyAppRule rule: rules) {
            boolean rulePermits = rule.permitsShowOfDialog();
            if (!rulePermits) {
                Log.d(LOG_TAG, rule.denialMessage());
            }
            canShow &= rule.permitsShowOfDialog();
        }
        return canShow;
    }

    String getStoreUrl() {
        return storeUrl;
    }

    String getAppVersion() {
        return appVersion;
    }

    /**
     * Builder class used to instantiate a {@link RateMyAppDialog}
     * <p>
     *     Buttons should be added in the order that you want them to display.
     * </p>
     */
    public static class Builder {

        private String storeUrl;

        private String appVersion;

        private List<RateMyAppRule> rules = new ArrayList<>();

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
        public Builder withVersion(final Context context, final String version) {
            this.appVersion = version;

            this.rules.add(new RateMyAppRule() {
                @Override
                public boolean permitsShowOfDialog() {
                    String storedVersion = context.getSharedPreferences(RateMyAppDialog.PREFS_FILE,
                            Context.MODE_PRIVATE).getString(RateMyAppDialog.PREFS_DONT_ASK_VERSION_KEY, "");

                    boolean canShow = !storedVersion.equals(version);

                    if (!canShow) {
                        Log.d(LOG_TAG, "Cannot show RateMyAppDialog, user has selected not to show again for this version");
                    }
                    return canShow;
                }

                @Override
                public String denialMessage() {
                    String storedVersion = context.getSharedPreferences(RateMyAppDialog.PREFS_FILE,
                            Context.MODE_PRIVATE).getString(RateMyAppDialog.PREFS_DONT_ASK_VERSION_KEY, "");

                    return String.format(Locale.US, "Stored version is %s, current version is %s, returning false.",
                            storedVersion, appVersion);
                }
            });

            return this;
        }

        public Builder withRule(RateMyAppRule rule) {
            this.rules.add(rule);

            return this;
        }

        /**
         * Creates the instance of {@link RateMyAppConfig}
         *
         * @return an instance of the dialog
         */
        public RateMyAppConfig build() {
            return new RateMyAppConfig(this);
        }
    }
}
