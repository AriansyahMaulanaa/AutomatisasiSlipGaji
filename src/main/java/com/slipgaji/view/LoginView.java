package com.slipgaji.view;

import com.slipgaji.controller.AuthController;
import com.slipgaji.model.User;
import com.slipgaji.ui.components.AppButton;
import com.slipgaji.ui.components.AppTextField;
import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;
import com.slipgaji.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * LoginView — split layout (brand left + form right).
 *
 * <p>Redesign: panel kiri solid PRIMARY_700 (dark navy soft) tanpa gradient mencolok,
 * form kanan pakai {@link AppTextField} + {@link AppButton}. Tidak ada gradient/glassmorphism.
 */
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
        setTitle("Masuk — " + Constants.APP_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 620);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIColors.NEUTRAL_0);
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;

        gbc.gridx = 0; gbc.weightx = 0.45; gbc.weighty = 1.0;
        mainPanel.add(createBrandPanel(), gbc);

        gbc.gridx = 1; gbc.weightx = 0.55;
        mainPanel.add(createFormPanel(), gbc);
    }

    // ============================================================
    // Brand panel (left)
    // ============================================================
    private JPanel createBrandPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Solid dark navy (bukan gradient mencolok)
                g2.setColor(UIColors.SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Halus: sedikit overlay diagonal soft
                g2.setColor(new Color(74, 127, 201, 22));
                g2.fillRect(0, 0, getWidth(), getHeight() / 2);
                g2.dispose();
            }
        };
        panel.setOpaque(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(48, 56, 48, 56));

        // Logo circle
        JLabel logo = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 28));
                g2.fillOval(0, 0, 72, 72);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(UIFonts.FAMILY, Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                String s = "Rp.";
                int x = (72 - fm.stringWidth(s)) / 2;
                int y = (72 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(s, x, y);
                g2.dispose();
            }
        };
        logo.setPreferredSize(new Dimension(72, 72));
        logo.setMaximumSize(new Dimension(72, 72));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appName = new JLabel(Constants.APP_NAME);
        appName.setFont(new Font(UIFonts.FAMILY, Font.BOLD, 30));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Otomatisasi Slip Gaji");
        tagline.setFont(new Font(UIFonts.FAMILY, Font.PLAIN, 14));
        tagline.setForeground(new Color(220, 231, 245));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel company = new JLabel(Constants.COMPANY_NAME);
        company.setFont(UIFonts.CAPTION);
        company.setForeground(new Color(133, 146, 166));
        company.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(logo);
        content.add(Box.createVerticalStrut(28));
        content.add(appName);
        content.add(Box.createVerticalStrut(6));
        content.add(tagline);
        content.add(Box.createVerticalGlue());
        content.add(company);
        panel.add(content);
        return panel;
    }

    // ============================================================
    // Form panel (right)
    // ============================================================
    private JPanel createFormPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UIColors.NEUTRAL_0);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(UIColors.NEUTRAL_0);
        form.setBorder(new EmptyBorder(40, 56, 40, 56));
        form.setPreferredSize(new Dimension(400, 0));

        JLabel welcome = new JLabel("Masuk");
        welcome.setFont(UIFonts.H1);
        welcome.setForeground(UIColors.NEUTRAL_800);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeSub = new JLabel("Silakan login untuk melanjutkan");
        welcomeSub.setFont(UIFonts.BODY);
        welcomeSub.setForeground(UIColors.NEUTRAL_600);
        welcomeSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(UIFonts.LABEL_BOLD);
        userLabel.setForeground(UIColors.NEUTRAL_800);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = AppTextField.create("Masukkan username");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIMetrics.INPUT_HEIGHT + 4));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UIFonts.LABEL_BOLD);
        passLabel.setForeground(UIColors.NEUTRAL_800);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = AppTextField.createPassword("Masukkan password");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIMetrics.INPUT_HEIGHT + 4));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIFonts.CAPTION);
        errorLabel.setForeground(UIColors.DANGER_FG);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginButton = AppButton.primary("Masuk");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIMetrics.BUTTON_HEIGHT_LG + 4));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setFont(new Font(UIFonts.FAMILY, Font.BOLD, 14));
        loginButton.addActionListener(e -> doLogin());

        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        form.add(Box.createVerticalStrut(16));
        form.add(welcome);
        form.add(Box.createVerticalStrut(4));
        form.add(welcomeSub);
        form.add(Box.createVerticalStrut(32));
        form.add(userLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(usernameField);
        form.add(Box.createVerticalStrut(18));
        form.add(passLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(6));
        form.add(errorLabel);
        form.add(Box.createVerticalStrut(20));
        form.add(loginButton);
        form.add(Box.createVerticalGlue());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        wrapper.add(form, gbc);
        return wrapper;
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
