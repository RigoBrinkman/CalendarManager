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

    public static void initialize(Properties dbProps) throws SQLException {
        if (users == null) {
            try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDBURL(), dbProps);
                    ResultSet rslts = DatabaseConnector.select(conn, DatabaseConnector.Table.USERS)) {
                ArrayList<CalmanUser> aList = new ArrayList();
                HashMap<String, CalmanUser> sMap = new HashMap();
                HashMap<Integer, CalmanUser> iMap = new HashMap();
                while (rslts.next()) {
                    CalmanUser cu = new CalmanUser(
                            rslts.getInt(1),
                            rslts.getString(2),
                            rslts.getString(3));
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

    public static ArrayList<CalmanUser> getUsers(){
        return users;
    }

    public static CalmanUser getUser(String name){
        return stringMap.get(name);
    }
    
    public static CalmanUser getUser(int id){
        return intMap.get(id);
    }

    int userId;
    String name;
    String mail;

    public CalmanUser(int userId, String name, String mail) {
        this.userId = userId;
        this.name = name;
        this.mail = mail;
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

}
