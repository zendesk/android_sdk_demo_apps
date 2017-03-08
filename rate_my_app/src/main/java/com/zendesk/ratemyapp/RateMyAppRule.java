package com.zendesk.ratemyapp;

/**
 * Defines the behaviour of a rule which will permit the {@link com.zendesk.rating.RateMyAppDialog}
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
}
