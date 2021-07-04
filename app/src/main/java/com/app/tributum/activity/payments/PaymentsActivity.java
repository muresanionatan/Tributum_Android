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
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.ui.LoadingScreen;
import com.app.tributum.utils.ui.RequestSent;
import com.app.tributum.utils.ui.UiUtils;

public class PaymentsActivity extends AppCompatActivity implements PaymentsView {

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
        populateList();
        presenter.onCreate();
        setupRecyclerView();
    }

    @SuppressLint("CutPasteId")
    private void populateList() {
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
                presenter.onAddNewPaymentClick();
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

        findViewById(R.id.invoices_send_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.handleSendButtonClick(payerEditText.getText().toString(),
                        payerEmailEditText.getText().toString(),
                        siteEditText.getText().toString(),
                        monthEditText.getText().toString());
            }
        });

        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), R.drawable.ic_icon_loader_rct);
        requestSent = new RequestSent(findViewById(android.R.id.content), R.drawable.request_sent_rct, getString(R.string.payment_sent_label), presenter);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.payments_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new PaymentsAdapter(presenter.getPaymentsList(), presenter);
        recyclerView.setAdapter(adapter);
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
    public void removeModel() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addModel(int itemNotified, int itemInserted) {
        adapter.notifyItemChanged(itemNotified);
        adapter.notifyItemInserted(itemInserted);
        recyclerView.scrollToPosition(itemInserted);
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
}