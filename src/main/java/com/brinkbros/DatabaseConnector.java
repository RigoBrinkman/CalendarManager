package com.brinkbros;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Properties;

public class DatabaseConnector {

    private static final String USERNAME = "thecorz0_planner";
    private static final String PASSWORD = "PFSPO2018test";
    private static final int PORT = 3306;
    private static final String IP_ADDRESS = "162.241.219.107";
    private static final String DB_NAME = "thecorz0_Planner";
    private static final String DB_URL = "jdbc:mysql://" + IP_ADDRESS + ":" + PORT + "/" + DB_NAME;
    private Optional<Connection> connection;

    ResultSet results;

    public static void main(String[] args) {
        try {
            DatabaseConnector session = new DatabaseConnector();
            PreparedStatement stmnt = session.getConnection().prepareStatement("INSERT INTO `PF_EVENTS`(`END_DATE`, `TITLE`, `DESCRIPTION`, `CATEGORY`, `TYPE`, `STATUS`) "
                    + "VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            stmnt.setString(1, "2018-06-30");
            stmnt.setString(2, "Nieuwe test");
            stmnt.setString(3, "omschrijving");
            stmnt.setInt(4, 1);
            stmnt.setInt(5, 2);
            stmnt.setInt(6, 3);
            stmnt.execute();
            ResultSet rslts = stmnt.getGeneratedKeys();

            rslts.next();
            System.out.println(rslts.getLong(1));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DatabaseConnector() throws SQLException {
        this.connection = Optional.empty();
        getConnection();
    }

    public Connection getConnection() throws SQLException {
        if (!connection.isPresent()) {
            Properties connectionProps = new Properties();
            connectionProps.put("user", USERNAME);
            connectionProps.put("password", PASSWORD);
            connectionProps.put("useJDBCCompliantTimezoneShift", "true");
            connection = Optional.of(DriverManager.getConnection(DB_URL, connectionProps));

        }
        return connection.get();
    }
}
