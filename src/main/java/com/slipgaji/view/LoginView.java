package com.slipgaji.view;

import com.slipgaji.controller.AuthController;
import com.slipgaji.model.User;
import com.slipgaji.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private JButton loginButton;
    private final AuthController authController;

    // Animation state for floating circles
    private float animOffset = 0f;
    private Timer animTimer;

    // Color scheme — Soft Blue (senada dengan sistem)
    private static final Color GRADIENT_START = new Color(37, 99, 235);    // Blue-600
    private static final Color GRADIENT_END   = new Color(59, 130, 246);   // Blue-500
    private static final Color LIGHT_BLUE_BG  = new Color(219, 234, 254); // Blue-100
    private static final Color FOCUS_RING     = new Color(59, 130, 246);   // Blue-500

    public LoginView() {
        this.authController = new AuthController();
        initUI();
        startAnimation();
    }

    private void startAnimation() {
        animTimer = new Timer(50, e -> {
            animOffset += 0.02f;
            if (animOffset > 2 * Math.PI) animOffset = 0;
            getContentPane().repaint();
        });
        animTimer.start();
    }

    private void initUI() {
        setTitle(Constants.APP_NAME + " - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true); // Remove title bar for rounded corners

        // Apply rounded shape to the window
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 28, 28));
            }
        });

        // Main container with rounded clipping
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 28, 28));
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();

        // ========== LEFT PANEL (Branding) ==========
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int w = getWidth();
                int h = getHeight();

                // Background gradient (Soft Blue diagonal)
                GradientPaint bgGrad = new GradientPaint(
                        0, 0, GRADIENT_START,
                        w, h, GRADIENT_END
                );
                g2.setPaint(bgGrad);
                // Rounded left corners
                g2.fill(new RoundRectangle2D.Double(0, 0, w + 20, h, 28, 28));

                // Animated floating circles (light blue tones)
                drawFloatingCircle(g2, w * 0.15, h * 0.2, 120, 18);
                drawFloatingCircle(g2, w * 0.75, h * 0.15, 80, 12);
                drawFloatingCircle(g2, w * 0.6, h * 0.7, 100, 15);
                drawFloatingCircle(g2, w * 0.25, h * 0.8, 60, 10);
                drawFloatingCircle(g2, w * 0.85, h * 0.5, 70, 8);
                drawFloatingCircle(g2, w * 0.4, h * 0.45, 50, 20);

                // Large decorative ring (top right)
                g2.setColor(new Color(255, 255, 255, 8));
                g2.setStroke(new BasicStroke(3));
                g2.draw(new Ellipse2D.Double(w * 0.5, -80, 300, 300));
                g2.draw(new Ellipse2D.Double(w * 0.55, -60, 250, 250));

                // Bottom-left ring
                g2.draw(new Ellipse2D.Double(-80, h * 0.6, 250, 250));

                // Wave at bottom
                drawWave(g2, w, h);

                g2.dispose();
            }

            private void drawFloatingCircle(Graphics2D g2, double cx, double cy, int size, int alphaBase) {
                double offsetY = Math.sin(animOffset + cx * 0.01) * 12;
                double offsetX = Math.cos(animOffset + cy * 0.01) * 8;
                g2.setColor(new Color(255, 255, 255, alphaBase));
                g2.fill(new Ellipse2D.Double(cx + offsetX - size / 2.0, cy + offsetY - size / 2.0, size, size));
            }

            private void drawWave(Graphics2D g2, int w, int h) {
                g2.setColor(new Color(255, 255, 255, 10));
                int waveH = 60;
                int baseY = h - waveH;
                java.awt.geom.GeneralPath wave = new java.awt.geom.GeneralPath();
                wave.moveTo(0, h);
                wave.lineTo(0, baseY + 20);
                for (int x = 0; x <= w; x += 4) {
                    double y = baseY + Math.sin((x + animOffset * 50) * 0.02) * 15
                            + Math.sin((x + animOffset * 30) * 0.01) * 10;
                    wave.lineTo(x, y);
                }
                wave.lineTo(w, h);
                wave.closePath();
                g2.fill(wave);
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setOpaque(false);

        // Left panel content
        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setOpaque(false);
        leftContent.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Large Logo
        JLabel logoLabel = new JLabel("") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glassy circle background
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillOval(0, 0, 88, 88);
                g2.setColor(new Color(255, 255, 255, 20));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(0, 0, 88, 88);

                // Icon
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 38));
                FontMetrics fm = g2.getFontMetrics();
                String icon = "₪";
                int x = (88 - fm.stringWidth(icon)) / 2;
                int y = (88 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(icon, x, y);

                g2.dispose();
            }
        };
        logoLabel.setPreferredSize(new Dimension(88, 88));
        logoLabel.setMaximumSize(new Dimension(88, 88));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // App Name (large)
        JLabel appNameLabel = new JLabel(Constants.APP_NAME);
        appNameLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 38));
        appNameLabel.setForeground(Color.WHITE);
        appNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tagline
        JLabel tagline = new JLabel("Sistem Otomatisasi Slip Gaji Karyawan");
        tagline.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 16));
        tagline.setForeground(new Color(191, 219, 254)); // Blue-200
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);


        // Feature list removed

        // Company info at bottom
        JLabel companyLabel = new JLabel(Constants.COMPANY_NAME);
        companyLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 12));
        companyLabel.setForeground(new Color(147, 197, 253)); // Blue-300
        companyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftContent.add(logoLabel);
        leftContent.add(Box.createVerticalStrut(28));
        leftContent.add(appNameLabel);
        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(tagline);
        leftContent.add(Box.createVerticalGlue());
        leftContent.add(companyLabel);

        leftPanel.add(leftContent);

        // ========== RIGHT PANEL (Login Form) ==========
        JPanel rightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new GridBagLayout());

        JPanel formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setOpaque(false);
        formWrapper.setBorder(new EmptyBorder(40, 56, 40, 56));
        formWrapper.setMaximumSize(new Dimension(480, 700));

        // Close button (top-right since we removed title bar)
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topBar.setOpaque(false);
        topBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        topBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JButton minimizeBtn = createWindowButton("—", false);
        minimizeBtn.addActionListener(e -> setState(Frame.ICONIFIED));
        JButton closeBtn = createWindowButton("✕", true);
        closeBtn.addActionListener(e -> System.exit(0));
        topBar.add(minimizeBtn);
        topBar.add(Box.createHorizontalStrut(6));
        topBar.add(closeBtn);

        // Welcome header
        JLabel welcomeLabel = new JLabel("Selamat Datang!");
        welcomeLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 30));
        welcomeLabel.setForeground(Constants.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeSub = new JLabel("Masuk ke akun Anda untuk melanjutkan");
        welcomeSub.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 15));
        welcomeSub.setForeground(Constants.TEXT_SECONDARY);
        welcomeSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 13));
        userLabel.setForeground(Constants.TEXT_PRIMARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = createLargeTextField("Masukkan username anda");
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 13));
        passLabel.setForeground(Constants.TEXT_PRIMARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = createLargePasswordField("Masukkan password anda");
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 12));
        errorLabel.setForeground(Constants.ACCENT_DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login button (blue gradient with rounded corners)
        loginButton = new JButton("Masuk ke Dashboard") {
            private float hoverAlpha = 0f;

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hoverAlpha = 0.15f;
                        repaint();
                    }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hoverAlpha = 0f;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = 16;

                // Shadow
                if (isEnabled()) {
                    g2.setColor(new Color(37, 99, 235, 40));
                    g2.fill(new RoundRectangle2D.Double(3, 4, w - 6, h - 2, arc, arc));
                }

                // Button fill
                if (!isEnabled()) {
                    g2.setColor(Constants.BORDER_COLOR);
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START,
                            w, 0, GRADIENT_END);
                    g2.setPaint(gp);
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h - 2, arc, arc));

                // Hover overlay
                if (hoverAlpha > 0 && isEnabled()) {
                    g2.setColor(new Color(255, 255, 255, (int)(hoverAlpha * 255)));
                    g2.fill(new RoundRectangle2D.Double(0, 0, w, h - 2, arc, arc));
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginButton.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 15));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(new EmptyBorder(16, 20, 16, 20));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> doLogin());

        // Access info
        JLabel infoLabel = new JLabel("<html><span style='color:#9CA3AF;font-size:11px'>" +
                "🔒 Akses eksklusif untuk General Manager / Admin</span></html>");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Version
        JLabel versionLabel = new JLabel("v" + Constants.APP_VERSION + " • Powered by MariaDB");
        versionLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 11));
        versionLabel.setForeground(new Color(209, 213, 219));
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Enter key listener
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        // Enable window dragging (since title bar is removed)
        enableDragging(mainPanel);

        // Assemble form
        formWrapper.add(topBar);
        formWrapper.add(Box.createVerticalStrut(12));
        formWrapper.add(welcomeLabel);
        formWrapper.add(Box.createVerticalStrut(8));
        formWrapper.add(welcomeSub);
        formWrapper.add(Box.createVerticalStrut(36));
        formWrapper.add(userLabel);
        formWrapper.add(Box.createVerticalStrut(8));
        formWrapper.add(usernameField);
        formWrapper.add(Box.createVerticalStrut(22));
        formWrapper.add(passLabel);
        formWrapper.add(Box.createVerticalStrut(8));
        formWrapper.add(passwordField);
        formWrapper.add(Box.createVerticalStrut(12));
        formWrapper.add(errorLabel);
        formWrapper.add(Box.createVerticalStrut(20));
        formWrapper.add(loginButton);
        formWrapper.add(Box.createVerticalStrut(20));
        formWrapper.add(infoLabel);
        formWrapper.add(Box.createVerticalGlue());
        formWrapper.add(versionLabel);

        rightPanel.add(formWrapper);

        // ========== LAYOUT: Left 55%, Right 45% ==========
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.weightx = 0.55;
        gbc.weighty = 1.0;
        mainPanel.add(leftPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.45;
        mainPanel.add(rightPanel, gbc);
    }

    private JButton createWindowButton(String text, boolean isDanger) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true; repaint(); }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovered) {
                    g2.setColor(isDanger ? new Color(239, 68, 68, 30) : new Color(0, 0, 0, 15));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 14));
        btn.setForeground(isDanger ? Constants.ACCENT_DANGER : Constants.TEXT_SECONDARY);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(36, 30));
        return btn;
    }

    private void enableDragging(JPanel panel) {
        final Point[] dragPoint = {null};
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                dragPoint[0] = e.getPoint();
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                dragPoint[0] = null;
            }
        });
        panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (dragPoint[0] != null) {
                    Point loc = getLocation();
                    setLocation(loc.x + e.getX() - dragPoint[0].x,
                                loc.y + e.getY() - dragPoint[0].y);
                }
            }
        });
    }

    private JTextField createLargeTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g3.setColor(new Color(156, 163, 175));
                    g3.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 14));
                    g3.drawString(placeholder, getInsets().left + 4, getHeight() / 2 + 5);
                    g3.dispose();
                }
            }
        };
        field.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 14));
        field.setForeground(Constants.TEXT_PRIMARY);
        field.setBackground(new Color(249, 250, 251));
        field.setCaretColor(Constants.TEXT_PRIMARY);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                new EmptyBorder(12, 18, 12, 18)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        field.setPreferredSize(new Dimension(400, 50));

        // Focus ring — blue
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FOCUS_RING, 2),
                        new EmptyBorder(11, 17, 11, 17)
                ));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                        new EmptyBorder(12, 18, 12, 18)
                ));
            }
        });

        return field;
    }

    private JPasswordField createLargePasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g3.setColor(new Color(156, 163, 175));
                    g3.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 14));
                    g3.drawString(placeholder, getInsets().left + 4, getHeight() / 2 + 5);
                    g3.dispose();
                }
            }
        };
        field.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 14));
        field.setForeground(Constants.TEXT_PRIMARY);
        field.setBackground(new Color(249, 250, 251));
        field.setCaretColor(Constants.TEXT_PRIMARY);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                new EmptyBorder(12, 18, 12, 18)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        field.setPreferredSize(new Dimension(400, 50));

        // Focus ring — blue
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FOCUS_RING, 2),
                        new EmptyBorder(11, 17, 11, 17)
                ));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                        new EmptyBorder(12, 18, 12, 18)
                ));
            }
        });

        return field;
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            loginButton.setEnabled(false);
            loginButton.setText("⏳ Memproses...");

            User user = authController.login(username, password);

            // Stop animation before transitioning
            if (animTimer != null) animTimer.stop();

            // Open main view
            SwingUtilities.invokeLater(() -> {
                MainView mainView = new MainView(user);
                mainView.setVisible(true);
                this.dispose();
            });

        } catch (IllegalArgumentException ex) {
            errorLabel.setText("⚠ " + ex.getMessage());
            loginButton.setEnabled(true);
            loginButton.setText("Masuk ke Dashboard");
            shakeWindow();
        }
    }

    private void shakeWindow() {
        Point original = getLocation();
        Timer timer = new Timer(30, null);
        final int[] count = {0};
        timer.addActionListener(e -> {
            if (count[0] >= 8) {
                timer.stop();
                setLocation(original);
                return;
            }
            int dx = (count[0] % 2 == 0) ? 8 : -8;
            setLocation(original.x + dx, original.y);
            count[0]++;
        });
        timer.start();
    }
}
