package com.app.tributum.activity.main;

public interface MainView {
    void showLanguageLayout();

    void hideLanguageLayout();

    void checkEnglishBox();

    void checkRomanianBox();

    void restartApp();

    void unCheckEnglishBox();

    void unCheckRomanianBox();

    void setWelcomeMessage(String message);

    void startVatActivity();

    void startPaymentsActivity();

    void startContractActivity();
}