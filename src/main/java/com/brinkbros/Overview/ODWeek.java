package com.brinkbros.Overview;

import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.WEEK_OF_YEAR;

/**
 *
 * @author ebrin
 */
public class ODWeek extends ArrayList<OverviewDate> {

    private int weekOfMonth;
    private Calendar firstDay;
    private Calendar lastDay;
    private final Calendar today;

    protected ODWeek(Calendar cal) {
        super();
        today = Calendar.getInstance();

        firstDay = (Calendar) cal.clone();

        for (int i = 0; i < 7; i++) {
            add(new OverviewDate(cal, cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)));
            cal.roll(DAY_OF_WEEK, true);
        }

        lastDay = (Calendar) cal.clone();
        cal.add(WEEK_OF_YEAR, 1);
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
