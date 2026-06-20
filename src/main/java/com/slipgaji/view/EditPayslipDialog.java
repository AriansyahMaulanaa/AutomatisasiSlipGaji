package com.slipgaji.view;

import com.slipgaji.model.Payslip;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;
import com.slipgaji.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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
        super(owner, "Edit Data - " + payslip.getEmployeeName(), ModalityType.APPLICATION_MODAL);
        this.payslip = payslip;
        this.db = DatabaseService.getInstance();
        initUI();
    }

    public boolean isSaved() { return saved; }

    private void initUI() {
        setSize(520, 580);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Constants.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        nameField = addEditRow(formPanel, gbc, row++, "Nama:", payslip.getEmployeeName());
        emailField = addEditRow(formPanel, gbc, row++, "Email:", payslip.getEmployeeEmail());

        // Position combo
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel posLabel = new JLabel("Posisi:");
        posLabel.setFont(Constants.FONT_BODY);
        posLabel.setForeground(Constants.TEXT_SECONDARY);
        formPanel.add(posLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        positionCombo = new JComboBox<>(new String[]{"Crewstore", "Store Leader", "Manager"});
        positionCombo.setFont(Constants.FONT_BODY);
        String currentPos = payslip.getPosition();
        if (currentPos != null) {
            for (int i = 0; i < positionCombo.getItemCount(); i++) {
                if (positionCombo.getItemAt(i).equalsIgnoreCase(currentPos)) {
                    positionCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        formPanel.add(positionCombo, gbc);
        row++;
        departmentField = addEditRow(formPanel, gbc, row++, "Departemen:", payslip.getDepartment());

        // Employee type
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel typeLabel = new JLabel("Tipe Karyawan:");
        typeLabel.setFont(Constants.FONT_BODY);
        typeLabel.setForeground(Constants.TEXT_SECONDARY);
        formPanel.add(typeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        typeCombo = new JComboBox<>(new String[]{"TETAP", "PKWT", "KANTOR"});
        typeCombo.setFont(Constants.FONT_BODY);
        // Try to determine type from payslip (we don't store it directly, so use default)
        typeCombo.setSelectedItem("TETAP");
        formPanel.add(typeCombo, gbc);
        row++;

        // Separator
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 8, 12, 8);
        JSeparator sep = new JSeparator();
        sep.setForeground(Constants.BORDER_COLOR);
        formPanel.add(sep, gbc);
        row++;
        gbc.insets = new Insets(6, 8, 6, 8);

        salaryField = addEditRow(formPanel, gbc, row++, "Gaji Pokok (Rp):", String.valueOf((int) payslip.getBaseSalary()));
        presentField = addEditRow(formPanel, gbc, row++, "Hari Hadir:", String.valueOf(payslip.getDaysPresent()));
        absentField = addEditRow(formPanel, gbc, row++, "Hari Absen:", String.valueOf(payslip.getDaysAbsent()));
        overtimeField = addEditRow(formPanel, gbc, row++, "Jam Lembur:", String.valueOf((int) payslip.getOvertimeHours()));

        // Buttons
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setOpaque(false);

        JButton saveBtn = UIHelper.createStyledButton("Simpan", Constants.ACCENT);
        saveBtn.addActionListener(e -> saveData());
        JButton cancelBtn = UIHelper.createStyledButton("Batal", Constants.REFRESH_BTN);
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        formPanel.add(btnPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JTextField addEditRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel(label);
        lbl.setFont(Constants.FONT_BODY);
        lbl.setForeground(Constants.TEXT_SECONDARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField field = UIHelper.createStyledTextField("");
        field.setText(value != null ? value : "");
        panel.add(field, gbc);
        return field;
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

        if (name.isEmpty()) {
            UIHelper.showError(this, "Nama tidak boleh kosong.");
            return;
        }
        if (!ValidationUtil.isValidEmail(email)) {
            UIHelper.showError(this, "Format email tidak valid: " + email);
            return;
        }
        double salary;
        try {
            salary = Double.parseDouble(salaryStr.replace(",", "").replace(".", ""));
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
        db.updatePayslipData(payslip.getId(), salary, present, absent, overtime);

        saved = true;
        UIHelper.showSuccess(this, "Data berhasil diperbarui.");
        dispose();
    }
}
