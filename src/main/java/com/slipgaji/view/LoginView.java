package com.slipgaji.view;

import com.slipgaji.controller.AuthController;
import com.slipgaji.model.User;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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
        setSize(480, 620);
        setLocationRelativeTo(null);
        setResizable(true); // Made responsive

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Constants.BG_DARK,
                        getWidth(), getHeight(), new Color(30, 27, 75));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        // Login card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 24, 24));
                g2.setColor(Constants.BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 24, 24));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(380, 480));

        // Logo / Icon
        JLabel iconLabel = new JLabel("") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Constants.PRIMARY);
                g2.fillOval(0, 0, 64, 64);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(64, 64));
        iconLabel.setMaximumSize(new Dimension(64, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel(Constants.APP_NAME);
        titleLabel.setFont(Constants.FONT_TITLE);
        titleLabel.setForeground(Constants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sistem Otomatisasi Slip Gaji");
        subtitleLabel.setFont(Constants.FONT_SMALL);
        subtitleLabel.setForeground(Constants.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(Constants.FONT_BODY);
        userLabel.setForeground(Constants.TEXT_SECONDARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = UIHelper.createStyledTextField("Masukkan username");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Constants.FONT_BODY);
        passLabel.setForeground(Constants.TEXT_SECONDARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = UIHelper.createStyledPasswordField("Masukkan password");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(Constants.FONT_SMALL);
        errorLabel.setForeground(Constants.ACCENT_DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login button
        loginButton = UIHelper.createStyledButton("Masuk", Constants.PRIMARY);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> doLogin());

        // Default credentials info
        JLabel infoLabel = new JLabel("<html><center><span style='color:#64748b;font-size:9px'>"
                + "Akses eksklusif untuk Manager / Admin</span></center></html>");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Enter key listener
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        // Assemble card
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(32));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(16));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(16));
        card.add(infoLabel);

        mainPanel.add(card);
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
            int dx = (count[0] % 2 == 0) ? 10 : -10;
            setLocation(original.x + dx, original.y);
            count[0]++;
        });
        timer.start();
    }
}
