package com.brinkbros.Overview;


import com.brinkbros.DatabaseConnector;
import com.brinkbros.DateEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.*;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_WEEK_IN_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SUNDAY;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

    public class ODMonth extends ArrayList<ODWeek> {

        private final Calendar monthCal;
        private final int year;
        private final int month;
        private final Calendar firstDay;
        private final Calendar lastDay;
        private final HashMap<Integer, OverviewDate> dates;

        public ODMonth(int year, int month, Properties dbProps) {
            super();
            monthCal = new GregorianCalendar(year, month, 1);
            monthCal.set(DAY_OF_WEEK, SUNDAY);
            monthCal.set(DAY_OF_WEEK_IN_MONTH, 1);
            monthCal.setFirstDayOfWeek(MONDAY);
            monthCal.roll(DAY_OF_WEEK, true);
            this.year = year;
            this.month = month;
            this.dates = new HashMap();

            for (int i = 0; i < 6; i++) {
                add(new ODWeek(monthCal));
            }
            for (ODWeek week : this) {
                for (OverviewDate day : week) {
                    dates.put(day.getCalendar().get(DAY_OF_YEAR), day);
                }
            }

            this.firstDay = get(0).get(0).getCalendar();
            this.lastDay = get(size() - 1).get(6).getCalendar();
            List<DateEvent> events = DatabaseConnector.getEvents(dbProps, firstDay, lastDay);
            for (DateEvent e : events) {
                dates.get(e.getCalendar().get(DAY_OF_YEAR)).addEvent(e);
            }

        }

        public boolean hasOverViewDate(Calendar calendar) {
            return dates.containsKey(calendar.get(DAY_OF_YEAR));
        }

        public OverviewDate getOverviewDate(Calendar calendar) {
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
    }
