package com.slipgaji.view;

import com.slipgaji.controller.KaryawanController;
import com.slipgaji.model.Employee;
import com.slipgaji.util.Constants;
import com.slipgaji.util.PhotoUtil;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TambahKaryawanDialog extends JDialog {

    private final KaryawanController controller = new KaryawanController();
    private JTextField fieldId, fieldNama, fieldEmail, fieldJabatan, fieldDept, fieldGaji, fieldBarcode;
    private JComboBox<String> comboType, comboStatus;
    private JTextField fieldTglLahir;
    private JLabel fotoPreview;
    private String photoPath;
    private boolean saved = false;
    private Employee editEmployee;

    public TambahKaryawanDialog(Window owner) {
        super(owner, "Tambah Karyawan", ModalityType.APPLICATION_MODAL);
        initUI();
        setLocationRelativeTo(owner);
    }

    public TambahKaryawanDialog(Window owner, Employee emp) {
        super(owner, "Edit Karyawan", ModalityType.APPLICATION_MODAL);
        this.editEmployee = emp;
        initUI();
        populateFields(emp);
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setSize(520, 600);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setBackground(Constants.BG_CARD);
        main.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel(editEmployee == null ? "Tambah Karyawan Baru" : "Edit Karyawan");
        title.setFont(Constants.FONT_TITLE);
        title.setForeground(Constants.TEXT_PRIMARY);
        main.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.weightx = 1.0;

        int row = 0;

        fotoPreview = new JLabel(UIHelper.createPlaceholderIcon(80));
        fotoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        form.add(fotoPreview, gbc);
        row++;

        JButton btnFoto = UIHelper.createStyledButton("Pilih Foto", Constants.PRIMARY);
        btnFoto.addActionListener(e -> browseFoto());
        gbc.gridy = row;
        form.add(btnFoto, gbc);
        row++;

        gbc.gridwidth = 1;
        fieldId = addField(form, gbc, row++, "ID Karyawan*", "");
        fieldNama = addField(form, gbc, row++, "Nama Lengkap*", "");
        fieldEmail = addField(form, gbc, row++, "Email", "");
        fieldJabatan = addField(form, gbc, row++, "Jabatan", "Crewstore");

        fieldDept = addField(form, gbc, row++, "Departemen", "");
        fieldGaji = addField(form, gbc, row++, "Gaji Pokok", "0");

        JPanel typePanel = new JPanel(new BorderLayout(4, 0));
        typePanel.setOpaque(false);
        JLabel typeLabel = new JLabel("Tipe Karyawan");
        typeLabel.setFont(Constants.FONT_SMALL);
        typeLabel.setForeground(Constants.TEXT_SECONDARY);
        comboType = new JComboBox<>(new String[]{"TETAP", "PKWT", "KANTOR"});
        comboType.setFont(Constants.FONT_BODY);
        typePanel.add(typeLabel, BorderLayout.NORTH);
        typePanel.add(comboType, BorderLayout.CENTER);
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(typePanel, gbc);

        JPanel statusPanel = new JPanel(new BorderLayout(4, 0));
        statusPanel.setOpaque(false);
        JLabel statusLabel = new JLabel("Status Karyawan");
        statusLabel.setFont(Constants.FONT_SMALL);
        statusLabel.setForeground(Constants.TEXT_SECONDARY);
        comboStatus = new JComboBox<>(new String[]{"Aktif", "Non-Aktif"});
        comboStatus.setFont(Constants.FONT_BODY);
        statusPanel.add(statusLabel, BorderLayout.NORTH);
        statusPanel.add(comboStatus, BorderLayout.CENTER);
        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(statusPanel, gbc);
        row++;

        fieldTglLahir = addField(form, gbc, row++, "Tanggal Lahir (yyyy-MM-dd)", "");
        fieldBarcode = addField(form, gbc, row++, "Barcode*", "");

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        main.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setOpaque(false);

        JButton btnBatal = UIHelper.createStyledButton("Batal", Constants.REFRESH_BTN);
        btnBatal.addActionListener(e -> dispose());

        JButton btnSimpan = UIHelper.createStyledButton("Simpan", Constants.ACCENT);
        btnSimpan.addActionListener(e -> saveKaryawan());

        btnPanel.add(btnBatal);
        btnPanel.add(btnSimpan);
        main.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(main);
    }

    private JTextField addField(JPanel panel, GridBagConstraints gbc, int row, String label, String defaultValue) {
        JPanel fieldPanel = new JPanel(new BorderLayout(4, 2));
        fieldPanel.setOpaque(false);
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(Constants.FONT_SMALL);
        jLabel.setForeground(Constants.TEXT_SECONDARY);
        JTextField field = UIHelper.createStyledTextField(defaultValue);
        fieldPanel.add(jLabel, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);
        gbc.gridx = row % 2 == 0 ? 0 : 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(fieldPanel, gbc);
        return field;
    }

    private void browseFoto() {
        FileDialog dialog = new FileDialog((Frame) null, "Pilih Foto", FileDialog.LOAD);
        dialog.setFilenameFilter((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png");
        });
        dialog.setVisible(true);
        if (dialog.getFile() != null) {
            File f = new File(dialog.getDirectory(), dialog.getFile());
            photoPath = f.getAbsolutePath();
            ImageIcon icon = new ImageIcon(f.getAbsolutePath());
            fotoPreview.setIcon(UIHelper.createRoundedImage(icon, 80));
        }
    }

    private void populateFields(Employee emp) {
        fieldId.setText(emp.getEmployeeId());
        fieldNama.setText(emp.getName());
        fieldEmail.setText(emp.getEmail());
        fieldJabatan.setText(emp.getPosition());
        fieldDept.setText(emp.getDepartment());
        fieldGaji.setText(String.valueOf((long) emp.getBaseSalary()));
        comboType.setSelectedItem(emp.getEmploymentType());
        comboStatus.setSelectedItem(emp.getStatus() != null ? emp.getStatus() : "Aktif");
        if (emp.getBirthDate() != null) {
            fieldTglLahir.setText(emp.getBirthDate().toString());
        }
        fieldBarcode.setText(emp.getBarcode());
        photoPath = emp.getPhoto();
        if (photoPath != null && !photoPath.isEmpty()) {
            fotoPreview.setIcon(UIHelper.createRoundedImageFromPath(photoPath, 80));
        }
    }

    private void saveKaryawan() {
        String id = fieldId.getText().trim();
        String nama = fieldNama.getText().trim();
        String barcode = fieldBarcode.getText().trim();

        if (id.isEmpty() || nama.isEmpty() || barcode.isEmpty()) {
            UIHelper.showError(this, "ID Karyawan, Nama, dan Barcode harus diisi.");
            return;
        }

        LocalDate birthDate = null;
        if (!fieldTglLahir.getText().trim().isEmpty()) {
            try {
                birthDate = LocalDate.parse(fieldTglLahir.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                UIHelper.showError(this, "Format tanggal lahir tidak valid. Gunakan yyyy-MM-dd");
                return;
            }
        }

        double gaji = 0;
        try {
            gaji = Double.parseDouble(fieldGaji.getText().trim().replace(".", "").replace(",", "."));
        } catch (NumberFormatException e) {
            UIHelper.showError(this, "Gaji pokok harus berupa angka.");
            return;
        }

        if (editEmployee != null) {
            String savedPhoto = editEmployee.getPhoto();
            if (photoPath != null && !photoPath.equals(editEmployee.getPhoto())) {
                savedPhoto = PhotoUtil.savePhoto(new File(photoPath), editEmployee.getId());
            }
            try {
                controller.updateKaryawan(editEmployee.getId(), id, nama, fieldEmail.getText().trim(),
                        fieldJabatan.getText().trim(), fieldDept.getText().trim(), gaji,
                        (String) comboType.getSelectedItem(), birthDate, savedPhoto, barcode,
                        (String) comboStatus.getSelectedItem());
                saved = true;
            } catch (Exception ex) {
                UIHelper.showError(this, "Gagal menyimpan: " + ex.getMessage());
            }
        } else {
            int empId = controller.addKaryawan(id, nama, fieldEmail.getText().trim(),
                    fieldJabatan.getText().trim(), fieldDept.getText().trim(), gaji,
                    (String) comboType.getSelectedItem(), birthDate, null, barcode,
                    (String) comboStatus.getSelectedItem());
            if (empId > 0) {
                if (photoPath != null) {
                    String savedPhoto = PhotoUtil.savePhoto(new File(photoPath), empId);
                    if (savedPhoto != null) {
                        Employee emp = controller.getById(empId);
                        if (emp != null) {
                            emp.setPhoto(savedPhoto);
                            controller.updateKaryawan(emp.getId(), emp.getEmployeeId(), emp.getName(),
                                    emp.getEmail(), emp.getPosition(), emp.getDepartment(),
                                    emp.getBaseSalary(), emp.getEmploymentType(), emp.getBirthDate(),
                                    savedPhoto, emp.getBarcode(), emp.getStatus());
                        }
                    }
                }
                saved = true;
            } else {
                UIHelper.showError(this, "Gagal menyimpan data. Periksa ID Karyawan dan Barcode mungkin sudah terdaftar.");
            }
        }
        if (saved) dispose();
    }

    public boolean isSaved() { return saved; }
}
