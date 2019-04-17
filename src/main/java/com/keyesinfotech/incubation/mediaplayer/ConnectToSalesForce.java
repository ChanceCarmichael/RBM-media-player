/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//https://jjldev-runningboards.cs47.force.com
package com.runningboards.mediaplayer;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.Connector;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *XiaQ9K53lLieGcwE2fhl1I6o
 * @author joe
 */
public class ConnectToSalesForce {
    final static String username = MediaPlayer.sfUser;
    final static String password = MediaPlayer.sfPass;
    static EnterpriseConnection connection;
    static String sessionId = "";
    static ConnectorConfig config = new ConnectorConfig();
    static ConnectorConfig callConfig = new ConnectorConfig();
    
    
    public static void setSessionId() throws ConnectionException { 
        config.setUsername(MediaPlayer.sfUser);
        config.setPassword(MediaPlayer.sfPass);
        config.setAuthEndpoint("https://login.salesforce.com/services/Soap/c/45.0");
        connection = Connector.newConnection(config);
        sessionId = config.getSessionId();
        callConfig.setSessionId(sessionId);
        callConfig.setAuthEndpoint(config.getAuthEndpoint());
        callConfig.setServiceEndpoint(config.getServiceEndpoint());
    }
    public static String UpdateStatus(String status, String message) throws ConnectionException {
        com.sforce.soap.MediaPlayerService.SoapConnection soap = com.sforce.soap.MediaPlayerService.Connector.newConnection("","");
        soap.setSessionHeader(config.getSessionId());
        String response = soap.updateStatus(MediaPlayer.playerId, status, message);
        return response;
    }
    public static String SendLogs(String logString) throws ConnectionException {
        com.sforce.soap.MediaPlayerService.SoapConnection soap = com.sforce.soap.MediaPlayerService.Connector.newConnection("","");
        soap.setSessionHeader(config.getSessionId());
        String response = soap.processLogs(MediaPlayer.playerId, logString);
        return response;
    }
    public static String GetSchedule() throws ConnectionException {
        com.sforce.soap.MediaPlayerService.SoapConnection soap = com.sforce.soap.MediaPlayerService.Connector.newConnection("","");
        soap.setSessionHeader(config.getSessionId());
        String response = soap.getCurrentSchedule(MediaPlayer.playerId);
        return response;
    }
    public static String GetMediaFiles(String contractId, int time, int page) throws ConnectionException {
        com.sforce.soap.MediaPlayerService.SoapConnection soap = com.sforce.soap.MediaPlayerService.Connector.newConnection("","");
        soap.setSessionHeader(config.getSessionId());
        String response = soap.getMediaFiles(contractId, time, page);
        return response;
    }
    public static String GetSplashAndAfterScreen() throws ConnectionException {
        com.sforce.soap.MediaPlayerService.SoapConnection soap = com.sforce.soap.MediaPlayerService.Connector.newConnection("","");
        soap.setSessionHeader(config.getSessionId());
        String response = soap.getMediaPlayerScreens(MediaPlayer.playerId);
        return response;
    }
    public static void main(String[] args) throws ConnectionException {
        setSessionId();
        System.out.println(GetSchedule());
        System.out.println(GetSplashAndAfterScreen());
    }
}
