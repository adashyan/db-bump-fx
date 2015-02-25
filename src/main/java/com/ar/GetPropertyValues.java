package com.ar;

import java.io.*;
import java.util.Properties;

/**
 * Created by ar on 2/9/15.
 */
public class GetPropertyValues {
    static String sql      = "";
    static Properties prop = null;

    public static Properties getProperties() {
        if(prop == null) {

            prop = new Properties();
            try {
                String propFileName = App.appRoot + File.separator + "data" + File.separator + "config.properties";

                InputStream inputStream = new FileInputStream(propFileName);

                if (inputStream != null) {
                    prop.load(inputStream);
                } else {
                    throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
                }

            }catch (IOException ex){
                prop = null;
            }
        }
        return prop;
    }

    public static String getProperty(String key){
        String val = null;
        prop = getProperties();
        if(prop != null){
            val = prop.getProperty(key);
        }
        return val;
    }

    public static String getFavorite(){

        if("".equals(sql)) {

            String file = App.appRoot + File.separator + "data" + File.separator + "favorite.sql";
            BufferedReader br = null;

            try {

                String sCurrentLine;
                br = new BufferedReader(new FileReader(file));
                while ((sCurrentLine = br.readLine()) != null) {
                    sql = sql + sCurrentLine + "\n";
                }
            } catch (IOException e) {}
            finally {
                try {
                    if (br != null) br.close();
                } catch (IOException ex) {}
            }
        }

        return sql;
    }
}
