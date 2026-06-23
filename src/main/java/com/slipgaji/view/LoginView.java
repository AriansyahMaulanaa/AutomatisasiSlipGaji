package com.slipgaji.view;

import com.slipgaji.controller.AuthController;
import com.slipgaji.model.User;
import com.slipgaji.util.Constants;

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

    private static final Color GRADIENT_START = new Color(37, 99, 235);
    private static final Color GRADIENT_END   = new Color(59, 130, 246);

    public LoginView() {
        this.authController = new AuthController();
        initUI();
    }

    private void initUI() {
        setTitle("Masuk - " + Constants.APP_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 640);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();

        // LEFT PANEL
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                GradientPaint bgGrad = new GradientPaint(0, 0, GRADIENT_START, w, h, GRADIENT_END);
                g2.setPaint(bgGrad);
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
        };
        leftPanel.setOpaque(false);

        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setOpaque(false);
        leftContent.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel logoLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(0, 0, 80, 80);
                g2.setColor(new Color(255, 255, 255, 20));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(0, 0, 80, 80);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
                FontMetrics fm = g2.getFontMetrics();
                String icon = "Rp.";
                int x = (80 - fm.stringWidth(icon)) / 2;
                int y = (80 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(icon, x, y);
                g2.dispose();
            }
        };
        logoLabel.setPreferredSize(new Dimension(80, 80));
        logoLabel.setMaximumSize(new Dimension(80, 80));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appNameLabel = new JLabel(Constants.APP_NAME);
        appNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 34));
        appNameLabel.setForeground(Color.WHITE);
        appNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Otomatisasi Slip Gaji");
        tagline.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        tagline.setForeground(new Color(191, 219, 254));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel companyLabel = new JLabel(Constants.COMPANY_NAME);
        companyLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        companyLabel.setForeground(new Color(147, 197, 253));
        companyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftContent.add(logoLabel);
        leftContent.add(Box.createVerticalStrut(24));
        leftContent.add(appNameLabel);
        leftContent.add(Box.createVerticalStrut(6));
        leftContent.add(tagline);
        leftContent.add(Box.createVerticalGlue());
        leftContent.add(companyLabel);
        leftPanel.add(leftContent);

        // RIGHT PANEL
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new GridBagLayout());

        JPanel formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setOpaque(false);
        formWrapper.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel welcomeLabel = new JLabel("Masuk");
        welcomeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        welcomeLabel.setForeground(Constants.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeSub = new JLabel("Silakan login untuk melanjutkan");
        welcomeSub.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        welcomeSub.setForeground(Constants.TEXT_SECONDARY);
        welcomeSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        userLabel.setForeground(Constants.TEXT_PRIMARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userLabel.setBorder(new EmptyBorder(0, 2, 0, 0));

        usernameField = createRoundedField("Username");
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        passLabel.setForeground(Constants.TEXT_PRIMARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passLabel.setBorder(new EmptyBorder(0, 2, 0, 0));

        passwordField = createRoundedPasswordField("Password");
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        errorLabel.setForeground(Constants.ACCENT_DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginButton = createLoginButton();
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        formWrapper.add(Box.createVerticalStrut(20));
        formWrapper.add(welcomeLabel);
        formWrapper.add(Box.createVerticalStrut(4));
        formWrapper.add(welcomeSub);
        formWrapper.add(Box.createVerticalStrut(32));
        formWrapper.add(userLabel);
        formWrapper.add(Box.createVerticalStrut(6));
        formWrapper.add(usernameField);
        formWrapper.add(Box.createVerticalStrut(18));
        formWrapper.add(passLabel);
        formWrapper.add(Box.createVerticalStrut(6));
        formWrapper.add(passwordField);
        formWrapper.add(Box.createVerticalStrut(10));
        formWrapper.add(errorLabel);
        formWrapper.add(Box.createVerticalStrut(20));
        formWrapper.add(loginButton);
        formWrapper.add(Box.createVerticalGlue());

        rightPanel.add(formWrapper);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0.48;
        gbc.weighty = 1.0;
        mainPanel.add(leftPanel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.52;
        mainPanel.add(rightPanel, gbc);
    }

    private JButton createLoginButton() {
        JButton btn = new JButton("Masuk") {
            private float hoverAlpha = 0f;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override public void mouseEntered(java.awt.event.MouseEvent e) { hoverAlpha = 0.12f; repaint(); }
                    @Override public void mouseExited(java.awt.event.MouseEvent e) { hoverAlpha = 0f; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), arc = 14;
                if (isEnabled()) {
                    g2.setColor(new Color(37, 99, 235, 40));
                    g2.fill(new RoundRectangle2D.Double(2, 3, w - 4, h - 2, arc, arc));
                    GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, w, 0, GRADIENT_END);
                    g2.setPaint(gp);
                } else g2.setColor(Constants.BORDER_COLOR);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h - 2, arc, arc));
                if (hoverAlpha > 0 && isEnabled()) {
                    g2.setColor(new Color(255, 255, 255, (int)(hoverAlpha * 255)));
                    g2.fill(new RoundRectangle2D.Double(0, 0, w, h - 2, arc, arc));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false); btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(14, 20, 14, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.addActionListener(e -> doLogin());
        return btn;
    }

    private JTextField createRoundedField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 14, 14));
                g2.setColor(Constants.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 14, 14));
                g2.dispose();
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g3.setColor(new Color(156, 163, 175));
                    g3.setFont(getFont());
                    FontMetrics fm = g3.getFontMetrics();
                    g3.drawString(placeholder, getInsets().left+4, (getHeight()-fm.getHeight())/2+fm.getAscent());
                    g3.dispose();
                }
            }
        };
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        field.setForeground(Constants.TEXT_PRIMARY);
        field.setBackground(new Color(249, 250, 251));
        field.setCaretColor(Constants.TEXT_PRIMARY);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(12, 16, 12, 16));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        field.setPreferredSize(new Dimension(340, 48));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { field.setBorder(new EmptyBorder(11,15,11,15)); field.setBackground(Color.WHITE); field.repaint(); }
            @Override public void focusLost(java.awt.event.FocusEvent e) { field.setBorder(new EmptyBorder(12,16,12,16)); field.setBackground(new Color(249,250,251)); field.repaint(); }
        });
        return field;
    }

    private JPasswordField createRoundedPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 14, 14));
                g2.setColor(Constants.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 14, 14));
                g2.dispose();
                super.paintComponent(g);
                if (getPassword().length==0 && !hasFocus()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g3.setColor(new Color(156, 163, 175));
                    g3.setFont(getFont());
                    FontMetrics fm = g3.getFontMetrics();
                    g3.drawString(placeholder, getInsets().left+4, (getHeight()-fm.getHeight())/2+fm.getAscent());
                    g3.dispose();
                }
            }
        };
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        field.setForeground(Constants.TEXT_PRIMARY);
        field.setBackground(new Color(249, 250, 251));
        field.setCaretColor(Constants.TEXT_PRIMARY);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(12, 16, 12, 16));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        field.setPreferredSize(new Dimension(340, 48));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { field.setBorder(new EmptyBorder(11,15,11,15)); field.setBackground(Color.WHITE); field.repaint(); }
            @Override public void focusLost(java.awt.event.FocusEvent e) { field.setBorder(new EmptyBorder(12,16,12,16)); field.setBackground(new Color(249,250,251)); field.repaint(); }
        });
        return field;
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        try {
            loginButton.setEnabled(false);
            loginButton.setText("Memproses...");
            User user = authController.login(username, password);
            SwingUtilities.invokeLater(() -> {
                new MainView(user).setVisible(true);
                this.dispose();
            });
        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
            loginButton.setEnabled(true);
            loginButton.setText("Masuk");
        }
    }
}
