package com.app.tributum.activity.salary;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.RequestSentListener;
import com.app.tributum.model.EmailBody;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.utils.CalendarUtils;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.ValidationUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SalaryPresenterImpl implements SalaryPresenter, RequestSentListener {

    final private SalaryView view;

    public SalaryPresenterImpl(SalaryView view) {
        this.view = view;
        this.view.setWeeklyCalendarDates(CalendarUtils.getCurrentWeek());
        setCurrentDayForWeeklySalary();
        this.view.setWeeklyType();
    }

    private void setCurrentDayForWeeklySalary() {
        int dayInWeek = CalendarUtils.getCurrentDayInt();
        view.deselectDays();
        switch (dayInWeek) {
            case 1:
                view.setMonday();
                break;
            case 2:
                view.setTuesday();
                break;
            case 3:
                view.setWednesday();
                break;
            case 4:
                view.setThursday();
                break;
            case 5:
                view.setFriday();
                break;
            case 6:
                view.setSaturday();
                break;
            case 7:
                view.setSunday();
                break;
        }
    }

    private void sendInquiry(String name, String email, String fullName, String pps,
                             String gross, String net, String hours, String overtime,
                             String subsistance, String bankHoliday, String holiday) {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.TRIBUTUM_EMAIL, generateInternalEmailMessage(name, email, fullName)));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (!response.isSuccessful()) {
                    view.showToast(TributumApplication.getInstance().getResources().getString(R.string.something_went_wrong));
                    view.hideLoadingScreen();
                } else {
                    view.hideLoadingScreen();
                    view.showRequestSent();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                view.hideLoadingScreen();
                view.showToast(TributumApplication.getInstance().getResources().getString(R.string.something_went_wrong));
            }
        });
    }

    private String generateInternalEmailMessage(String name, String email, String description) {
        String formattedString = name.toUpperCase();
        formattedString = formattedString.replaceAll(" ", "%20");

        String resultString = name + " requested an inquiry:\n\n'" + description + "'";
        resultString += "\n\nPlease respond to: " + email;

        return resultString;
    }

    @Override
    public void onSendClick(String name, String email, String fullName, String pps,
                            String gross, String net, String hours, String overtime,
                            String subsistance, String bankHoliday, String holiday) {
        if (view == null)
            return;

        if (name.equals("")) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_name));
            view.setFocusOnName();
        } else if (!ValidationUtils.isEmailValid(email)) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_correct_email));
            view.setFocusOnEmail();
        } else if (fullName.equals("")) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_full_name));
            view.setFocusOnFullName();
        } else if (pps.equals("")) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_pps));
            view.setFocusOnPps();
        } else if (gross.equals("")) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_gross));
            view.setFocusOnGross();
        } else if (net.equals("")) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_net));
            view.setFocusOnNet();
        } else if (hours.equals("")) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_hours));
            view.setFocusOnHours();
        } else if (overtime.equals("")) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_overtime));
            view.setFocusOnOvertime();
        } else if (subsistance.equals("")) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_subsistance));
            view.setFocusOnSubsistance();
        } else if (bankHoliday.equals("")) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_bank_holiday));
            view.setFocusOnBankHoliday();
        } else if (holiday.equals("")) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_holiday));
            view.setFocusOnHoliday();
        } else {
            view.hideKeyboard();
            view.showLoadingScreen();
            sendInquiry(name, email, fullName, pps, gross, net, hours, overtime, subsistance, bankHoliday, holiday);
        }
    }

    @Override
    public void onMondayClick() {
        if (view == null)
            return;

        view.deselectDays();
        view.setMonday();
        view.setDate(CalendarUtils.getCurrentWeek()[0] + CalendarUtils.getCurrentMonthAndYear());
    }

    @Override
    public void onTuesdayClick() {
        if (view == null)
            return;

        view.deselectDays();
        view.setTuesday();
        view.setDate(CalendarUtils.getCurrentWeek()[1] + CalendarUtils.getCurrentMonthAndYear());
    }

    @Override
    public void onWednesdayClick() {
        if (view == null)
            return;

        view.deselectDays();
        view.setWednesday();
        view.setDate(CalendarUtils.getCurrentWeek()[2] + CalendarUtils.getCurrentMonthAndYear());
    }

    @Override
    public void onThursdayClick() {
        if (view == null)
            return;

        view.deselectDays();
        view.setThursday();
        view.setDate(CalendarUtils.getCurrentWeek()[3] + CalendarUtils.getCurrentMonthAndYear());
    }

    @Override
    public void onFridayClick() {
        if (view == null)
            return;

        view.deselectDays();
        view.setFriday();
        view.setDate(CalendarUtils.getCurrentWeek()[4] + CalendarUtils.getCurrentMonthAndYear());
    }

    @Override
    public void onSaturdayClick() {
        if (view == null)
            return;

        view.deselectDays();
        view.setSaturday();
        view.setDate(CalendarUtils.getCurrentWeek()[5] + CalendarUtils.getCurrentMonthAndYear());
    }

    @Override
    public void onSundayClick() {
        if (view == null)
            return;

        view.deselectDays();
        view.setSunday();
        view.setDate(CalendarUtils.getCurrentWeek()[6] + CalendarUtils.getCurrentMonthAndYear());
    }

    @Override
    public void onWeeklyClick() {
        if (view == null)
            return;

        view.deselectDateTypes();
        view.setWeeklyType();
        view.showWeekLayout();
        view.hideMonthLayout();
    }

    @Override
    public void onMonthlyClick() {
        if (view == null)
            return;

        view.deselectDateTypes();
        view.setMonthlyType();
        view.showMonthLayout();
        view.hideWeekLayout();
    }

    @Override
    public void onFortnightlyClick() {
        if (view == null)
            return;

        view.deselectDateTypes();
        view.setFortnightlyType();
        view.showMonthLayout();
        view.hideWeekLayout();
    }

    @Override
    public void onSelectedDayChange(int year, int month, int day) {
        if (view == null)
            return;

        view.setDate(day + "/" + (month + 1) + "/" + year);
    }

    @Override
    public void onBackPressed() {
        if (view == null)
            return;

        view.closeActivity();
    }

    @Override
    public void onOkClicked() {
        if (view != null)
            view.closeActivity();
    }
}