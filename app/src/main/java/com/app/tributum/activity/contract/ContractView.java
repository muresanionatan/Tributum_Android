package com.app.tributum.activity.contract;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;

public interface ContractView {

    void setStartingDateText(String string);

    void moveStartingDayCursorToEnd();

    void showToast(int stringId);

    void showLoadingScreen();

    void hideLoadingScreen();

    void closeActivity();

    void clearSignature();

    void showClearButton();

    void hideClearButton();

    void setDrawingCacheEnabled();

    Bitmap getSignatureFile();

    void showRequestSentScreen();

    void selectVat();

    void deselectVat();

    void selectRct();

    void deselectRct();
}