package com.app.tributum.activity.salary;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.tributum.R;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.ui.LoadingScreen;
import com.app.tributum.utils.ui.RequestSent;

public class SalaryActivity extends AppCompatActivity implements SalaryView {

    private EditText name;

    private EditText email;

    private EditText fullName;

    private EditText pps;

    private EditText gross;

    private EditText net;

    private EditText hours;

    private EditText overtime;

    private EditText subsistance;

    private EditText bankHoliday;

    private EditText holiday;

    private LoadingScreen loadingScreen;

    private SalaryPresenterImpl presenter;

    private RequestSent requestSent;

    private ScrollView scrollView;

    private CalendarView calendarView;

    private View calendars;

    private TextView dateView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsGeneral.changeLocaleForContext(this, TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        setContentView(R.layout.activity_salary);
        StatusBarUtils.makeStatusBarTransparent(this);

        presenter = new SalaryPresenterImpl(this);
        setupViews();
        setupClicks();
        calendarView.setVisibility(View.GONE);
    }

    @SuppressLint("CutPasteId")
    private void setupViews() {
        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), R.drawable.ic_icon_loader_salary, R.color.salary);
        requestSent = new RequestSent(findViewById(android.R.id.content), R.drawable.request_sent_salary, getString(R.string.inquiry_sent_label), presenter);

        scrollView = findViewById(R.id.salary_scroll_view_id);
        name = findViewById(R.id.payer_edit_text);
        email = findViewById(R.id.payer_email_edit_text);
        fullName = findViewById(R.id.salary_beneficiary_edit_text);
        pps = findViewById(R.id.salary_pps_edit_text);
        gross = findViewById(R.id.salary_gross_edit_text);
        net = findViewById(R.id.salary_net_edit_text);
        hours = findViewById(R.id.salary_basic_edit_text);
        overtime = findViewById(R.id.salary_overtime_edit_text);
        subsistance = findViewById(R.id.salary_subsistance_edit_text);
        bankHoliday = findViewById(R.id.salary_bank_holiday_edit_text);
        holiday = findViewById(R.id.salary_holiday_edit_text);

        calendarView = findViewById(R.id.calendar_view_id);
        dateView = findViewById(R.id.date_text_text_id);

        name.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        email.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        fullName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        pps.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        gross.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        net.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        hours.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        overtime.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        subsistance.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        bankHoliday.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        findViewById(R.id.salary_send_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSendClick(name.getText().toString().trim(), email.getText().toString().trim(),
                        fullName.getText().toString().trim(), pps.getText().toString().trim(),
                        gross.getText().toString().trim(), net.getText().toString().trim(),
                        hours.getText().toString().trim(), overtime.getText().toString().trim(),
                        subsistance.getText().toString().trim(), bankHoliday.getText().toString().trim(),
                        holiday.getText().toString().trim());
            }
        });

        findViewById(R.id.salary_back_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackPressed();
            }
        });
    }

    private void setupClicks() {
        findViewById(R.id.monday_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMondayClick();
            }
        });
        findViewById(R.id.tuesday_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onTuesdayClick();
            }
        });
        findViewById(R.id.wednesday_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onWednesdayClick();
            }
        });
        findViewById(R.id.thursday_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onThursdayClick();
            }
        });
        findViewById(R.id.friday_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onFridayClick();
            }
        });
        findViewById(R.id.saturday_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onSaturdayClick();
            }
        });
        findViewById(R.id.sunday_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onSundayClick();
            }
        });

        findViewById(R.id.weekly_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onWeeklyClick();
            }
        });
        findViewById(R.id.monthly_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMonthlyClick();
            }
        });
        findViewById(R.id.fortnightly_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onFortnightlyClick();
            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                System.out.println("SalaryActivity.onSelectedDayChange " + i + " " + i1 + " " + i2);
                presenter.onSelectedDayChange(i, i1, i2);
            }
        });
    }

    private void scrollToEditText(View view) {
        if (view == null)
            return;

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, view.getTop());
            }
        });
    }

    @Override
    public void setFocusOnName() {
        UtilsGeneral.setFocusOnInput(name);
        scrollToEditText(name);
    }

    @Override
    public void setFocusOnEmail() {
        UtilsGeneral.setFocusOnInput(email);
        scrollToEditText(email);
    }

    @Override
    public void setFocusOnFullName() {
        UtilsGeneral.setFocusOnInput(fullName);
        scrollToEditText(fullName);
    }

    @Override
    public void setFocusOnPps() {
        UtilsGeneral.setFocusOnInput(pps);
        scrollToEditText(pps);
    }

    @Override
    public void setFocusOnGross() {
        UtilsGeneral.setFocusOnInput(gross);
        scrollToEditText(gross);
    }

    @Override
    public void setFocusOnNet() {
        UtilsGeneral.setFocusOnInput(net);
        scrollToEditText(net);
    }

    @Override
    public void setFocusOnHours() {
        UtilsGeneral.setFocusOnInput(hours);
        scrollToEditText(hours);
    }

    @Override
    public void setFocusOnOvertime() {
        UtilsGeneral.setFocusOnInput(overtime);
        scrollToEditText(overtime);
    }

    @Override
    public void setFocusOnSubsistance() {
        UtilsGeneral.setFocusOnInput(subsistance);
        scrollToEditText(subsistance);
    }

    @Override
    public void setFocusOnBankHoliday() {
        UtilsGeneral.setFocusOnInput(bankHoliday);
        scrollToEditText(bankHoliday);
    }

    @Override
    public void setFocusOnHoliday() {
        UtilsGeneral.setFocusOnInput(holiday);
        scrollToEditText(holiday);
    }

    @Override
    public void hideKeyboard() {
        UtilsGeneral.hideSoftKeyboard(this);
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
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setDate(String date) {
        dateView.setText(date);
    }

    @Override
    public void showRequestSent() {
        requestSent.show();
    }

    @Override
    public void setWeeklyCalendarDates(String[] currentWeek) {
        ((TextView) findViewById(R.id.monday_id)).setText(currentWeek[0]);
        ((TextView) findViewById(R.id.tuesday_id)).setText(currentWeek[1]);
        ((TextView) findViewById(R.id.wednesday_id)).setText(currentWeek[2]);
        ((TextView) findViewById(R.id.thursday_id)).setText(currentWeek[3]);
        ((TextView) findViewById(R.id.saturday_id)).setText(currentWeek[5]);
        ((TextView) findViewById(R.id.friday_id)).setText(currentWeek[4]);
        ((TextView) findViewById(R.id.sunday_id)).setText(currentWeek[6]);
    }

    @Override
    public void setMonday() {
        findViewById(R.id.monday_layout_id).setSelected(true);
    }

    @Override
    public void setTuesday() {
        findViewById(R.id.tuesday_layout_id).setSelected(true);
    }

    @Override
    public void setWednesday() {
        findViewById(R.id.wednesday_layout_id).setSelected(true);
    }

    @Override
    public void setThursday() {
        findViewById(R.id.thursday_layout_id).setSelected(true);
    }

    @Override
    public void setFriday() {
        findViewById(R.id.friday_layout_id).setSelected(true);
    }

    @Override
    public void setSaturday() {
        findViewById(R.id.saturday_layout_id).setSelected(true);
    }

    @Override
    public void setSunday() {
        findViewById(R.id.sunday_layout_id).setSelected(true);
    }

    @Override
    public void setWeeklyType() {
        findViewById(R.id.weekly_layout_id).setSelected(true);
    }

    @Override
    public void setMonthlyType() {
        findViewById(R.id.monthly_layout_id).setSelected(true);
    }

    @Override
    public void setFortnightlyType() {
        findViewById(R.id.fortnightly_layout_id).setSelected(true);
    }

    @Override
    public void deselectDateTypes() {
        findViewById(R.id.weekly_layout_id).setSelected(false);
        findViewById(R.id.monthly_layout_id).setSelected(false);
        findViewById(R.id.fortnightly_layout_id).setSelected(false);
    }

    @Override
    public void showWeekLayout() {
        findViewById(R.id.week_view_id).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideWeekLayout() {
        findViewById(R.id.week_view_id).setVisibility(View.GONE);
    }

    @Override
    public void showMonthLayout() {
        calendarView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMonthLayout() {
        calendarView.setVisibility(View.GONE);
    }

    @Override
    public void deselectDays() {
        findViewById(R.id.monday_layout_id).setSelected(false);
        findViewById(R.id.tuesday_layout_id).setSelected(false);
        findViewById(R.id.wednesday_layout_id).setSelected(false);
        findViewById(R.id.thursday_layout_id).setSelected(false);
        findViewById(R.id.friday_layout_id).setSelected(false);
        findViewById(R.id.saturday_layout_id).setSelected(false);
        findViewById(R.id.sunday_layout_id).setSelected(false);
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public void closeActivity() {
        finish();
    }
}