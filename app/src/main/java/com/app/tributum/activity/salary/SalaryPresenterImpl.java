package com.app.tributum.activity.salary;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.RequestSentListener;
import com.app.tributum.model.EmailBody;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.utils.CalendarUtils;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.ValidationUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SalaryPresenterImpl implements SalaryPresenter, RequestSentListener {

    final private SalaryView view;

    private ArrayList<CalendarDay> datesSelected = new ArrayList<>();

    private boolean fortnightly;

    private CalendarMode mode = CalendarMode.WEEKS;

    public SalaryPresenterImpl(SalaryView view) {
        this.view = view;
        this.view.setWeeklyType();
    }

    @Override
    public void onCreate() {
        if (view != null)
            view.populateInputsWithValues(
                    TributumAppHelper.getStringSetting(AppKeysValues.SALARY_PAYER),
                    TributumAppHelper.getStringSetting(AppKeysValues.SALARY_EMAIL));
    }

    private void sendInquiry(String name, String email, String fullName, String pps,
                             String rate, String hours, String overtime,
                             String subsistance, String bankHoliday, String holiday) {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.TRIBUTUM_EMAIL,
                generateInternalEmailMessage(name, email, fullName, pps, rate, hours, overtime, subsistance, bankHoliday, holiday)));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (!response.isSuccessful()) {
                    view.showToast(TributumApplication.getInstance().getResources().getString(R.string.something_went_wrong));
                    view.hideLoadingScreen();
                } else {
                    Call<Object> callForClient = api.sendEmail(new EmailBody(email,
                            generateClientEmailMessage(fullName, pps, rate, hours, overtime, subsistance, bankHoliday, holiday)));
                    callForClient.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                            if (!response.isSuccessful()) {
                                view.showToast(TributumApplication.getInstance().getResources().getString(R.string.something_went_wrong));
                                view.hideLoadingScreen();
                            } else {
                                view.hideLoadingScreen();
                                view.showRequestSent();
                                saveListToPreferences(name, email);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                            view.hideLoadingScreen();
                            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.something_went_wrong));
                        }
                    });
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

    private String generateInternalEmailMessage(String name, String email, String fullName, String pps,
                                                String rate, String hours, String overtime,
                                                String subsistance, String bankHoliday, String holiday) {
        String period;
        if (mode == CalendarMode.WEEKS)
            period = "weekly";
        else if (!fortnightly)
            period = "monthly";
        else
            period = "fortnightly";

        String resultString = "New Salary\n\n" + name + " wants to pay salary for " + fullName + " (" + pps + ")"
                + " with details:\n"
                + "- salary type: " + period + "\n"
                + "- salary date(s): " + CalendarUtils.getDatesToBeDisplayed(datesSelected) + "\n"
                + "- gross rate per hour: " + rate + "\n"
                + "- hours: " + hours + "\n"
                + "- overtime: " + overtime + "\n"
                + "- subsistance: " + subsistance + "\n"
                + "- bank holidays: " + bankHoliday + "\n"
                + "- holiday: " + holiday;
        resultString += "\n\nPlease respond to: " + email;

        return resultString;
    }

    private String generateClientEmailMessage(String fullName, String pps,
                                              String rate, String hours, String overtime,
                                              String subsistance, String bankHoliday, String holiday) {
        String period;
        if (mode == CalendarMode.WEEKS)
            period = "weekly";
        else if (!fortnightly)
            period = "monthly";
        else
            period = "fortnightly";

        String resultString = "New Salary\n\n" + "You requested a salary for " + fullName + " (" + pps + ")"
                + " with details:\n"
                + "- salary type: " + period + "\n"
                + "- salary date(s): " + CalendarUtils.getDatesToBeDisplayed(datesSelected) + "\n"
                + "- gross rate per hour: " + rate + "\n"
                + "- hours: " + hours + "\n"
                + "- overtime: " + overtime + "\n"
                + "- subsistance: " + subsistance + "\n"
                + "- bank holidays: " + bankHoliday + "\n"
                + "- holiday: " + holiday;
        resultString += "\n\nYour request will be taken care of in the shortest time possible.";

        return resultString;
    }

    @Override
    public void onSendClick(String name, String email, String fullName, String pps,
                            String rate, String hours, String overtime,
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
        } else if (!ValidationUtils.isPpsValid(pps)) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_pps));
            view.setFocusOnPps();
        } else if (datesSelected.size() == 0) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_select_date));
            view.scrollToCalendar();
        } else {
            view.hideKeyboard();
            view.showLoadingScreen();
            sendInquiry(name, email, fullName, pps, rate, hours, overtime, subsistance, bankHoliday, holiday);
        }
    }

    @Override
    public void onWeeklyClick() {
        if (view == null)
            return;

        fortnightly = false;
        view.deselectDateTypes();
        view.setWeeklyType();
        view.setCalendarSingleSelection();
        view.clearCalendarSelection();
        if (mode != CalendarMode.WEEKS) {
            mode = CalendarMode.WEEKS;
        }
        view.setDate(TributumApplication.getInstance().getString(R.string.date_you_choose));
        datesSelected = new ArrayList<>();
    }

    @Override
    public void onMonthlyClick() {
        if (view == null)
            return;

        fortnightly = false;
        view.setCalendarSingleSelection();
        setMonthLayout();
        view.setMonthlyType();
    }

    @Override
    public void onFortnightlyClick() {
        if (view == null)
            return;

        fortnightly = true;
        view.setCalendarMultipleSelection();
        setMonthLayout();
        view.setFortnightlyType();
    }

    private void setMonthLayout() {
        view.deselectDateTypes();
        view.setDate(TributumApplication.getInstance().getString(R.string.date_you_choose));
        view.clearCalendarSelection();
        if (mode != CalendarMode.MONTHS) {
            mode = CalendarMode.MONTHS;
        }
        datesSelected = new ArrayList<>();
    }

    @Override
    public void onDateSelected(CalendarDay date, boolean selected) {
        if (datesSelected.size() == 0) {
            datesSelected.add(date);
        } else if (datesSelected.size() == 1) {
            if (fortnightly) {
                if (selected) {
                    datesSelected.add(date);
                } else {
                    datesSelected.remove(date);
                }
            } else {
                datesSelected.clear();
                datesSelected.add(date);
            }
        } else if (datesSelected.size() == 2) {
            if (selected) {
                datesSelected.remove(0);
                datesSelected.add(date);
            } else {
                datesSelected.remove(date);
            }
            view.clearCalendarSelection();
            for (CalendarDay day : datesSelected)
                view.selectDate(day);
        }
        if (view != null)
            view.setDate(CalendarUtils.getDatesToBeDisplayed(datesSelected));
    }

    private void saveListToPreferences(String payer, String email) {
        TributumAppHelper.saveSetting(AppKeysValues.SALARY_PAYER, payer);
        TributumAppHelper.saveSetting(AppKeysValues.SALARY_EMAIL, email);
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