package com.slipgaji.view;

import com.slipgaji.controller.AuthController;
import com.slipgaji.model.User;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainView extends JFrame {
    private final User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private final Map<String, JButton> menuButtons = new LinkedHashMap<>();

    // Panels
    private DashboardPanel dashboardPanel;
    private ImportPanel importPanel;
    private PayslipPanel payslipPanel;
    private HistoryPanel historyPanel;
    private SettingsPanel settingsPanel;

    public MainView(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle(Constants.APP_NAME + " - " + currentUser.getRole().getDisplayName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Constants.BG_DARK);
        setContentPane(mainPanel);

        // Sidebar
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Constants.BG_DARK);

        dashboardPanel = new DashboardPanel();
        importPanel = new ImportPanel(this);
        payslipPanel = new PayslipPanel();
        historyPanel = new HistoryPanel();

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(importPanel, "import");
        contentPanel.add(payslipPanel, "payslip");
        contentPanel.add(historyPanel, "history");

        if (currentUser.isGeneralManager()) {
            settingsPanel = new SettingsPanel();
            contentPanel.add(settingsPanel, "settings");
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Show dashboard by default
        switchPanel("dashboard");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Constants.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Constants.BORDER_COLOR));

        // App header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Constants.SIDEBAR_BG);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel appName = new JLabel(Constants.APP_NAME);
        appName.setFont(Constants.FONT_SUBTITLE);
        appName.setForeground(Constants.TEXT_PRIMARY);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel versionLabel = new JLabel("v" + Constants.APP_VERSION);
        versionLabel.setFont(Constants.FONT_SMALL);
        versionLabel.setForeground(Constants.TEXT_SECONDARY);
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(appName);
        headerPanel.add(Box.createVerticalStrut(2));
        headerPanel.add(versionLabel);
        sidebar.add(headerPanel);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(Constants.BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(12));

        // Menu label
        JLabel menuLabel = new JLabel("  MENU");
        menuLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 10));
        menuLabel.setForeground(Constants.TEXT_SECONDARY);
        menuLabel.setBorder(new EmptyBorder(0, 16, 8, 0));
        menuLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        sidebar.add(menuLabel);

        // Menu items
        addMenuItem(sidebar, "dashboard", "", "Dashboard");
        addMenuItem(sidebar, "import", "", "Import Data");
        addMenuItem(sidebar, "payslip", "", "Slip Gaji");
        addMenuItem(sidebar, "history", "", "Histori Pengiriman");

        if (currentUser.isGeneralManager()) {
            sidebar.add(Box.createVerticalStrut(12));
            JLabel adminLabel = new JLabel("  ADMIN");
            adminLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 10));
            adminLabel.setForeground(Constants.TEXT_SECONDARY);
            adminLabel.setBorder(new EmptyBorder(0, 16, 8, 0));
            adminLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
            sidebar.add(adminLabel);
            addMenuItem(sidebar, "settings", "", "Pengaturan");
        }

        sidebar.add(Box.createVerticalGlue());

        // User info at bottom
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(Constants.BORDER_COLOR);
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep2);

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(Constants.SIDEBAR_BG);
        userPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel userName = new JLabel(currentUser.getUsername());
        userName.setFont(Constants.FONT_BODY);
        userName.setForeground(Constants.TEXT_PRIMARY);
        userName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel(currentUser.getRole().getDisplayName());
        roleLabel.setFont(Constants.FONT_SMALL);
        roleLabel.setForeground(Constants.ACCENT);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton logoutBtn = UIHelper.createStyledButton("Logout", Constants.ACCENT_DANGER);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setFont(Constants.FONT_SMALL);
        logoutBtn.addActionListener(e -> {
            if (UIHelper.showConfirm(this, "Yakin ingin logout?")) {
                AuthController.logout();
                new LoginView().setVisible(true);
                this.dispose();
            }
        });

        userPanel.add(userName);
        userPanel.add(Box.createVerticalStrut(2));
        userPanel.add(roleLabel);
        userPanel.add(Box.createVerticalStrut(8));
        userPanel.add(logoutBtn);
        sidebar.add(userPanel);

        return sidebar;
    }

    private void addMenuItem(JPanel sidebar, String key, String icon, String text) {
        JButton btn = UIHelper.createSidebarButton(text, "");
        btn.addActionListener(e -> switchPanel(key));
        menuButtons.put(key, btn);
        sidebar.add(btn);
    }

    private void switchPanel(String key) {
        cardLayout.show(contentPanel, key);

        // Update sidebar button states
        menuButtons.forEach((k, btn) -> UIHelper.setSidebarButtonActive(btn, k.equals(key)));

        // Refresh panel data
        switch (key) {
            case "dashboard" -> dashboardPanel.refresh();
            case "payslip" -> payslipPanel.refresh();
            case "history" -> historyPanel.refresh();
        }
    }

    public void navigateToPayslips() {
        switchPanel("payslip");
    }
}
