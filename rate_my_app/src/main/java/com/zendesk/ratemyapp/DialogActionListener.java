package com.zendesk.ratemyapp;

import java.io.Serializable;

public interface DialogActionListener extends Serializable {

    void storeButtonClicked();

    void feedbackButtonClicked();

    void dontAskAgainClicked();

    void cancelled();

    void onSendButtonClicked(String feedback);
}
