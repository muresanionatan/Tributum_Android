package com.app.tributum.activity.main;

public interface MainView {
    void showLanguageLayout();

    void hideLanguageLayout();

    void checkEnglishBox();

    void checkRomanianBox();

    void restartApp();

    void unCheckEnglishBox();

    void unCheckRomanianBox();

    void setWelcomeMessage(int stringId);

    void startVatActivity();

    void startPaymentsActivity();

    void startContractActivity();

    void startFaqActivity();

    void showTermsAndConditionsScreen();

    void hideTermsAndConditionsScreen();

    void showPopup();

    void setLanguageLabel(int languageId);

    void hideScrollToBottomButton();

    void showScrollToBottomButton();

    void scrollViewToBottom();

    void closeApp();

    void requestPermissions(String[] toArray, int multiplePermissionsPpsFront);

    int checkPermission(String permission);

    boolean shouldShowStorageRationale();

    boolean shouldShowCameraRationale();

    void takeUserToAppSettings();

    void startInquiryActivity();

    void startPpsActivity();
}