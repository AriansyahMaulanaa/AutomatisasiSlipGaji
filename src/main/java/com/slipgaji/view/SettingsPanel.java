package com.slipgaji.view;

import com.slipgaji.service.DatabaseService;
import com.slipgaji.service.EmailService;
import com.slipgaji.ui.components.AppButton;
import com.slipgaji.ui.components.AppCard;
import com.slipgaji.ui.components.AppTextField;
import com.slipgaji.ui.components.SegmentedControl;
import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;
import com.slipgaji.util.ConfigManager;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * SettingsPanel — konfigurasi SMTP, perusahaan, dan parameter gaji.
 *
 * <p>Redesign:
 * <ul>
 *   <li>3 section (SMTP / Perusahaan / Parameter Gaji) tiap-tiap dalam {@link AppCard}.</li>
 *   <li>Tab jabatan pakai {@link SegmentedControl} (bg netral, tab aktif putih + text soft blue).</li>
 *   <li>Hierarki tombol per section: 1 Primary (aksi utama = Simpan) + Secondary (Test Koneksi).</li>
 * </ul>
 */
public class SettingsPanel extends JPanel {
    private JTextField smtpHostField, smtpPortField, smtpEmailField;
    private JPasswordField smtpPasswordField;
    private JTextField companyNameField, companyAddressField;

    private JTextField overtimeRateField, divisorField, transportField, mealField, nightShiftRateField;
    private JLabel paramTitle;
    private String selectedPosition = "Crewstore";

    private final DatabaseService db;

    public SettingsPanel() {
        this.db = DatabaseService.getInstance();
        initUI();
        loadSettings();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, UIMetrics.SPACE_16));
        setBackground(UIColors.NEUTRAL_50);
        setBorder(new EmptyBorder(UIMetrics.SPACE_24, UIMetrics.SPACE_24,
                                   UIMetrics.SPACE_24, UIMetrics.SPACE_24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Pengaturan");
        title.setFont(UIFonts.H1);
        title.setForeground(UIColors.NEUTRAL_800);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(buildSmtpSection());
        content.add(Box.createVerticalStrut(UIMetrics.SPACE_16));
        content.add(buildCompanySection());
        content.add(Box.createVerticalStrut(UIMetrics.SPACE_16));
        content.add(buildSalarySection());

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

    // ============================================================
    // SMTP Section
    // ============================================================
    private AppCard buildSmtpSection() {
        AppCard card = new AppCard("Konfigurasi SMTP Email");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        smtpHostField = AppTextField.create("smtp.gmail.com");
        smtpPortField = AppTextField.create("587");
        smtpEmailField = AppTextField.create("email@gmail.com");
        smtpPasswordField = AppTextField.createPassword("App Password");

        addFormRow(form, gbc, 0, "SMTP Host:", smtpHostField);
        addFormRow(form, gbc, 1, "SMTP Port:", smtpPortField);
        addFormRow(form, gbc, 2, "Email Pengirim:", smtpEmailField);
        addFormRow(form, gbc, 3, "Password/App Password:", smtpPasswordField);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UIMetrics.SPACE_8, 0));
        btnPanel.setOpaque(false);

        // Test Koneksi = Secondary, Simpan = Primary (aksi utama section)
        JButton testBtn = AppButton.secondary("Test Koneksi");
        testBtn.addActionListener(e -> testSmtp());
        JButton saveSmtpBtn = AppButton.primary("Simpan SMTP");
        saveSmtpBtn.addActionListener(e -> saveSmtpSettings());
        btnPanel.add(testBtn);
        btnPanel.add(saveSmtpBtn);
        form.add(btnPanel, gbc);

        card.addBody(form);
        return card;
    }

    // ============================================================
    // Company Section
    // ============================================================
    private AppCard buildCompanySection() {
        AppCard card = new AppCard("Informasi Perusahaan");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        companyNameField = AppTextField.create("PT. Maju Bersama");
        companyAddressField = AppTextField.create("Jl. Sudirman No. 123");
        addFormRow(form, gbc, 0, "Nama Perusahaan:", companyNameField);
        addFormRow(form, gbc, 1, "Alamat:", companyAddressField);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setOpaque(false);
        JButton saveCompBtn = AppButton.primary("Simpan");
        saveCompBtn.addActionListener(e -> saveCompanySettings());
        btnPanel.add(saveCompBtn);
        form.add(btnPanel, gbc);

        card.addBody(form);
        return card;
    }

    // ============================================================
    // Salary Params Section
    // ============================================================
    private AppCard buildSalarySection() {
        AppCard card = new AppCard("Parameter Gaji Berdasarkan Jabatan");

        JPanel body = new JPanel(new BorderLayout(0, UIMetrics.SPACE_16));
        body.setOpaque(false);

        // === Init fields FIRST — SegmentedControl.setSelected() akan trigger
        //     listener onChange -> selectPosition -> loadSalaryFields yang
        //     mengakses field-field ini. Kalau field belum diinit, NPE. ===
        overtimeRateField = AppTextField.create("25000");
        divisorField = AppTextField.create("22");
        transportField = AppTextField.create("500000");
        mealField = AppTextField.create("300000");
        nightShiftRateField = AppTextField.create("50000");

        paramTitle = new JLabel("Parameter Gaji — Crewstore");
        paramTitle.setFont(UIFonts.H3);
        paramTitle.setForeground(UIColors.NEUTRAL_800);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(paramTitle, BorderLayout.WEST);

        SegmentedControl segments = new SegmentedControl();
        segments.addSegment("Crewstore", "Crewstore");
        segments.addSegment("Store Leader", "Store Leader");
        segments.addSegment("Manager", "Manager");
        segments.onChange(this::selectPosition);
        segments.setSelected("Crewstore");
        topRow.add(segments, BorderLayout.EAST);
        body.add(topRow, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(form, gbc, 0, "Tarif Lembur/Jam (Rp):", overtimeRateField);
        addFormRow(form, gbc, 1, "Hari Kerja/Bulan:", divisorField);
        addFormRow(form, gbc, 2, "Tunj. Transport (Rp):", transportField);
        addFormRow(form, gbc, 3, "Tunj. Makan (Rp):", mealField);
        addFormRow(form, gbc, 4, "Insentif Shift Malam (Rp):", nightShiftRateField);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setOpaque(false);
        JButton saveSalaryBtn = AppButton.primary("Simpan Parameter");
        saveSalaryBtn.addActionListener(e -> saveSalarySettings());
        btnPanel.add(saveSalaryBtn);
        form.add(btnPanel, gbc);

        body.add(form, BorderLayout.CENTER);
        card.addBody(body);
        return card;
    }

    private void selectPosition(String position) {
        selectedPosition = position;
        paramTitle.setText("Parameter Gaji — " + position);
        loadSalaryFields();
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIFonts.LABEL);
        lbl.setForeground(UIColors.NEUTRAL_600);
        lbl.setPreferredSize(new Dimension(210, 28));
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        field.setPreferredSize(new Dimension(320, UIMetrics.INPUT_HEIGHT));
        panel.add(field, gbc);
    }

    // ============================================================
    // Data ops
    // ============================================================
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
            @Override protected Boolean doInBackground() {
                EmailService svc = new EmailService(host, port, email, pass);
                return svc.testConnection();
            }
            @Override protected void done() {
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
