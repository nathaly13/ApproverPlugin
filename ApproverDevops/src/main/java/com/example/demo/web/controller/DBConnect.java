package com.example.demo.web.controller;

import com.example.demo.Entity.IssueApp;
import org.slf4j.Logger;
import java.sql.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.springframework.jdbc.datasource.init.*;
import org.springframework.core.io.*;
import org.springframework.core.io.support.EncodedResource;
import static org.slf4j.LoggerFactory.getLogger;


public class DBConnect {

    /*
     * Database Class
     * In this class you can find methods to connect and create data on database and also check if database exists.
     * */

    private static final Logger LOG = getLogger(DBConnect.class);
    private static String url = "jdbc:mysql://localhost:3306";
    private static String url1 = "jdbc:mysql://localhost:3306/approver";
    private static String driverName = "com.mysql.jdbc.Driver";
    private static String username = "root";
    private static String password = "root";
    private static Connection con;


    public static Connection getConnection() {

        Boolean connection = Boolean.FALSE;
        try {
            Class.forName(driverName);
            try {

                con = DriverManager.getConnection(url1, username, password);
                if (!con.isClosed()) connection = Boolean.TRUE;

            } catch (SQLException ex) {
                // log an exception. fro example:
                System.out.println("Failed to create the database connection.");
            }
        } catch (ClassNotFoundException ex) {
            // log an exception. for example:
            System.out.println("Driver not found.");
        }
        return con;
    }

    public static String insertData(List<IssueApp> IssueList, String project, String projectUrl) throws SQLException {

        Connection con = null;
        String query;
        String result = "KO";
        con = getConnection();

        if (!con.isClosed() && !IssueList.isEmpty()) {

            for (IssueApp issueapp : IssueList) {


                query = "INSERT INTO issue (Title, State, Md5, Date, Project, Project_url, Numissue) VALUES (?,?,?,?,?,?,?)";
                try (PreparedStatement pstmt = con.prepareStatement(query)) {
                    pstmt.setString(1, issueapp.getTitle());
                    pstmt.setString(2, issueapp.getState());
                    pstmt.setString(3, issueapp.getMd5());
                    pstmt.setString(4, issueapp.getDate());
                    pstmt.setString(5, project);
                    pstmt.setString(6, projectUrl);
                    pstmt.setString(7, issueapp.getNumIssue());
                    pstmt.executeUpdate();
                    result = "OK";

                }
                catch (SQLException ex) {
                    // Exception handling
                    //Logger.getLogger(FormTwo1.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                }
            }
        }
        return result;
    }


    public static Boolean checkDB(String DBName) throws SQLException {
        Boolean exists;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        String sql2 = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + DBName + "'";
        con = DriverManager.getConnection(url, username, password);


            stmt = con.createStatement();
            rs = stmt.executeQuery(sql2);

            if (!rs.next()) {
                System.out.println("Database doesn't exist");
                //create database
                ScriptUtils.executeSqlScript(con, new EncodedResource(new ClassPathResource("sql/database.sql")));
                exists = Boolean.FALSE;

            } else {
                System.out.println("Database exist");
                exists = Boolean.TRUE;
            }


        return exists;
    }


}
