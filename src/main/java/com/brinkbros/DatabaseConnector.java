package com.brinkbros;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

public class DatabaseConnector {

    private static final int PORT = 3306;
    private static final String IP_ADDRESS = "162.241.219.107";
    private static final String DB_NAME = "thecorz0_Planner";
    private static final String DB_URL = "jdbc:mysql://" + IP_ADDRESS + ":" + PORT + "/" + DB_NAME;
    private static final String INSERT_EVENT_QUERY = "INSERT INTO `PF_EVENTS`(`END_DATE`, `TITLE`, `DESCRIPTION`, `CATEGORY`, `TYPE`, `STATUS`) VALUES (?,?,?,?,?,?)";
    private static final DateEvent ERROR_EVENT = new DateEvent(-1, Calendar.getInstance(), "ERROR", "ERROR", 101, 201, 301);

    /*
     1   EVENT_ID        int
     2   END_DATE        Date
     3   TITLE           String
     4   Description     String
     5   CATEGORY        int
     6   TYPE            int
     7   STATUS          int
     */
    private static final String SELECT_EVENT_QUERY = "SELECT * FROM `PF_EVENTS`";
    private static final String SELECT_EVENT_BETWEEN_QUERY = "SELECT * FROM `PF_EVENTS` WHERE `END_DATE` BETWEEN ? AND ?";

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe"));
    }

    public static void main(String[] args) {
    }

    private static String calToString(Calendar cal) {
        StringBuilder sb = new StringBuilder();
        sb
                .append(cal.get(Calendar.YEAR))
                .append('-')
                .append(cal.get(Calendar.MONTH) < 10 ? "0" : "")
                .append(cal.get(Calendar.MONTH))
                .append('-')
                .append(cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
                .append(cal.get(Calendar.DAY_OF_MONTH));
        return sb.toString();
    }

    private static Calendar stringToCal(String str) {
        Calendar cal = new GregorianCalendar(
                Integer.parseInt(str.substring(0, 4)),
                Integer.parseInt(str.substring(5, 7)),
                Integer.parseInt(str.substring(8, 10)));
        return cal;
    }

    private static int insertEvent(Connection con, String endDate, String title, String description, int category, int type, int status) {
        try (PreparedStatement stmnt = con.prepareStatement(INSERT_EVENT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            stmnt.setString(1, endDate);
            stmnt.setString(2, title);
            stmnt.setString(3, description);
            stmnt.setInt(4, category);
            stmnt.setInt(5, type);
            stmnt.setInt(6, status);
            stmnt.execute();
            try (ResultSet rslts = stmnt.getGeneratedKeys()) {
                rslts.next();
                return (int) rslts.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static List<DateEvent> getEvents(Properties props) {
        List<DateEvent> events = new LinkedList();
        try (Connection con = DriverManager.getConnection(DB_URL, props);
                PreparedStatement stmnt = con.prepareStatement(SELECT_EVENT_QUERY);
                ResultSet rslts = stmnt.executeQuery()) {
            while (rslts.next()) {
                events
                        .add(new DateEvent(
                                        rslts.getInt(1), //id
                                        stringToCal(rslts.getString(2)), //date
                                        rslts.getString(3), //title
                                        rslts.getString(4), //description
                                        rslts.getInt(5), //category
                                        rslts.getInt(6), //type
                                        rslts.getInt(7)));                  //status
            }
        } catch (SQLException e) {
            events.add(ERROR_EVENT);
        }
        return events;
    }

    public static List<DateEvent> getEvents(Properties props, Calendar firstDay, Calendar lastDay) {
        List<DateEvent> events = new ArrayList();
        try (Connection con = DriverManager.getConnection(DB_URL, props);
                PreparedStatement stmnt = con.prepareStatement(SELECT_EVENT_BETWEEN_QUERY)) {
            stmnt.setString(1, calToString(firstDay));
            stmnt.setString(2, calToString(lastDay));
            try (ResultSet rslts = stmnt.executeQuery()) {
                while (rslts.next()) {
                    events.add(new DateEvent(
                            rslts.getInt(1), //id
                            stringToCal(rslts.getString(2)), //date
                            rslts.getString(3), //title
                            rslts.getString(4), //description
                            rslts.getInt(5), //category
                            rslts.getInt(6), //type
                            rslts.getInt(7)));                  //status
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            events.add(new DateEvent(-1, Calendar.getInstance(), e.getMessage(), "ERROR", 101, 201, 301));
        }
        return events;
    }
}
