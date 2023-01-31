package com.app.tributum.activity.payments;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.activity.payments.model.PaymentModel;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.RequestSentListener;
import com.app.tributum.model.EmailBody;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.utils.CalendarUtils;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PaymentsPresenterImpl implements PaymentsPresenter, RequestSentListener {

    private final PaymentsView view;

    private final Resources resources;

    private boolean isNet = TributumAppHelper.getBooleanSetting(AppKeysValues.NET);

    PaymentsPresenterImpl(PaymentsView paymentsView) {
        this.view = paymentsView;
        this.resources = TributumApplication.getInstance().getResources();
    }

    @Override
    public void onCreate() {
        if (view == null)
            return;

        view.populateInputsWithValues(
                TributumAppHelper.getStringSetting(AppKeysValues.PAYER_NAME),
                TributumAppHelper.getStringSetting(AppKeysValues.CLIENT_PAYMENT_EMAIL),
                TributumAppHelper.getStringSetting(AppKeysValues.SITE),
                CalendarUtils.getCurrentMonth()
        );

        if (TributumAppHelper.getBooleanSetting(AppKeysValues.NET))
            view.setNetViews();
        else
            view.setGrossViews();
    }

    @Override
    public void handleSendButtonClick(String payer, String email, String month) {
        if (view == null)
            return;

        if (NetworkUtils.isNetworkConnected()) {
            if (payer.equals("")) {
                view.showToast(resources.getString(R.string.please_enter_name));
                view.setFocusOnName();
            } else if (email.equals("")) {
                view.showToast(resources.getString(R.string.please_enter_correct_email));
                view.setFocusOnEmail();
            } else if (month.equals("")) {
                view.showToast(resources.getString(R.string.please_enter_payment_month));
                view.setFocusOnMonth();
            } else if (view.areThereEmptyInputs()) {
                view.focusOnEmptyInputFromRecyclerView();
            } else {
                saveListToPreferences(payer, email);
                sendInternalEmail(payer, email, month);
            }
        } else {
            view.showToast(resources.getString(R.string.try_again));
        }
    }

    private String concatenateInternalMail(String payer, String email, String month) {
        String amountType;
        if (isNet)
            amountType = resources.getString(R.string.net_label);
        else
            amountType = resources.getString(R.string.gross_label);

        StringBuilder message = new StringBuilder("Payment from " + payer
                + " for " + month
                + "\n \n" + "Please register the following payments: " + "\n");
        for (PaymentModel model : view.getPaymentList()) {
            message.append(model.getName()).append(" (").append(model.getPps()).append(") - ").append(model.getAmount())
                    .append(", site: ").append(model.getSite()).append("\n");
        }
        message.append("\n \n").append("All of them are ").append(amountType.toUpperCase()).append("\n \n");
        message.append("Please respond to ").append(email).append(".");
        return message.toString();
    }

    private String concatenateClientMail(String month) {
        String amountType;
        if (isNet)
            amountType = resources.getString(R.string.net_label);
        else
            amountType = resources.getString(R.string.gross_label);

        StringBuilder message = new StringBuilder("The following payments for " + month
                + " will be made for:\n\n");
        for (PaymentModel model : view.getPaymentList()) {
            message.append(model.getName()).append(" (").append(model.getPps()).append(") - ").append(model.getAmount())
                    .append(", site: ").append(model.getSite()).append("\n");
        }
        message.append("\n \n").append("All of them are ").append(amountType.toUpperCase()).append(".");
        return message.toString();
    }

    private void saveListToPreferences(String payer, String email) {
        List<PaymentModel> paymentModels = new ArrayList<>();
        for (PaymentModel model : view.getPaymentList()) {
            paymentModels.add(new PaymentModel(model.getName(), model.getPps(), model.getAmount(), model.getSite()));
        }
        TributumAppHelper.saveSetting(AppKeysValues.PAYMENT_LIST, paymentModels);
        TributumAppHelper.saveSetting(AppKeysValues.PAYER_NAME, payer);
        TributumAppHelper.saveSetting(AppKeysValues.CLIENT_PAYMENT_EMAIL, email);
        TributumAppHelper.saveSetting(AppKeysValues.NET, isNet);
    }

    private void sendInternalEmail(String payer, String email, String month) {
        if (view == null)
            return;

        view.showLoadingScreen();
        view.hideKeyboard();

        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.TRIBUTUM_EMAIL, concatenateInternalMail(payer, email, month)));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful())
                    sendClientEmail(email, month);
                else
                    view.showToast(resources.getString(R.string.something_went_wrong));

                view.hideLoadingScreen();
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                view.hideLoadingScreen();
                view.showToast(resources.getString(R.string.something_went_wrong));
            }
        });
    }

    private void sendClientEmail(String email, String month) {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(email, concatenateClientMail(month)));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                System.out.println("ContractFragment.onResponse " + response.body());
                if (response.isSuccessful())
                    view.showRequestSentScreen();
                else
                    view.showToast(resources.getString(R.string.something_went_wrong));

                view.hideLoadingScreen();
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                view.hideLoadingScreen();
                view.showToast(resources.getString(R.string.something_went_wrong));
            }
        });
    }

    @Override
    public void onNetClick() {
        isNet = true;
        if (view != null)
            view.setNetViews();
    }

    @Override
    public void onGrossClick() {
        isNet = false;
        if (view != null)
            view.setGrossViews();
    }

    @Override
    public void onPause(String payer, String email) {
        saveListToPreferences(payer, email);
    }

    @Override
    public void onDestroy(String payer, String email) {
        saveListToPreferences(payer, email);
    }

    @Override
    public void onBackPressed() {
        if (view != null)
            view.closeActivity();
    }

    @Override
    public void onOkClicked() {
        if (view != null)
            view.closeActivity();
    }
}