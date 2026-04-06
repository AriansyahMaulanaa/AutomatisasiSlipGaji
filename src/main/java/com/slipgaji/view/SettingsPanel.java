package com.slipgaji.view;

import com.slipgaji.service.DatabaseService;
import com.slipgaji.service.EmailService;
import com.slipgaji.util.ConfigManager;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private JTextField smtpHostField, smtpPortField, smtpEmailField;
    private JPasswordField smtpPasswordField;
    private JTextField companyNameField, companyAddressField;
    private JTextField overtimeRateField, divisorField, transportField, mealField, nightShiftRateField;
    private final DatabaseService db;

    public SettingsPanel() {
        this.db = DatabaseService.getInstance();
        initUI();
        loadSettings();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(32, 32, 32, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel title = new JLabel("Pengaturan");
        title.setFont(Constants.FONT_TITLE);
        title.setForeground(Constants.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // SMTP Settings
        JPanel smtpCard = UIHelper.createCard("Konfigurasi SMTP Email");
        JPanel smtpForm = new JPanel(new GridBagLayout());
        smtpForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        smtpHostField = UIHelper.createStyledTextField("smtp.gmail.com");
        smtpPortField = UIHelper.createStyledTextField("587");
        smtpEmailField = UIHelper.createStyledTextField("email@gmail.com");
        smtpPasswordField = UIHelper.createStyledPasswordField("App Password");

        addFormRow(smtpForm, gbc, 0, "SMTP Host:", smtpHostField);
        addFormRow(smtpForm, gbc, 1, "SMTP Port:", smtpPortField);
        addFormRow(smtpForm, gbc, 2, "Email Pengirim:", smtpEmailField);
        addFormRow(smtpForm, gbc, 3, "Password/App Password:", smtpPasswordField);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel smtpBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        smtpBtnPanel.setOpaque(false);
        JButton testBtn = UIHelper.createStyledButton("Test Koneksi", Constants.ACCENT_WARN);
        testBtn.addActionListener(e -> testSmtp());
        JButton saveSmtpBtn = UIHelper.createStyledButton("Simpan SMTP", Constants.PRIMARY);
        saveSmtpBtn.addActionListener(e -> saveSmtpSettings());
        smtpBtnPanel.add(testBtn);
        smtpBtnPanel.add(saveSmtpBtn);
        smtpForm.add(smtpBtnPanel, gbc);

        smtpCard.add(smtpForm, BorderLayout.CENTER);

        // Company Settings
        JPanel companyCard = UIHelper.createCard("Informasi Perusahaan");
        JPanel companyForm = new JPanel(new GridBagLayout());
        companyForm.setOpaque(false);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(8, 8, 8, 8);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        companyNameField = UIHelper.createStyledTextField("PT. Maju Bersama");
        companyAddressField = UIHelper.createStyledTextField("Jl. Sudirman No. 123");
        addFormRow(companyForm, gbc2, 0, "Nama Perusahaan:", companyNameField);
        addFormRow(companyForm, gbc2, 1, "Alamat:", companyAddressField);

        gbc2.gridx = 0; gbc2.gridy = 2; gbc2.gridwidth = 2;
        JButton saveCompBtn = UIHelper.createStyledButton("Simpan", Constants.PRIMARY);
        saveCompBtn.addActionListener(e -> saveCompanySettings());
        companyForm.add(saveCompBtn, gbc2);

        companyCard.add(companyForm, BorderLayout.CENTER);

        // Salary Settings
        JPanel salaryCard = UIHelper.createCard("Parameter Gaji");
        JPanel salaryForm = new JPanel(new GridBagLayout());
        salaryForm.setOpaque(false);
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.insets = new Insets(8, 8, 8, 8);
        gbc3.fill = GridBagConstraints.HORIZONTAL;

        overtimeRateField = UIHelper.createStyledTextField("25000");
        divisorField = UIHelper.createStyledTextField("22");
        transportField = UIHelper.createStyledTextField("500000");
        mealField = UIHelper.createStyledTextField("300000");
        nightShiftRateField = UIHelper.createStyledTextField("50000");

        addFormRow(salaryForm, gbc3, 0, "Tarif Lembur/Jam (Rp):", overtimeRateField);
        addFormRow(salaryForm, gbc3, 1, "Hari Kerja/Bulan:", divisorField);
        addFormRow(salaryForm, gbc3, 2, "Tunj. Transport (Rp):", transportField);
        addFormRow(salaryForm, gbc3, 3, "Tunj. Makan (Rp):", mealField);
        addFormRow(salaryForm, gbc3, 4, "Insentif Shift Malam (Rp):", nightShiftRateField);

        gbc3.gridx = 0; gbc3.gridy = 5; gbc3.gridwidth = 2;
        JButton saveSalaryBtn = UIHelper.createStyledButton("Simpan Parameter", Constants.PRIMARY);
        saveSalaryBtn.addActionListener(e -> saveSalarySettings());
        salaryForm.add(saveSalaryBtn, gbc3);

        salaryCard.add(salaryForm, BorderLayout.CENTER);

        content.add(smtpCard);
        content.add(Box.createVerticalStrut(20));
        content.add(companyCard);
        content.add(Box.createVerticalStrut(20));
        content.add(salaryCard);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(content, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel(label);
        lbl.setFont(Constants.FONT_BODY);
        lbl.setForeground(Constants.TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(190, 28));
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        field.setPreferredSize(new Dimension(300, 40));
        panel.add(field, gbc);
    }

    private void loadSettings() {
        smtpHostField.setText(ConfigManager.get("smtp_host", "smtp.gmail.com"));
        smtpPortField.setText(ConfigManager.get("smtp_port", "587"));
        smtpEmailField.setText(ConfigManager.get("smtp_email", ""));
        smtpPasswordField.setText(ConfigManager.get("smtp_password", ""));
        companyNameField.setText(db.getSetting("company_name"));
        companyAddressField.setText(db.getSetting("company_address"));
        overtimeRateField.setText(db.getSetting("overtime_rate_per_hour"));
        divisorField.setText(db.getSetting("daily_rate_divisor"));
        transportField.setText(db.getSetting("transport_allowance"));
        mealField.setText(db.getSetting("meal_allowance"));
        nightShiftRateField.setText(db.getSetting("night_shift_rate"));
    }

    private void saveSmtpSettings() {
        ConfigManager.set("smtp_host", smtpHostField.getText().trim());
        ConfigManager.set("smtp_port", smtpPortField.getText().trim());
        ConfigManager.set("smtp_email", smtpEmailField.getText().trim());
        ConfigManager.set("smtp_password", new String(smtpPasswordField.getPassword()));
        UIHelper.showSuccess(this, "Konfigurasi SMTP berhasil disimpan.");
    }

    private void saveCompanySettings() {
        db.saveSetting("company_name", companyNameField.getText().trim());
        db.saveSetting("company_address", companyAddressField.getText().trim());
        UIHelper.showSuccess(this, "Informasi perusahaan berhasil disimpan.");
    }

    private void saveSalarySettings() {
        db.saveSetting("overtime_rate_per_hour", overtimeRateField.getText().trim());
        db.saveSetting("daily_rate_divisor", divisorField.getText().trim());
        db.saveSetting("transport_allowance", transportField.getText().trim());
        db.saveSetting("meal_allowance", mealField.getText().trim());
        db.saveSetting("night_shift_rate", nightShiftRateField.getText().trim());
        UIHelper.showSuccess(this, "Parameter gaji berhasil disimpan.");
    }

    private void testSmtp() {
        String host = smtpHostField.getText().trim();
        String port = smtpPortField.getText().trim();
        String email = smtpEmailField.getText().trim();
        String pass = new String(smtpPasswordField.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            UIHelper.showError(this, "Email dan password harus diisi.");
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                EmailService svc = new EmailService(host, port, email, pass);
                return svc.testConnection();
            }
            @Override
            protected void done() {
                try {
                    if (get()) UIHelper.showSuccess(SettingsPanel.this, "✅ Koneksi SMTP berhasil!");
                    else UIHelper.showError(SettingsPanel.this, "❌ Koneksi SMTP gagal.");
                } catch (Exception ex) {
                    UIHelper.showError(SettingsPanel.this, "Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
