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
    private JLabel paramTitle;
    private JToggleButton crewstoreBtn, storeLeaderBtn, managerBtn;
    private String selectedPosition = "Crewstore";

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

        addSmtpSection(content);
        content.add(Box.createVerticalStrut(20));
        addCompanySection(content);
        content.add(Box.createVerticalStrut(20));
        addDynamicSalarySection(content);

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

    private void addSmtpSection(JPanel content) {
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
        content.add(smtpCard);
    }

    private void addCompanySection(JPanel content) {
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
        content.add(companyCard);
    }

    private void addDynamicSalarySection(JPanel content) {
        JPanel salaryCard = UIHelper.createCard("Parameter Gaji Berdasarkan Jabatan");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);

        ButtonGroup group = new ButtonGroup();

        crewstoreBtn = createTabButton("Crewstore", true);
        storeLeaderBtn = createTabButton("Store Leader", false);
        managerBtn = createTabButton("Manager", false);

        group.add(crewstoreBtn);
        group.add(storeLeaderBtn);
        group.add(managerBtn);

        crewstoreBtn.addActionListener(e -> selectPosition("Crewstore"));
        storeLeaderBtn.addActionListener(e -> selectPosition("Store Leader"));
        managerBtn.addActionListener(e -> selectPosition("Manager"));

        btnPanel.add(crewstoreBtn);
        btnPanel.add(storeLeaderBtn);
        btnPanel.add(managerBtn);

        paramTitle = new JLabel("Parameter Gaji - Crewstore");
        paramTitle.setFont(Constants.FONT_SUBTITLE);
        paramTitle.setForeground(Constants.TEXT_PRIMARY);

        topPanel.add(paramTitle, BorderLayout.WEST);
        topPanel.add(btnPanel, BorderLayout.EAST);
        salaryCard.add(topPanel, BorderLayout.NORTH);

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
        content.add(salaryCard);
    }

    private JToggleButton createTabButton(String text, boolean selected) {
        return UIHelper.createStyledToggleButton(text, selected);
    }

    private void selectPosition(String position) {
        selectedPosition = position;
        paramTitle.setText("Parameter Gaji - " + position);
        loadSalaryFields();
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

        loadSalaryFields();
    }

    private void loadSalaryFields() {
        String prefix = switch (selectedPosition) {
            case "Store Leader" -> "store_leader";
            case "Manager" -> "manager";
            default -> "crewstore";
        };

        overtimeRateField.setText(db.getSetting(prefix + "_overtime_rate_per_hour"));
        divisorField.setText(db.getSetting(prefix + "_daily_rate_divisor"));
        transportField.setText(db.getSetting(prefix + "_transport_allowance"));
        mealField.setText(db.getSetting(prefix + "_meal_allowance"));
        nightShiftRateField.setText(db.getSetting(prefix + "_night_shift_rate"));
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
        String prefix = switch (selectedPosition) {
            case "Store Leader" -> "store_leader";
            case "Manager" -> "manager";
            default -> "crewstore";
        };

        // Validate all fields are numeric and positive
        String[][] fields = {
            {overtimeRateField.getText().trim(), "Tarif Lembur/Jam"},
            {divisorField.getText().trim(), "Hari Kerja/Bulan"},
            {transportField.getText().trim(), "Tunj. Transport"},
            {mealField.getText().trim(), "Tunj. Makan"},
            {nightShiftRateField.getText().trim(), "Insentif Shift Malam"}
        };
        for (String[] f : fields) {
            String val = f[0];
            String label = f[1];
            if (val.isEmpty()) {
                UIHelper.showError(this, label + " tidak boleh kosong.");
                return;
            }
            try {
                double d = Double.parseDouble(val.replace(",", "").replace(".", ""));
                if (d <= 0) {
                    UIHelper.showError(this, label + " harus bernilai positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                UIHelper.showError(this, label + " harus berisi angka. Ditemukan: " + val);
                return;
            }
        }

        db.saveSetting(prefix + "_overtime_rate_per_hour", overtimeRateField.getText().trim());
        db.saveSetting(prefix + "_daily_rate_divisor", divisorField.getText().trim());
        db.saveSetting(prefix + "_transport_allowance", transportField.getText().trim());
        db.saveSetting(prefix + "_meal_allowance", mealField.getText().trim());
        db.saveSetting(prefix + "_night_shift_rate", nightShiftRateField.getText().trim());
        UIHelper.showSuccess(this, "Parameter gaji untuk " + selectedPosition + " berhasil disimpan.");
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
                    if (get()) UIHelper.showSuccess(SettingsPanel.this, "Koneksi SMTP berhasil!");
                    else UIHelper.showError(SettingsPanel.this, "Koneksi SMTP gagal.");
                } catch (Exception ex) {
                    UIHelper.showError(SettingsPanel.this, "Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
