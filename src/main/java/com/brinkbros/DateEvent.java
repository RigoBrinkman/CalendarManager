package com.brinkbros;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Properties;

public final class DateEvent implements Serializable {

    private final int id;
    private final Calendar calendar;
    private final String title;
    private final String description;
    private final int category;
    private final int type;
    private final int status;
    private final EventColor color;
    Optional<ArrayList<DateEvent>> subevents;

    public DateEvent(int id, Calendar calendar, String title, String description, int category, int type, int status) {
        this.id = id;
        this.calendar = calendar;
        this.title = title;
        this.description = description;
        this.category = category;
        this.type = type;
        this.status = status;
        this.color = EventColor.getColor(category);
        this.subevents = Optional.empty();
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        switch (category) {
            case Category.CATEGORY1:
                return "Cat1";
            case Category.CATEGORY2:
                return "Cat2";
            case Category.CATEGORY3:
                return "Cat3";
            case Category.CATEGORY4:
                return "Cat4";
            default:
                return "Onbekende Categorie";
        }
    }

    public String getType() {
        switch (type) {
            case Type.EVENT:
                return "Evenement";
            case Type.DOSSIER:
                return "Dossier";
            default:
                return "Onbekend type";
        }
    }

    public String getStatus() {
        switch (status) {
            case Status.ABANDONED:
                return "Geannuleerd";
            case Status.ACTIVE:
                return "Actief";
            case Status.COMPLETED:
                return "Voltooid";
            case Status.ON_HOLD:
                return "On hold";
            case Status.PREPARATION:
                return "In voorbereiding";
            default:
                return "Onbekende status";
        }
    }

    public EventColor getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return title;
    }

    public ArrayList<DateEvent> getSubevents(Properties dbProps) throws SQLException {
        if (!subevents.isPresent()) {
            try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDBURL(), dbProps);
                    ResultSet rslts = DatabaseConnector.select(conn, DatabaseConnector.Table.SUBEVENTS, "EVENT_ID", id)) {
                ArrayList<DateEvent> aList = new ArrayList();
                while (rslts.next()) {
                    aList.add(new DateEvent(
                            rslts.getInt(2), //id
                            DatabaseConnector.stringToCal(rslts.getString(3)), //calendar
                            rslts.getString(4), //title
                            rslts.getString(5), //description
                            rslts.getInt(6), //category
                            rslts.getInt(7), //type
                            rslts.getInt(8) //status
                    ));
                }
                subevents = Optional.of(aList);
            }
        }
        return new ArrayList(subevents.get());
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

        public static EventColor getColor(int category) {
            switch (category) {
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

    public static class Category {

        public static final int CATEGORY1 = 101;
        public static final int CATEGORY2 = 102;
        public static final int CATEGORY3 = 103;
        public static final int CATEGORY4 = 104;
    }

    public static class Type {

        public static final int EVENT = 201;
        public static final int DOSSIER = 202;
    }

    public static class Status {

        public static final int PREPARATION = 301;
        public static final int ACTIVE = 302;
        public static final int ON_HOLD = 303;
        public static final int COMPLETED = 304;
        public static final int ABANDONED = 305;

    }
}
