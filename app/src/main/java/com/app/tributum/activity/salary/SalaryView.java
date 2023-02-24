package com.app.tributum.activity.salary;

public interface SalaryView {

    void showToast(String message);

    void closeActivity();

    void setFocusOnName();

    void setFocusOnEmail();

    void setFocusOnFullName();

    void setFocusOnPps();

    void setFocusOnGross();

    void setFocusOnNet();

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

    void setWeeklyCalendarDates(String[] currentWeek);

    void setMonday();

    void setTuesday();

    void setWednesday();

    void setThursday();

    void setFriday();

    void setSaturday();

    void setSunday();

    void deselectDays();

    void setWeeklyType();

    void setMonthlyType();

    void setFortnightlyType();

    void deselectDateTypes();

    void showWeekLayout();

    void hideWeekLayout();

    void showMonthLayout();

    void hideMonthLayout();
}