package com.autoFunctionTest.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 配置加载工具类
 */
public class ConfigLoader {
    private static final String CONFIG_FILE = "src/test/resources/config.properties";
    private static Properties config = null;
    
    /**
     * 加载配置文件
     */
    public static Properties loadConfig() {
        if (config == null) {
            config = new Properties();
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                config.load(fis);
            } catch (IOException e) {
                System.err.println("无法加载配置文件: " + CONFIG_FILE);
                e.printStackTrace();
            }
        }
        return config;
    }
    
    /**
     * 获取配置属性
     */
    public static String getProperty(String key) {
        if (config == null) {
            loadConfig();
        }
        return config.getProperty(key);
    }
    
    /**
     * 获取配置属性，提供默认值
     */
    public static String getProperty(String key, String defaultValue) {
        if (config == null) {
            loadConfig();
        }
        return config.getProperty(key, defaultValue);
    }
    
    /**
     * 获取整数配置属性
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("无法将属性转换为整数: " + key + ", 值: " + value);
            }
        }
        return defaultValue;
    }
    
    /**
     * 获取布尔配置属性
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
} 