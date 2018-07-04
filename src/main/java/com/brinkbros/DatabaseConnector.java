package com.brinkbros;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnector {

    public enum Table {

        ASSIGNMENTS("PF_ASSIGNMENTS", true)/*
         1  ASSIGN_ID       int(Auto)
         2  EVENT_ID        int
         3  USER_ID         int
         4  MAIN_ASSIGN     int
         */,
        EVENTS("PF_EVENTS", true) /*
         1  EVENT_ID        int(Auto)
         2  END_DATE        Date
         3  CON_AH_DATE     Date
         4  CON_DI_DATE     Date
         5  DEF_DATE        Date
         6  TITLE           String
         7  DESCRIPTION     String
         8  CATEGORY        int
         9  TYPE            int
         10 STATUS          int
         11 PAR_ID          int
         */,
        USERS("PF_USERS", true)/*
         1  USER_ID         int(Auto)
         2  NAME_FULL       String
         3  MAIL            String
         */;
        private final String name;
        private boolean autoID;

        private Table(String tableName, boolean autoID) {
            this.name = tableName;
            this.autoID = autoID;
        }

        public String getName() {
            return name;
        }

        public boolean hasAutoID() {
            return autoID;
        }
    };

    private static final int PORT = 3306;
    private static final String IP_ADDRESS = "162.241.219.107";
    private static final String DB_NAME = "thecorz0_Planner";
    private static final String DB_URL = "jdbc:mysql://" + IP_ADDRESS + ":" + PORT + "/" + DB_NAME;

    private static final String INSERT_INTO = "insert into ";
    private static final String VALUES = " values (?";
    private static final String VARIABLE = ",?";
    private static final String CLOSE = ")";
    private static final String SELECT_FROM = "select * from ";
    private static final String WHERE = " where ";
    private static final String IS = " = ";
    private static final String IS_NULL = " is null";
    private static final String BETWEEN = " between ";
    private static final String AND = " and ";
    private static final String UPDATE = "update ";
    private static final String SET = " set ";

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe"));
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props = new Properties();
        props.put("user", "thecorz0_planner");
        props.put("password", "PFSPO2018test");
        props.put("useLegacyDatetimeCode", "false");
        props.put("serverTimezone", "UTC");
        
    }

    public static ResultSet select(Connection conn, Table table) throws SQLException {
        PreparedStatement stmnt = conn.prepareStatement(SELECT_FROM + table.getName());
        stmnt.closeOnCompletion();
        return stmnt.executeQuery();
    }

    public static ResultSet select(Connection conn, Table table, String columnName, String value) throws SQLException {
        PreparedStatement stmnt = conn.prepareStatement(SELECT_FROM + table.getName() + WHERE + columnName + IS + '\'' + value + '\'');
        stmnt.closeOnCompletion();
        return stmnt.executeQuery();
    }

    public static ResultSet select(Connection conn, Table table, String columnName, int value) throws SQLException {
        PreparedStatement stmnt = conn.prepareStatement(SELECT_FROM + table.getName() + WHERE + columnName + IS + value);
        stmnt.closeOnCompletion();
        return stmnt.executeQuery();
    }

    public static ResultSet select(Connection conn, Table table, String columnName, Calendar from, Calendar to) throws SQLException {
        PreparedStatement stmnt = conn.prepareStatement(SELECT_FROM + table.getName() + WHERE + columnName + BETWEEN + calToString(from) + AND + calToString(to));
        stmnt.closeOnCompletion();
        return stmnt.executeQuery();
    }

    public static ResultSet select(Connection conn, Table table, String columnName, Calendar from, Calendar to, String notNullColumn) throws SQLException {
        PreparedStatement stmnt = conn.prepareStatement(SELECT_FROM + table.getName() + WHERE + columnName + BETWEEN + calToString(from) + AND + calToString(to) + AND + notNullColumn + IS_NULL);
        stmnt.closeOnCompletion();
        return stmnt.executeQuery();
    }

    public static int insert(Connection conn, Table table, String[] values) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb
                .append(INSERT_INTO)
                .append(table.getName())
                .append(VALUES);
        for (int i = table.hasAutoID() ? 0 : 1; i < values.length; i++) {
            sb.append(VARIABLE);
        }

        sb.append(CLOSE);
        try (PreparedStatement stmnt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS)) {
            stmnt.closeOnCompletion();
            if (table.hasAutoID()) {
                stmnt.setNull(1, Types.INTEGER);
                for (int i = 0; i < values.length; i++) {
                    if (values[i] == null || values[i].equals("")) {
                        stmnt.setNull(i + 2, Types.VARCHAR);
                    } else {
                        stmnt.setString(i + 2, values[i]);
                    }
                }
            } else {
                for (int i = 0; i < values.length; i++) {
                    if (values[i] == null || values[i].equals("")) {
                        stmnt.setNull(i + 1, Types.INTEGER);
                    } else {
                        stmnt.setString(i + 1, values[i]);
                    }
                }
            }
            stmnt.execute();
            if (table.hasAutoID()) {
                try (ResultSet rslts = stmnt.getGeneratedKeys()) {
                    rslts.next();
                    return rslts.getInt(1);
                }
            } else {
                return -1;
            }
        }
    }

    public static void update(Connection conn, Table table, String[] columns, String[] values, String idColumn, int id) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb
                .append(UPDATE)
                .append(table.getName())
                .append(SET)
                .append(columns[0])
                .append(" = ")
                .append('\'')
                .append(values[0])
                .append('\'');

        if (columns.length > 1) {
            for (int i = 1; i < columns.length; i++) {
                sb
                        .append(", ")
                        .append(columns[i])
                        .append(" = ")
                        .append('\'')
                        .append(values[i])
                        .append('\'');

            }
        }
        sb
                .append(WHERE)
                .append(idColumn)
                .append(IS)
                .append(id);
        System.out.println(sb.toString());

        try (PreparedStatement stmnt = conn.prepareStatement(sb.toString())) {
            stmnt.executeUpdate();
        }
    }

    public static String calToString(Calendar cal) {
        StringBuilder sb = new StringBuilder();
        sb
                .append('\'')
                .append(cal.get(Calendar.YEAR))
                .append('-')
                .append(cal.get(Calendar.MONTH) < 9 ? "0" : "")
                .append(cal.get(Calendar.MONTH) + 1)
                .append('-')
                .append(cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
                .append(cal.get(Calendar.DAY_OF_MONTH))
                .append('\'');
        return sb.toString();
    }

    public static Calendar stringToCal(String str) {
        if (str == null || str.toLowerCase().equals("null") || str.equals("")) {
            return null;
        }
        Calendar cal = new GregorianCalendar(
                Integer.parseInt(str.substring(0, 4)),
                Integer.parseInt(str.substring(5, 7)) - 1,
                Integer.parseInt(str.substring(8, 10)));
        return cal;
    }

    public static String getDBURL() {
        return DB_URL;
    }

}
