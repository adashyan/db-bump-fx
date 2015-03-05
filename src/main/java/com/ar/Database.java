package com.ar;

import com.mysql.jdbc.Connection;

import java.io.*;
import java.sql.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

        createDump(dbFrom);
        return null;
    }

    private Boolean createDump(String dbFrom){
        try {

            File file = getFile(dbFrom);

            BufferedWriter out = new BufferedWriter(new FileWriter(file));

            //set database
            connector.connFrom.setCatalog(dbFrom);

            dumpCreateTable(connector.connFrom, out, "h6_content");
            dumpTable(connector.connFrom, out, "h6_content");

            out.close();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println(".... done ....");
        return true;
    }

    private void dumpTable(Connection conn, BufferedWriter out, String table) {
        dumpTable(conn, out, table, 100);
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
                    prefix += rsMetaData.getColumnName(i) + ") \nVALUES \n   ";
                } else {
                    prefix += rsMetaData.getColumnName(i) + ",";
                }
            }

            String postfix;
            int count = 1;

            System.out.println(prefix);

            while (rs.next()) {

                postfix = "";

                for (int i = 1; i <= columnCount; i++) {
                    if (i != columnCount) {
                        postfix += "'" + ((rs.getBytes(i) == null) ? "null" : escapeString(rs.getBytes(i)).toString()) + "',";
                    } else {
                        postfix += "'" + ((rs.getBytes(i) == null) ? "null" : escapeString(rs.getBytes(i)).toString()) + "'";
                    }
                }

                if (count == 1) {
                    out.write(prefix + "(" + postfix + "),\n");
                    ++count;
                } else if (count < chunk) {
                    out.write("   (" + postfix + "),\n");
                    ++count;
                } else {
                    count = 1;
                    out.write("   (" + postfix + ");\n");
                }

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
        return "\n\n# Dump of table `" + table + "` \n# --------------------------------------------------\n\n DROP TABLE IF EXISTS `" + table + "`;\n\n";
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
    private File getFile(String db) throws IOException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        String d = dateFormat.format(date).toString();

        String s = App.appRoot + File.separator + "db" + File.separator;

        File temp = new File(s + db + "_" + d +".sql");

        if (!temp.exists()) {
            temp.createNewFile();
        }

        return temp;
    }
}