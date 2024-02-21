package com.app.tributum.activity.contract;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.app.tributum.R;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.helper.DrawingView;
import com.app.tributum.utils.CustomTextWatcher;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.ui.CustomScrollView;
import com.app.tributum.utils.ui.LoadingScreen;
import com.app.tributum.utils.ui.RequestSent;

import java.util.Calendar;

public class ContractActivity extends AppCompatActivity implements ContractView {

    private EditText firstNameEditText;

    private EditText lastNameEditText;

    private EditText ppsNumberEditText;

    private RelativeLayout parentView;

    private LoadingScreen loadingScreen;

    private RequestSent requestSent;

    private EditText startingDate;

    private DrawingView signatureDraw;

    private ContractPresenterImpl presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsGeneral.changeLocaleForContext(this, TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        setContentView(R.layout.contract_activity);
        StatusBarUtils.makeStatusBarTransparent(this);

        presenter = new ContractPresenterImpl(this);
        setupViews();
    }

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
    private void setupViews() {
        NestedScrollView scrollView = findViewById(R.id.scrollView);
        findViewById(R.id.contract_back_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackPressed();
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null && event.getAction() == MotionEvent.ACTION_MOVE) {
                    UtilsGeneral.hideSoftKeyboard(ContractActivity.this);
                }
                return false;
            }
        });

        firstNameEditText = findViewById(R.id.first_name_edit_text);
        lastNameEditText = findViewById(R.id.last_name_edit_text);
        ppsNumberEditText = findViewById(R.id.pps_number_edit_text);

        firstNameEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        lastNameEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        ppsNumberEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(ppsNumberEditText, 9, true);

        startingDate = findViewById(R.id.edittext_starting_date_id);
        UtilsGeneral.setMaxLengthEditText(startingDate, 10);

        findViewById(R.id.starting_date_image_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog picker = new DatePickerDialog(ContractActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                presenter.onStartingDateSet(year, monthOfYear, dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        startingDate.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.beforeStartingDateChanged(s.length());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterStartingDateChanged(s);
            }
        });

        findViewById(R.id.contract_send_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.handleSendButtonClick(
                        firstNameEditText.getText().toString().trim(),
                        lastNameEditText.getText().toString().trim(),
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ppsNumberEditText.getText().toString(),
                        startingDate.getText().toString(),
                        ""
                );
            }
        });

        findViewById(R.id.vat_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setVatCheckbox();
            }
        });
        findViewById(R.id.vat_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setVatCheckbox();
            }
        });

        findViewById(R.id.rct_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setRctCheckbox();
            }
        });

        findViewById(R.id.rct_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setRctCheckbox();
            }
        });

        parentView = findViewById(R.id.signature_drawing_view);
        signatureDraw = new DrawingView(ContractActivity.this, presenter);
        parentView.addView(signatureDraw);
        ((CustomScrollView) scrollView).setScrollingEnabled(false);

        findViewById(R.id.delete_signature_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onClearSignatureClick();
            }
        });

        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), R.drawable.ic_icon_loader_contract, R.color.contract_1);
        loadingScreen.setText(getString(R.string.might_take));
        requestSent = new RequestSent(findViewById(android.R.id.content), R.drawable.request_sent_contract, getString(R.string.contract_sent_label), presenter);
    }

    @Override
    public void clearSignature() {
        signatureDraw.clear();
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
    public void selectVat() {
        ((CheckBox) findViewById(R.id.vat_checkbox)).setChecked(true);
    }

    @Override
    public void deselectVat() {
        ((CheckBox) findViewById(R.id.vat_checkbox)).setChecked(false);
    }

    @Override
    public void selectRct() {
        ((CheckBox) findViewById(R.id.rct_checkbox)).setChecked(true);
    }

    @Override
    public void deselectRct() {
        ((CheckBox) findViewById(R.id.rct_checkbox)).setChecked(false);
    }

    @Override
    public void closeActivity() {
        finish();
    }

    @Override
    public void showToast(int stringId) {
        Toast.makeText(ContractActivity.this, getResources().getString(stringId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setStartingDateText(String string) {
        startingDate.setText(string);
    }

    @Override
    public void moveStartingDayCursorToEnd() {
        startingDate.setSelection(startingDate.getText().length());
    }

    @Override
    public void showClearButton() {
        findViewById(R.id.delete_signature_id).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideClearButton() {
        findViewById(R.id.delete_signature_id).setVisibility(View.GONE);
    }

    @Override
    public void setDrawingCacheEnabled() {
        parentView.setDrawingCacheEnabled(true);
    }

    @Override
    public Bitmap getSignatureFile() {
        return parentView.getDrawingCache();
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }
}