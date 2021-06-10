package com.app.tributum.listener;

/**
 * Used to know when the user started drawing in order to set the isSignatureSet to true so we
 * are not sending an email without a signature
 */
public interface SignatureListener {

    void onDrawingStarted();

    void onDrawingFinished();
}