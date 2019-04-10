package com.runningboards.mediaplayer;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DisplayImage extends Component{
    public BufferedImage img;
    public static CustomFrame FirstScreen;
    public static CustomFrame SecondScreen;
    public static CustomFrame ThirdScreen;

    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

    public static int setup(String splash_screen_path) {

        GraphicsEnvironment ge = GraphicsEnvironment.
                getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        String screenName = null;
        for (int j = 0; j < ge.getScreenDevices().length; j++) {
            if (j > 2) {
                System.out.println("Error: Too many screens");
                break;
            }
            int height = gs[j].getDefaultConfiguration().getBounds().height;
            int width = gs[j].getDefaultConfiguration().getBounds().width;
            if (j == 0){
                screenName = "FirstScreen";
            } else if (j == 1) {
                screenName = "SecondScreen";
            }else if (j == 2) {
                screenName = "ThirdScreen";
            } else {
                System.out.println("Something went wrong with the screen names");
                break;
            }
            GraphicsDevice gd = gs[j];
            GraphicsConfiguration[] gc = gd.getConfigurations();
            if (j == 0){
                FirstScreen = new CustomFrame(screenName,gs[j].getDefaultConfiguration());
                FirstScreen.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                gs[0].setFullScreenWindow(FirstScreen);
                FirstScreen.setHeightWidth(height, width);
                FirstScreen.swapImage(splash_screen_path, false);
                FirstScreen.setSize(width, height);
                //FirstScreen.setExtendedState(JFrame.MAXIMIZED_BOTH);
                FirstScreen.setAlwaysOnTop(true);
                //FirstScreen.setUndecorated(true);
                FirstScreen.setVisible(true);
                FirstScreen.repaint();
            } else if (j == 1) {
                SecondScreen = new CustomFrame(screenName, gs[j].getDefaultConfiguration());
                SecondScreen.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                gs[1].setFullScreenWindow(SecondScreen);
                SecondScreen.setHeightWidth(height, width);
                SecondScreen.swapImage(splash_screen_path, false);
                SecondScreen.setSize(width, height);
                //SecondScreen.setExtendedState(JFrame.MAXIMIZED_BOTH);
                SecondScreen.setAlwaysOnTop(true);
                //SecondScreen.setUndecorated(true);
                SecondScreen.setVisible(true);
                SecondScreen.repaint();
            }else if (j == 2) {
                ThirdScreen = new CustomFrame(screenName, gs[j].getDefaultConfiguration());
                gs[2].setFullScreenWindow(ThirdScreen);
                ThirdScreen.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                ThirdScreen.setHeightWidth(height, width);
                ThirdScreen.swapImage(splash_screen_path, false);
                ThirdScreen.setSize(width, height);
                //ThirdScreen.setExtendedState(JFrame.MAXIMIZED_BOTH);
                ThirdScreen.setAlwaysOnTop(true);
                //ThirdScreen.setUndecorated(true);
                ThirdScreen.setVisible(true);
            } else {
                System.out.println("Something went wrong with the screen names");
                break;
            }
        }
        return ge.getScreenDevices().length;
    }
}