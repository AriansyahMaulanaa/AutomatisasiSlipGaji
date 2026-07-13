package com.slipgaji.view;

import com.slipgaji.model.Payslip;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.ui.components.AppButton;
import com.slipgaji.ui.components.AppTextField;
import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;
import com.slipgaji.util.UIHelper;
import com.slipgaji.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.File;

/**
 * EditPayslipDialog — form edit data slip gaji (nama, gaji, hari, dsb.).
 */
public class EditPayslipDialog extends JDialog {

    private final DatabaseService db;
    private final Payslip payslip;

    private JTextField nameField, emailField;
    private JComboBox<String> positionCombo;
    private JTextField departmentField;
    private JTextField salaryField, presentField, absentField, overtimeField;
    private JComboBox<String> typeCombo;

    private boolean saved = false;

    public EditPayslipDialog(Window owner, Payslip payslip) {
        super(owner, "Edit Data — " + payslip.getEmployeeName(), ModalityType.APPLICATION_MODAL);
        this.payslip = payslip;
        this.db = DatabaseService.getInstance();
        initUI();
    }

    public boolean isSaved() { return saved; }

    private void initUI() {
        setSize(540, 640);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, UIMetrics.SPACE_16));
        mainPanel.setBackground(UIColors.NEUTRAL_0);
        mainPanel.setBorder(new EmptyBorder(UIMetrics.SPACE_24, UIMetrics.SPACE_24,
                                             UIMetrics.SPACE_20, UIMetrics.SPACE_24));

        JLabel title = new JLabel("Edit Data Slip Gaji");
        title.setFont(UIFonts.H1);
        title.setForeground(UIColors.NEUTRAL_800);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        nameField = addRow(form, gbc, row++, "Nama:", payslip.getEmployeeName());
        emailField = addRow(form, gbc, row++, "Email:", payslip.getEmployeeEmail());

        // Position combo
        addComboRow(form, gbc, row++, "Posisi:",
                positionCombo = new JComboBox<>(new String[]{"Crewstore", "Store Leader", "Manager"}));
        selectComboValue(positionCombo, payslip.getPosition());

        departmentField = addRow(form, gbc, row++, "Departemen:", payslip.getDepartment());

        // Employee type
        addComboRow(form, gbc, row++, "Tipe Karyawan:",
                typeCombo = new JComboBox<>(new String[]{"TETAP", "PKWT", "KANTOR"}));
        loadEmploymentType();

        // Separator
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        gbc.insets = new Insets(UIMetrics.SPACE_8, 6, UIMetrics.SPACE_8, 6);
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(UIColors.NEUTRAL_200);
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        sep.setOpaque(false);
        sep.setPreferredSize(new Dimension(0, 1));
        form.add(sep, gbc);
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridwidth = 1;

        salaryField = addRow(form, gbc, row++, "Gaji Pokok (Rp):", String.valueOf((int) payslip.getBaseSalary()));
        presentField = addRow(form, gbc, row++, "Hari Hadir:", String.valueOf(payslip.getDaysPresent()));
        absentField = addRow(form, gbc, row++, "Hari Absen:", String.valueOf(payslip.getDaysAbsent()));
        overtimeField = addRow(form, gbc, row++, "Jam Lembur:", String.valueOf((int) payslip.getOvertimeHours()));

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIMetrics.SPACE_8, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(UIMetrics.SPACE_16, 0, 0, 0));

        JButton cancelBtn = AppButton.secondary("Batal");
        cancelBtn.addActionListener(e -> dispose());
        JButton saveBtn = AppButton.primary("Simpan");
        saveBtn.addActionListener(e -> saveData());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void loadEmploymentType() {
        String sql = "SELECT employment_type FROM employees WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payslip.getEmployeeId());
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) selectComboValue(typeCombo, rs.getString("employment_type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectComboValue(JComboBox<String> combo, String value) {
        if (value == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equalsIgnoreCase(value.trim())) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private JTextField addRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIFonts.LABEL);
        lbl.setForeground(UIColors.NEUTRAL_600);
        lbl.setPreferredSize(new Dimension(140, 24));
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField field = AppTextField.create("");
        if (value != null) field.setText(value);
        panel.add(field, gbc);
        return field;
    }

    private void addComboRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComboBox<String> combo) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIFonts.LABEL);
        lbl.setForeground(UIColors.NEUTRAL_600);
        lbl.setPreferredSize(new Dimension(140, 24));
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        combo.setFont(UIFonts.BODY);
        combo.setBackground(UIColors.NEUTRAL_0);
        combo.setPreferredSize(new Dimension(0, UIMetrics.INPUT_HEIGHT));
        panel.add(combo, gbc);
    }

    private void saveData() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String position = (String) positionCombo.getSelectedItem();
        String department = departmentField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        String salaryStr = salaryField.getText().trim();
        String presentStr = presentField.getText().trim();
        String absentStr = absentField.getText().trim();
        String overtimeStr = overtimeField.getText().trim();

        if (name.isEmpty()) { UIHelper.showError(this, "Nama tidak boleh kosong."); return; }
        if (!ValidationUtil.isValidEmail(email)) {
            UIHelper.showError(this, "Format email tidak valid: " + email);
            return;
        }

        double salary;
        try {
            String cleanSalary = salaryStr.replace(".", "").replace(",", ".");
            salary = Double.parseDouble(cleanSalary);
            if (salary <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            UIHelper.showError(this, "Gaji Pokok harus berupa angka positif.");
            return;
        }
        int present, absent;
        double overtime;
        try {
            present = Integer.parseInt(presentStr);
            absent = Integer.parseInt(absentStr);
            overtime = Double.parseDouble(overtimeStr);
        } catch (NumberFormatException e) {
            UIHelper.showError(this, "Hari Hadir, Hari Absen, dan Jam Lembur harus berupa angka.");
            return;
        }
        if (present < 0 || absent < 0 || overtime < 0) {
            UIHelper.showError(this, "Nilai tidak boleh negatif.");
            return;
        }

        // Update employee
        com.slipgaji.model.Employee emp = new com.slipgaji.model.Employee();
        emp.setId(payslip.getEmployeeId());
        emp.setEmployeeId(payslip.getEmployeeIdCode());
        emp.setName(name);
        emp.setEmail(email);
        emp.setPosition(position);
        emp.setDepartment(department);
        emp.setBaseSalary(salary);
        emp.setEmploymentType(type);
        db.updateEmployee(emp);

        // Delete old PDF & clear path
        if (payslip.getPdfPath() != null && !payslip.getPdfPath().isEmpty()) {
            File oldPdf = new File(payslip.getPdfPath());
            if (oldPdf.exists()) oldPdf.delete();
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE payslips SET pdf_path = NULL WHERE id = ?")) {
                ps.setInt(1, payslip.getId());
                ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
            payslip.setPdfPath(null);
        }

        db.updatePayslipData(payslip.getId(), salary, present, absent, overtime);

        // Recalculate computed fields
        String prefix = switch (position.toUpperCase()) {
            case "STORE LEADER" -> "store_leader";
            case "MANAGER" -> "manager";
            default -> "crewstore";
        };

        double overtimeRate = db.getSettingDouble(prefix + "_overtime_rate_per_hour", 25000);
        int divisor = (int) db.getSettingDouble(prefix + "_daily_rate_divisor", 22);
        double transport = db.getSettingDouble(prefix + "_transport_allowance", 500000);
        double meal = db.getSettingDouble(prefix + "_meal_allowance", 300000);
        double nightShiftRate = db.getSettingDouble(prefix + "_night_shift_rate", 50000);

        double dailyRate = divisor > 0 ? salary / divisor : 0;
        double recalcDeductions = dailyRate * absent;
        double recalcOvertimePay = overtime * overtimeRate;
        double recalcAllowances = transport + meal;
        double recalcNightShiftIncentive = payslip.getNightShiftIncentive();
        if (recalcNightShiftIncentive == 0 && payslip.isNightShift()) {
            recalcNightShiftIncentive = nightShiftRate;
        }
        double recalcNetSalary = salary - recalcDeductions + recalcOvertimePay + recalcAllowances + recalcNightShiftIncentive;

        String recalcSql = "UPDATE payslips SET overtime_pay=?, deductions=?, allowances=?, net_salary=?, night_shift_incentive=? WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(recalcSql)) {
            ps.setDouble(1, recalcOvertimePay);
            ps.setDouble(2, recalcDeductions);
            ps.setDouble(3, recalcAllowances);
            ps.setDouble(4, recalcNetSalary);
            ps.setDouble(5, recalcNightShiftIncentive);
            ps.setInt(6, payslip.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            UIHelper.showError(this, "Gagal merekalkulasi slip gaji: " + e.getMessage());
            return;
        }

        saved = true;
        UIHelper.showSuccess(this, "Data berhasil diperbarui.\nSlip gaji otomatis dihitung ulang.");
        dispose();
    }
}
