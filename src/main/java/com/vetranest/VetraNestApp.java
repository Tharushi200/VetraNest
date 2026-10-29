package com.vetranest;

import com.vetranest.config.AppTheme;
import com.vetranest.view.auth.LoginFrame;
import com.vetranest.view.auth.SplashScreen;

import javax.swing.*;

/**
 * VetraNest Enterprise Application Entry Point.
 */
public class VetraNestApp {

    public static void main(String[] args) {
        // Initialize FlatLaf Theme & UI configurations
        AppTheme.setupFlatLaf();

        // Launch Smooth Animated Splash Screen on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.startLoading(() -> {
                // When loading reaches 100%, transition smoothly to Login Frame
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        });
    }
}
