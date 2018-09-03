package com.brinkbros;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

public class CalmanDate extends GregorianCalendar implements Serializable {

  List<CalmanEvent> events;
  boolean isToday;
  boolean curentMonth;


  public CalmanDate(Calendar calendar) {
    this(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH));
  }
  
  public CalmanDate(int year, int month, int day){
    super(year, month, day);
    this.isToday = get(DAY_OF_YEAR) == Calendar.getInstance().get(DAY_OF_YEAR) && get(YEAR) == Calendar.getInstance().get(YEAR);
    events = new ArrayList();
  }

  public void addEvent(CalmanEvent de) {
    events.add(de);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb
        .append(get(DAY_OF_MONTH))
        .append('/')
        .append(get(MONTH) + 1)
        .append('/')
        .append(get(YEAR))
        .append(isToday ? " That's today!" : "");
    for (CalmanEvent de : events) {
      sb
          .append(" ")
          .append(de);
    }
    return sb.toString();
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += get(YEAR);
    hash *= 7;
    hash += get(DAY_OF_YEAR);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CalmanDate other = (CalmanDate) obj;
    if (!super.equals(other)) {
      return false;
    }
    if (!Objects.equals(this.events, other.events)) {
      return false;
    }
    if (this.isToday != other.isToday) {
      return false;
    }
    if (this.curentMonth != other.curentMonth) {
      return false;
    }
    return true;
  }


  public int getDay() {
    return get(DAY_OF_MONTH);
  }

  public List<CalmanEvent> getEvents() {
    return events;
  }

  public String getKey() {
    StringBuilder sb = new StringBuilder();
    sb
        .append(get(DAY_OF_MONTH))
        .append('/')
        .append(get(MONTH) + 1);
    return sb.toString();
  }

  public boolean isToday() {
    return isToday;
  }

}
