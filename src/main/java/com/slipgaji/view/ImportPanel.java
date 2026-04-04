package com.slipgaji.view;

import com.slipgaji.controller.ImportController;
import com.slipgaji.model.Employee;
import com.slipgaji.model.Payslip;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ImportPanel extends JPanel {
    private final ImportController importController;
    private final MainView mainView;
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JLabel fileLabel;
    private JTextField periodField;
    private JButton processButton;
    private List<Employee> importedEmployees;

    public ImportPanel(MainView mainView) {
        this.mainView = mainView;
        this.importController = new ImportController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Constants.BG_DARK);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.BG_DARK);
        headerPanel.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel pageTitle = new JLabel("📥 Import Data Presensi");
        pageTitle.setFont(Constants.FONT_TITLE);
        pageTitle.setForeground(Constants.TEXT_PRIMARY);
        headerPanel.add(pageTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Top controls
        JPanel controlCard = UIHelper.createCard("");
        controlCard.setLayout(new BoxLayout(controlCard, BoxLayout.Y_AXIS));

        // Row 1: File selection
        JPanel fileRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        fileRow.setBackground(Constants.BG_CARD);

        JButton browseBtn = UIHelper.createStyledButton("📂 Pilih File Excel", Constants.PRIMARY);
        browseBtn.addActionListener(e -> browseFile());

        fileLabel = new JLabel("Belum ada file dipilih");
        fileLabel.setFont(Constants.FONT_BODY);
        fileLabel.setForeground(Constants.TEXT_SECONDARY);

        fileRow.add(browseBtn);
        fileRow.add(fileLabel);
        controlCard.add(fileRow);
        controlCard.add(Box.createVerticalStrut(12));

        // Row 2: Period + Process
        JPanel periodRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        periodRow.setBackground(Constants.BG_CARD);

        JLabel periodLabel = new JLabel("Periode Gaji:");
        periodLabel.setFont(Constants.FONT_BODY);
        periodLabel.setForeground(Constants.TEXT_SECONDARY);

        String currentPeriod = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        periodField = UIHelper.createStyledTextField("yyyy-MM");
        periodField.setText(currentPeriod);
        periodField.setPreferredSize(new Dimension(140, 36));

        processButton = UIHelper.createStyledButton("⚡ Proses & Simpan", Constants.ACCENT);
        processButton.setEnabled(false);
        processButton.addActionListener(e -> processData());

        periodRow.add(periodLabel);
        periodRow.add(periodField);
        periodRow.add(Box.createHorizontalStrut(20));
        periodRow.add(processButton);
        controlCard.add(periodRow);

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setBackground(Constants.BG_DARK);
        topWrapper.setBorder(new EmptyBorder(0, 0, 16, 0));
        topWrapper.add(controlCard, BorderLayout.CENTER);

        // Table
        String[] columns = {"No", "ID Karyawan", "Nama", "Email", "Posisi", "Departemen",
                "Gaji Pokok", "Hari Hadir", "Hari Absen", "Jam Lembur"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        dataTable = new JTable(tableModel);
        UIHelper.styleTable(dataTable);

        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.getViewport().setBackground(Constants.BG_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));

        JPanel tableCard = UIHelper.createCard("📋 Preview Data Import");
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Constants.BG_DARK);
        centerPanel.add(topWrapper, BorderLayout.NORTH);
        centerPanel.add(tableCard, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void browseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        chooser.setDialogTitle("Pilih File Data Presensi");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            fileLabel.setText("📎 " + file.getName());
            fileLabel.setForeground(Constants.ACCENT);

            try {
                importedEmployees = importController.readExcel(file);
                populateTable(importedEmployees);
                processButton.setEnabled(true);

                UIHelper.showSuccess(this, "Berhasil membaca " + importedEmployees.size()
                        + " data karyawan dari file Excel.");

            } catch (Exception ex) {
                UIHelper.showError(this, "Gagal membaca file Excel:\n" + ex.getMessage());
                processButton.setEnabled(false);
            }
        }
    }

    private void populateTable(List<Employee> employees) {
        tableModel.setRowCount(0);
        int no = 1;
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                    no++,
                    emp.getEmployeeId(),
                    emp.getName(),
                    emp.getEmail(),
                    emp.getPosition(),
                    emp.getDepartment(),
                    UIHelper.formatCurrency(emp.getBaseSalary()),
                    emp.getDaysPresent(),
                    emp.getDaysAbsent(),
                    emp.getOvertimeHours()
            });
        }
    }

    private void processData() {
        if (importedEmployees == null || importedEmployees.isEmpty()) {
            UIHelper.showError(this, "Tidak ada data untuk diproses.");
            return;
        }

        String period = periodField.getText().trim();
        if (!period.matches("\\d{4}-\\d{2}")) {
            UIHelper.showError(this, "Format periode tidak valid. Gunakan format: yyyy-MM (contoh: 2026-03)");
            return;
        }

        if (!UIHelper.showConfirm(this, "Proses " + importedEmployees.size()
                + " data karyawan untuk periode " + period + "?")) {
            return;
        }

        processButton.setEnabled(false);
        processButton.setText("⏳ Memproses...");

        SwingWorker<List<Payslip>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Payslip> doInBackground() {
                return importController.processAndSave(importedEmployees, period);
            }

            @Override
            protected void done() {
                try {
                    List<Payslip> payslips = get();
                    UIHelper.showSuccess(ImportPanel.this,
                            "Berhasil memproses " + payslips.size() + " slip gaji.\n\n"
                                    + "Silakan buka menu 'Slip Gaji' untuk preview dan kirim email.");

                    // Navigate to payslip panel
                    mainView.navigateToPayslips();

                } catch (Exception ex) {
                    UIHelper.showError(ImportPanel.this, "Error: " + ex.getMessage());
                } finally {
                    processButton.setEnabled(true);
                    processButton.setText("⚡ Proses & Simpan");
                }
            }
        };
        worker.execute();
    }
}
