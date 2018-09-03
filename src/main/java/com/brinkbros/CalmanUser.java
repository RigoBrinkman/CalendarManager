package com.brinkbros;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class CalmanUser {

  private static ArrayList<CalmanUser> users;
  private static HashMap<String, CalmanUser> stringMap;
  private static HashMap<Integer, CalmanUser> intMap;

  public static void initialize(Connection conn) throws SQLException {
    if (users == null) {
      try (ResultSet rslts = DatabaseConnector.select(conn, DatabaseConnector.Table.USERS)) {
        ArrayList<CalmanUser> aList = new ArrayList();
        HashMap<String, CalmanUser> sMap = new HashMap();
        HashMap<Integer, CalmanUser> iMap = new HashMap();
        while (rslts.next()) {
          CalmanUser cu = new CalmanUser(
              rslts.getInt(1),
              rslts.getString(2),
              rslts.getString(3),
              rslts.getInt(4));
          aList.add(cu);
          sMap.put(cu.getName(), cu);
          iMap.put(cu.getUserId(), cu);
        }
        users = aList;
        stringMap = sMap;
        intMap = iMap;
      }
    }
  }

  public static ArrayList<CalmanUser> getUsers() {
    return users;
  }

  public static CalmanUser getUser(String name) {
    return stringMap.get(name);
  }

  public static CalmanUser getUser(int id) {
    return intMap.get(id);
  }

  private final int userId;
  private final String name;
  private final String mail;
  private final int role;

  public CalmanUser(int userId, String name, String mail, int role) {
    this.userId = userId;
    this.name = name;
    this.mail = mail;
    this.role = role;
  }

  public int getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public String getMail() {
    return mail;
  }

  public int getRole() {
    return role;
  }

}
