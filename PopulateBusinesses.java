package com.runningboards.mediaplayer;

import com.sforce.ws.ConnectionException;
import java.io.BufferedOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PopulateBusinesses {
    private static final int Buffer_Size = 4096;
    public static void download(String fileUrl, String saveLocation) {
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(fileUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String filename = "";
                String disposition = httpURLConnection.getHeaderField("Content-Disposition");
                String contentType = httpURLConnection.getContentType();
                int contentLength = httpURLConnection.getContentLength();

                if (disposition != null) {
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        String fileName = disposition.substring(index + 10, disposition.length() -1);
                    }
                } else {
                    String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") +1, fileUrl.length());
                }

                InputStream stream = httpURLConnection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(saveLocation);

                int bytesRead = -1;
                byte[]  buffer = new byte[Buffer_Size];
                while ((bytesRead = stream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                stream.close();
            }else {
                System.exit(1);
            }
            httpURLConnection.disconnect();
        } catch (IOException e) {
            Logging.LOGGER.log(Level.SEVERE, e.getMessage());
        }



    }

//    public static void getImageFiles() {
//        try {
//
//            Document d = null;
//            String[] businesses = ConfigSettings.getUniqueBusinesses();
//
//            for (String b : businesses) {
//                Document doc = Jsoup.connect(MediaPlayer.businessesServer + b).get();
//                Elements links = doc.getElementsByTag("a");
//                File dir = new File(MediaPlayer.rootDir + "businesses/" + b);
//                if (!dir.exists()) {
//                    dir.mkdir();
//                }
//                int count = 0;
//                for (Element link : links) {
//                    if (count >= 5) {
//                        download(MediaPlayer.businessesServer + b + "/" + link.attr("href"), MediaPlayer.rootDir + "businesses/" + b + "/" + link.text());
//                    }
//                    count++;
//                }
//            }
//            d = Jsoup.connect(MediaPlayer.resourcesServer).get();
//            Elements linkz = d.getElementsByTag("a");
//            int c = 0;
//            for (Element link : linkz) {
//                if (c >= 5) {
//                    download(MediaPlayer.resourcesServer + "/" + link.attr("href"), MediaPlayer.rootDir + "resources/" + link.text());
//                }
//                c++;
//            }
//        } catch (IOException e) {
//            Logging.LOGGER.log(Level.SEVERE, e.getMessage());
//        }
//
//    }
    
    public static void getBusinessImages(String contractId, int time) throws ConnectionException, JSONException {
        
            ConnectToSalesForce.setSessionId();
            String driverDirPath = MediaPlayer.rootDir + "businesses/" + time + "/" + contractId + "/driver/";
            String passengerDirPath = MediaPlayer.rootDir + "businesses/" + time + "/" + contractId + "/passenger/";
            String rearDirPath = MediaPlayer.rootDir + "businesses/" + time + "/" + contractId + "/rear/";
            File driverDir = new File(driverDirPath);
            if (!driverDir.exists()) {
                driverDir.mkdirs();
            }
            File passengerDir = new File(passengerDirPath);
            if (!passengerDir.exists()) {
                passengerDir.mkdirs();
            }
            File rearDir = new File(rearDirPath);
            if (!rearDir.exists()) {
                rearDir.mkdirs();
            }
            try {
                int maxPages = 2;
                int currentPage = 1;
                while (currentPage <= maxPages) {
            String bs = ConnectToSalesForce.GetMediaFiles(contractId, time, currentPage);
            if (bs.length() < 1000 ) {
                ConnectToSalesForce.setSessionId();
                ConnectToSalesForce.UpdateStatus("Error", "Couldn't find media for " + contractId + "at" + time);
                String mediaId = "SPLASH";
                String extension = ConfigSettings.getSplashType();
                String imageBytes = ConfigSettings.getSplashData();
                Base64ToPNG.convert(imageBytes, MediaPlayer.rootDir + "businesses/" + time + "/" + contractId + "/rear" + "/" + mediaId + "-rearSide." + extension);
                Base64ToPNG.convert(imageBytes, MediaPlayer.rootDir + "businesses/" + time + "/" + contractId + "/driver" + "/" + mediaId + "-driverSide." + extension);
                Base64ToPNG.convert(imageBytes, MediaPlayer.rootDir + "businesses/" + time + "/" + contractId + "/passenger" + "/" + mediaId + "-passSide." + extension);
                currentPage++;
                continue;
            } else {
                JSONObject obj = new JSONObject(bs);
                maxPages = Integer.parseInt(obj.getString("totalPages"));
                JSONArray data = obj.getJSONArray("MediaArray");
                currentPage++;
                for (int i=0; i < data.length(); i++) {
                    JSONObject working = data.getJSONObject(i);
                    String mediaId = working.getString("mediaId");
                    String imageBytes = working.getString("body");
                    String extension = working.getString("fileType");
                    if ("Rear".equals(working.getString("name"))) {
                        Base64ToPNG.convert(imageBytes, MediaPlayer.rootDir + "businesses/" + time + "/" + contractId + "/rear" + "/" + mediaId + "-rearSide." + extension);
                    } else if ("Pass".equals(working.getString("name"))) {
                        Base64ToPNG.convert(imageBytes, MediaPlayer.rootDir + "businesses/" + time + "/" + contractId + "/passenger" + "/" + mediaId + "-passSide." + extension);
                    }else if ("Drive".equals(working.getString("name"))) {
                        Base64ToPNG.convert(imageBytes, MediaPlayer.rootDir + "businesses/" + time + "/" + contractId + "/driver" + "/" + mediaId + "-driverSide." + extension);
                    }
                }
            }
                }
            } catch (com.sforce.ws.SoapFaultException e) {
                System.out.println(e);
                ConnectToSalesForce.setSessionId();
                ConnectToSalesForce.UpdateStatus("Error", "Call too big for: " + contractId + "at" + time);
                String mediaId = "SPLASH";
                String extension = ConfigSettings.getSplashType();
                String imageBytes = ConfigSettings.getSplashData();
                Base64ToPNG.convert(imageBytes, MediaPlayer.rootDir + "businesses/" + time + "/" + contractId + "/rear" + "/" + mediaId + "-rearSide." + extension);
                Base64ToPNG.convert(imageBytes, MediaPlayer.rootDir + "businesses/" + time + "/" + contractId + "/driver" + "/" + mediaId + "-driverSide." + extension);
                Base64ToPNG.convert(imageBytes, MediaPlayer.rootDir + "businesses/" + time + "/" + contractId + "/passenger" + "/" + mediaId + "-passSide." + extension);

            }   
    }
}
