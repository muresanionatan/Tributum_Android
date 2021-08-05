package com.app.tributum.activity.payments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tributum.R;
import com.app.tributum.activity.payments.adapter.PaymentsAdapter;
import com.app.tributum.activity.payments.model.PaymentModel;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.listener.PaymentsItemClickListener;
import com.app.tributum.listener.RecyclerViewInputListener;
import com.app.tributum.utils.CustomTextWatcher;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.ui.LoadingScreen;
import com.app.tributum.utils.ui.RequestSent;
import com.app.tributum.utils.ui.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class PaymentsActivity extends AppCompatActivity implements PaymentsView, PaymentsItemClickListener, RecyclerViewInputListener {

    private RecyclerView recyclerView;

    private PaymentsAdapter adapter;

    private EditText payerEditText;

    private EditText payerEmailEditText;

    private CheckBox netCheckbox;

    private CheckBox grossCheckbox;

    private EditText siteEditText;

    private EditText monthEditText;

    private LoadingScreen loadingScreen;

    private RequestSent requestSent;

    private PaymentsPresenterImpl presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsGeneral.changeLocaleForContext(this, TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        setContentView(R.layout.activity_payments);
        StatusBarUtils.makeStatusBarTransparent(this);

        presenter = new PaymentsPresenterImpl(this);
        setupViews();
        setupRecyclerView();
        presenter.onCreate();
        validateAddedInformation();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setFocusOnName();
            }
        }, 100);
    }

    @SuppressLint("CutPasteId")
    private void setupViews() {
        netCheckbox = findViewById(R.id.net_checkbox);
        grossCheckbox = findViewById(R.id.gross_checkbox);
        netCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onNetClick();
            }
        });
        findViewById(R.id.net_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onNetClick();
            }
        });

        grossCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onGrossClick();
            }
        });

        findViewById(R.id.gross_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onGrossClick();
            }
        });

        findViewById(R.id.add_new_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addModel();
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                validateAddedInformation();
            }
        });

        findViewById(R.id.payments_back_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackPressed();
            }
        });

        payerEditText = findViewById(R.id.payer_edit_text);
        payerEmailEditText = findViewById(R.id.payer_email_edit_text);

        payerEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        payerEmailEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        siteEditText = findViewById(R.id.site_edit_text);
        monthEditText = findViewById(R.id.month_edit_text);

        findViewById(R.id.payments_send_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.handleSendButtonClick(payerEditText.getText().toString().trim(),
                        payerEmailEditText.getText().toString().trim(),
                        siteEditText.getText().toString().trim(),
                        monthEditText.getText().toString().trim());
            }
        });
        payerEditText.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateAddedInformation();
            }
        });
        payerEmailEditText.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateAddedInformation();
            }
        });
        siteEditText.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateAddedInformation();
            }
        });
        monthEditText.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateAddedInformation();
            }
        });

        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), R.drawable.ic_icon_loader_rct);
        requestSent = new RequestSent(findViewById(android.R.id.content), R.drawable.request_sent_rct, getString(R.string.payment_sent_label), presenter);
    }

    private void validateAddedInformation() {
        presenter.onTextChanged(payerEditText.getText().toString().trim(),
                payerEmailEditText.getText().toString().trim(),
                siteEditText.getText().toString().trim(),
                monthEditText.getText().toString().trim());
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.payments_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        List<PaymentModel> paymentList = new ArrayList<>(TributumAppHelper.getListSetting(AppKeysValues.PAYMENT_LIST));
        if (paymentList.size() == 0)
            paymentList.add(new PaymentModel("", "", ""));

        adapter = new PaymentsAdapter(paymentList, this, this);
        recyclerView.setAdapter(adapter);
    }

    private void scrollToEditText(View view) {
        if (view == null)
            return;

        findViewById(R.id.vat_scroll_view_id).post(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.vat_scroll_view_id).scrollTo(0, view.getTop());
            }
        });
    }

    @Override
    public void populateInputsWithValues(String payer, String email, String site, String currentMonth) {
        payerEditText.setText(payer);
        payerEmailEditText.setText(email);
        siteEditText.setText(site);
        monthEditText.setText(currentMonth);
    }

    @Override
    public void setNetViews() {
        UiUtils.setFontFamily(R.font.manrope_bold, findViewById(R.id.net_text_id));
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.gross_text_id));
        findViewById(R.id.net_layout_id).setBackgroundResource(R.drawable.white_rectangle_corners);
        findViewById(R.id.gross_layout_id).setBackgroundResource(R.drawable.grey_rectangle_corners);
        netCheckbox.setChecked(true);
        grossCheckbox.setChecked(false);
    }

    @Override
    public void setGrossViews() {
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.net_text_id));
        UiUtils.setFontFamily(R.font.manrope_bold, findViewById(R.id.gross_text_id));
        findViewById(R.id.net_layout_id).setBackgroundResource(R.drawable.grey_rectangle_corners);
        findViewById(R.id.gross_layout_id).setBackgroundResource(R.drawable.white_rectangle_corners);
        netCheckbox.setChecked(false);
        grossCheckbox.setChecked(true);
    }

    @Override
    public void showLoadingScreen() {
        loadingScreen.show();
    }

    @Override
    public void hideLoadingScreen() {
        loadingScreen.hide();
    }

    @Override
    public void showRequestSentScreen() {
        requestSent.show();
    }

    @Override
    public boolean areThereEmptyInputs() {
        return adapter.areThereEmptyInputs();
    }

    @Override
    public void hideKeyboard() {
        UtilsGeneral.hideSoftKeyboard(this);
    }

    @Override
    public void disableSend() {
        findViewById(R.id.payments_send_text_id).setEnabled(false);
    }

    @Override
    public void enableSend() {
        findViewById(R.id.payments_send_text_id).setEnabled(true);
    }

    @Override
    public void setFocusOnName() {
        UtilsGeneral.setFocusOnInput(payerEditText);
        scrollToEditText(payerEditText);
    }

    @Override
    public void setFocusOnEmail() {
        UtilsGeneral.setFocusOnInput(payerEmailEditText);
        scrollToEditText(payerEmailEditText);
    }

    @Override
    public void setFocusOnSite() {
        UtilsGeneral.setFocusOnInput(siteEditText);
        scrollToEditText(siteEditText);
    }

    @Override
    public void setFocusOnMonth() {
        UtilsGeneral.setFocusOnInput(monthEditText);
        scrollToEditText(monthEditText);
    }

    @Override
    public void setFocusOnRecyclerView() {
//        UtilsGeneral.setFocusOnInput(adapter);
//        scrollToEditText(payerEditText);
    }

    @Override
    public List<PaymentModel> getPaymentList() {
        return adapter.getPaymentList();
    }

    @Override
    public void closeActivity() {
        finish();
    }

    @Override
    public void onPause() {
        presenter.onPause(payerEditText.getText().toString(), payerEmailEditText.getText().toString(), siteEditText.getText().toString());
        super.onPause();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy(payerEditText.getText().toString(), payerEmailEditText.getText().toString(), siteEditText.getText().toString());
        super.onDestroy();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(PaymentsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void removeItem(int position) {
        adapter.remove(position);
        validateAddedInformation();
    }

    @Override
    public void onTextChanged() {
        validateAddedInformation();
    }

    @Override
    public void focusOnEmptyInputFromRecyclerView() {
        adapter.findEmptyInputs();
    }

    @Override
    public void scrollToNameItem(int position) {
//        recyclerView.scrollToPosition(position);
        Toast.makeText(PaymentsActivity.this, getString(R.string.please_enter_name), Toast.LENGTH_SHORT).show();
        scrollToEditText(recyclerView);
    }

    @Override
    public void scrollToPpsItem(int position) {
//        recyclerView.scrollToPosition(position);
        Toast.makeText(PaymentsActivity.this, getString(R.string.please_enter_pps), Toast.LENGTH_SHORT).show();
        scrollToEditText(recyclerView);
    }

    @Override
    public void scrollToAmountItem(int position) {
//        recyclerView.scrollToPosition(position);
        Toast.makeText(PaymentsActivity.this, getString(R.string.please_enter_amount), Toast.LENGTH_SHORT).show();
        scrollToEditText(recyclerView);
    }
}