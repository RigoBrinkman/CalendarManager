package com.brinkbros;

import java.io.Serializable;
import java.util.Calendar;

public class DateEvent implements Serializable {

    private final String name;
    private final Calendar calendar;
    private final EventColor color;
    private final int id;

    protected DateEvent(String name, Calendar calendar, EventColor color, int id) {
        this.name = name;
        this.calendar = calendar;
        this.color = color;
        this.id = id;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public EventColor getColor() {
        return color;
    }

    public int getId() {
        return id;
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

    public enum EventColor {

        BLUE("color: blue;"),
        GREEN("color: green;"),
        RED("color: red;"),
        YELLOW("color: yellow;");

        private final String styleAttr;

        private EventColor(String styleAttr) {
            this.styleAttr = styleAttr;
        }

        public String getStyleAttr() {
            return styleAttr;
        }
    }
}
