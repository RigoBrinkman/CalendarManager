package com.brinkbros;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import java.util.Properties;

public final class DateEvent implements Serializable {

    private final int id;
    private final Calendar calendar;
    private final String title;
    private final String description;
    private final boolean hasExtraDeadlines;
    private final Calendar conAhCal;
    private final Calendar conDiCal;
    private final Calendar defCal;
    private final int category;
    private final int type;
    private final int status;
    private final EventColor color;

    private ArrayList<DateEvent> subevents;
    private DateEvent parentEvent;
    private ArrayList<CalmanAssignment> assignments;

    public DateEvent(int id, Calendar calendar, Calendar conAhCal, Calendar conDiCal, Calendar defCal, String title, String description, int category, int type, int status, DateEvent parentEvent) {
        this.id = id;
        hasExtraDeadlines = conAhCal != null;
        this.calendar = calendar;
        this.conAhCal = conAhCal;
        this.conDiCal = conDiCal;
        this.defCal = defCal;
        this.title = title;
        this.description = description;
        this.category = category;
        this.type = type;
        this.status = status;
        this.color = EventColor.getColor(category);
        this.parentEvent = parentEvent;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public Optional<Calendar> getConAhCal() {
        if (conAhCal == null) {
            return Optional.empty();
        } else {
            return Optional.of(conAhCal);
        }
    }

    public Optional<Calendar> getConDiCal() {
        if (conDiCal == null) {
            return Optional.empty();
        } else {
            return Optional.of(conDiCal);
        }
    }

    public Optional<Calendar> getDefCal() {
        if (defCal == null) {
            return Optional.empty();
        } else {
            return Optional.of(defCal);
        }
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

    public int getCategoryInt() {
        return category;
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

    public int getTypeInt() {
        return type;
    }

    public String getStatus() {
        switch (status) {
            case Status.DEFINITIEF:
                return "Definitief";
            case Status.CONCEPT_AH:
                return "Concept bij afdelingshoofd";
            case Status.CONCEPT_DR:
                return "Concept bij directeur";
            case Status.CONCEPT_BH:
                return "Concept bij behandelaar";
            case Status.IN_BEHANDELING:
                return "In behandeling";
            case Status.VOLTOOID:
                return "Voltooid";
            case Status.GEANNULEERD:
                return "Geannuleerd";
            default:
                return "Onbekende status";
        }
    }

    public int getStatusInt() {
        return status;
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

    public DateEvent getParentEvent() {
        return parentEvent;
    }

    public ArrayList<CalmanAssignment> getAssignments(Properties dbProps) throws SQLException {
        if (assignments == null) {
            try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDBURL(), dbProps);
                    ResultSet rslts = DatabaseConnector.select(conn, DatabaseConnector.Table.ASSIGNMENTS, "EVENT_ID", id)) {
                ArrayList<CalmanAssignment> aList = new ArrayList();
                while (rslts.next()) {
                    aList.add(new CalmanAssignment(
                            rslts.getInt(1),
                            rslts.getInt(2),
                            CalmanUser.getUser(rslts.getInt(3)),
                            rslts.getInt(4)
                    ));

                    assignments = aList;
                }
            }
        }
        return assignments;
    }

    public ArrayList<CalmanAssignment> getAssignments() {
        if (assignments != null) {
            return assignments;
        } else {
            throw new IllegalArgumentException("Assignments have not yet been retrieved from database");
        }
    }

    public ArrayList<DateEvent> getSubevents(Properties dbProps) throws SQLException {
        if (subevents == null) {
            try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDBURL(), dbProps);
                    ResultSet rslts = DatabaseConnector.select(conn, DatabaseConnector.Table.EVENTS, "PAR_ID", id)) {
                ArrayList<DateEvent> aList = new ArrayList();
                while (rslts.next()) {
                    aList.add(new DateEvent(
                            rslts.getInt(1), //id
                            DatabaseConnector.stringToCal(rslts.getString(2)), //calendar
                            DatabaseConnector.stringToCal(rslts.getString(3)) == null ? null : DatabaseConnector.stringToCal(rslts.getString(3)), //calendar
                            DatabaseConnector.stringToCal(rslts.getString(4)) == null ? null : DatabaseConnector.stringToCal(rslts.getString(4)), //calendar
                            DatabaseConnector.stringToCal(rslts.getString(5)) == null ? null : DatabaseConnector.stringToCal(rslts.getString(5)), //calendar
                            rslts.getString(6), //title
                            rslts.getString(7), //description
                            rslts.getInt(8), //category
                            rslts.getInt(9), //type
                            rslts.getInt(10), //status
                            this //parentEvent
                    ));
                }
                subevents = aList;
            }
        }
        return new ArrayList(subevents);
    }

    public boolean hasExtraDeadlines() {
        return hasExtraDeadlines;
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

        public static final int IN_BEHANDELING = 301;
        public static final int CONCEPT_AH = 302;
        public static final int CONCEPT_BH = 303;
        public static final int CONCEPT_DR = 304;
        public static final int DEFINITIEF = 305;
        public static final int VOLTOOID = 306;
        public static final int GEANNULEERD = 307;

    }
}
