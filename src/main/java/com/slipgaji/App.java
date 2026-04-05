package com.slipgaji;

import com.formdev.flatlaf.FlatLightLaf;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.view.LoginView;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        try {
            // Initialize Database and Tables
            DatabaseService.getInstance().initialize();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Database initialization failed:\n" + e.getMessage(),
                    "Fatal Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize LaF");
        }

        // Launch the application GUI
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}
