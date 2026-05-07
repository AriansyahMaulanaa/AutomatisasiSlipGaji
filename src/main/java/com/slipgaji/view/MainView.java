package com.slipgaji.view;

import com.slipgaji.controller.AuthController;
import com.slipgaji.model.User;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
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
        setSize(1280, 800);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Constants.BG_DARK);
        setContentPane(mainPanel);

        // Sidebar
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Content area with padding for floating card effect
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(Constants.BG_DARK);
        contentWrapper.setBorder(new EmptyBorder(16, 8, 16, 16));

<<<<<<< HEAD
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int r = Constants.CARD_RADIUS * 2;

                // Shadow
                for (int i = 3; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 5 * (4 - i)));
                    g2.fill(new RoundRectangle2D.Double(i, i + 1, w - i * 2, h - i * 2, r, r));
                }

                // Background
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, r, r));

                g2.dispose();
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

=======
>>>>>>> 0274c08
        dashboardPanel = new DashboardPanel(this);
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

        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        // Show dashboard by default
        switchPanel("dashboard");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // White sidebar background
                g2.setColor(Constants.SIDEBAR_BG);
                g2.fillRect(0, 0, w, h);

                // Subtle right shadow
                for (int i = 0; i < 4; i++) {
                    g2.setColor(new Color(0, 0, 0, 8 - i * 2));
                    g2.drawLine(w - 1 + i, 0, w - 1 + i, h);
                }

                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(Constants.SIDEBAR_WIDTH, 0));

        // App header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(24, 24, 20, 24));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        JLabel appName = new JLabel(Constants.APP_NAME);
        appName.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 20));
        appName.setForeground(Constants.TEXT_PRIMARY);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel versionLabel = new JLabel("v" + Constants.APP_VERSION);
        versionLabel.setFont(Constants.FONT_SMALL);
        versionLabel.setForeground(Constants.TEXT_SECONDARY);
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(appName);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(versionLabel);
        sidebar.add(headerPanel);

        // Separator
        sidebar.add(createSeparator());
        sidebar.add(Box.createVerticalStrut(16));

        // Menu label
        JLabel menuLabel = new JLabel("  MENU");
        menuLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 10));
        menuLabel.setForeground(new Color(156, 163, 175));
        menuLabel.setBorder(new EmptyBorder(0, 20, 10, 0));
        menuLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        sidebar.add(menuLabel);

        // Menu items
        addMenuItem(sidebar, "dashboard", "📊", "Dashboard");
        addMenuItem(sidebar, "import", "📥", "Import Data");
        addMenuItem(sidebar, "payslip", "📄", "Slip Gaji");
        addMenuItem(sidebar, "history", "📋", "Histori Pengiriman");

        if (currentUser.isGeneralManager()) {
            sidebar.add(Box.createVerticalStrut(16));
            JLabel adminLabel = new JLabel("  ADMIN");
            adminLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 10));
            adminLabel.setForeground(new Color(156, 163, 175));
            adminLabel.setBorder(new EmptyBorder(0, 20, 10, 0));
            adminLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
            sidebar.add(adminLabel);
            addMenuItem(sidebar, "settings", "⚙", "Pengaturan");
        }

        sidebar.add(Box.createVerticalGlue());

        // User info at bottom
        sidebar.add(createSeparator());

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setOpaque(false);
        userPanel.setBorder(new EmptyBorder(16, 20, 16, 20));
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel userName = new JLabel(currentUser.getUsername());
        userName.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 13));
        userName.setForeground(Constants.TEXT_PRIMARY);
        userName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel(currentUser.getRole().getDisplayName());
        roleLabel.setFont(Constants.FONT_SMALL);
        roleLabel.setForeground(Constants.PRIMARY);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton logoutBtn = UIHelper.createStyledButton("Logout", Constants.ACCENT_DANGER);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
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
        userPanel.add(Box.createVerticalStrut(3));
        userPanel.add(roleLabel);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(logoutBtn);
        sidebar.add(userPanel);

        return sidebar;
    }

    private JPanel createSeparator() {
        JPanel sep = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Constants.BORDER_COLOR);
                g.drawLine(20, 0, getWidth() - 20, 0);
            }
        };
        sep.setOpaque(false);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setPreferredSize(new Dimension(0, 1));
        return sep;
    }

    private void addMenuItem(JPanel sidebar, String key, String icon, String text) {
        JButton btn = UIHelper.createSidebarButton(icon + "  " + text, "");
        btn.addActionListener(e -> switchPanel(key));
        menuButtons.put(key, btn);
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(4));
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

    public void navigateToImport() {
        switchPanel("import");
    }
}
