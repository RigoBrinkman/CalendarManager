package com.brinkbros.overview;

import com.brinkbros.CalmanDate;
import com.brinkbros.CalmanPeriod;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.WEEK_OF_YEAR;
import java.util.HashMap;
import java.util.Properties;

public final class OverviewWeek extends CalmanPeriod {

  public static OverviewWeek build(Calendar cal, Properties dbProps) {
    Calendar firstDay = (Calendar) cal.clone();
    ArrayList dateList = new ArrayList();
    HashMap dateMap = new HashMap();

    for (int i = 0; i < 7; i++) {
      CalmanDate date = new CalmanDate(cal);
      dateList.add(date);
      dateMap.put(date.get(Calendar.DAY_OF_YEAR), date);
      cal.roll(DAY_OF_WEEK, true);
    }

    Calendar lastDay = (Calendar) cal.clone();
    cal.add(WEEK_OF_YEAR, 1);
    return new OverviewWeek(firstDay, lastDay, dateList, dateMap, dbProps);
  }

  private OverviewWeek(Calendar firstDay, Calendar lastDay, ArrayList dateList, HashMap dateMap, Properties dbProps) {
    super(firstDay, lastDay, dateList, dateMap, dbProps);

  }

  @Override
  public CalmanDate getDate(int key) {
    return dateMap.get(key);
  }

}
