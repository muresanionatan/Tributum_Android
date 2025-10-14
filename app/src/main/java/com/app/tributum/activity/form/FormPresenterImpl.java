package com.app.tributum.activity.form;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.activity.inquiry.InquiryPresenter;
import com.app.tributum.activity.inquiry.InquiryView;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.AsyncListener;
import com.app.tributum.listener.RequestSentListener;
import com.app.tributum.model.EmailBody;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.utils.CalendarUtils;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.ImageUtils;
import com.app.tributum.utils.UploadAsyncTask;
import com.app.tributum.utils.ValidationUtils;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FormPresenterImpl implements FormPresenter, RequestSentListener {

    final private FormView view;

    public FormPresenterImpl(FormView view) {
        this.view = view;
    }

    private void collapseBottomSheet() {
//        isBottomSheetVisible = false;
//        view.collapseBottomSheet();
    }

    @Override
    public void onBackPressed() {
        if (view == null)
            return;

//        if (isPreview) {
//            isPreview = false;
//            view.hidePreview();
//        } else if (isBottomSheetVisible) {
//            collapseBottomSheet();
//        } else {
//            view.closeActivity();
//        }
    }

    @Override
    public void handleSendButtonClick(String firstName, String lastName, String address1, String address2, String address3, String eircode, String birthday, String occupation, String phone, String email, String bankAccount, String pps, String noOfKids) {

    }

    @Override
    public void onCreate() {

    }



    @Override
    public void onOkClicked() {

    }
}