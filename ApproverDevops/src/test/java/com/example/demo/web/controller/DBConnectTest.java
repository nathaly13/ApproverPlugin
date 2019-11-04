package com.example.demo.web.controller;

import com.example.demo.Entity.IssueApp;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DBConnectTest {

    /*
     * To test this class you have to create a DataBase called by "approver"
     */

    private static String URL = "jdbc:mysql://localhost:3306";
    private static String URL1 = "jdbc:mysql://localhost:3306/approver";
    private static String USERNAME = "root";
    private static String PASSWORD = "root";
    private static Connection con;

    @Test
    public void getConnectionTest_OK() throws SQLException {

        assertTrue(!DBConnect.getConnection().isClosed());
    }

    @Test
    public void getConnectionTest_Without_DBName() throws SQLException {
        try {
            con = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (SQLException ex) {
            System.out.println("Failed to create the database connection.");
        }

        assertFalse(con.isClosed());

    }

    @Test
    public void insertDataTest_OK() throws SQLException {
        List<IssueApp> IssueList = new ArrayList<IssueApp>();
        IssueApp issueapp = new IssueApp();
        issueapp.setTitle("TitleDB");
        issueapp.setState("StateDB");
        issueapp.setMd5("Md5DB");
        issueapp.setDate("DateDB");
        IssueList.add(issueapp);
        String project = "project";
        String projectUrl = "projectUrl";
        String result = DBConnect.insertData(IssueList,project,projectUrl);
        assertEquals("OK", result);
    }

    @Test
    public void insertDataTest_KO() throws SQLException {
        List<IssueApp> IssueList = new ArrayList<IssueApp>();
        String project = "project";
        String projectUrl = "projectUrl";
        String result = DBConnect.insertData(IssueList,project,projectUrl);
        assertEquals("KO", result);
    }

    @Test
    public void checkDBTest_Database_Exists() throws SQLException {

        String DBName = "approver";
        assertEquals(Boolean.TRUE, DBConnect.checkDB(DBName));

    }

    @Test
    public void checkDBTest_Database_DOESNT_Exists() throws SQLException {

        String DBName = "approverDB";
        assertEquals(Boolean.FALSE, DBConnect.checkDB(DBName));

    }
}