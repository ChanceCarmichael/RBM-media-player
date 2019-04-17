package com.runningboards.mediaplayer;

import com.sforce.ws.ConnectionException;
import java.util.Properties;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    public static JSONObject getConfigObject() throws IOException {
        URL url = new URL(MediaPlayer.configServer);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        try {
            return new JSONObject(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void readConfigFile() {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            MediaPlayer.rootDir = prop.getProperty("rootDir");
            MediaPlayer.configServer = prop.getProperty("configServer");
            MediaPlayer.resourcesServer = prop.getProperty("resourcesServer");
            MediaPlayer.businessesServer = prop.getProperty("businessesServer");
            MediaPlayer.logPath = prop.getProperty("logPath");
            MediaPlayer.playerId=prop.getProperty("playerId");
            MediaPlayer.sfUser=prop.getProperty("sfUser");
            MediaPlayer.sfPass=prop.getProperty("sfPass");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public static Boolean getScheduleDiff() throws ConnectionException, JSONException {
        JSONObject schedule = new JSONObject(ConnectToSalesForce.GetSchedule());
        if (ConfigSettings.checkModified(schedule.getString("lastModified"))) {
            return true;
        } else {
            return false;
        }
    }

    public static void salesforceConfig() throws ConnectionException, JSONException {
        ConnectToSalesForce.setSessionId();
        String sch = ConnectToSalesForce.GetSchedule();
        JSONObject config = new JSONObject(sch);
        ConfigSettings.checkModified(config.getString("lastModified"));
        MediaPlayer.delay = Integer.parseInt(config.getString("imageInterval"));
        JSONObject schedule = config.getJSONObject("schedule");
        schedule.remove("outside_hours");
        for(int i = 0; i<schedule.names().length(); i++){
            String blockName = schedule.names().getString(i);
            String blockVal = schedule.getString(blockName);
            ConfigSettings.addToSchedule(blockName, blockVal);
            
        }
        Logging.logMessage("INFO:Successfully Processed Schedule");
    }
}