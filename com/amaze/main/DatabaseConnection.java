package com.amaze.main;

import java.sql.*;

public class DatabaseConnection {

	//JDBC connection stuff
    private static final String dbDriver = "org.mariadb.jdbc.Driver";
    private static final String dbLoc = "jdbc:mysql://178.62.72.43:3306/aMazeDB";

	//Credentials for the database
    private static final String username = "phpuser";
    private static final String password = "aMaze";
    private Connection conn = null;
    private Statement stmt = null;

    public DatabaseConnection() {
        try {
            Class.forName(dbDriver);
            conn = DriverManager.getConnection(dbLoc, username, password);
        } catch(Exception e){
            // Exception handler
            e.printStackTrace();
        }
    }

    public void uploadResult(String name, int score, int level, String compTime) {
        int uid = 0;
        String username = name.replaceAll("[^a-zA-Z0-9]+","");

        String uidGet = "SELECT `uid` FROM `users` WHERE `uName` = '" + username + "';";
        String uploadName = "INSERT INTO `users`" + "(`uName`)" + "VALUES" + "('" + username + "');";

        try {
            stmt = conn.createStatement();
            stmt.executeQuery(uploadName);
            ResultSet rs = stmt.executeQuery(uidGet);
            while(rs.next()) {
                uid = rs.getInt("uid");
            }
            String uploadData = "INSERT INTO `leaderboard`" + " (`uid`, `levelNo`, `score`, `compTime`)" + "VALUES" + "('" + uid + "','" + level + "','" + score + "','" + compTime + "');";
            stmt.executeQuery(uploadData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clean() {
        try {
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
