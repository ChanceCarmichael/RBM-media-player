package com.runningboards.mediaplayer;

import com.sforce.ws.ConnectionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.*;
import java.util.Date;

public class Logging {
    public static Logger LOGGER = Logger.getLogger("runningboardslog");

    public static String getExactTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SS");
        Date dateObj = new Date();
        return dateFormat.format(dateObj);
    }

    public static void setupLogger() {

        FileHandler fh;
        //read(LogManager.getLogManager(), create());
        //Handler h = new ConsoleHandler();
        //System.out.println(h.getLevel());
        //h.close();
        SimpleFormatter formatter = new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] %3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                );
            }
        };

        try {
            fh = new FileHandler(MediaPlayer.logPath + "MediaPlayer%u.log", 1024000000, 1, true);
            LOGGER.addHandler(fh);
            fh.setFormatter(formatter);
        } catch (SecurityException | IOException s) {
            s.printStackTrace();
        }

    }

    private static void read(LogManager manager, Properties props) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream(512);
        props.store(out, "No comment");
        manager.readConfiguration(new ByteArrayInputStream(out.toByteArray()));
    }

    public static void logImagePlay(String image, String bizName, String startTime, String endTime) {
        String formatted = String.format("PLAYBACK:I=%s,B=%s,S=%s,E=%s", image, bizName, startTime, endTime);
        LOGGER.info(formatted);
    }
    public static void logMessage(String message) {
        LOGGER.info(message);
    }
}
