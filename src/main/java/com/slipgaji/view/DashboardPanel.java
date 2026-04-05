package com.slipgaji.view;

import com.slipgaji.controller.HistoryController;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private JLabel empCountLabel;
    private JLabel payslipCountLabel;
    private JLabel sentCountLabel;
    private JLabel failedCountLabel;

    public DashboardPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Constants.BG_DARK);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.BG_DARK);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(Constants.FONT_TITLE);
        pageTitle.setForeground(Constants.TEXT_PRIMARY);
        headerPanel.add(pageTitle, BorderLayout.WEST);

        JLabel welcomeLabel = new JLabel("Selamat datang di " + Constants.APP_NAME);
        welcomeLabel.setFont(Constants.FONT_BODY);
        welcomeLabel.setForeground(Constants.TEXT_SECONDARY);
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Stats grid
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setBackground(Constants.BG_DARK);
        statsPanel.setPreferredSize(new Dimension(0, 100));

        empCountLabel = UIHelper.createStatCard("Total Karyawan", "0", Constants.PRIMARY);
        payslipCountLabel = UIHelper.createStatCard("Total Slip Gaji", "0", Constants.ACCENT);
        sentCountLabel = UIHelper.createStatCard("Email Terkirim", "0", new Color(16, 185, 129));
        failedCountLabel = UIHelper.createStatCard("Email Gagal", "0", Constants.ACCENT_DANGER);

        statsPanel.add(empCountLabel);
        statsPanel.add(payslipCountLabel);
        statsPanel.add(sentCountLabel);
        statsPanel.add(failedCountLabel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Constants.BG_DARK);
        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // Info card
        JPanel infoCard = UIHelper.createCard("Panduan Singkat");
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel infoText = new JLabel("<html><div style='width:600px; line-height:1.8'>"
                + "<p style='color:#333333;'>Langkah-langkah menggunakan " + Constants.APP_NAME + ":</p>"
                + "<ol style='color:#555555;'>"
                + "<li><b>Import Data</b> — Upload file Excel (.xlsx) berisi data presensi karyawan</li>"
                + "<li><b>Review Slip</b> — Sistem otomatis menghitung gaji dan generate slip</li>"
                + "<li><b>Preview PDF</b> — Cek desain slip gaji sebelum dikirim</li>"
                + "<li><b>Kirim Email</b> — Kirim slip ke email masing-masing karyawan</li>"
                + "<li><b>Histori</b> — Cek status pengiriman email</li>"
                + "</ol>"
                + "<p style='color:#777777; font-size:11px; margin-top:8px;'>"
                + "💡 <i>Format Excel: Employee ID | Nama | Email | Posisi | Departemen | Gaji Pokok | Hari Hadir | Hari Absen | Jam Lembur</i>"
                + "</p>"
                + "</div></html>");
        infoText.setFont(Constants.FONT_BODY);
        infoCard.add(infoText, BorderLayout.CENTER);

        JPanel infoWrapper = new JPanel(new BorderLayout());
        infoWrapper.setBackground(Constants.BG_DARK);
        infoWrapper.setBorder(new EmptyBorder(20, 0, 0, 0));
        infoWrapper.add(infoCard, BorderLayout.NORTH);

        centerPanel.add(infoWrapper, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        DatabaseService db = DatabaseService.getInstance();
        HistoryController hc = new HistoryController();

        int empCount = db.getEmployeeCount();
        int slipCount = db.getPayslipCount();
        int sentCount = hc.getSentCount();
        int failedCount = hc.getFailedCount();

        updateStatCard(empCountLabel, "Total Karyawan", String.valueOf(empCount), Constants.PRIMARY);
        updateStatCard(payslipCountLabel, "Total Slip Gaji", String.valueOf(slipCount), Constants.ACCENT);
        updateStatCard(sentCountLabel, "Email Terkirim", String.valueOf(sentCount), new Color(16, 185, 129));
        updateStatCard(failedCountLabel, "Email Gagal", String.valueOf(failedCount), Constants.ACCENT_DANGER);
    }

    private void updateStatCard(JLabel card, String label, String value, Color accent) {
        card.setText("<html><div style='padding:8px'>"
                + "<span style='color:#666666;font-size:10px'>" + label + "</span><br>"
                + "<span style='color:#111111;font-size:20px'><b>" + value + "</b></span>"
                + "</div></html>");
    }
}
