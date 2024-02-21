package com.app.tributum.activity.contract;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.Editable;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.activity.contract.model.ContractModel;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.AsyncListener;
import com.app.tributum.listener.RequestSentListener;
import com.app.tributum.listener.SignatureListener;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ContractPresenterImpl implements ContractPresenter, SignatureListener, AsyncListener, RequestSentListener {

    private Bitmap signatureFile;

    private final ContractView view;

    private final Resources resources;

    private boolean isVat = true;

    private int previousStartingDateLength = 0;

    ContractPresenterImpl(ContractView contractView) {
        this.view = contractView;
        this.resources = TributumApplication.getInstance().getResources();
    }

    @Override
    public void onStartingDateSet(int year, int monthOfYear, int dayOfMonth) {
        if (view == null)
            return;

        String dayString = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10)
            dayString = "0" + dayOfMonth;
        monthOfYear = monthOfYear + 1;
        String monthString = String.valueOf(monthOfYear);
        if (monthOfYear < 10)
            monthString = "0" + monthOfYear;
        view.setStartingDateText(dayString + "/" + monthString + "/" + year);
    }

    @Override
    public void beforeStartingDateChanged(int length) {
        previousStartingDateLength = length;
    }

    @Override
    public void afterStartingDateChanged(Editable string) {
        handleDates(string, previousStartingDateLength);
    }

    private void handleDates(Editable s, int previousValue) {
        if (view == null)
            return;

        if (s.length() > previousValue) {
            if (s.length() == 2) {
                try {
                    if (Integer.parseInt(s.toString()) > 31) {
                        view.setStartingDateText("31/");
                    } else {
                        view.setStartingDateText(s + "/");
                    }
                } catch (NumberFormatException e) {
                    int currentDay = java.util.Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    String day = String.valueOf(currentDay);
                    if (currentDay < 10) {
                        day = "0" + day;
                    }
                    view.setStartingDateText(day + "/");
                }
                view.moveStartingDayCursorToEnd();
            } else if (s.length() == 3) {
                String birthday = String.valueOf(s);
                if (!birthday.endsWith("/")) {
                    String string = birthday.substring(0, birthday.length() - 1) + "/" + birthday.substring(birthday.length() - 1);
                    view.setStartingDateText(string);
                }
                view.moveStartingDayCursorToEnd();
            } else if (s.length() == 4) {
                int month;
                try {
                    month = Integer.parseInt(s.toString().substring(s.toString().length() - 1));
                    if (month > 1) {
                        String string = s.toString().substring(0, 3) + "0" + month + "/";
                        view.setStartingDateText(string);
                    }
                } catch (NumberFormatException e) {
                    int currentMonth = java.util.Calendar.getInstance().get(Calendar.MONTH) + 1;
                    String monthString = String.valueOf(currentMonth);
                    if (currentMonth < 10) {
                        monthString = "0" + monthString;
                    }
                    String string = s.toString().substring(0, 3) + monthString + "/";
                    view.setStartingDateText(string);
                }
                view.moveStartingDayCursorToEnd();
            } else if (s.length() == 5) {
                String string = s.toString();
                string = string.substring(3);
                try {
                    if (Integer.parseInt(string) > 12) {
                        String firstString = s.toString();
                        view.setStartingDateText(firstString.substring(0, 3) + "12/");
                    } else {
                        view.setStartingDateText(s + "/");
                    }
                } catch (NumberFormatException e) {
                    int c = s.toString().charAt(3) - '0';
                    if (c == 1) {
                        view.setStartingDateText(s.toString().substring(0, 3) + "01/");
                    } else {
                        int currentMonth = java.util.Calendar.getInstance().get(Calendar.MONTH) + 1;
                        String month = String.valueOf(currentMonth);
                        if (currentMonth < 10) {
                            month = "0" + month;
                        }
                        String string1 = s.toString().substring(0, 3) + month + "/";
                        view.setStartingDateText(string1);
                    }
                }
                view.moveStartingDayCursorToEnd();
            } else if (s.length() == 6) {
                String birthday = String.valueOf(s);
                if (!birthday.endsWith("/")) {
                    String string = birthday.substring(0, birthday.length() - 1) + "/" + birthday.substring(birthday.length() - 1);
                    view.setStartingDateText(string);
                }
                view.moveStartingDayCursorToEnd();
            } else if (s.length() == 10) {
                String string = s.toString();
                string = string.substring(6);
                int currentYear = java.util.Calendar.getInstance().get(Calendar.YEAR);
                try {
                    if (Integer.parseInt(string) > currentYear) {
                        String firstString = s.toString();
                        view.setStartingDateText(firstString.substring(0, 6) + currentYear);
                    }
                } catch (NumberFormatException nfe) {
                    String string1 = s.toString().substring(0, 6) + currentYear;
                    view.setStartingDateText(string1);
                }
                view.moveStartingDayCursorToEnd();
            }
        }
    }

    @Override
    public void onClearSignatureClick() {
        if (view == null)
            return;
        view.clearSignature();
        view.hideClearButton();
        signatureFile = null;
    }

    @Override
    public void handleSendButtonClick(String firstName, String lastName, String address1, String address2, String address3, String eircode,
                                      String birthday, String occupation, String phone, String email, String bankAccount,
                                      String pps, String startingDate, String noOfKids) {
        if (view == null)
            return;

        sendInfo(firstName, lastName, address1, address2, address3, eircode, pps, email, birthday, occupation, phone, bankAccount, noOfKids, startingDate);
    }

    @Override
    public void setVatCheckbox() {
        isVat = true;
        if (view == null)
            return;

        view.selectVat();
        view.deselectRct();
    }

    @Override
    public void setRctCheckbox() {
        isVat = false;
        if (view == null)
            return;

        view.selectRct();
        view.deselectVat();
    }

    @Override
    public void onBackPressed() {
        if (view == null)
            return;
        view.closeActivity();
    }

    @Override
    public void onDrawingStarted() {
        if (view != null) {
            view.showClearButton();
        }
    }

    private void sendInfo(String firstName, String lastName, String address1, String address2, String address3, String eircode,
                          String pps, String email, String birthday, String occupation,
                          String phone, String bankAccount, String noOfKids, String startingDate) {
        ContractModel contractModel = new ContractModel(
                firstName + " " + lastName,
                address1 + " " + address2 + " " + address3 + " " + eircode,
                pps,
                "muresanionatan@gmail.com",
                startingDate,
                birthday);

        contractModel.setMaritalStatus(resources.getString(R.string.single_label));
        contractModel.setMaritalStatus(resources.getString(R.string.cohabiting_label));

        List<String> employmentList = new ArrayList<>();
        employmentList.add(resources.getString(R.string.self_employed_label));
        contractModel.setEmployment(employmentList);

        List<String> taxes = new ArrayList<>();
        if (isVat) {
            taxes.add(resources.getString(R.string.value_added_tax_label));
        } else {
            taxes.add(resources.getString(R.string.income_tax_label));
            taxes.add(resources.getString(R.string.relevant_contract_label));
        }

        contractModel.setOccupation(occupation);
        contractModel.setMessage(resources.getString(R.string.contract_mail_message));
        contractModel.setStartingDate(startingDate);

        saveSignatureImage();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        signatureFile.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        contractModel.setTaxes(taxes);
        contractModel.setSignature(byteArray);

        view.showLoadingScreen();

        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.generateAln(contractModel);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (!response.isSuccessful()) {
                    view.showToast(R.string.something_went_wrong);
                } else {
                    view.hideLoadingScreen();
                    view.showRequestSentScreen();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                view.hideLoadingScreen();
                view.showToast(R.string.something_went_wrong);
            }
        });
    }

    private void saveSignatureImage() {
        if (view == null)
            return;
        view.setDrawingCacheEnabled();
        signatureFile = view.getSignatureFile();
        File signature = new File(TributumApplication.getInstance().getFilesDir(), "/signature.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(signature);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        signatureFile.compress(Bitmap.CompressFormat.PNG, 95, fos);
    }

    @Override
    public void onTaskCompleted() {
        if (view != null) {
            view.hideLoadingScreen();
            view.showRequestSentScreen();
        }
    }

    @Override
    public void onOkClicked() {
        if (view != null)
            view.closeActivity();
    }
}