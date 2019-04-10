package com.runningboards.mediaplayer;

import com.sforce.ws.ConnectionException;
import java.io.*;
import java.util.Base64;

public class Base64ToPNG {
    static byte[] byteArray;

    public static void convert(String stringToConvert, String path) throws ConnectionException {

        try {
            FileOutputStream fos = new FileOutputStream(path);
            byte[] byteArray = Base64.getDecoder().decode(stringToConvert);
            fos.write(byteArray);

            fos.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ConnectToSalesForce.setSessionId();
                ConnectToSalesForce.UpdateStatus("Error", "Failed to decode file to: " + path);
        }
    }



}
