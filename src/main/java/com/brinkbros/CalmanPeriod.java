package com.brinkbros;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
    List<CalmanEvent> events = new ArrayList();
    try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDbUrl(), dbProps);
        ResultSet rslts = conn.createStatement().executeQuery(
            "SELECT * "
            + "FROM PF_EVENTS l "
            + "LEFT JOIN PF_OPT_DATES r "
            + "ON l.event_id = r.event_id "
            + "WHERE l.end_date "
            + "BETWEEN '" + DatabaseConnector.calToString(firstDay)
            + "' AND '" + DatabaseConnector.calToString(lastDay) + "'"
            + "AND l.par_id is NULL "
            + "AND NOT l.status = 307")) {
      //ResultSet rslts = DatabaseConnector.select(conn, DatabaseConnector.Table.EVENTS, "end_date", firstDay, lastDay, "par_id")) {
      CalmanUser.initialize(conn);
      while (rslts.next()) {
        events.add(new CalmanEvent(
            rslts.getInt(1), //id
            DatabaseConnector.stringToCal(rslts.getString(2)), //calendar
            rslts.getString(3), //title
            rslts.getString(4), //description
            rslts.getInt(5), //category
            rslts.getInt(6), //type
            rslts.getInt(7), //status
            null, //parentEvent id
            CalmanUser.getUser(rslts.getInt(9)), //MT id
            CalmanUser.getUser(rslts.getInt(10)), //Trekker id
            DatabaseConnector.stringToCal(rslts.getString(13)), //deadline1(nullable)
            DatabaseConnector.stringToCal(rslts.getString(14)), //deadline2(nullable)
            DatabaseConnector.stringToCal(rslts.getString(15)) //deadline3(nullable)
        ));
      }
    } catch (SQLException ex) {
      throw new RuntimeException(ex.getMessage());
    }
    for (CalmanEvent e : events) {
      getDate(e.getCalendar().get(Calendar.DAY_OF_YEAR)).addEvent(e);
    }

  }

  public ArrayList<CalmanDate> asList() {
    return dateList;
  }

}
