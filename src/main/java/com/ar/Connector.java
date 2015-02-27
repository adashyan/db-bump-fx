package com.ar;


import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import java.sql.DriverManager;
import java.util.Properties;


public class Connector {
	static Connection connFrom, connTo;
	static Statement statFrom, statTo;
    static String driver;
	static String urlFrom;
	static String urlTo;

	//just for adding in dump file
	static String dbHostFrom;
	static String dbHostTo;

	public Connector(Properties propsFrom, Properties propsTo){
        urlFrom = getUrl(propsFrom);
        urlTo   = getUrl(propsTo);
        driver = "com.mysql.jdbc.Driver";

		dbHostFrom = propsFrom.getProperty("host");
		dbHostTo   = propsTo.getProperty("host");
	}

	public boolean open() {
		try {
			DriverManager.registerDriver((java.sql.Driver) Class.forName(driver).newInstance());
			connFrom = (Connection)DriverManager.getConnection(urlFrom);
			statFrom = (Statement)connFrom.createStatement();

			DriverManager.registerDriver((java.sql.Driver) Class.forName(driver).newInstance());
			connTo = (Connection) DriverManager.getConnection(urlTo);
			statTo = (Statement) connTo.createStatement();

		} catch (Exception e) {
			return false;
		}
		return !(connFrom == null || connTo == null);
	}


    private String getUrl(Properties props){

		String host = props.getProperty("host");
		String port = props.getProperty("port");
        String user = props.getProperty("user");
        String pass = props.getProperty("pass");

        return "jdbc:mysql://"+host+":"+port+"/?user="+user+"&password="+pass;
    }
}
