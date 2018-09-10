package com.brinkbros;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import java.util.Properties;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.Model;

public final class CalmanEvent implements Serializable {

  private final int id;
  private final Calendar calendar;
  private final String title;
  private final String description;
  private final boolean hasExtraDeadlines;
  private final Calendar deadline1;
  private final Calendar deadline2;
  private final Calendar deadline3;
  private final int category;
  private final int type;
  private final int status;
  private final EventColor color;
  private final CalmanUser mtAssignee;
  private final CalmanUser trekkerAssignee;

  private int parentId;
  private boolean checkedForParentEvent;

  private ArrayList<CalmanEvent> subevents;
  private CalmanEvent parentEvent;
  private ArrayList<CalmanUser> assignments;

  public CalmanEvent(int id, Calendar calendar, String title, String description, int category, int type, int status, CalmanEvent parentEvent, CalmanUser mtAssignee, CalmanUser trekkerAssignee, Calendar deadline1, Calendar deadline2, Calendar deadline3) {
    this.id = id;
    this.calendar = calendar;
    this.title = title;
    this.description = description;
    this.category = category;
    this.type = type;
    this.status = status;
    this.color = EventColor.getColor(category);
    this.parentEvent = parentEvent;
    this.deadline1 = deadline1;
    this.deadline2 = deadline2;
    this.deadline3 = deadline3;
    this.hasExtraDeadlines = deadline1 != null;
    this.mtAssignee = mtAssignee;
    this.trekkerAssignee = trekkerAssignee;
    this.checkedForParentEvent = true;
  }

  public CalmanEvent(int id, Calendar calendar, String title, String description, int category, int type, int status, int parentId, CalmanUser mtAssignee, CalmanUser trekkerAssignee, Calendar deadline1, Calendar deadline2, Calendar deadline3) {
    this(id, calendar, title, description, category, type, status, null, mtAssignee, trekkerAssignee, deadline1, deadline2, deadline3);
    this.parentId = parentId;
    this.checkedForParentEvent = false;
  }
  public Calendar getCalendar() {
    return calendar;
  }

  public Optional<Calendar[]> getDeadlines() {
    if (deadline1 == null) {
      return Optional.empty();
    } else {
      return Optional.of(new Calendar[]{deadline1, deadline2, deadline3});
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

  public Optional<CalmanEvent> getParentEvent(Connection conn) throws SQLException {
    if (!checkedForParentEvent) {
      try (Statement stmnt = conn.createStatement();
          ResultSet rslts = stmnt.executeQuery("SELECT * FROM PF_EVENTS l "
              + "LEFT JOIN PF_OPT_DATES r "
              + "ON l.event_id = r.event_id "
              + "WHERE l.event_id = " + String.valueOf(parentId))) {
        if (rslts.next()) {
          this.parentEvent = new CalmanEvent(
              rslts.getInt(1),
              DatabaseConnector.stringToCal(rslts.getString(2)),
              rslts.getString(3),
              rslts.getString(4),
              rslts.getInt(5),
              rslts.getInt(6),
              rslts.getInt(7),
              rslts.getInt(8),
              CalmanUser.getUser(rslts.getInt(9)),
              CalmanUser.getUser(rslts.getInt(10)),
              DatabaseConnector.stringToCal(rslts.getString(13)),
              DatabaseConnector.stringToCal(rslts.getString(14)),
              DatabaseConnector.stringToCal(rslts.getString(15)));
        }
        checkedForParentEvent = true;
      }
    }
    return this.parentEvent == null ? Optional.empty() : Optional.of(parentEvent);
  }

  public CalmanUser getMtAssignee() {
    return mtAssignee;
  }

  public CalmanUser getTrekkerAssignee() {
    return trekkerAssignee;
  }

  public ArrayList<CalmanUser> getAssignments(Connection conn) throws SQLException {
    if (assignments == null) {
      try (ResultSet rslts = DatabaseConnector.select(conn, DatabaseConnector.Table.ASSIGNMENTS, "EVENT_ID", id)) {
        ArrayList<CalmanUser> aList = new ArrayList();
        while (rslts.next()) {
          aList.add(CalmanUser.getUser(rslts.getInt(3)));

        }
        assignments = aList;
        return assignments;
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    } else {
      return assignments;
    }
  }

  public ArrayList<CalmanUser> getAssignments() {
    if (assignments != null) {
      return assignments;
    } else {
      throw new IllegalArgumentException("Assignments have not yet been retrieved from database");
    }
  }

  public ArrayList<CalmanEvent> getSubevents(Connection conn) throws SQLException {
    if (subevents == null) {
      try (ResultSet rslts = conn.createStatement().executeQuery(
          "SELECT * "
          + "FROM PF_EVENTS l "
          + "LEFT JOIN PF_OPT_DATES r "
          + "ON l.event_id = r.event_id "
          + "WHERE l.par_id = " + String.valueOf(id)
          + " AND NOT l.status = 307")) {
        //ResultSet rslts = DatabaseConnector.select(conn, DatabaseConnector.Table.EVENTS, "PAR_ID", id)) {
        ArrayList<CalmanEvent> aList = new ArrayList();
        while (rslts.next()) {
          aList.add(new CalmanEvent(
              rslts.getInt(1), //id
              DatabaseConnector.stringToCal(rslts.getString(2)), //calendar
              rslts.getString(3), //title
              rslts.getString(4), //description
              rslts.getInt(5), //category
              rslts.getInt(6), //type
              rslts.getInt(7), //status
              this, //parentEvent
              CalmanUser.getUser(rslts.getInt(9)),
              CalmanUser.getUser(rslts.getInt(10)),
              DatabaseConnector.stringToCal(rslts.getString(13)), //deadline1
              DatabaseConnector.stringToCal(rslts.getString(14)), //deadline2
              DatabaseConnector.stringToCal(rslts.getString(15)) //deadline3
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

  public boolean isDeadlineViolated() {
    if (hasExtraDeadlines) {
      switch (status) {
        case 301:
          return deadline1.before(Calendar.getInstance());
        case 302:
          return deadline2.before(Calendar.getInstance());
        case 303:
          return deadline3.before(Calendar.getInstance());
        case 304:
        case 305:
          return calendar.before(Calendar.getInstance());
        case 306:
        case 307:
        default:
          return false;
      }
    } else if (status < 305) {
      return calendar.before(Calendar.getInstance());
    } else {
      return false;
    }
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
  public ArrayList<AttributeModifier> getAttributeModifiers() {
    ArrayList list = new ArrayList();
    if (isDeadlineViolated()) {
//      list.add(new AttributeModifier("style", new Model("color: red;")));
    }
    switch (this.color) {
      case BLUE:
        list.add(new AttributeModifier("style", new Model((isDeadlineViolated() ? "color: red; " : "") + "border-color: blue; background-color: lightblue;")));
        break;
      case GREEN:
        list.add(new AttributeModifier("style", new Model((isDeadlineViolated() ? "color: red; " : "") + "border-color: green; background-color: lightgreen;")));
        break;
      case RED:
        list.add(new AttributeModifier("style", new Model((isDeadlineViolated() ? "color: red; " : "") + "border-color: red; background-color: lightcoral;")));
        break;
      case YELLOW:
        list.add(new AttributeModifier("style", new Model((isDeadlineViolated() ? "color: red; " : "") + "border-color: yellow; background-color: lightyellow;")));
        break;
    }
    return list;
  }

  public enum EventColor {

    BLUE("border-color: blue; background-color: lightblue;"),
    GREEN("border-color: green; background-color: lightgreen;"),
    RED("border-color: black; background-color: white;"),
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
