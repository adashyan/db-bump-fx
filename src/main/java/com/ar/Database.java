package com.ar;

import com.mysql.jdbc.Connection;

import java.io.*;
import java.sql.*;

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

    public Boolean execute(String dbFrom, String dbTo, Boolean backup, String qs) {

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
            
            dumpCreateTable(connector.connFrom, out, "h6_content");
            dumpTable(connector.connFrom, out, "h6_content");


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

    private void dumpTable(Connection conn, BufferedWriter out, String table) {
        dumpTable(conn, out, table, 1000);
    }

    protected void dumpTable(Connection conn, BufferedWriter out, String table, int chunk) {
        try {
            Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            out.write("\n\n--\n-- Dumping data for table `" + table + "`\n--\n\n");

            

            s.executeQuery("SELECT /*!40001 SQL_NO_CACHE */ * FROM " + table);
            ResultSet rs = s.getResultSet();

            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();

            String prefix = new String("INSERT INTO " + table + " (");

            for (int i = 1; i <= columnCount; i++) {
                if (i == columnCount) {
                    prefix += rsMetaData.getColumnName(i) + ") VALUES( ";
                } else {
                    prefix += rsMetaData.getColumnName(i) + ",";
                }
            }

            String postfix;
            int count = 0;

            while (rs.next()) {

                postfix = "";
                for (int i = 1; i <= columnCount; i++) {
                    if (i == columnCount) {
                        System.err.println(rs.getMetaData().getColumnClassName(i));
                        postfix += "'" + rs.getString(i) + "');\n";
                    } else {

                        System.err.println(rs.getMetaData().getColumnTypeName(i));
                        if (rs.getMetaData().getColumnTypeName(i).equalsIgnoreCase("LONGBLOB")) {
                            try {
                                postfix += "'" + escapeString(rs.getBytes(i)).toString() + "',";
                            } catch (Exception e) {
                                postfix += "NULL,";
                            }
                        } else {
                            try {
                                postfix += "'" + rs.getString(i).replaceAll("\n", "\\\\n").replaceAll("'", "\\\\'") + "',";
                            } catch (Exception e) {
                                postfix += "NULL,";
                            }
                        }
                    }
                }
                out.write(prefix + postfix + "\n");
                ++count;
            }
            rs.close();
            s.close();
        } catch (IOException | SQLException e) {
            System.err.println(e.getMessage());
        }


    }

    protected void dumpCreateTable(Connection conn, BufferedWriter out, String table) {
        String createTable = null;
        try {
            out.write(getDropTable(table));
            Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            s.executeQuery("SHOW CREATE TABLE " + table);
            ResultSet rs = s.getResultSet();

            while (rs.next()) {
                createTable = rs.getString("Create Table") + ";";
            }
            out.write(createTable);

        } catch (SQLException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    protected String getDropTable(String table) {
        return "\n\n# Dump of table " + table + " \n# --------------------------------------------------\n\n DROP TABLE IF EXISTS `" + table + "`;\n\n";
    }

    protected String getHeader() {
        //return Dump Header
//        return "----- MySQL Dump -----" + version + "\n--\n-- Host: " + hostname + "    " + "Database: " + database + "\n-- ------------------------------------------------------\n-- Server Version: " + databaseProductVersion + "\n--";
        return null;
    }

    /**
     * Escape string ready for insert via mysql client
     *
     * @param bIn String to be escaped passed in as byte array
     * @return bOut MySQL compatible insert ready ByteArrayOutputStream
     */
    private ByteArrayOutputStream escapeString(byte[] bIn) {
        int numBytes = bIn.length;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(numBytes + 2);
        for (int i = 0; i < numBytes; ++i) {
            byte b = bIn[i];

            switch (b) {
                case 0: /* Must be escaped for 'mysql' */
                    bOut.write('\\');
                    bOut.write('0');
                    break;

                case '\n': /* Must be escaped for logs */
                    bOut.write('\\');
                    bOut.write('n');
                    break;

                case '\r':
                    bOut.write('\\');
                    bOut.write('r');
                    break;

                case '\\':
                    bOut.write('\\');
                    bOut.write('\\');
                    break;

                case '\'':
                    bOut.write('\\');
                    bOut.write('\'');
                    break;

                case '"':
                    bOut.write('\\');
                    bOut.write('"');
                    break;

                case '\032': /* This gives problems on Win32 */
                    bOut.write('\\');
                    bOut.write('Z');
                    break;

                default:
                    bOut.write(b);
            }
        }
        return bOut;
    }
}