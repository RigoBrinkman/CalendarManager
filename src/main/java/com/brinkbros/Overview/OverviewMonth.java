package com.brinkbros.Overview;

import com.brinkbros.CalmanDate;
import com.brinkbros.CalmanPeriod;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_WEEK_IN_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SUNDAY;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public final class OverviewMonth extends CalmanPeriod {

    private final int year;
    private final int month;
    private ArrayList<ArrayList<CalmanDate>> weeks;
    
    public static OverviewMonth build(int year, int month, Properties dbProps){
        Calendar monthCal = new GregorianCalendar(year, month, 1);
        monthCal.set(DAY_OF_WEEK, SUNDAY);
        monthCal.set(DAY_OF_WEEK_IN_MONTH, 1);
        monthCal.setFirstDayOfWeek(MONDAY);
        monthCal.roll(DAY_OF_WEEK, true);
        HashMap dateMap = new HashMap();
        ArrayList<ArrayList<CalmanDate>> weeks = new ArrayList();
        ArrayList<CalmanDate> dateList = new ArrayList();

        for (int i = 0; i < 6; i++) {
            weeks.add(OverviewWeek.build(monthCal, dbProps).asList());
        }
        for (ArrayList<CalmanDate> week : weeks) {
            for (CalmanDate day : week) {
                dateList.add(day);
                dateMap.put(day.getCalendar().get(DAY_OF_YEAR), day);
            }
        }

        Calendar firstDay = weeks.get(0).get(0).getCalendar();
        Calendar lastDay = weeks.get(weeks.size() - 1).get(6).getCalendar();
        
        return new OverviewMonth(firstDay, lastDay, dateList, dateMap, dbProps, year, month, weeks);
    }

    private OverviewMonth(Calendar firstDay, Calendar lastDay, ArrayList dateList, HashMap dateMap, Properties dbProps, int year, int month, ArrayList<ArrayList<CalmanDate>> weeks) {
        super(firstDay, lastDay, dateList, dateMap, dbProps);
        this.year = year;
        this.month = month;
        this.weeks = weeks;
        addEvents();
    }

    public boolean hasOverViewDate(Calendar calendar) {
        return dateMap.containsKey(calendar.get(DAY_OF_YEAR));
    }

    public CalmanDate getOverviewDate(Calendar calendar) {
        return dateMap.get(calendar.get(DAY_OF_YEAR));
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

    @Override
    public CalmanDate getDate(int key) {
        return dateMap.get(key);
    }

    ArrayList<ArrayList<CalmanDate>> weeks() {
        return weeks;
    }
}
