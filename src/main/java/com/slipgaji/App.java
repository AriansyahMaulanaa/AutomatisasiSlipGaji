package com.slipgaji;

import com.formdev.flatlaf.FlatLightLaf;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.util.Constants;
import com.slipgaji.view.LoginView;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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

        // Register SF Pro font
        try {
            File sfPro = new File("/usr/share/fonts/sfpro/SF-Pro.ttf");
            if (sfPro.exists()) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, sfPro);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(f);
            }
        } catch (Exception e) {
            System.err.println("SF Pro font registration skipped: " + e.getMessage());
        }

        try {
            // Configure FlatLaf for modern look
            UIManager.put("Button.arc", Constants.BUTTON_RADIUS * 2);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.trackArc", 999);
            UIManager.put("TabbedPane.selectedBackground", Constants.BG_CARD);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);

            // Set default font
            UIManager.put("defaultFont", new Font(Constants.FONT_FAMILY, Font.PLAIN, 13));

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
