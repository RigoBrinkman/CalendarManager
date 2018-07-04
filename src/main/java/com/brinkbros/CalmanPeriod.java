package com.brinkbros;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public abstract class CalmanPeriod {

    protected final Calendar firstDay;
    protected final Calendar lastDay;
    protected final ArrayList<CalmanDate> dateList;
    protected final HashMap<Integer, CalmanDate> dateMap;
    protected final Properties dbProps;

    public abstract CalmanDate getDate(int key);

    public CalmanPeriod(Calendar firstDay, Calendar lastDay, ArrayList dateList, HashMap dateMap, Properties dbProps) {
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.dbProps = dbProps;
        this.dateList = dateList;
        this.dateMap = dateMap;
    }

    public void addEvents() {
        List<DateEvent> events = new ArrayList();
        try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDBURL(), dbProps);
                ResultSet rslts = DatabaseConnector.select(conn, DatabaseConnector.Table.EVENTS, "end_date", firstDay, lastDay, "par_id")) {
            while (rslts.next()) {
                events.add(new DateEvent(
                        rslts.getInt(1), //id
                        DatabaseConnector.stringToCal(rslts.getString(2)), //calendar
                        DatabaseConnector.stringToCal(rslts.getString(3)), //calendar
                        DatabaseConnector.stringToCal(rslts.getString(4)), //calendar
                        DatabaseConnector.stringToCal(rslts.getString(5)), //calendar
                        rslts.getString(6), //title
                        rslts.getString(7), //description
                        rslts.getInt(8), //category
                        rslts.getInt(9), //type
                        rslts.getInt(10), //status
                        null //parentEvent
                ));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        for (DateEvent e : events) {
            getDate(e.getCalendar().get(Calendar.DAY_OF_YEAR)).addEvent(e);
        }

    }

    public ArrayList<CalmanDate> asList() {
        return dateList;
    }

}
