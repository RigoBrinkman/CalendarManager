package com.brinkbros;

import com.brinkbros.DateEvent;
import com.brinkbros.BasePage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import java.util.List;
import java.util.Objects;

public class CalmanDate implements Serializable {

    Calendar calendar;
    List<DateEvent> events;
    boolean isToday;
    boolean curentMonth;

    public CalmanDate(Calendar calendar) {
        this.isToday = calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR);
        this.calendar = (Calendar) calendar.clone();
        events = new ArrayList();
        isToday = false;

    }

    public void addEvent(DateEvent de) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CalmanDate other = (CalmanDate) obj;
        if (!Objects.equals(this.calendar, other.calendar)) {
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

    public boolean isToday() {
        return isToday;
    }

}
