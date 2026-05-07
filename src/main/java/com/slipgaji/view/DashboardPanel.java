package com.slipgaji.view;

import com.slipgaji.controller.HistoryController;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private MainView mainView;
    private JLabel empCountLabel;
    private JLabel payslipCountLabel;
    private JLabel sentCountLabel;
    private JLabel failedCountLabel;

    public DashboardPanel(MainView mainView) {
        this.mainView = mainView;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Constants.BG_CARD);
        setOpaque(false);
        setBorder(new EmptyBorder(32, 32, 32, 32));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 24, 0));

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
        statsPanel.setOpaque(false);
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
        centerPanel.setOpaque(false);
        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // Info card
        JPanel infoCard = UIHelper.createCard("Panduan Singkat");

        JLabel infoText = new JLabel("<html><div style='width:550px; line-height:1.8'>"
                + "<p style='color:#374151;'>Langkah-langkah menggunakan " + Constants.APP_NAME + ":</p>"
                + "<ol style='color:#4B5563;'>"
                + "<li><b>Import Data</b> — Upload file Excel (.xlsx) berisi data presensi karyawan</li>"
                + "<li><b>Slip Gaji</b> — Sistem otomatis menghitung gaji, Anda dpt generate PDF untuk periode bersangkutan</li>"
                + "<li><b>Kirim Email</b> — Mengoperasikan bulk send/batch kirim email massal</li>"
                + "<li><b>Histori</b> — Cek rekam status pengiriman email</li>"
                + "</ol>"
<<<<<<< HEAD
=======
<<<<<<< HEAD
                + "<p style='color:#777777; font-size:11px; margin-top:8px;'>"
                + "💡 <i>Format Excel: Employee ID | Nama | Email | Posisi | Departemen | Gaji Pokok | Hari Hadir | Hari Absen | Jam Lembur</i>"
                + "</p>"
=======
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
                + "</div></html>");
        infoText.setFont(Constants.FONT_BODY);
        infoCard.add(infoText, BorderLayout.CENTER);

        JPanel infoWrapper = new JPanel(new BorderLayout());
        infoWrapper.setOpaque(false);
        infoWrapper.setBorder(new EmptyBorder(24, 0, 0, 0));
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

        updateStatCard(empCountLabel, "Total Karyawan", String.valueOf(empCount));
        updateStatCard(payslipCountLabel, "Total Slip Gaji", String.valueOf(slipCount));
        updateStatCard(sentCountLabel, "Email Terkirim", String.valueOf(sentCount));
        updateStatCard(failedCountLabel, "Email Gagal", String.valueOf(failedCount));
    }

    private void updateStatCard(JLabel card, String label, String value) {
        card.setText("<html><div style='padding:8px'>"
                + "<span style='color:#6B7280;font-size:11px'>" + label + "</span><br>"
                + "<span style='color:#111827;font-size:22px'><b>" + value + "</b></span>"
                + "</div></html>");
    }
}
