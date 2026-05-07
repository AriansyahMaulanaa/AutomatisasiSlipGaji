package com.slipgaji.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties props = new Properties();

    static {
        load();
    }

    private static void load() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static void set(String key, String value) {
        props.setProperty(key, value);
        save();
    }

    private static void save() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "SlipGaji Pro - Local Configurations");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Database config helpers
    public static String getDbHost() {
        return get("db_host", Constants.DB_HOST);
    }

    public static int getDbPort() {
        try {
            return Integer.parseInt(get("db_port", String.valueOf(Constants.DB_PORT)));
        } catch (NumberFormatException e) {
            return Constants.DB_PORT;
        }
    }

    public static String getDbName() {
        return get("db_name", Constants.DB_NAME);
    }

    public static String getDbUser() {
        return get("db_user", Constants.DB_USER);
    }

    public static String getDbPassword() {
        return get("db_password", Constants.DB_PASS);
    }
}
