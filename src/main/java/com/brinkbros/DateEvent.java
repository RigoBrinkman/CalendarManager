package com.brinkbros;

import java.io.Serializable;
import java.util.Calendar;
import static java.util.Calendar.DAY_OF_MONTH;

public class DateEvent implements Serializable {

    private String name;
    private Calendar calendar;
    private EventColor color;

    public enum EventColor {

        BLUE("color: blue;"),
        GREEN("color: green;"),
        RED("color: red;"),
        YELLOW("color: yellow;");

        private String styleAttr;

        private EventColor(String styleAttr) {
            this.styleAttr = styleAttr;
        }

        public String getStyleAttr() {
            return styleAttr;
        }
    }

    public DateEvent(String name, Calendar calendar, EventColor color) {
        this.name = name;
        this.calendar = calendar;
        this.color = color;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public EventColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb
                .append(name)
                .append('(')
                .append(color)
                .append(')');
        return sb.toString();
    }
}
