package com.app.tributum.activity.salary;

import com.prolificinteractive.materialcalendarview.CalendarDay;

public interface SalaryView {

    void showToast(String message);

    void closeActivity();

    void setFocusOnName();

    void setFocusOnEmail();

    void setFocusOnFullName();

    void setFocusOnPps();

    void setFocusOnHours();

    void setFocusOnOvertime();

    void setFocusOnSubsistance();

    void setFocusOnBankHoliday();

    void setFocusOnHoliday();

    void hideKeyboard();

    void showLoadingScreen();

    void hideLoadingScreen();

    void showRequestSent();

    void setDate(String date);

    void setWeeklyType();

    void setMonthlyType();

    void setFortnightlyType();

    void deselectDateTypes();

    void setCalendarSingleSelection();

    void setCalendarMultipleSelection();

    void clearCalendarSelection();

    void selectDate(CalendarDay date);

    void scrollToCalendar();

    void populateInputsWithValues(String payerName, String payerEmail);
}