package com.slipgaji.view;

import com.slipgaji.controller.KaryawanController;
import com.slipgaji.model.Employee;
import com.slipgaji.ui.components.AppButton;
import com.slipgaji.ui.components.AppTextField;
import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;
import com.slipgaji.util.PhotoUtil;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * TambahKaryawanDialog — form tambah/edit karyawan.
 *
 * <p>Redesign: field pakai {@link AppTextField}, tombol Simpan Primary,
 * tombol Batal Secondary, tombol "Pilih Foto" Secondary.
 */
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
        setSize(560, 660);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(0, UIMetrics.SPACE_16));
        main.setBackground(UIColors.NEUTRAL_0);
        main.setBorder(new EmptyBorder(UIMetrics.SPACE_24, UIMetrics.SPACE_24,
                                        UIMetrics.SPACE_20, UIMetrics.SPACE_24));

        JLabel title = new JLabel(editEmployee == null ? "Tambah Karyawan Baru" : "Edit Karyawan");
        title.setFont(UIFonts.H1);
        title.setForeground(UIColors.NEUTRAL_800);
        main.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;

        // Foto preview + button
        JPanel fotoBlock = new JPanel();
        fotoBlock.setLayout(new BoxLayout(fotoBlock, BoxLayout.Y_AXIS));
        fotoBlock.setOpaque(false);
        fotoBlock.setBorder(new EmptyBorder(0, 0, UIMetrics.SPACE_8, 0));

        fotoPreview = new JLabel(createAvatarPlaceholder(80));
        fotoPreview.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnFoto = AppButton.secondary("Pilih Foto");
        btnFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnFoto.addActionListener(e -> browseFoto());

        fotoBlock.add(fotoPreview);
        fotoBlock.add(Box.createVerticalStrut(UIMetrics.SPACE_8));
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnWrap.setOpaque(false);
        btnWrap.add(btnFoto);
        fotoBlock.add(btnWrap);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row++;
        form.add(fotoBlock, gbc);

        fieldId = addLabeledField(form, gbc, row++, "ID Karyawan *", "");
        fieldNama = addLabeledField(form, gbc, row++, "Nama Lengkap *", "");
        fieldEmail = addLabeledField(form, gbc, row++, "Email", "");
        fieldJabatan = addLabeledField(form, gbc, row++, "Jabatan", "Crewstore");
        fieldDept = addLabeledField(form, gbc, row++, "Departemen", "");
        fieldGaji = addLabeledField(form, gbc, row++, "Gaji Pokok", "0");

        // Tipe + Status side-by-side
        JPanel dualRow = new JPanel(new GridLayout(1, 2, UIMetrics.SPACE_12, 0));
        dualRow.setOpaque(false);
        dualRow.add(createCombo("Tipe Karyawan", comboType = new JComboBox<>(new String[]{"TETAP", "PKWT", "KANTOR"})));
        dualRow.add(createCombo("Status Karyawan", comboStatus = new JComboBox<>(new String[]{"Aktif", "Non-Aktif"})));
        gbc.gridx = 0; gbc.gridy = row++;
        form.add(dualRow, gbc);

        fieldTglLahir = addLabeledField(form, gbc, row++, "Tanggal Lahir (yyyy-MM-dd)", "");
        fieldBarcode = addLabeledField(form, gbc, row++, "Barcode *", "");

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        main.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIMetrics.SPACE_8, 0));
        btnPanel.setOpaque(false);

        JButton btnBatal = AppButton.secondary("Batal");
        btnBatal.addActionListener(e -> dispose());
        JButton btnSimpan = AppButton.primary("Simpan");
        btnSimpan.addActionListener(e -> saveKaryawan());

        btnPanel.add(btnBatal);
        btnPanel.add(btnSimpan);
        main.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(main);
    }

    private JPanel createCombo(String label, JComboBox<String> combo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(UIFonts.LABEL);
        l.setForeground(UIColors.NEUTRAL_600);
        combo.setFont(UIFonts.BODY);
        combo.setBackground(UIColors.NEUTRAL_0);
        combo.setPreferredSize(new Dimension(0, UIMetrics.INPUT_HEIGHT));
        p.add(l, BorderLayout.NORTH);
        p.add(combo, BorderLayout.CENTER);
        return p;
    }

    private JTextField addLabeledField(JPanel panel, GridBagConstraints gbc, int row, String label, String defaultValue) {
        JPanel fieldPanel = new JPanel(new BorderLayout(0, 4));
        fieldPanel.setOpaque(false);
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(UIFonts.LABEL);
        jLabel.setForeground(UIColors.NEUTRAL_600);
        JTextField field = AppTextField.create("");
        if (defaultValue != null && !defaultValue.isEmpty()) field.setText(defaultValue);
        fieldPanel.add(jLabel, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);
        gbc.gridx = 0; gbc.gridy = row;
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

    /** Avatar placeholder — soft primary bg. */
    private ImageIcon createAvatarPlaceholder(int size) {
        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UIColors.PRIMARY_50);
        g2.fill(new RoundRectangle2D.Double(0, 0, size, size, size, size));
        g2.setColor(UIColors.PRIMARY_500);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = size / 2, cy = size / 2 - size / 10;
        int hr = size / 7;
        g2.drawOval(cx - hr, cy - hr, hr * 2, hr * 2);
        int shoulderW = size / 3;
        int bodyTop = cy + hr + size / 20;
        g2.drawArc(cx - shoulderW, bodyTop, shoulderW * 2, shoulderW * 2, 0, 180);
        g2.dispose();
        return new ImageIcon(bi);
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
        if (emp.getBirthDate() != null) fieldTglLahir.setText(emp.getBirthDate().toString());
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
