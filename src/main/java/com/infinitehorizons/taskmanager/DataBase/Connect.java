package com.infinitehorizons.taskmanager.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

    public Connection databaseLink;

    public Connection getConnection()  {
        String databaseName = "task-manager";
        String username = "root";
        String password = "Manager23";
        String url = "jdbc:mysql://task-manager.ctm72fipu0iz.us-east-2.rds.amazonaws.com:3306/" + databaseName;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Failed connection: \nError:" + e);

        }
        return databaseLink;
    }

}
