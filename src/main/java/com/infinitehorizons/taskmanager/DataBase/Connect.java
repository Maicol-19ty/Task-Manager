package com.infinitehorizons.taskmanager.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

    public Connection databaseLink;

    public Connection getConnection()  {
        String databaseName = "bs3as3yvtz0sthxrb2ul";
        String username = "u8gyahbpad4guuiq";
        String password = "zsTCkDsaAOZ6OuIDNRUw";
        String url = "jdbc:mysql://bs3as3yvtz0sthxrb2ul-mysql.services.clever-cloud.com:21251/" + databaseName;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Failed connection: n\nError:" + e);

        }
        return databaseLink;
    }

}
