package com.app.tributum.activity.salary;

import com.prolificinteractive.materialcalendarview.CalendarDay;

public interface SalaryPresenter {

    void onSendClick(String name, String email, String fullName, String pps,
                     String gross, String net, String hours, String overtime,
                     String subsistance, String bankHoliday, String holiday);

    void onBackPressed();

    void onWeeklyClick();

    void onMonthlyClick();

    void onFortnightlyClick();

    void onDateSelected(CalendarDay date, boolean selected);
}