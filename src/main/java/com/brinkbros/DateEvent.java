package com.brinkbros;

import java.io.Serializable;
import java.util.Calendar;

public final class DateEvent implements Serializable {

    private final int id;
    private final Calendar calendar;
    private final String title;
    private final String description;
    private final int category;
    private final int type;
    private final int status;
    private final EventColor color;

    protected DateEvent(int id, Calendar calendar, String title, String description, int category, int type, int status) {
        this.id = id;
        this.calendar = calendar;
        this.title = title;
        this.description = description;
        this.category = category;
        this.type = type;
        this.status = status;
        this.color = EventColor.getColor(category);
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public EventColor getColor() {
        return color;
    }
    
    public String getDescription(){
        return description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return title;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb
                .append(title)
                .append('(')
                .append(color)
                .append(')');
        return sb.toString();
    }

    public enum EventColor {

        BLUE("border-color: blue; background-color: lightblue;"),
        GREEN("border-color: green; background-color: lightgreen;"),
        RED("border-color: red; background-color: lightcoral;"),
        YELLOW("border-color: yellow; background-color: lightyellow;");

        private final String styleAttr;
        
        public static EventColor getColor(int category){
            switch(category){
                case Category.CATEGORY1:
                    return BLUE;
                case Category.CATEGORY2:
                    return GREEN;
                case Category.CATEGORY3:
                    return RED;
                case Category.CATEGORY4:
                    return YELLOW;
                default:
                    throw new IllegalArgumentException("Unknown category " + category);
            }
        }

        private EventColor(String styleAttr) {
            this.styleAttr = styleAttr;
        }

        public String getStyleAttr() {
            return styleAttr;
        }
    }
    public static class Category{
        public static final int CATEGORY1 = 101;
        public static final int CATEGORY2 = 102;
        public static final int CATEGORY3 = 103;
        public static final int CATEGORY4 = 104;
    }
    
    public static class Type{
        public static final int EVENT = 201;
        public static final int DOSSIER = 202;
    }
    
    public static class Status{
        public static final int PREPARATION = 301;
        public static final int ACTIVE = 302;
        public static final int ON_HOLD = 303;
        public static final int COMPLETED = 304;
        public static final int ABANDONED = 305;
    
    }
}
