package com.ar;

import com.mysql.jdbc.Connection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by ar on 2/26/15.
 */
public class Database {

    private final static String version = "0.1";

    private DatabaseMetaData databaseMetaData;
    private String databaseProductVersion = null;
    private String mysqlVersion = null;

    Connector connector;

    public Database(Connector connector) {
        this.connector = connector;
    }

    public Boolean execute(String dbFrom, String dbTo, Boolean backup, String qs){

//        System.out.println(connector.dbHostTo);
//        System.out.println(connector.dbHostFrom);
//        System.out.println(dbFrom+" " + dbTo + " " + backup + " " + qs);

//        BufferedWriter out;
        try {
            String s = App.appRoot + File.separator + "db" + File.separator;
            System.out.println(s);
            File temp = new File(s + dbFrom + ".sql");

            if (!temp.exists()) {
                temp.createNewFile();
            }

            BufferedWriter out = new BufferedWriter(new FileWriter(temp));

            connector.connFrom.setCatalog(dbFrom);
            out.write(dumpCreateTable(connector.connFrom, out, "h6_admin_user"));


//            out.write(content);
            out.close();
//            System.out.println(out.toString());


        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String dumpCreateTable(Connection conn, BufferedWriter out, String table) {
        String createTable = null;
        try{
            out.write(getDropTable(table));
            Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            s.executeQuery("SHOW CREATE TABLE " + table);
            ResultSet rs = s.getResultSet();

            while(rs.next()) {
                createTable = rs.getString("Create Table") + ";";
            }


        } catch (SQLException e) {
            System.out.println("test");
            System.err.println(e.getMessage());

        } catch(IOException e){
            System.err.println(e.getMessage());
        }

        return createTable;
    }

    private String getDropTable(String table) {
        return "\n\n# Dump of table " + table + " \n# --------------------------------------------------\n\n DROP TABLE IF EXISTS `" + table + "`;\n\n";

    }

    private String getHeader() {
        //return Dump Header
//        return "----- MySQL Dump -----" + version + "\n--\n-- Host: " + hostname + "    " + "Database: " + database + "\n-- ------------------------------------------------------\n-- Server Version: " + databaseProductVersion + "\n--";
        return null;
    }
}
