package com.sodiumtracker.models;

import java.util.Calendar;

public class ScheduleModel {

    Calendar calendar;
    boolean isDate = true;

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public boolean isDate() {
        return isDate;
    }

    public void setDate(boolean date) {
        isDate = date;
    }
}
