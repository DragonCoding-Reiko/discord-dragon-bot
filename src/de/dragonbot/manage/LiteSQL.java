package de.dragonbot.manage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LiteSQL {

	private static Connection conn;
	private static Statement stmt;

	public static void connect() {
		conn = null;

		try {
			File file = new File("datenbank.db");
			if(!file.exists()) {
				file.createNewFile();
			}

			String url = "jdbc:sqlite:" + file.getPath();
			conn = DriverManager.getConnection(url);
			System.out.println("Datenbank verbunden.");

			stmt = conn.createStatement();

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void disconnect() {
		try {
			if(conn != null) {
				conn.close();
				System.out.println("Datenbank getrennt.");
			}
		} catch (SQLException  e) {
			e.printStackTrace();
		}
	}

	public static void newTable(String name, String primarykey, String fields) {
		String sql = "CREATE TABLE IF NOT EXISTS " + name + " (" + primarykey + " NOT NULL PRIMARY KEY AUTOINCREMENT, " + fields + ")";

		try {

			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void newEntry(String name, String fields, String values) {
		String sql = "INSERT INTO " + name + "(" + fields + ") VALUES(" + values + ")";

		try {

			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void updateEntry(String name, String setter, String where) {
		String sql = "UPDATE " + name + " SET " + setter + " WHERE " + where;

		try {

			stmt.executeUpdate(sql);

		} catch (SQLException e) {
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
			System.out.println(e.getMessage());
		}
	}
}
