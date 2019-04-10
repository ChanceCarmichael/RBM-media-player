package com.runningboards.mediaplayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CustomFrame extends JFrame {
    public int height;
    public int width;
    public JPanel p1 = new JPanel((new GridLayout(1, 0, 0, 0)));
    public JPanel p2 = new JPanel((new GridLayout(1, 0, 0, 0)));
    public JLabel l1 = new JLabel();
    public JLabel l2 = new JLabel();
    public Boolean p1Active = true;
    public JLayeredPane MasterPanel = new JLayeredPane();
    public static int delay = 1;

    public CustomFrame(String screenName, GraphicsConfiguration defaultConfiguration) {
        super(screenName, defaultConfiguration);
        this.add(MasterPanel, BorderLayout.CENTER);
        MasterPanel.setVisible(true);
        MasterPanel.setLayout(new OverlayLayout(MasterPanel));
        p1.add(l1);
        p2.add(l2);
    }

    public void setHeightWidth(int h, int w) {
        height = h;
        width = w;
        MasterPanel.setSize(h,w);
        p1.setSize(h,w);
        p2.setSize(h,w);
    }

    public Image fitImage(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void swapImage(String path, Boolean animated) {
        if (animated) {
            try {
                animatedSwap(path);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            unAnimatedSwap(path);
        }
    }

    private void unAnimatedSwap(String path) {
        MasterPanel.removeAll();
        MasterPanel.add(p1);
        MasterPanel.setLayer(p1, 1);
        l1.setIcon(new ImageIcon(fitImage(path)));
    }

    private void animatedSwap(String path) throws InterruptedException {
        int c = 1;
        if (p1Active) {
            MasterPanel.add(p2);
            MasterPanel.setLayer(p2, 0);
            MasterPanel.setLayer(p1, 1);
            this.repaint();
            l2.setIcon(new ImageIcon(fitImage(path)));
            for (int i = 0; i < l1.getWidth(); i+= 5) {
                p1.setLocation(p1.getX() - 5, p1.getY());
                MasterPanel.repaint();
                if (c > 0) {
                    TimeUnit.NANOSECONDS.sleep(delay);
                    //c = c * -1;
                }
            }
            MasterPanel.remove(p1);
            p1Active = false;
            p1.setLocation(0, 0);
            MasterPanel.setLayer(p2, 3);
            this.repaint();
        } else {
            MasterPanel.add(p1);
            MasterPanel.setLayer(p1, 1);
            this.repaint();
            l1.setIcon(new ImageIcon(fitImage(path)));
            for (int i = 0; i < l2.getWidth(); i += 5) {
                p2.setLocation(p2.getX() - 5, p2.getY());
                MasterPanel.repaint();
                if (c > 0) {
                    TimeUnit.NANOSECONDS.sleep(delay);
                    //c = c * -1;
                }
            }
            MasterPanel.remove(p2);
            p1Active = true;
            p2.setLocation(0, 0);
            MasterPanel.setLayer(p1, 4);
            this.repaint();
        }
    }
}

