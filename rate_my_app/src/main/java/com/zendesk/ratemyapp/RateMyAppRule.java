package com.zendesk.ratemyapp;

import android.support.v7.app.AppCompatActivity;

/**
 * Defines the behaviour of a rule which will permit the {@link com.zendesk.ratemyapp.RateMyAppDialog}
 * to show or not.
 *
 * Created by Zendesk on 30/07/15.
 */
public interface RateMyAppRule {

    /**
     * Checks if this rule permits the showing of the dialogue, false if it does not
     *
     * @return true if this rule permits the showing of the dialogue, false if it does not
     */
    boolean permitsShowOfDialog();

    /**
     * Returns an explanation for why {@code permitsShowOfDialog()}returned false. It is invoked by
     * {@link RateMyAppDialog#show(AppCompatActivity, RateMyAppConfig, DialogActionListener)} on any
     * rules which return false.
     *
     * This method makes no guarantee that {@code permitsShowOfDialog()} actually returns false. It
     * will not be called by {@link RateMyAppDialog} if the rule returns true, but a client who
     * has created a rule could call {@code denialMessage()} on it even if it permits showing of
     * the dialog.
     *
     * @return the message to log when the rule returns false for {@code permitsShowOfDialog}.
     */
    String denialMessage();
}
