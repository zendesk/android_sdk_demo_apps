package com.zendesk.ratemyapp;

public interface DialogActionListener {

    void storeButtonClicked();

    void feedbackButtonClicked();

    void dontAskAgainClicked();

    void cancelled();

    void onSendButtonClicked(String feedback);
}
