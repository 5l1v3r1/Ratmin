package me.slimig.ratmin.user_interface.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    // Maybe use the ConfigManager from my mc plugins

    public static Properties prop = new Properties();
    public static String ConfigFileName = "config.properties";

    public static void editEntry(String key, String value) {
        if (prop.getProperty(key) != null) {
            prop.replace(key, value);
            try {
                prop.store(new FileOutputStream(ConfigFileName), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readKey(String key) {
        try {
            prop.load(new FileInputStream(ConfigFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.get(key).toString();
    }

    public static void createConfig() {
        try {
            if (!(new File(ConfigFileName).exists())) {
                // set the properties value

                prop.setProperty("Port", "3055");
                prop.setProperty("ListenOnLaunch", "false");
                prop.setProperty("UPnP", "false");
                prop.setProperty("Notification", "false");
                System.out.println("Config created!");
                // save properties to project root folder
                prop.store(new FileOutputStream(ConfigFileName), null);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
