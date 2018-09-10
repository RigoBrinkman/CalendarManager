package com.brinkbros;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;

public class DatabaseConnector {
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Columns {
    Column[] value();
  }

  @Documented
  @Repeatable(Columns.class)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Column {
    int number();
    String name();
    String type();
  }

  public static class ColumnType {
    public static final String AUTO_INT = "int(Auto)";
    public static final String INT = "int";
    public static final String STRING = "String";
    public static final String DATE = "Date";
  }

  public enum Table {

    /**Contains assignment relations between {@link #EVENTS Events} and {@link #USERS Users}*/
    @Column(number = 1, name = "ASSIGN_ID", type = ColumnType.AUTO_INT)
    @Column(number = 2, name = "EVENT_ID", type = ColumnType.INT)
    @Column(number = 3, name = "USER_ID", type = ColumnType.INT)
    ASSIGNMENTS("PF_OPT_ASSIGN", true),
    /** Contains rows with 3 extra deadlines for an {@link #EVENTS Event}.*/
    @Column(number = 1, name = "DATE_ID", type = ColumnType.AUTO_INT)
    @Column(number = 2, name = "EVENT_ID", type = ColumnType.INT)
    @Column(number = 3, name = "DATE_AH", type = ColumnType.DATE)
    @Column(number = 4, name = "DATE_DR", type = ColumnType.DATE)
    @Column(number = 5, name = "DATE_IZ", type = ColumnType.DATE)
    DEADLINES("PF_OPT_DATES", true),
    /**Contains all events, including subEvents*/
    @Column(number = 1, name = "EVENT_ID", type = ColumnType.AUTO_INT)
    @Column(number = 2, name = "END_DATE", type = ColumnType.DATE)
    @Column(number = 3, name = "TITLE", type = ColumnType.STRING)
    @Column(number = 4, name = "DESCRIPTION", type = ColumnType.STRING)
    @Column(number = 5, name = "CATEGORY", type = ColumnType.INT)
    @Column(number = 6, name = "TYPE", type = ColumnType.INT)
    @Column(number = 7, name = "STATUS", type = ColumnType.INT)
    @Column(number = 8, name = "PAR_ID", type = ColumnType.INT)
    @Column(number = 9, name = "MT_ID", type = ColumnType.INT)
    @Column(number = 10, name = "TRACKER_ID", type = ColumnType.INT)
    EVENTS("PF_EVENTS", true),
    /**Contains all users, for the purpose of {@link #ASSIGNMENTS Assignments} and retrieving personal information*/
    @Column(number = 1, name = "USER_ID", type = ColumnType.AUTO_INT)
    @Column(number = 2, name = "NAME_FULL", type = ColumnType.STRING)
    @Column(number = 3, name = "MAIL", type = ColumnType.STRING)
    @Column(number = 4, name = "ROLE", type = ColumnType.INT)
    @Column(number = 5, name = "PASSWORD", type = ColumnType.STRING)
    USERS("PF_USERS", true);
    private final String name;
    private boolean autoID;

    private Table(String tableName, boolean autoID) {
      this.name = tableName;
      this.autoID = autoID;
    }

    public String getName() {
      return name;
    }

    /**
     * Returns whether the {@link #Table(java.lang.String, boolean) Table} has an auto incrementing ID column.
     * This is the first column
     */
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
  private static final String LEFT_JOIN = " LEFT JOIN ";

  static {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe"));
  }

  public static void main(String[] args) {
    Properties props = new Properties();
    props.put("user", "thecorz0_planner");
    props.put("password", "PFSPO2018test");
    props.put("useLegacyDatetimeCode", "false");
    props.put("serverTimezone", "UTC");
    

    try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDbUrl(), props)){
      Statement stmnt = conn.createStatement();
      stmnt.executeUpdate("UPDATE PF_USERS SET password = sha2(name_full, 512)");

    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  public static ResultSet select(Connection conn, Table table) throws SQLException {
    PreparedStatement stmnt = conn.prepareStatement(SELECT_FROM + table.getName());
    stmnt.closeOnCompletion();
    return stmnt.executeQuery();
  }

  public static ResultSet select(Connection conn, Table table, String column, Table table2, String column2) throws SQLException {
    PreparedStatement stmnt = conn.prepareStatement(SELECT_FROM + table.getName() + " l " + LEFT_JOIN + table2.getName() + " r " + " ON " + "l." + column + " = r." + column2);
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
    for (int i = 1; i < values.length; i++) {
      sb.append(VARIABLE);
    }

    sb.append(CLOSE);
    try (PreparedStatement stmnt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS)) {
      stmnt.closeOnCompletion();
      for (Column c : Table.class.getField(table.name()).getAnnotation(Columns.class).value()) {
        switch (c.type()) {
          case (ColumnType.AUTO_INT):
            stmnt.setNull(c.number(), Types.INTEGER);
            break;
          case (ColumnType.INT):
            if (values[c.number() - 1] != null) {
              stmnt.setInt(c.number(), Integer.parseInt(values[c.number() - 1]));
            } else {
              stmnt.setNull(c.number(), Types.INTEGER);
            }
            break;
          case (ColumnType.STRING):
          case (ColumnType.DATE):
            stmnt.setString(c.number(), values[c.number() - 1]);

            break;
          default:
            throw new IllegalArgumentException("Unknown ColumnType: " + c.type());
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
    } catch (NoSuchFieldException ex) {
      throw new Error("SQL Table Annotation missing");
    } catch (SecurityException ex) {
      throw new Error("Illegal data types found");
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
        //.append('\'')
        .append(cal.get(Calendar.YEAR))
        .append('-')
        .append(cal.get(Calendar.MONTH) < 9 ? "0" : "")
        .append(cal.get(Calendar.MONTH) + 1)
        .append('-')
        .append(cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
        .append(cal.get(Calendar.DAY_OF_MONTH)) //.append('\'')
        ;
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

  public static String getDbUrl() {
    return DB_URL;
  }

}
