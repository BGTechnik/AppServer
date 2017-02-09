package de.bsesw.appserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {

	private Connection c = null;
	String host;
	String port;
	String database;
	String username;
	String password;

	public MySQL(String ho, String po, String db, String un, String pw) {
		host = ho;
		port = po;
		database = db;
		username = un;
		password = pw;
	}

	private Connection conn() {
		if (c == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				c = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + 3306 + "/" + this.database
						+ "?user=" + this.username + "&password=" + this.password + "&autoReconnect=" + true
						+ "&failOverReadOnly=false&maxReconnects=" + 5);
			} catch (SQLException e) {
				System.out.println("Fehler: bei openConnection()[MySQL.java]  SQLException   " + e.getMessage());
			} catch (ClassNotFoundException e) {
				System.out.println("Fehler: bei openConnection()[MySQL.java]  ClassNotFoundException");
			}
		}
		try {
			if (c.isClosed()) {
				try {
					Class.forName("com.mysql.jdbc.Driver");
					c = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + 3306 + "/" + this.database
							+ "?user=" + this.username + "&password=" + this.password + "&autoReconnect=" + true
							+ "&failOverReadOnly=false&maxReconnects=" + 5);
				} catch (SQLException e) {
					System.out.println("Fehler: bei openConnection()[MySQL.java]  SQLException   " + e.getMessage());
				} catch (ClassNotFoundException e) {
					System.out.println("Fehler: bei openConnection()[MySQL.java]  ClassNotFoundException");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return c;
	}

	public void update(String q) {
		try {
			PreparedStatement ps = conn().prepareStatement(q);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet query(String q) {
		try {
			PreparedStatement ps = conn().prepareStatement(q);
			return ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}