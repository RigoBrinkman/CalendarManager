package com.brinkbros;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_WEEK_IN_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.WEEK_OF_MONTH;
import static java.util.Calendar.WEEK_OF_YEAR;
import static java.util.Calendar.YEAR;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class OverviewDate implements Serializable {

    Calendar calendar;
    List<DateEvent> events;
    boolean isToday;
    boolean curentMonth;

    private OverviewDate(Calendar calendar, boolean currentMonth) {
        this.curentMonth = currentMonth;
        this.calendar = (Calendar) calendar.clone();
        events = new ArrayList();
        isToday = false;

    }

    public static void main(String[] args) {
        Calendar cal1 = new GregorianCalendar(2018, 5, 1);
        System.out.println(cal1.get(DAY_OF_MONTH) + "/" + cal1.get(MONTH) + "/" + cal1.get(YEAR) + " " + cal1.getMaximum(WEEK_OF_MONTH));
        cal1.set(MONTH, 4);
        System.out.println(cal1.get(DAY_OF_MONTH) + "/" + cal1.get(MONTH) + "/" + cal1.get(YEAR) + " " + cal1.getMaximum(WEEK_OF_MONTH));
        cal1.set(MONTH, 5);
        System.out.println(cal1.get(DAY_OF_MONTH) + "/" + cal1.get(MONTH) + "/" + cal1.get(YEAR) + " " + cal1.getMaximum(WEEK_OF_MONTH));
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash += calendar.get(YEAR);
        hash *= 7;
        hash += calendar.get(DAY_OF_YEAR);
        return hash;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public int getDay() {
        return calendar.get(DAY_OF_MONTH);
    }

    public List<DateEvent> getEvents() {
        return events;
    }

    public String getKey() {
        StringBuilder sb = new StringBuilder();
        sb
                .append(calendar.get(DAY_OF_MONTH))
                .append('/')
                .append(calendar.get(MONTH) + 1);
        return sb.toString();
    }

    private int getWeekOfYear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void setToday(boolean today) {
        isToday = today;
    }

    public static ODMonth getWeekList(int year, int month) {
        ODMonth odMonth = ODMonth.getInstance(year, month);

        DatabaseDummy.getEvents(
                odMonth.getFirstDay(), odMonth.getLastDay())
                .stream()
                .forEach((DateEvent x) -> {
                    if (odMonth.hasOverViewDate(x.getCalendar())) {
                        odMonth.getOverviewDate(x.getCalendar());
                    }
                });

        return odMonth;
    }

    public static ODMonth getPreviousWeekList(int year, int month) {
        if (month == 0) {
            return getWeekList(year - 1, 11);
        } else {
            return getWeekList(year, month - 1);
        }
    }

    public static ODMonth getNextWeekList(int year, int month) {
        if (month == 12) {
            return getWeekList(year + 1, 0);
        } else {
            return getWeekList(year, month + 1);
        }
    }

    public static class ODMonth extends ArrayList<ODWeek> {

        private Calendar monthCal;
        private Calendar thisMonth;
        private Calendar today;
        private int year;
        private int month;
        private Calendar firstDay;
        private Calendar lastDay;
        private HashMap<Integer, OverviewDate> dates;

        private ODMonth(int year, int month) {
            super();
            monthCal = new GregorianCalendar(year, month, 1);
            monthCal.set(DAY_OF_WEEK, SUNDAY);
            monthCal.set(DAY_OF_WEEK_IN_MONTH, 1);
            monthCal.setFirstDayOfWeek(MONDAY);
            thisMonth = (Calendar) monthCal.clone();
            monthCal.roll(DAY_OF_WEEK, true);
            today = Calendar.getInstance();
            this.year = year;
            this.month = month;
            this.dates = new HashMap();

            do {
                add(new ODWeek(monthCal));
            } while (thisMonth.get(MONTH) == monthCal.get(MONTH));

            for (ODWeek week : this) {
                for (OverviewDate day : week) {
                    dates.put(day.getCalendar().get(DAY_OF_YEAR), day);
                }
            }

            this.firstDay = get(0).get(0).getCalendar();
            this.lastDay = get(size() - 1).get(6).getCalendar();

        }

        private boolean hasOverViewDate(Calendar calendar) {
            return dates.containsKey(calendar.get(DAY_OF_YEAR));
        }

        private OverviewDate getOverviewDate(Calendar calendar) {
            return dates.get(calendar.get(DAY_OF_YEAR));
        }

        public Calendar getFirstDay() {
            return firstDay;
        }

        public Calendar getLastDay() {
            return lastDay;
        }

        public int getMonth() {
            return month;
        }

        public int getYear() {
            return year;
        }

        private static ODMonth getInstance(int year, int month) {
            return new ODMonth(year, month);
        }
    }

    public static class ODWeek extends ArrayList<OverviewDate> {

        private int weekOfMonth;
        private Calendar firstDay;
        private Calendar lastDay;

        private ODWeek(Calendar cal) {
            super();

            firstDay = (Calendar) cal.clone();

            for (int i = 0; i < 7; i++) {
                add(new OverviewDate(cal, true));
                cal.roll(DAY_OF_WEEK, true);
            }

            lastDay = (Calendar) cal.clone();
            cal.roll(WEEK_OF_YEAR, true);
        }

        @Override
        public OverviewDate get(int i) {
            if (i < 0) {
                return super.get(i + 7);
            } else if (i >= 7) {
                return super.get(i - 7);
            } else {
                return super.get(i);
            }
        }

    }
}
