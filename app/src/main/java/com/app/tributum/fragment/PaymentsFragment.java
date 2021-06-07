package com.app.tributum.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tributum.R;
import com.app.tributum.activity.MainActivity;
import com.app.tributum.adapter.PaymentsAdapter;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.listener.KeyboardListener;
import com.app.tributum.listener.PaymentsItemClickListener;
import com.app.tributum.model.EmailBody;
import com.app.tributum.model.PaymentModel;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.utils.CalendarUtils;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.NetworkUtils;
import com.app.tributum.utils.animation.AnimUtils;
import com.app.tributum.utils.ui.KeyboardVisibility;
import com.app.tributum.utils.ui.LoadingScreen;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PaymentsFragment extends Fragment implements PaymentsItemClickListener, KeyboardListener {

    private RecyclerView recyclerView;

    private PaymentsAdapter adapter;

    private List<PaymentModel> paymentList;

    private EditText payerEditText;

    private EditText payerEmailEditText;

    private CheckBox netCheckbox;

    private EditText siteEditText;

    private EditText monthEditText;

    private LoadingScreen loadingScreen;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payments_fragment, container, false);
        populateList(view);
        setupRecyclerView(view);
        return view;
    }

    private void populateList(View view) {
        netCheckbox = view.findViewById(R.id.net_checkbox);
        final CheckBox grossCheckbox = view.findViewById(R.id.gross_checkbox);
        netCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netCheckbox.setChecked(true);
                grossCheckbox.setChecked(false);
            }
        });

        grossCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netCheckbox.setChecked(false);
                grossCheckbox.setChecked(true);
            }
        });

        payerEditText = view.findViewById(R.id.payer_edit_text);
        payerEmailEditText = view.findViewById(R.id.payer_email_edit_text);

        payerEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        payerEmailEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        siteEditText = view.findViewById(R.id.site_edit_text);
        monthEditText = view.findViewById(R.id.month_edit_text);

        view.findViewById(R.id.clear_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClearButtonClick();
            }
        });

        view.findViewById(R.id.send_payment_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendButtonClick();
            }
        });

        paymentList = new ArrayList<>();
        List<PaymentModel> paymentModels = TributumAppHelper.getListSetting(AppKeysValues.PAYMENT_LIST);
        for (PaymentModel model : paymentModels) {
            paymentList.add(new PaymentModel(model.getName(), model.getPps(), model.getAmount()));
        }
        payerEditText.setText(TributumAppHelper.getStringSetting(AppKeysValues.PAYER_NAME));
        payerEmailEditText.setText(TributumAppHelper.getStringSetting(AppKeysValues.CLIENT_PAYMENT_EMAIL));
        siteEditText.setText(TributumAppHelper.getStringSetting(AppKeysValues.SITE));
        monthEditText.setText(CalendarUtils.getCurrentMonth());
        if (TributumAppHelper.getBooleanSetting(AppKeysValues.NET)) {
            netCheckbox.setChecked(true);
            grossCheckbox.setChecked(false);
        } else {
            netCheckbox.setChecked(false);
            grossCheckbox.setChecked(true);
        }

        KeyboardVisibility keyboardVisibility = new KeyboardVisibility(this);
        keyboardVisibility.handleKeyboardVisibility(view.findViewById(R.id.payments_main_layout_id));

        if (getActivity() != null)
            loadingScreen = new LoadingScreen(getActivity(), getActivity().findViewById(android.R.id.content));
    }

    private void handleClearButtonClick() {
        paymentList = new ArrayList<>();
        paymentList.add(new PaymentModel("", "", ""));
//        adapter.setList(paymentList);
        adapter.notifyDataSetChanged();
        payerEditText.setText("");
        payerEmailEditText.setText("");
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.payments_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (paymentList.size() == 0)
            paymentList.add(new PaymentModel("", "", "0"));

        adapter = new PaymentsAdapter(paymentList, this);
        recyclerView.setAdapter(adapter);
    }

    private void handleSendButtonClick() {
        if (getActivity() == null)
            return;

        if (NetworkUtils.isNetworkConnected()) {
            if (!payerEditText.getText().toString().equals("")
                    && !payerEmailEditText.getText().toString().equals("")
                    && !siteEditText.toString().equals("")
                    && !monthEditText.toString().equals("")
                    && !adapter.areThereEmptyInputs()) {
                saveListToPreferences();
                sendInternalEmail();
            } else {
                Toast.makeText(getActivity(), getString(R.string.add_all_info), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
        }
    }

    private String concatenateInternalMail() {
        String amountType;
        if (netCheckbox.isChecked())
            amountType = getString(R.string.net_label);
        else
            amountType = getString(R.string.gross_label);

        String message = "Payment from " + payerEditText.getText().toString() + " at " + siteEditText.getText().toString()
                + " for " + monthEditText.getText().toString()
                + "\n \n" + "Please register the following payments: " + "\n";
        for (PaymentModel model : paymentList) {
            message = message + model.getName() + " (" + model.getPps() + ") - " + model.getAmount() + "\n";
        }
        message = message + "\n \n" + "All of them are " + amountType.toUpperCase() + "\n \n";
        message = message + "Please respond to " + payerEmailEditText.getText().toString() + ".";
        return message;
    }

    private String concatenateClientMail() {
        String amountType;
        if (netCheckbox.isChecked())
            amountType = getString(R.string.net_label);
        else
            amountType = getString(R.string.gross_label);

        String message = "The following payments at " + siteEditText.getText().toString()
                + " for " + monthEditText.getText().toString()
                + " will be made for:\n\n";
        for (PaymentModel model : paymentList) {
            message = message + model.getName() + " (" + model.getPps() + ") - " + model.getAmount() + "\n";
        }
        message = message + "\n \n" + "All of them are " + amountType.toUpperCase() + ".";
        return message;
    }

    private void saveListToPreferences() {
        List<PaymentModel> paymentModels = new ArrayList<>();
        for (PaymentModel model : paymentList) {
            paymentModels.add(new PaymentModel(model.getName(), model.getPps(), model.getAmount()));
        }
        TributumAppHelper.saveSetting(AppKeysValues.PAYMENT_LIST, paymentModels);
        TributumAppHelper.saveSetting(AppKeysValues.PAYER_NAME, payerEditText.getText().toString());
        TributumAppHelper.saveSetting(AppKeysValues.CLIENT_PAYMENT_EMAIL, payerEmailEditText.getText().toString());
        TributumAppHelper.saveSetting(AppKeysValues.SITE, siteEditText.getText().toString());
        TributumAppHelper.saveSetting(AppKeysValues.NET, netCheckbox.isChecked());
    }

    private void sendInternalEmail() {
        loadingScreen.show();
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.TRIBUTUM_EMAIL, concatenateInternalMail()));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                System.out.println("ContractFragment.onResponse " + response.body());
                if (response.isSuccessful())
                    sendClientEmail();
                else
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                loadingScreen.hide();
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                loadingScreen.hide();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendClientEmail() {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(payerEmailEditText.getText().toString(), concatenateClientMail()));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                System.out.println("ContractFragment.onResponse " + response.body());
                if (response.isSuccessful())
                    Toast.makeText(getActivity(), getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                loadingScreen.hide();
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                loadingScreen.hide();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        saveListToPreferences();
    }

    @Override
    public void removeItem(int position) {
        paymentList.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        saveListToPreferences();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        saveListToPreferences();
        super.onDestroyView();
    }

    @Override
    public void onKeyboardStateChanged(boolean opened) {
        if (getView() == null || getActivity() == null)
            return;
        View sendButton = getView().findViewById(R.id.send_payment_button);
        if (opened) {
            AnimUtils.getTranslationYAnimator(sendButton, 250).start();
            AnimUtils.getFadeOutAnimator(((MainActivity) getActivity()).getBottomNavigation()).start();
        } else {
            AnimUtils.getTranslationYAnimator(sendButton, 0).start();
            AnimUtils.getFadeInAnimator(((MainActivity) getActivity()).getBottomNavigation(), AnimUtils.DURATION_50, AnimUtils.DURATION_50, null, null).start();
        }
    }
}