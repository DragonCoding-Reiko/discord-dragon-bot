package de.dragonbot.manage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.dragonbot.DragonBot;

public class MySQL {
    
    private Connection conn;
    private Statement stmt;


    public void connect() {
        this.conn = null;
        
    
        try { 
            String url = DragonBot.INSTANCE.mysqlLink;
            String user = DragonBot.INSTANCE.mysqlUser;
            String pswd = DragonBot.INSTANCE.mysqlPswd;
            
            this.conn  = DriverManager.getConnection(url, user, pswd);
            System.out.println("MySQL verbunden.");
            
            this.stmt = this.conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    public void disconnect() {
        try {
            if(conn != null) {
            	this.stmt.close();
            	this.conn.close();
                System.out.println("MySQL getrennt.");
            }
        } catch (SQLException  e) {
            e.printStackTrace();
        }
    }
    
    public void newTable(String name, String primarykey, String fields) {
		String sql = "CREATE TABLE IF NOT EXISTS " + name + "(" + primarykey + " NOT NULL PRIMARY KEY AUTO_INCREMENT, " + fields + ")";

		try {
			this.stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(sql);
			System.out.println(e.getMessage());
		}
	}

	public void newEntry(String name, String fields, String values) {
		String sql = "INSERT INTO " + name + "(" + fields + ") VALUES(" + values + ")";

		try {
			this.stmt.executeUpdate(sql);

		} catch (SQLException e) {
			System.out.println(sql);
			System.out.println(e.getMessage());
		}
	}

	public void updateEntry(String name, String setter, String where) {
		String sql = "UPDATE " + name + " SET " + setter + " WHERE " + where;

		try {
			this.stmt.executeUpdate(sql);

		} catch (SQLException e) {
			System.out.println(sql);
			System.out.println(e.getMessage());
		}
	}

	public ResultSet getAllEntrys(String fields, String name) {
		String sql = "SELECT " + fields + " FROM " + name;

		try {
			return this.stmt.executeQuery(sql);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	public ResultSet getEntrys(String fields, String name, String where) {
		String sql = "SELECT " + fields + " FROM " + name + " WHERE " + where;
		
		try {
			return this.stmt.executeQuery(sql);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	public void deleteEntry(String name, String where) {
		String sql = "DELETE FROM " + name + " WHERE " + where;

		try {
			this.stmt.executeUpdate(sql);

		} catch (SQLException e) {
			System.out.println(sql);
			System.out.println(e.getMessage());
		}
	}
    
}