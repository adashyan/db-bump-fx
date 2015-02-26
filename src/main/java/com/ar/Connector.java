package com.ar;

import java.sql.*;
import java.util.Properties;


public class Connector {
	static Connection connFrom, connTo;
	static Statement statFrom, statTo;
    static String driver;
	static String urlFrom;
	static String urlTo;

	public Connector(Properties propsFrom, Properties propsTo){
        urlFrom = getUrl(propsFrom);
        urlTo   = getUrl(propsTo);
        driver = "com.mysql.jdbc.Driver";
	}

	public boolean open() {
		try {
			DriverManager.registerDriver((java.sql.Driver) Class.forName(driver).newInstance());
			connFrom = DriverManager.getConnection(urlFrom);
			statFrom = connFrom.createStatement();

			DriverManager.registerDriver((java.sql.Driver) Class.forName(driver).newInstance());
			connTo = DriverManager.getConnection(urlTo);
			statTo = connTo.createStatement();

		} catch (Exception e) {
			return false;
		}
		return !(connFrom == null || connTo == null);
	}

	public ResultSet executeQueryFrom(String s) throws SQLException {
		return statFrom.executeQuery(s);
	}

	public ResultSet executeQueryTo(String s) throws SQLException {
		return statTo.executeQuery(s);
	}

	public void executeUpdate(String s) throws SQLException {
		statFrom.executeUpdate(s);
	}

    private String getUrl(Properties props){

		String host = props.getProperty("host");
		String port = props.getProperty("port");
        String user = props.getProperty("user");
        String pass = props.getProperty("pass");

        return "jdbc:mysql://"+host+":"+port+"/?user="+user+"&password="+pass;
    }
}
