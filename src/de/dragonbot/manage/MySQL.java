package de.dragonbot.manage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.dragonbot.DragonBot;

public class MySQL {
    
    private static Connection conn;
    private static Statement stmt;

    public static void connect() {
        conn = null;
        
    
        try { 
            String url = DragonBot.INSTANCE.mysqlLink;
            String user = DragonBot.INSTANCE.mysqlUser;
            String pswd = DragonBot.INSTANCE.mysqlPswd;
            
            conn  = DriverManager.getConnection(url, user, pswd);
            System.out.println("MySQL verbunden.");
            
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    public static void disconnect() {
        try {
            if(conn != null) {
                conn.close();
                System.out.println("MySQL getrennt.");
            }
        } catch (SQLException  e) {
            e.printStackTrace();
        }
    }
    
    public static void newTable(String name, String primarykey, String fields) {
		String sql = "CREATE TABLE IF NOT EXISTS " + name + "(" + primarykey + " NOT NULL PRIMARY KEY AUTO_INCREMENT, " + fields + ")";

		try {

			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(sql);
			System.out.println(e.getMessage());
		}
	}

	public static void newEntry(String name, String fields, String values) {
		String sql = "INSERT INTO " + name + "(" + fields + ") VALUES(" + values + ")";

		try {

			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			System.out.println(sql);
			System.out.println(e.getMessage());
		}
	}

	public static void updateEntry(String name, String setter, String where) {
		String sql = "UPDATE " + name + " SET " + setter + " WHERE " + where;

		try {

			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			System.out.println(sql);
			System.out.println(e.getMessage());
		}
	}

	public static ResultSet getAllEntrys(String fields, String name) {
		String sql = "SELECT " + fields + " FROM " + name;

		try {

			return stmt.executeQuery(sql);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	public static ResultSet getEntrys(String fields, String name, String where) {
		String sql = "SELECT " + fields + " FROM " + name + " WHERE " + where;

		try {

			return stmt.executeQuery(sql);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	public static void deleteEntry(String name, String where) {
		String sql = "DELETE FROM " + name + " WHERE " + where;

		try {

			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			System.out.println(sql);
			System.out.println(e.getMessage());
		}
	}
    
}