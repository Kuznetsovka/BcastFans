package com.systemair.bcastfans;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Objects;

public class LoadingWindow {

    public JFrame getInitWindow() {

        JFrame loadingFrame = new JFrame();

        URL urlIcon = this.getClass().getResource("/loading.gif");
        Icon icon = new ImageIcon(Objects.requireNonNull(urlIcon));
        JLabel labelIcon = new JLabel(icon);

        loadingFrame.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/logo.png")).getPath()).getImage());
        loadingFrame.setUndecorated(true);
        loadingFrame.setBackground(new Color(0f, 0f, 0f, 0f));

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        loadingFrame.setLocation((int) ((screenBounds.getWidth() - 600) / 2), (int) ((screenBounds.getHeight() - 0) / 2));
        loadingFrame.getContentPane().add(labelIcon);
        loadingFrame.pack();
        loadingFrame.setLocationRelativeTo(null);
        loadingFrame.setVisible(true);
        return loadingFrame;
    }

}