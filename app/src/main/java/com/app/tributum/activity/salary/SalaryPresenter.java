package com.app.tributum.activity.salary;

public interface SalaryPresenter {

    void onSendClick(String name, String email, String fullName, String pps,
                     String gross, String net, String hours, String overtime,
                     String subsistance, String bankHoliday, String holiday);

    void onBackPressed();

    void onMondayClick();

    void onTuesdayClick();

    void onWednesdayClick();

    void onThursdayClick();

    void onFridayClick();

    void onSaturdayClick();

    void onSundayClick();

    void onWeeklyClick();

    void onMonthlyClick();

    void onFortnightlyClick();

    void onSelectedDayChange(int year, int month, int day);
}