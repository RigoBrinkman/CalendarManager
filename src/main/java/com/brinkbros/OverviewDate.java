package com.brinkbros;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.WEEK_OF_MONTH;
import static java.util.Calendar.YEAR;
import java.util.GregorianCalendar;
import java.util.List;

public class OverviewDate implements Serializable {

    Calendar calendar;
    List<DateEvent> events;
    boolean isToday;
    boolean curentMonth;

    private OverviewDate(Calendar calendar, boolean currentMonth) {
        this.curentMonth = currentMonth;
        this.calendar = new GregorianCalendar(
                calendar.get(YEAR),
                calendar.get(MONTH),
                calendar.get(DAY_OF_MONTH));
        events = new ArrayList();
        isToday = false;

    }

    private void addEvent(DateEvent de) {
        events.add(de);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb
                .append(calendar.get(DAY_OF_MONTH))
                .append('/')
                .append(calendar.get(MONTH) + 1)
                .append('/')
                .append(calendar.get(YEAR))
                .append(isToday ? " That's today!" : "");
        for (DateEvent de : events) {
            sb
                    .append(" ")
                    .append(de);
        }
        return sb.toString();
    }

    public int getDay() {
        return calendar.get(DAY_OF_MONTH);
    }

    public List<DateEvent> getEvents() {
        return events;
    }

    private int getWeekOfYear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void setToday(boolean today) {
        isToday = today;
    }

    public static WeekList getWeekList(int year, int month) {
        WeekList weeks = WeekList.getInstance(year, month);

        DatabaseDummy.getEvents(
                year, month)
                .stream()
                .forEach((DateEvent x)
                        -> weeks
                        .getOverviewDate(x.getDay())
                        .addEvent(x));

        return weeks;
    }

    public static WeekList getPreviousWeekList(int year, int month) {
        if (month == 0) {
            return getWeekList(year - 1, 11);
        }else{
            return getWeekList(year, month - 1);
        }
    }

    public static WeekList getNextWeekList(int year, int month) {
        if(month == 12){
            return getWeekList(year + 1, 0);
        }else{
            return getWeekList(year, month + 1);
        }
    }

    public static class WeekList extends ArrayList<DayList> {

        private Calendar weekListCal;
        private Calendar today;
        private int year;
        private int month;

        private WeekList(int year, int month) {
            super();
            weekListCal = new GregorianCalendar(year, month, 1);
            today = Calendar.getInstance();
            this.year = year;
            this.month = month;

            for (int i = weekListCal.getActualMinimum(WEEK_OF_MONTH) + 1; i <= weekListCal.getActualMaximum(WEEK_OF_MONTH); i++) {
                add(new DayList(i, month));
            }
            if (today.get(MONTH) == month) {
                //getOverviewDate(today.get(DAY_OF_MONTH)).setToday(true);
            }

        }

        private OverviewDate getOverviewDate(int day) {
            weekListCal.set(DAY_OF_MONTH, day);
            return get(weekListCal.get(WEEK_OF_MONTH) - 1)
                    .get(weekListCal.get(DAY_OF_WEEK) - 2);
        }
        
        public int getMonth(){
            return month;
        }
        
        public int getYear(){
            return year;
        }

        private static WeekList getInstance(int year, int month) {
            return new WeekList(year, month);
        }
    }

    public static class DayList extends ArrayList<OverviewDate> {

        private int weekOfMonth;

        private DayList(int weekOfMonth, int month) {
            super();
            this.weekOfMonth = weekOfMonth;
            Calendar cal = Calendar.getInstance();
            cal.set(MONTH, month);
            cal.set(WEEK_OF_MONTH, weekOfMonth);

            for (int i = 0; i < 7; i++) {
                add(new OverviewDate(cal, true));
                cal.roll(DAY_OF_WEEK, true);
            }
        }

        @Override
        public OverviewDate get(int i) {
            if (i < 7) {
                return super.get(i);
            } else {
                return super.get(i - 7);
            }
        }

    }
}
