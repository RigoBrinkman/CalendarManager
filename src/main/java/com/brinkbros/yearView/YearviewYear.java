package com.brinkbros.yearView;

import com.brinkbros.CalmanDate;
import com.brinkbros.CalmanPeriod;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class YearviewYear extends CalmanPeriod {

    public static YearviewYear build(int year, Properties dbProps) {
        Calendar firstDay = new GregorianCalendar(year, 0, 1);
        Calendar lastDay = new GregorianCalendar(year, 11, 31);
        ArrayList<CalmanDate> dateList = new ArrayList();
        HashMap<Integer, CalmanDate> dateMap = new HashMap();
        ArrayList<ArrayList<CalmanDate>> months = new ArrayList();
        Calendar rollCal = (GregorianCalendar) firstDay.clone();
        for (int i = 0; i < 12; i++) {
            ArrayList<CalmanDate> month = new ArrayList();
            for (int j = 0; j < rollCal.getActualMaximum(Calendar.DAY_OF_MONTH); j++) {
                CalmanDate date = new CalmanDate(rollCal);
                month.add(date);
                dateList.add(date);
                dateMap.put(date.getCalendar().get(Calendar.DAY_OF_YEAR), date);
                rollCal.roll(Calendar.DAY_OF_MONTH, true);
            }
            months.add(month);
            rollCal.roll(Calendar.MONTH, true);
        }

        return new YearviewYear(firstDay, lastDay, dateList, dateMap, dbProps, year, months);

    }

    static String getMonthName(int index) {
        switch(index){
            case 0:
                return "Januari";
            case 1:
                return "Februari";
            case 2:
                return "Maart";
            case 3:
                return "April";
            case 4:
                return "Mei";
            case 5:
                return "Juni";
            case 6:
                return "Juli";
            case 7:
                return "Augustus";
            case 8:
                return "September";
            case 9:
                return "Oktober";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                return "Onbekende maand";
                
        }
    }
    private final int year;
    private final ArrayList<ArrayList<CalmanDate>> months;

    private YearviewYear(Calendar firstDay, Calendar lastDay, ArrayList dateList, HashMap dateMap, Properties dbProps, int year, ArrayList<ArrayList<CalmanDate>> months) {
        super(firstDay, lastDay, dateList, dateMap, dbProps);
        this.year = year;
        this.months = months;
        addEvents();
    }

    public ArrayList<ArrayList<CalmanDate>> getMonths() {
        return months;
    }

    @Override
    public CalmanDate getDate(int key) {
        return dateMap.get(key);
    }

}
