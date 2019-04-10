package com.runningboards.mediaplayer;

import com.sforce.ws.ConnectionException;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MediaPlayer {

    public static String rootDir = "/home/joe/Desktop/KIT/RunningBoards/MediaPlayer/sample_images/";
    public static String logPath = "/home/joe/Desktop/KIT/RunningBoards/MediaPlayer/sample_images/logs/";
    public static String playerId = "U686628";
    public static String sfUser = "zachy@runningboardsmarketing.com";
    public static String sfPass = "Adsonthego5614GcUGAQGny7t9QZcZHVshQYdl";

    public static String configServer = "http://localhost/rbm/schedule_files";
    public static String resourcesServer = "http://localhost/rbm/resources";
    public static String businessesServer = "http://localhost/rbm/businesses/";
    public static Boolean animateTransitions = true;
    public static String[] rearSch;
    public static String[] passSch;
    public static String[] driveSch;
    public static int Screens = 1;
    public static Boolean paused = false;
    public static int delay = 6;

    public static int getHour() {
        DateTime dt = new DateTime();
        return dt.getHourOfDay();
    }

    public static int[] getBlockHours(String blockName) {
        int first = parseInt(blockName.split("-")[0]);
        int second = parseInt(blockName.split("-")[1]) - 1;
        if (second == -1) {
            second = 23;
        }
        int[] ret = {first, second};
        return ret;
    }

    public static void bootUpDownload() throws JSONException, ConnectionException {
        try {
            Runtime.getRuntime().exec("rm -rf " + rootDir + "businesses/");
        } catch (IOException ex) {
            Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        Iterator it = ConfigSettings.getSchedule().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String block = pair.getKey().toString();
            String cleanedUp = pair.getValue().toString().replace(" ", "").replace("\"", "").replace("[", "").replace("]", "");
            String[] toParse = cleanedUp.split(",");
            String[] parsed1 = toParse[0].split("-");
            String businessId1 = parsed1[1];
            String[] parsed2 = toParse[1].split("-");
            String businessId2 = parsed2[1];
            int[] blockHours = getBlockHours(block);
            for (int i = 0; i < blockHours.length; i++) {
                int time = blockHours[i];
                try {
                    PopulateBusinesses.getBusinessImages(businessId2, time);
                    PopulateBusinesses.getBusinessImages(businessId1, time);

                } catch (ConnectionException ex) {
                    Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static Boolean checkHour(String range, int hour) {
        //if hour in range return true
        //
        int lower = parseInt(range.split("-")[0]);
        int higher = parseInt(range.split("-")[1]);

        if (hour >= lower && hour < higher) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean checkOutsideHours() {
        int t = getHour();
        //on true break and then take the value and parse it out
        //wipe dir and download the images
        Iterator it = ConfigSettings.getSchedule().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (checkHour(pair.getKey().toString(), t)) {
                return false;

            } else {
                String[] rearPath = {ConfigSettings.getOutsideHours()};
                String[] driverPath = {ConfigSettings.getOutsideHours()};
                String[] passPath = {ConfigSettings.getOutsideHours()};
                rearSch = rearPath;
                passSch = passPath;
                driveSch = driverPath;
                animateTransitions = false;
                return true;
            }
        }
        animateTransitions = true;
        return false;
    }

    static class quickDriverScreenChange implements Runnable {

        String string;
        Boolean aBoolean;

        quickDriverScreenChange(String s, Boolean animate) {
            string = s;
            aBoolean = animate;
        }

        @Override
        public void run() {
            DisplayImage.FirstScreen.swapImage(string, aBoolean);
        }
    }

    static class quickPassScreenChange implements Runnable {

        String string;
        Boolean aBoolean;

        quickPassScreenChange(String s, Boolean animate) {
            string = s;
            aBoolean = animate;
        }

        @Override
        public void run() {
            DisplayImage.SecondScreen.swapImage(string, aBoolean);
        }
    }

    static class quickRearScreenChange implements Runnable {

        String string;
        Boolean aBoolean;

        quickRearScreenChange(String s, Boolean animate) {
            string = s;
            aBoolean = animate;
        }

        @Override
        public void run() {
            DisplayImage.ThirdScreen.swapImage(string, aBoolean);
        }
    }

    public static void initialize() throws JSONException, ConnectionException {
        getSplashAndAfter();
        String splash_screen = ConfigSettings.getSplashScreen();
        String startTime;
        int Screens = DisplayImage.setup(splash_screen);
        startTime = Logging.getExactTime();
        bootUpDownload();
        Logging.logImagePlay(ConfigSettings.getSplashScreen(), "RESOURCES", startTime, Logging.getExactTime());

        do {
            try {
                int passC = 0;
                int rearC = 0;
                int cHour = MediaPlayer.getHour();
                MediaPlayer.buildSlideshow();
                if (!paused) {
                    for (int a = 0; a < MediaPlayer.driveSch.length; a++) {
                        if ((cHour != MediaPlayer.getHour())) {
                            cHour = MediaPlayer.getHour();
                            getSplashAndAfter();
                            break;
                        }
                        if (paused) {
                            break;
                        }
                        if (passC >= passSch.length) {
                            passC = 0;
                        }
                        if (rearC >= rearSch.length) {
                            rearC = 0;
                        }
                        if (Screens == 1) {
                            Thread t1 = new Thread(new quickDriverScreenChange(MediaPlayer.driveSch[a], MediaPlayer.animateTransitions));
                            t1.start();
                            TimeUnit.SECONDS.sleep(1);
                            startTime = Logging.getExactTime();
                        } else if (Screens == 2) {
                            Thread t1 = new Thread(new quickDriverScreenChange(MediaPlayer.driveSch[a], MediaPlayer.animateTransitions));
                            Thread t2 = new Thread(new quickPassScreenChange(MediaPlayer.passSch[passC], MediaPlayer.animateTransitions));
                            t1.start();
                            t2.start();
                            TimeUnit.SECONDS.sleep(1);
                            startTime = Logging.getExactTime();
                        } else {
                            Thread t1 = new Thread(new quickDriverScreenChange(MediaPlayer.driveSch[a], MediaPlayer.animateTransitions));
                            Thread t2 = new Thread(new quickPassScreenChange(MediaPlayer.passSch[passC], MediaPlayer.animateTransitions));
                            Thread t3 = new Thread(new quickRearScreenChange(MediaPlayer.rearSch[rearC], MediaPlayer.animateTransitions));
                            t1.start();
                            t2.start();
                            t3.start();
                            TimeUnit.SECONDS.sleep(1);
                            startTime = Logging.getExactTime();
                        }
                        TimeUnit.SECONDS.sleep(delay);
                        String[] cut = MediaPlayer.driveSch[a].split("/");
                        String bizname = cut[cut.length - 3];
                        String name = MediaPlayer.driveSch[a].substring(MediaPlayer.driveSch[a].lastIndexOf("/") + 1);
                        Logging.logImagePlay(name, bizname, startTime, Logging.getExactTime());
                        String[] cutPass = MediaPlayer.passSch[passC].split("/");
                        String biznamePass = cutPass[cutPass.length - 3];
                        String namePass = MediaPlayer.passSch[passC].substring(MediaPlayer.passSch[passC].lastIndexOf("/") + 1);
                        Logging.logImagePlay(namePass, biznamePass, startTime, Logging.getExactTime());
                        String[] cutRear = MediaPlayer.rearSch[rearC].split("/");
                        String biznameRear = cutRear[cutRear.length - 3];
                        String nameRear = MediaPlayer.rearSch[a].substring(MediaPlayer.rearSch[a].lastIndexOf("/") + 1);
                        Logging.logImagePlay(nameRear, biznameRear, startTime, Logging.getExactTime());
                        passC++;
                        rearC++;
                    }
                } else {
                    if (Screens == 1) {
                        DisplayImage.FirstScreen.swapImage(splash_screen, false);
                    } else if (Screens == 2) {
                        DisplayImage.FirstScreen.swapImage(splash_screen, false);
                        DisplayImage.SecondScreen.swapImage(splash_screen, false);
                    } else {
                        DisplayImage.FirstScreen.swapImage(splash_screen, false);
                        DisplayImage.SecondScreen.swapImage(splash_screen, false);
                        DisplayImage.ThirdScreen.swapImage(splash_screen, false);

                    }
                }
            } catch (ConnectionException | InterruptedException | JSONException e) {
                Logging.LOGGER.log(Level.SEVERE, e.getMessage());
                break;
            }

        } while (true);
    }

    public static String[] buildMediaArray(File[] list1, File[] list2) {
        List<String> paths1 = new ArrayList<>();
        List<String> paths2 = new ArrayList<>();
        List<String> slideshow = new ArrayList<>();
        for (File file : list1) {
            if (file.isFile()) {
                if (file.getName().contains(".JPEG") || file.getName().contains(".JPG") || file.getName().contains(".PNG") || file.getName().contains(".jpeg") || file.getName().contains(".jpg") || file.getName().contains(".png")) {
                    paths1.add(file.getPath());
                }
            }
        }
        for (File file : list2) {
            if (file.isFile()) {
                if (file.getName().contains(".JPEG") || file.getName().contains(".JPG") || file.getName().contains(".PNG") || file.getName().contains(".PNG") || file.getName().contains(".jpeg") || file.getName().contains(".jpg") || file.getName().contains(".png")) {
                    paths2.add(file.getPath());
                }
            }
        }
        int c = 0;
        String[] bizArray1 = new String[paths1.size()];
        paths1.toArray(bizArray1);
        Arrays.sort(bizArray1);
        String[] bizArray2 = new String[paths2.size()];
        paths2.toArray(bizArray2);
        Arrays.sort(bizArray2);
        if (bizArray1.length >= bizArray2.length) {
            for (int i = 0; i < bizArray1.length; i++) {
                if (c >= bizArray2.length) {
                    c = 0;
                }
                slideshow.add(bizArray1[i]);
                slideshow.add(bizArray2[c]);
                c++;
            }
        } else {
            for (int i = 0; i < bizArray2.length; i++) {
                if (c >= bizArray1.length) {
                    c = 0;
                }
                slideshow.add(bizArray2[i]);
                slideshow.add(bizArray1[c]);
                c++;
            }
        }
        String[] order = new String[slideshow.size()];
        slideshow.toArray(order);
        return order;
    }

    public static void buildSlideshow() throws ConnectionException, JSONException {
        correctSchedule();
        animateTransitions = true;
        File timeDir = new File(rootDir + "businesses/" + getHour());
        if (timeDir.isDirectory()) {
            File[] drive1;
            File[] drive2;
            File[] pass1;
            File[] pass2;
            File[] rear1;
            File[] rear2;
            File[] businesses = timeDir.listFiles();
            int count = 0;
            File pass = new File(businesses[0].getAbsolutePath() + "/passenger");
            File drive = new File(businesses[0].getAbsolutePath() + "/driver");
            File rear = new File(businesses[0].getAbsolutePath() + "/rear");
            drive1 = drive.listFiles();
            pass1 = pass.listFiles();
            rear1 = rear.listFiles();
            File pass3 = new File(businesses[1].getAbsolutePath() + "/passenger");
            File drive3 = new File(businesses[1].getAbsolutePath() + "/driver");
            File rear3 = new File(businesses[1].getAbsolutePath() + "/rear");
            drive2 = drive3.listFiles();
            pass2 = pass3.listFiles();
            rear2 = rear3.listFiles();
            rearSch = buildMediaArray(rear1, rear2);
            passSch = buildMediaArray(pass1, pass2);
            driveSch = buildMediaArray(drive1, drive2);
            ConnectToSalesForce.setSessionId();
            ConnectToSalesForce.UpdateStatus("Active", "Schedule running normally");
            Logging.logMessage("INFO:Built Normal Slideshow");
        } else {
            if (checkOutsideHours()) {
                ConnectToSalesForce.setSessionId();
                ConnectToSalesForce.UpdateStatus("Active", "Schedule over, running after hours");
                Logging.logMessage("INFO:Started Outside Hours");
            } else {
                ConnectToSalesForce.setSessionId();
                ConnectToSalesForce.UpdateStatus("Error", "Failed to build schedule, attempt reboot");
            }
        }
    }

    public static void correctSchedule() throws ConnectionException, JSONException {
        if (Config.getScheduleDiff()) {
            paused = true;
            splashAll();
            Config.salesforceConfig();
            bootUpDownload();
            paused = false;
        }
    }

    public static void getSplashAndAfter() throws ConnectionException, JSONException {
        JSONObject splashAndAfterData = new JSONObject(ConnectToSalesForce.GetSplashAndAfterScreen());
        JSONArray data = splashAndAfterData.getJSONArray("MediaArray");
        for (int i = 0; i < data.length(); i++) {
            JSONObject working = data.getJSONObject(i);
            String imageBytes = working.getString("body");
            String extension = working.getString("fileType");
            if ("OutsideHours".equals(working.getString("name"))) {
                ConfigSettings.setSplashType(extension);
                ConfigSettings.setSplashData(imageBytes);
                String splashPath = rootDir + "extras/SplashScreen." + extension;
                ConfigSettings.setSplashScreen(splashPath);
                Base64ToPNG.convert(imageBytes, splashPath);
            } else if ("Splash".equals(working.getString("name"))) {
                String afterPath = rootDir + "extras/AfterHours." + extension;
                ConfigSettings.setOutsideHours(afterPath);
                Base64ToPNG.convert(imageBytes, afterPath);
            }
        }
//        String filetype = splashData.getString("fileType");
//        String data = splashData.getString("body");
//        ConfigSettings.setSplashType(filetype);
//        ConfigSettings.setSplashData(data);
//        String splashPath = rootDir + "extras/SplashScreen." + filetype;
//        ConfigSettings.setSplashScreen(splashPath);
//        Base64ToPNG.convert(data, splashPath);
        //String afterfiletype = afterHoursData.getString("fileType");
        //String afterdata = afterHoursData.getString("body");
        //String afterPath = "extras/AfterHours." + afterfiletype;
        //ConfigSettings.setOutsideHours(afterPath);
        //Base64ToPNG.convert(afterdata, rootDir + afterPath);
    }
    public static void splashAll() {
        if (Screens == 1) {
                        DisplayImage.FirstScreen.swapImage(ConfigSettings.getSplashScreen(), false);
                    } else if (Screens == 2) {
                        DisplayImage.FirstScreen.swapImage(ConfigSettings.getSplashScreen(), false);
                        DisplayImage.SecondScreen.swapImage(ConfigSettings.getSplashScreen(), false);
                    } else {
                        DisplayImage.FirstScreen.swapImage(ConfigSettings.getSplashScreen(), false);
                        DisplayImage.SecondScreen.swapImage(ConfigSettings.getSplashScreen(), false);
                        DisplayImage.ThirdScreen.swapImage(ConfigSettings.getSplashScreen(), false);
                    }
    }
    public static void main(String[] args) {
        try {
            Config.readConfigFile();
            Logging.setupLogger();
            Config.salesforceConfig();

            initialize();
        } catch (JSONException ex) {
            Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                ConnectToSalesForce.UpdateStatus("Error", "Failed to parse JSON");
            } catch (ConnectionException ex1) {
                Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (ConnectionException ex) {
            Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
