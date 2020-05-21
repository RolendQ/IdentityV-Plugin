package com.roland.identityv.utils;

import com.roland.identityv.core.IdentityV;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

/**
 * Edit and retrieve from the config
 */
public class Config {
    public static IdentityV plugin;
    public static FileConfiguration config;

    public Config(IdentityV plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    public static void set(String key, String value) {
        // Int
        try {
            config.set(key,Integer.parseInt(value));
            return;
        } catch (Exception e) {

        }
        // Double
        try {
            config.set(key,Double.parseDouble(value));
            return;
        } catch (Exception e) {

        }

        // String
        try {
            config.set(key,value);
            return;
        } catch (Exception e) {

        }
    }

    public static void set(ConfigurationSection cs, String key, String value) {
        // Int
        try {
            cs.set(key,Integer.parseInt(value));
            return;
        } catch (Exception e) {

        }
        // Double
        try {
            cs.set(key,Double.parseDouble(value));
            return;
        } catch (Exception e) {

        }

        // String
        try {
            cs.set(key,value);
            return;
        } catch (Exception e) {

        }
    }

    public static String getStr(String path, String key) {
        try {
            return getSection(path).getString(key);
        } catch (Exception e) {
            return "";
        }
    }

    public static int getInt(String path, String key) {
        try {
            return getSection(path).getInt(key);
        } catch (Exception e) {
            return -1;
        }
    }

    public static double getDouble(String path, String key) {
        try {
            return getSection(path).getDouble(key);
        } catch (Exception e) {
            return -1;
        }
    }

    public static ConfigurationSection getSection(String path) {
        String[] sections = path.split("\\.");
        ConfigurationSection cs = config.getConfigurationSection(sections[0]);
        for (int i = 1; i < sections.length; i++) {
            cs = cs.getConfigurationSection(sections[i]);
        }
        return cs;
    }
}
