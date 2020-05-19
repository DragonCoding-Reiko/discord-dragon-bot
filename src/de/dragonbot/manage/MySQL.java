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


    public void connect(String database) {
        this.conn = null;
        
        try { 
            String url = DragonBot.INSTANCE.mysqlLink 
            		   + database
            		   + "?autoReconnect=true"
            		   + "&useUnicode=true"
            		   + "&characterEncoding=UTF-8"
            		   + "&useJDBCCompliantTimezoneShift=true"
            		   + "&useLegacyDatetimeCode=false"
            		   + "&serverTimezone=UTC";
            String user = DragonBot.INSTANCE.mysqlUser;
            String pswd = DragonBot.INSTANCE.mysqlPswd;
            
            this.conn  = DriverManager.getConnection(url, user, pswd);
            System.out.println("MySQL verbunden.");
            
            this.stmt = this.conn.createStatement();
        } catch (SQLException e) {
        	Utils.printError(e, null);
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
        	Utils.printError(e, null);
        }
    }

    public ResultSet getData(String sql) {
		try {
			return this.stmt.executeQuery(sql);
		} catch (SQLException | NullPointerException e1) {
			Utils.printError(e1, sql);
			
			try {
				return this.stmt.executeQuery(sql);
			} catch (SQLException | NullPointerException e2) {
				Utils.printError(e2, sql);
			}
		}
		
		return null;
    }
    
    public void execute(String sql) {
		try {
			this.stmt.execute(sql);
		} catch (SQLException | NullPointerException e) {
			Utils.printError(e, sql);
			try {
				this.stmt.execute(sql);
			} catch (SQLException | NullPointerException e1) {
				Utils.printError(e, sql);
			}
		}
	}
  
}