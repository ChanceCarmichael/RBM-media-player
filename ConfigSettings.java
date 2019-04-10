package com.runningboards.mediaplayer;


import java.util.HashMap;
import java.util.Map;

public class ConfigSettings {

    private static String splash_screen;
    private static String outside_hours;
    private static Map schedule = new HashMap();
    private static String lastChanged = "";
    private static String splashData = "";
    private static String splashType = "";
    
    public static String getSplashData() {
        return splashData;
    }
    public static void setSplashData(String splash) {
        splashData = splash;
    }
    public static String getSplashType() {
        return splashType;
    }
    public static void setSplashType(String splash) {
        splashType = splash;
    }

    public static Boolean checkModified(String recentChange) {
        if (!recentChange.equals(lastChanged)) {
            lastChanged = recentChange;
            return true;
        } else {
            return false;
        }
    }
    public static void addToSchedule(String key, String val) {
        schedule.put(key, val);
    }
    
    public static void setSplashScreen(String val) {
        splash_screen = val;
    }
    public static void setOutsideHours(String val) {
        outside_hours = val;
    }
    
    public static Map getSchedule() {
        return schedule;
    }
    
    public static String getSplashScreen() {
        return splash_screen;
    }
    public static String getOutsideHours() {
        return outside_hours;
    }
}
