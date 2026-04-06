package com.slipgaji.view;

import com.slipgaji.controller.AuthController;
import com.slipgaji.model.User;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private JButton loginButton;
    private final AuthController authController;

    public LoginView() {
        this.authController = new AuthController();
        initUI();
    }

    private void initUI() {
        setTitle(Constants.APP_NAME + " - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setResizable(true);

        // Main panel with soft gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Soft gradient: Indigo to Purple (diagonal)
                GradientPaint gp = new GradientPaint(
                        0, 0, Constants.LOGIN_GRADIENT_START,
                        getWidth(), getHeight(), Constants.LOGIN_GRADIENT_END
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle decorative circles
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(-100, -100, 400, 400);
                g2.fillOval(getWidth() - 200, getHeight() - 200, 350, 350);
                g2.setColor(new Color(255, 255, 255, 8));
                g2.fillOval(getWidth() / 2 - 150, -50, 300, 300);

                g2.dispose();
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        // Login card with shadow
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int radius = 24;

                // Multi-layer shadow
                for (int i = 5; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 6 * (6 - i)));
                    g2.fill(new RoundRectangle2D.Double(i + 2, i + 3, w - (i + 2) * 2, h - (i + 3) * 2 + 2, radius, radius));
                }

                // Card background
                g2.setColor(new Color(255, 255, 255, 250));
                g2.fill(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, radius, radius));

                // Very subtle border
                g2.setColor(new Color(255, 255, 255, 180));
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, radius, radius));

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(48, 48, 48, 48));

        // Make card responsive (~32% of window width)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Set initial card size
        card.setPreferredSize(new Dimension(420, 520));

        // Responsive resizing
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int pw = mainPanel.getWidth();
                int cardWidth = Math.max(380, Math.min(450, (int)(pw * 0.33)));
                card.setPreferredSize(new Dimension(cardWidth, 520));
                card.revalidate();
            }
        });

        // Logo / Icon
        JLabel iconLabel = new JLabel("") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient circle
                GradientPaint gp = new GradientPaint(0, 0, Constants.LOGIN_GRADIENT_START,
                        64, 64, Constants.LOGIN_GRADIENT_END);
                g2.setPaint(gp);
                g2.fillOval(0, 0, 56, 56);
                // Inner icon (document symbol)
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
                FontMetrics fm = g2.getFontMetrics();
                String icon = "₪";
                int x = (56 - fm.stringWidth(icon)) / 2;
                int y = (56 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(icon, x, y);
                g2.dispose();
            }
        };
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(56, 56));
        iconLabel.setMaximumSize(new Dimension(56, 56));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel(Constants.APP_NAME);
        titleLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 24));
        titleLabel.setForeground(Constants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sistem Otomatisasi Slip Gaji");
        subtitleLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 13));
        subtitleLabel.setForeground(Constants.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 12));
        userLabel.setForeground(Constants.TEXT_SECONDARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = UIHelper.createStyledTextField("Masukkan username");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 12));
        passLabel.setForeground(Constants.TEXT_SECONDARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = UIHelper.createStyledPasswordField("Masukkan password");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(Constants.FONT_SMALL);
        errorLabel.setForeground(Constants.ACCENT_DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login button with gradient
        loginButton = new JButton("Masuk") {
            private float hoverAlpha = 0f;

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hoverAlpha = 0.12f;
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

                if (!isEnabled()) {
                    g2.setColor(Constants.BORDER_COLOR);
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, Constants.LOGIN_GRADIENT_START,
                            w, 0, Constants.LOGIN_GRADIENT_END);
                    g2.setPaint(gp);
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 16, 16));

                if (hoverAlpha > 0 && isEnabled()) {
                    g2.setColor(new Color(255, 255, 255, (int)(hoverAlpha * 255)));
                    g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 16, 16));
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginButton.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(new EmptyBorder(12, 20, 12, 20));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> doLogin());

        // Default credentials info
        JLabel infoLabel = new JLabel("<html><center><span style='color:#9CA3AF;font-size:10px'>"
                + "Akses eksklusif untuk Manager / Admin</span></center></html>");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Version label
        JLabel versionLabel = new JLabel("v" + Constants.APP_VERSION);
        versionLabel.setFont(Constants.FONT_SMALL);
        versionLabel.setForeground(new Color(156, 163, 175));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Enter key listener
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        // Assemble card with improved spacing
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(36));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(20));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(20));
        card.add(infoLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(versionLabel);

        mainPanel.add(card, gbc);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            loginButton.setEnabled(false);
            loginButton.setText("⏳ Memproses...");

            User user = authController.login(username, password);

            if (user.getRole() != User.Role.GENERAL_MANAGER) {
                errorLabel.setText("⚠ Akses Ditolak! Hanya untuk Admin/Manager.");
                loginButton.setEnabled(true);
                loginButton.setText("Masuk");
                shakeWindow();
                AuthController.logout();
                return;
            }

            // Open main view
            SwingUtilities.invokeLater(() -> {
                MainView mainView = new MainView(user);
                mainView.setVisible(true);
                this.dispose();
            });

        } catch (IllegalArgumentException ex) {
            errorLabel.setText("⚠ " + ex.getMessage());
            loginButton.setEnabled(true);
            loginButton.setText("Masuk");
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
