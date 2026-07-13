package com.slipgaji.view;

import com.slipgaji.controller.AuthController;
import com.slipgaji.model.User;
import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MainView — dark-navy sidebar + soft neutral content area.
 *
 * <p>Sidebar mengikuti token {@link UIColors#SIDEBAR_BG}, item aktif
 * mendapat indicator kiri 3px berwarna {@link UIColors#PRIMARY_500}.
 */
public class MainView extends JFrame {
    private final User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private final Map<String, SidebarItem> menuButtons = new LinkedHashMap<>();

    // Panels
    private DashboardPanel dashboardPanel;
    private PresensiPanel presensiPanel;
    private KelolaKaryawanPanel kelolaKaryawanPanel;
    private HistoryPresensiPanel historyPresensiPanel;
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

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIColors.NEUTRAL_50);
        setContentPane(mainPanel);

        // Sidebar
        mainPanel.add(createSidebar(), BorderLayout.WEST);

        // Content wrapper
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(UIColors.NEUTRAL_50);
        contentWrapper.setBorder(new EmptyBorder(UIMetrics.SPACE_16, UIMetrics.SPACE_16,
                                                  UIMetrics.SPACE_16, UIMetrics.SPACE_16));

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        dashboardPanel = new DashboardPanel(this);
        presensiPanel = new PresensiPanel();
        kelolaKaryawanPanel = new KelolaKaryawanPanel();
        historyPresensiPanel = new HistoryPresensiPanel();
        payslipPanel = new PayslipPanel();
        historyPanel = new HistoryPanel();

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(presensiPanel, "presensi");
        contentPanel.add(kelolaKaryawanPanel, "kelola");
        contentPanel.add(historyPresensiPanel, "riwayat_presensi");
        contentPanel.add(payslipPanel, "payslip");
        contentPanel.add(historyPanel, "history");

        if (AuthController.isManager()) {
            settingsPanel = new SettingsPanel();
            contentPanel.add(settingsPanel, "settings");
        }

        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        switchPanel("dashboard");
    }

    // ============================================================
    // Sidebar
    // ============================================================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIColors.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(UIMetrics.SIDEBAR_WIDTH, 0));

        // App brand header
        sidebar.add(createBrandHeader());
        sidebar.add(createSidebarDivider());
        sidebar.add(Box.createVerticalStrut(UIMetrics.SPACE_16));

        sidebar.add(createSectionLabel("MENU"));
        addMenuItem(sidebar, "dashboard", "Dashboard");
        addMenuItem(sidebar, "presensi", "Presensi Scan");
        addMenuItem(sidebar, "kelola", "Kelola Karyawan");
        addMenuItem(sidebar, "riwayat_presensi", "Riwayat Presensi");
        addMenuItem(sidebar, "payslip", "Slip Gaji");
        addMenuItem(sidebar, "history", "Histori");

        if (AuthController.isManager()) {
            sidebar.add(Box.createVerticalStrut(UIMetrics.SPACE_20));
            sidebar.add(createSectionLabel("ADMIN"));
            addMenuItem(sidebar, "settings", "Pengaturan");
        }

        sidebar.add(Box.createVerticalGlue());

        sidebar.add(createSidebarDivider());
        sidebar.add(createUserFooter());

        return sidebar;
    }

    private JPanel createBrandHeader() {
        JPanel h = new JPanel();
        h.setLayout(new BoxLayout(h, BoxLayout.Y_AXIS));
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(UIMetrics.SPACE_24, UIMetrics.SPACE_20,
                                     UIMetrics.SPACE_20, UIMetrics.SPACE_20));
        h.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appName = new JLabel(Constants.APP_NAME);
        appName.setFont(new Font(UIFonts.FAMILY, Font.BOLD, 18));
        appName.setForeground(UIColors.SIDEBAR_TEXT_ACTIVE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel version = new JLabel("v" + Constants.APP_VERSION);
        version.setFont(UIFonts.CAPTION);
        version.setForeground(UIColors.SIDEBAR_TEXT_INACTIVE);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);

        h.add(appName);
        h.add(Box.createVerticalStrut(2));
        h.add(version);
        h.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        return h;
    }

    private JLabel createSectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(UIFonts.FAMILY, Font.BOLD, 11));
        l.setForeground(new Color(UIColors.SIDEBAR_TEXT_INACTIVE.getRed(),
                                   UIColors.SIDEBAR_TEXT_INACTIVE.getGreen(),
                                   UIColors.SIDEBAR_TEXT_INACTIVE.getBlue(), 180));
        l.setBorder(new EmptyBorder(0, UIMetrics.SPACE_20, UIMetrics.SPACE_8, UIMetrics.SPACE_20));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        return l;
    }

    private Component createSidebarDivider() {
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(UIColors.SIDEBAR_DIVIDER);
                g.drawLine(UIMetrics.SPACE_16, 0, getWidth() - UIMetrics.SPACE_16, 0);
            }
        };
        sep.setOpaque(false);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setPreferredSize(new Dimension(0, 1));
        return sep;
    }

    private void addMenuItem(JPanel sidebar, String key, String text) {
        SidebarItem item = new SidebarItem(text);
        item.addActionListener(e -> switchPanel(key));
        menuButtons.put(key, item);
        sidebar.add(item);
        sidebar.add(Box.createVerticalStrut(2));
    }

    private JPanel createUserFooter() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(UIMetrics.SPACE_16, UIMetrics.SPACE_20,
                                     UIMetrics.SPACE_16, UIMetrics.SPACE_20));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel name = new JLabel(currentUser.getUsername());
        name.setFont(UIFonts.BODY_BOLD);
        name.setForeground(UIColors.SIDEBAR_TEXT_ACTIVE);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel role = new JLabel(currentUser.getRole().getDisplayName());
        role.setFont(UIFonts.CAPTION);
        role.setForeground(UIColors.SIDEBAR_TEXT_INACTIVE);
        role.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Logout — Ghost text button (soft) sesuai spec 4.1
        JButton logout = new JButton("Logout") {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hover) {
                    g2.setColor(new Color(255, 255, 255, 18));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                            UIMetrics.RADIUS_BUTTON * 2, UIMetrics.RADIUS_BUTTON * 2));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logout.setFont(UIFonts.BODY_BOLD);
        logout.setForeground(UIColors.SIDEBAR_TEXT_ACTIVE);
        logout.setBorderPainted(false);
        logout.setContentAreaFilled(false);
        logout.setFocusPainted(false);
        logout.setOpaque(false);
        logout.setHorizontalAlignment(SwingConstants.LEFT);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.setBorder(new EmptyBorder(6, 8, 6, 8));
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        logout.addActionListener(e -> {
            if (UIHelper.showConfirm(p, "Yakin ingin logout?")) {
                AuthController.logout();
                new LoginView().setVisible(true);
                dispose();
            }
        });

        p.add(name);
        p.add(Box.createVerticalStrut(2));
        p.add(role);
        p.add(Box.createVerticalStrut(UIMetrics.SPACE_8));
        p.add(logout);
        return p;
    }

    private void switchPanel(String key) {
        if (!"presensi".equals(key) && presensiPanel != null) {
            presensiPanel.stopCamera();
        }
        cardLayout.show(contentPanel, key);
        menuButtons.forEach((k, btn) -> btn.setActive(k.equals(key)));

        switch (key) {
            case "dashboard" -> dashboardPanel.refresh();
            case "presensi" -> {
                if (presensiPanel != null) presensiPanel.startCamera();
            }
            case "kelola" -> { if (kelolaKaryawanPanel != null) kelolaKaryawanPanel.refresh(); }
            case "riwayat_presensi" -> { if (historyPresensiPanel != null) historyPresensiPanel.refresh(); }
            case "payslip" -> payslipPanel.refresh();
            case "history" -> historyPanel.refresh();
        }
    }

    public void navigateToPayslips() { switchPanel("payslip"); }
    public void navigateToPresensi() { switchPanel("presensi"); }

    // ============================================================
    // Sidebar item — custom-painted with left indicator
    // ============================================================
    private static class SidebarItem extends JButton {
        private boolean active = false;
        private boolean hovered = false;

        SidebarItem(String text) {
            super(text);
            setFont(UIFonts.BODY);
            setForeground(UIColors.SIDEBAR_TEXT_INACTIVE);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setBorder(new EmptyBorder(11, UIMetrics.SPACE_20, 11, UIMetrics.SPACE_20));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, UIMetrics.SIDEBAR_ITEM_HEIGHT));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    if (!active) { hovered = true; repaint(); }
                }
                @Override public void mouseExited(MouseEvent e) {
                    hovered = false; repaint();
                }
            });
        }

        void setActive(boolean active) {
            this.active = active;
            setFont(active ? UIFonts.BODY_BOLD : UIFonts.BODY);
            setForeground(active ? UIColors.SIDEBAR_TEXT_ACTIVE : UIColors.SIDEBAR_TEXT_INACTIVE);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (active) {
                // subtle bg overlay 4%
                g2.setColor(UIColors.SIDEBAR_BG_ACTIVE);
                g2.fillRect(0, 0, w, h);
                // left indicator 3px soft-blue
                g2.setColor(UIColors.PRIMARY_500);
                g2.fillRect(0, 6, UIMetrics.SIDEBAR_ACTIVE_INDICATOR, h - 12);
            } else if (hovered) {
                g2.setColor(new Color(255, 255, 255, 8));
                g2.fillRect(0, 0, w, h);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
