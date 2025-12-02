package com.app.tributum.activity.company;

import android.icu.util.Calendar;
import android.text.Editable;

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
import com.app.tributum.utils.ValidationUtils;
import com.app.tributum.utils.ui.FileUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CompanyPresenterImpl implements AsyncListener, CompanyPresenter, RequestSentListener {

    final private CompanyView view;

    private boolean hasSecondDirector;

    private boolean hasThirdDirector;

    private int previousDirector1Length;
    private int previousDirector2Length;
    private int previousDirector3Length;
    private int previousSecretaryLength;

    private Company company;
    private Director director1;
    private Director director2 = new Director();
    private Director director3 = new Director();

    private boolean acceptedTerms;

    @CompanyProgressState
    private int state = CompanyProgressState.COMPANY;

    public CompanyPresenterImpl(CompanyView view) {
        this.view = view;
    }

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

    private void moveToDirectorScreen() {
        company = view.getCompanyDetails();
        if (company.getFirstName().isEmpty())
            view.showToast(R.string.please_enter_first_name);
        else if (company.getSurName().isEmpty())
            view.showToast(R.string.please_enter_sur_name);
        else if (!ValidationUtils.isEmailValid(company.getEmail()))
            view.showToast(R.string.please_enter_correct_email);
        else if (company.getPhone().isEmpty())
            view.showToast(R.string.please_enter_phone);
        else if (company.getCompanyName().isEmpty())
            view.showToast(R.string.please_enter_company_name);
        else if (company.getProposedCompanyName().isEmpty())
            view.showToast(R.string.please_enter_proposed);
        else if (company.getAddress().isEmpty())
            view.showToast(R.string.please_enter_address);
        else if (company.getTown().isEmpty())
            view.showToast(R.string.please_enter_town);
        else if (company.getCountry().isEmpty())
            view.showToast(R.string.please_enter_country);
        else if (company.getActivities().isEmpty())
            view.showToast(R.string.please_enter_activities);
        else if (company.getRegisteredOffice().isEmpty())
            view.showToast(R.string.please_enter_registered_address);
        else {
            state = CompanyProgressState.DIRECTOR;
            view.hideCompanyView();
            view.showDirectorViewFromRight();
            view.setConfirmationButtonText(R.string.continue_label);
        }
    }

    private void moveToSecretaryScreen() {
        director1 = view.getDirector1Details();
        if (hasSecondDirector)
            director2 = view.getDirector2Details();
        if (hasThirdDirector)
            director3 = view.getDirector3Details();

        if (director1.getFirstName().isEmpty()
                || (hasSecondDirector && director2.getFirstName().isEmpty())
                || (hasThirdDirector && director3.getFirstName().isEmpty()))
            view.showToast(R.string.please_enter_first_name);
        else if (director1.getSurName().isEmpty()
                || (hasSecondDirector && director2.getSurName().isEmpty())
                || (hasThirdDirector && director3.getSurName().isEmpty()))
            view.showToast(R.string.please_enter_sur_name);
        else if (director1.getBirthday().isEmpty()
                || (hasSecondDirector && director2.getBirthday().isEmpty())
                || (hasThirdDirector && director3.getBirthday().isEmpty()))
            view.showToast(R.string.please_enter_birthday_format);
        else if (!ValidationUtils.isPpsValid(director1.getPps())
                || (hasSecondDirector && !ValidationUtils.isPpsValid(director2.getPps()))
                || (hasThirdDirector && !ValidationUtils.isPpsValid(director3.getPps())))
            view.showToast(R.string.please_enter_pps);
        else if (director1.getNationality().isEmpty()
                || (hasSecondDirector && director2.getNationality().isEmpty())
                || (hasThirdDirector && director3.getNationality().isEmpty()))
            view.showToast(R.string.please_enter_nationality);
        else if (director1.getOccupation().isEmpty()
                || (hasSecondDirector && director2.getOccupation().isEmpty())
                || (hasThirdDirector && director3.getOccupation().isEmpty()))
            view.showToast(R.string.please_enter_occupation);
        else if (director1.getAddress().isEmpty()
                || (hasSecondDirector && director2.getAddress().isEmpty())
                || (hasThirdDirector && director3.getAddress().isEmpty()))
            view.showToast(R.string.please_enter_address);
        else {
            state = CompanyProgressState.SECRETARY;
            view.hideDirectorViewToLeft();
            view.showSecretaryView();
            view.setConfirmationButtonText(R.string.send_form_label);
        }
    }

    private void sendInfo() {
        Secretary secretary = view.getSecretaryDetails();
        if (secretary.getFirstName().isEmpty())
            view.showToast(R.string.please_enter_first_name);
        else if (secretary.getSurName().isEmpty())
            view.showToast(R.string.please_enter_sur_name);
        else if (secretary.getBirthday().isEmpty())
            view.showToast(R.string.please_enter_birthday_format);
        else if (!ValidationUtils.isEmailValid(secretary.getEmail()))
            view.showToast(R.string.please_enter_correct_email);
        else if (!ValidationUtils.isPpsValid(secretary.getPps()))
            view.showToast(R.string.please_enter_pps);
        else if (secretary.getNationality().isEmpty())
            view.showToast(R.string.please_enter_nationality);
        else if (secretary.getAddress().isEmpty())
            view.showToast(R.string.please_enter_address);
        else if (!acceptedTerms)
            view.showToast(R.string.please_accept_terms);
        else {
            view.showLoadingScreen();
            UploadAsyncTask uploadOneFileTask = new UploadAsyncTask(
                    company.getFirstName() + " " + company.getSurName(),
                    FileUtils.createCompanyFile(company, director1, director2, director3, secretary),
                    this,
                    UploadAsyncTask.UploadType.USER_INFO,
                    "COMPANY_FOUNDATION");
            uploadOneFileTask.execute();
        }
    }

    @Override
    public void afterBirthdayChanged(Editable s, int resourceId) {
        if (resourceId == R.id.director_1_layout_id)
            handleDates(s, previousDirector1Length, resourceId);
        else if (resourceId == R.id.director_2_layout_id)
            handleDates(s, previousDirector2Length, resourceId);
        else if ((resourceId == R.id.director_3_layout_id))
            handleDates(s, previousDirector3Length, resourceId);
        else
            handleDates(s, previousSecretaryLength, resourceId);
    }

    @Override
    public void beforeBirthdayChanged(int length, int resourceId) {
        if (resourceId == R.id.director_1_layout_id)
            previousDirector1Length = length;
        else if (resourceId == R.id.director_2_layout_id)
            previousDirector2Length = length;
        else if (resourceId == R.id.director_3_layout_id)
            previousDirector3Length = length;
        else
            previousSecretaryLength = length;
    }

    private void handleDates(Editable s, int previousValue, int id) {
        if (view == null)
            return;

        if (s.length() > previousValue) {
            if (s.length() == 2) {
                try {
                    if (Integer.parseInt(s.toString()) > 31) {
                        view.setBirthdayText("31/", id);
                    } else {
                        view.setBirthdayText(s + "/", id);
                    }
                } catch (NumberFormatException e) {
                    int currentDay = java.util.Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    String day = String.valueOf(currentDay);
                    if (currentDay < 10) {
                        day = "0" + day;
                    }
                    view.setBirthdayText(day + "/", id);
                }
                view.moveBirthdayCursorToEnd(id);
            } else if (s.length() == 3) {
                String birthday = String.valueOf(s);
                if (!birthday.endsWith("/")) {
                    String string = birthday.substring(0, birthday.length() - 1) + "/" + birthday.substring(birthday.length() - 1);
                    view.setBirthdayText(string, id);
                }
                view.moveBirthdayCursorToEnd(id);
            } else if (s.length() == 4) {
                int month;
                try {
                    month = Integer.parseInt(s.toString().substring(s.toString().length() - 1));
                    if (month > 1) {
                        String string = s.toString().substring(0, 3) + "0" + month + "/";
                        view.setBirthdayText(string, id);
                    }
                } catch (NumberFormatException e) {
                    int currentMonth = java.util.Calendar.getInstance().get(Calendar.MONTH) + 1;
                    String monthString = String.valueOf(currentMonth);
                    if (currentMonth < 10) {
                        monthString = "0" + monthString;
                    }
                    String string = s.toString().substring(0, 3) + monthString + "/";
                    view.setBirthdayText(string, id);
                }
                view.moveBirthdayCursorToEnd(id);
            } else if (s.length() == 5) {
                String string = s.toString();
                string = string.substring(3);
                try {
                    if (Integer.parseInt(string) > 12) {
                        String firstString = s.toString();
                        view.setBirthdayText(firstString.substring(0, 3) + "12/", id);
                    } else {
                        view.setBirthdayText(s + "/", id);
                    }
                } catch (NumberFormatException e) {
                    int c = s.toString().charAt(3) - '0';
                    if (c == 1) {
                        view.setBirthdayText(s.toString().substring(0, 3) + "01/", id);
                    } else {
                        int currentMonth = java.util.Calendar.getInstance().get(Calendar.MONTH) + 1;
                        String month = String.valueOf(currentMonth);
                        if (currentMonth < 10) {
                            month = "0" + month;
                        }
                        String string1 = s.toString().substring(0, 3) + month + "/";
                        view.setBirthdayText(string1, id);
                    }
                }
                view.moveBirthdayCursorToEnd(id);
            } else if (s.length() == 6) {
                String birthday = String.valueOf(s);
                if (!birthday.endsWith("/")) {
                    String string = birthday.substring(0, birthday.length() - 1) + "/" + birthday.substring(birthday.length() - 1);
                    view.setBirthdayText(string, id);
                }
                view.moveBirthdayCursorToEnd(id);
            } else if (s.length() == 10) {
                String string = s.toString();
                string = string.substring(6);
                int currentYear = java.util.Calendar.getInstance().get(Calendar.YEAR);
                try {
                    if (Integer.parseInt(string) > currentYear) {
                        String firstString = s.toString();
                        view.setBirthdayText(firstString.substring(0, 6) + currentYear, id);
                    }
                } catch (NumberFormatException nfe) {
                    String string1 = s.toString().substring(0, 6) + currentYear;
                    view.setBirthdayText(string1, id);
                }
                view.moveBirthdayCursorToEnd(id);
            }
        }
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
    public void onAgreeTermsClick(boolean checkboxClicked) {
        acceptedTerms = !acceptedTerms;
        if (!checkboxClicked)
            view.checkTheAgreeBox(acceptedTerms);
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