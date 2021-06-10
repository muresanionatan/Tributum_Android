package com.app.tributum.activity.payments;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.PaymentsItemClickListener;
import com.app.tributum.model.EmailBody;
import com.app.tributum.model.PaymentModel;
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

public class PaymentsPresenterImpl implements PaymentsPresenter, PaymentsItemClickListener {

    private final PaymentsView view;

    private final Resources resources;

    private List<PaymentModel> paymentList;

    private boolean isNet = TributumAppHelper.getBooleanSetting(AppKeysValues.NET);

    PaymentsPresenterImpl(PaymentsView paymentsView) {
        this.view = paymentsView;
        this.resources = TributumApplication.getInstance().getResources();
    }

    @Override
    public void onCreate() {
        paymentList = new ArrayList<>(TributumAppHelper.getListSetting(AppKeysValues.PAYMENT_LIST));
        if (paymentList.size() == 0)
            paymentList.add(new PaymentModel("", "", "0"));
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
    public List<PaymentModel> getPaymentsList() {
        return paymentList;
    }

    @Override
    public void handleSendButtonClick(String payer, String email, String site, String month) {
        if (view == null)
            return;

        if (NetworkUtils.isNetworkConnected()) {
            if (!payer.equals("")
                    && !email.equals("")
                    && !site.equals("")
                    && !month.equals("")
                    && !view.areThereEmptyInputs()) {
                saveListToPreferences(payer, email, site);
                sendInternalEmail(payer, email, site, month);
            } else {
                view.showToast(resources.getString(R.string.add_all_info));
            }
        } else {
            view.showToast(resources.getString(R.string.try_again));
        }
    }

    private String concatenateInternalMail(String payer, String email, String site, String month) {
        String amountType;
        if (isNet)
            amountType = resources.getString(R.string.net_label);
        else
            amountType = resources.getString(R.string.gross_label);

        String message = "Payment from " + payer + " at " + site
                + " for " + month
                + "\n \n" + "Please register the following payments: " + "\n";
        for (PaymentModel model : paymentList) {
            message = message + model.getName() + " (" + model.getPps() + ") - " + model.getAmount() + "\n";
        }
        message = message + "\n \n" + "All of them are " + amountType.toUpperCase() + "\n \n";
        message = message + "Please respond to " + email + ".";
        return message;
    }

    private String concatenateClientMail(String site, String month) {
        String amountType;
        if (isNet)
            amountType = resources.getString(R.string.net_label);
        else
            amountType = resources.getString(R.string.gross_label);

        String message = "The following payments at " + site
                + " for " + month
                + " will be made for:\n\n";
        for (PaymentModel model : paymentList) {
            message = message + model.getName() + " (" + model.getPps() + ") - " + model.getAmount() + "\n";
        }
        message = message + "\n \n" + "All of them are " + amountType.toUpperCase() + ".";
        return message;
    }

    private void saveListToPreferences(String payer, String email, String site) {
        List<PaymentModel> paymentModels = new ArrayList<>();
        for (PaymentModel model : paymentList) {
            paymentModels.add(new PaymentModel(model.getName(), model.getPps(), model.getAmount()));
        }
        TributumAppHelper.saveSetting(AppKeysValues.PAYMENT_LIST, paymentModels);
        TributumAppHelper.saveSetting(AppKeysValues.PAYER_NAME, payer);
        TributumAppHelper.saveSetting(AppKeysValues.CLIENT_PAYMENT_EMAIL, email);
        TributumAppHelper.saveSetting(AppKeysValues.SITE, site);
        TributumAppHelper.saveSetting(AppKeysValues.NET, isNet);
    }

    private void sendInternalEmail(String payer, String email, String site, String month) {
        if (view != null)
            view.showLoadingScreen();

        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.TRIBUTUM_EMAIL, concatenateInternalMail(payer, email, site, month)));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (view != null) {
                    if (response.isSuccessful())
                        sendClientEmail(payer, site, month);
                    else
                        view.showToast(resources.getString(R.string.something_went_wrong));

                    view.hideLoadingScreen();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                if (view != null) {
                    view.hideLoadingScreen();
                    view.showToast(resources.getString(R.string.something_went_wrong));
                }
            }
        });
    }

    private void sendClientEmail(String payer, String site, String month) {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(payer, concatenateClientMail(site, month)));
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
    public void onAddNewPaymentClick() {
        paymentList.add(new PaymentModel("", "", ""));
        if (view != null)
            view.addModel(paymentList.size() - 2, paymentList.size() - 1);
    }

    @Override
    public void removeItem(int position) {
        paymentList.remove(position);
        if (view != null)
            view.removeModel();
    }

    @Override
    public void onPause(String payer, String email, String site) {
        saveListToPreferences(payer, email, site);
    }

    @Override
    public void onDestroy(String payer, String email, String site) {
        saveListToPreferences(payer, email, site);
    }

    @Override
    public void onBackPressed() {
        if (view != null)
            view.closeActivity();
    }
}