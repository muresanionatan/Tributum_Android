package com.app.tributum.activity.company;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.activity.company.model.Company;
import com.app.tributum.activity.company.model.Director;
import com.app.tributum.activity.company.model.Secretary;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.AsyncListener;
import com.app.tributum.listener.RequestSentListener;
import com.app.tributum.model.EmailBody;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.UploadAsyncTask;
import com.app.tributum.utils.ui.FileUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CompanyPresenterImpl implements AsyncListener, CompanyPresenter, RequestSentListener {

    final private CompanyView view;

    private boolean hasSecondDirector;

    private boolean hasThirdDirector;

    private Company company;
    private Director director2 = new Director();
    private Director director3 = new Director();

    @CompanyProgressState
    private int state = CompanyProgressState.COMPANY;

    public CompanyPresenterImpl(CompanyView view) {
        this.view = view;
    }

    private void sendInquiry(String name, String email, String description) {
//        Retrofit retrofit = RetrofitClientInstance.getInstance();
//        InterfaceAPI api = retrofit.create(InterfaceAPI.class);
//
//        Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.TRIBUTUM_EMAIL, generateInternalEmailMessage(name, email, description), "Android"));
//        call.enqueue(new Callback<Object>() {
//            @Override
//            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
//                if (!response.isSuccessful()) {
//                    view.showToast(TributumApplication.getInstance().getResources().getString(R.string.something_went_wrong));
//                    view.hideLoadingScreen();
//                } else {
//                    if (pictureImagePath != null && !pictureImagePath.equals("")) {
//                        UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(name,
//                                pictureImagePath,
//                                inquiryPhotoName,
//                                CompanyPresenterImpl.this,
//                                UploadAsyncTask.UploadType.INQUIRY);
//                        uploadAsyncTask.execute();
//                    } else {
//                        view.hideLoadingScreen();
//                        view.showRequestSent();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
//                view.hideLoadingScreen();
//                view.showToast(TributumApplication.getInstance().getResources().getString(R.string.something_went_wrong));
//            }
//        });
    }

//    private String generateInternalEmailMessage(String name, String email, String description) {
//        String formattedString = name.toUpperCase();
//        formattedString = formattedString.replaceAll(" ", "%20");
//
//        String resultString = name + " requested an inquiry:\n\n'" + description + "'";
//
//        if (pictureImagePath != null) {
//            inquiryPhotoName = CalendarUtils.getCurrentMonth() + "_"
//                    + CalendarUtils.getCurrentDateInMilies();
//
//            resultString += "\n\n" + "Click on below link to access the file\n\n"
//                    + "https://www.dropbox.com/home/Apps/Tributum/INQUIRIES/"
//                    + formattedString + "?preview="
//                    + inquiryPhotoName
//                    + ".png";
//        }
//
//        resultString += "\n\nPlease respond to: " + email;
//
//        return resultString;
//    }

    @Override
    public void onMainButtonClick() {
        if (state == CompanyProgressState.COMPANY) {
            moveToDirectorScreen();
        } else if (state == CompanyProgressState.DIRECTOR) {
            moveToSecretaryScreen();
        } else {
            sendInfo();
        }
    }

    private void moveToCompanyScreen() {
        state = CompanyProgressState.COMPANY;
        view.hideDirectorViewToLeft();
        view.showCompanyView();
    }

    private void moveToDirectorScreen() {
        state = CompanyProgressState.DIRECTOR;
        view.hideCompanyView();
        view.showDirectorViewFromRight();
        view.setConfirmationButtonText(R.string.continue_label);
    }

    private void moveToSecretaryScreen() {
        state = CompanyProgressState.SECRETARY;
        view.hideDirectorViewToLeft();
        view.showSecretaryView();
        view.setConfirmationButtonText(R.string.send_form_label);
    }

    private void sendInfo() {
        view.showLoadingScreen();
        company = view.getCompanyDetails();
        Director director1 = view.getDirector1Details();
        if (hasSecondDirector)
            director2 = view.getDirector2Details();
        if (hasThirdDirector)
            director3 = view.getDirector3Details();
        Secretary secretary = view.getSecretaryDetails();
        UploadAsyncTask uploadOneFileTask = new UploadAsyncTask(
                company.getFirstName() + " " + company.getSurName(),
                FileUtils.createCompanyFile(company, director1, director2, director3, secretary),
                this,
                UploadAsyncTask.UploadType.USER_INFO,
                "COMPANY_FOUNDATION");
        uploadOneFileTask.execute();
    }

    @Override
    public void onDirector2Click() {
        if (hasSecondDirector)
            view.hideSecondDirector();
        else
            view.showSecondDirector();
        hasSecondDirector = !hasSecondDirector;
    }

    @Override
    public void onDirector3Click() {
        if (hasThirdDirector)
            view.hideThirdDirector();
        else
            view.showThirdDirector();
        hasThirdDirector = !hasThirdDirector;
    }

    @Override
    public void onBackPressed() {
        if (view == null)
            return;

        if (state == CompanyProgressState.SECRETARY) {
            state = CompanyProgressState.DIRECTOR;
            view.hideSecretaryView();
            view.showDirectorViewFromLeft();
            view.setConfirmationButtonText(R.string.continue_label);
        } else if (state == CompanyProgressState.DIRECTOR) {
            state = CompanyProgressState.COMPANY;
            view.hideDirectorViewToRight();
            view.showCompanyView();
        } else if (state == CompanyProgressState.COMPANY) {
            view.closeActivity();
        }
    }

    @Override
    public void onOkClicked() {
        if (view != null)
            view.closeActivity();
    }

    @Override
    public void onTaskCompleted(String process) {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.TRIBUTUM_EMAIL, generateInternalEmailMessage(company.getFirstName() + " " + company.getSurName()), "Android"));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (!response.isSuccessful()) {
                    view.showToast(R.string.something_went_wrong);
                } else {
                    sendClientMail(company.getEmail(), TributumApplication.getInstance().getString(R.string.contract_mail_message));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                view.hideLoadingScreen();
                view.showToast(R.string.something_went_wrong);
            }
        });
    }

    private String generateInternalEmailMessage(String name) {
        String formattedString = name.toUpperCase();
        formattedString = formattedString.replaceAll(" ", "%20");
        return "New Company request for " + company.getFirstName() + " " + company.getSurName()
                + "\n\n" + "Click on below link to access files\n\n"
                + "https://www.dropbox.com/home/Apps/Tributum/COMPANY_FOUNDATION/"
                + formattedString;
    }

    private void sendClientMail(String email, String message) {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(email, message, "Android"));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    view.hideLoadingScreen();
                    view.showRequestSent();
                } else {
                    view.showToast(R.string.something_went_wrong);
                }

                view.hideLoadingScreen();
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                view.hideLoadingScreen();
                view.showToast(R.string.something_went_wrong);
            }
        });
    }
}