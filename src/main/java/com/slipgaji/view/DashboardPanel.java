package com.slipgaji.view;

import com.slipgaji.controller.AuthController;
import com.slipgaji.controller.HistoryController;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
        setOpaque(false);
        setBorder(new EmptyBorder(28, 32, 32, 32));

        add(createHeader(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);

        centerPanel.add(createStatsGrid(), BorderLayout.NORTH);
        centerPanel.add(createBottomSection(), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        refresh();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        int hour = java.time.LocalTime.now().getHour();
        String timeOfDay;
        if (hour < 10) timeOfDay = "Pagi";
        else if (hour < 15) timeOfDay = "Siang";
        else if (hour < 18) timeOfDay = "Sore";
        else timeOfDay = "Malam";

        String userName = AuthController.getCurrentUser() != null
                ? AuthController.getCurrentUser().getUsername() : "";

        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);

        JLabel greeting = new JLabel("Selamat " + timeOfDay + ", " + userName);
        greeting.setFont(Constants.FONT_BODY);
        greeting.setForeground(Constants.TEXT_SECONDARY);

        JLabel pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(Constants.FONT_TITLE);
        pageTitle.setForeground(Constants.TEXT_PRIMARY);

        leftCol.add(pageTitle);
        leftCol.add(Box.createVerticalStrut(2));
        leftCol.add(greeting);

        header.add(leftCol, BorderLayout.WEST);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id", "ID")));
        JLabel dateLabel = new JLabel(today);
        dateLabel.setFont(Constants.FONT_BODY);
        dateLabel.setForeground(Constants.TEXT_SECONDARY);
        header.add(dateLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createStatsGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 0));
        grid.setOpaque(false);

        empCountLabel = UIHelper.createStatCard("Total Karyawan", "0", Constants.PRIMARY);
        payslipCountLabel = UIHelper.createStatCard("Total Slip Gaji", "0", Constants.ACCENT);
        sentCountLabel = UIHelper.createStatCard("Email Terkirim", "0", new Color(16, 185, 129));
        failedCountLabel = UIHelper.createStatCard("Email Gagal", "0", Constants.ACCENT_DANGER);

        grid.add(empCountLabel);
        grid.add(payslipCountLabel);
        grid.add(sentCountLabel);
        grid.add(failedCountLabel);

        return grid;
    }

    private JPanel createBottomSection() {
        JPanel section = new JPanel(new BorderLayout(0, 16));
        section.setOpaque(false);

        JPanel topRow = new JPanel(new GridLayout(1, 2, 16, 0));
        topRow.setOpaque(false);
        topRow.add(createPanduanCard());
        topRow.add(createTipsCard());

        section.add(topRow, BorderLayout.NORTH);
        section.add(createFormatCard(), BorderLayout.CENTER);

        return section;
    }

    private JPanel createPanduanCard() {
        JPanel card = UIHelper.createCard("Panduan Singkat");
        JLabel text = new JLabel("<html><div style='line-height:1.8;padding:4px 0'>"
                + "<ol style='color:#4B5563;margin:0;padding-left:18px;font-size:12px;font-family:" + Constants.FONT_FAMILY + "'>"
                + "<li><b>Import Data</b> — Upload Excel (.xlsx)</li>"
                + "<li><b>Slip Gaji</b> — Generate PDF</li>"
                + "<li><b>Kirim Email</b> — Batch kirim</li>"
                + "<li><b>Histori</b> — Cek status kirim</li>"
                + "</ol></div></html>");
        text.setFont(Constants.FONT_BODY);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTipsCard() {
        JPanel card = UIHelper.createCard("Informasi Penting");
        JLabel text = new JLabel("<html><div style='line-height:1.9;padding:4px 0'>"
                + "<ul style='color:#4B5563;margin:0;padding-left:16px;font-size:12px;font-family:" + Constants.FONT_FAMILY + "'>"
                + "<li>File <b>.xlsx</b> (bukan .xls)</li>"
                + "<li>Periode: <b>yyyy-MM</b></li>"
                + "<li>Parameter gaji di <b>Pengaturan</b></li>"
                + "<li>Konfigurasi <b>SMTP</b> sebelum kirim</li>"
                + "<li>Login: <b>spv</b> / <b>manager</b></li>"
                + "</ul></div></html>");
        text.setFont(Constants.FONT_BODY);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFormatCard() {
        JPanel card = UIHelper.createCard("Format Data Excel");
        JLabel text = new JLabel("<html><div style='line-height:1.7;padding:4px 0'>"
                + "<table style='border-collapse:collapse;font-size:11px;color:#4B5563;width:100%;font-family:" + Constants.FONT_FAMILY + "'>"
                + "<tr style='background:#F3F4F6'>"
                + "<td style='padding:3px 8px;border:1px solid #E5E7EB;font-weight:bold'>Kolom</td>"
                + "<td style='padding:3px 8px;border:1px solid #E5E7EB;font-weight:bold'>Keterangan</td></tr>"
                + "<tr><td style='padding:3px 8px;border:1px solid #E5E7EB'>ID Karyawan</td><td style='padding:3px 8px;border:1px solid #E5E7EB'>Wajib, teks unik</td></tr>"
                + "<tr><td style='padding:3px 8px;border:1px solid #E5E7EB'>Nama</td><td style='padding:3px 8px;border:1px solid #E5E7EB'>Wajib, tidak boleh hanya angka</td></tr>"
                + "<tr><td style='padding:3px 8px;border:1px solid #E5E7EB'>Email</td><td style='padding:3px 8px;border:1px solid #E5E7EB'>Wajib, format email valid</td></tr>"
                + "<tr><td style='padding:3px 8px;border:1px solid #E5E7EB'>Posisi</td><td style='padding:3px 8px;border:1px solid #E5E7EB'>Crewstore / Store Leader / Manager</td></tr>"
                + "<tr><td style='padding:3px 8px;border:1px solid #E5E7EB'>Departemen</td><td style='padding:3px 8px;border:1px solid #E5E7EB'>Opsional</td></tr>"
                + "<tr><td style='padding:3px 8px;border:1px solid #E5E7EB'>Gaji Pokok</td><td style='padding:3px 8px;border:1px solid #E5E7EB'>Wajib, angka positif</td></tr>"
                + "<tr><td style='padding:3px 8px;border:1px solid #E5E7EB'>Hari Hadir</td><td style='padding:3px 8px;border:1px solid #E5E7EB'>0-31</td></tr>"
                + "<tr><td style='padding:3px 8px;border:1px solid #E5E7EB'>Hari Absen</td><td style='padding:3px 8px;border:1px solid #E5E7EB'>0-31</td></tr>"
                + "<tr><td style='padding:3px 8px;border:1px solid #E5E7EB'>Jam Lembur</td><td style='padding:3px 8px;border:1px solid #E5E7EB'>0-240</td></tr>"
                + "<tr><td style='padding:3px 8px;border:1px solid #E5E7EB'>Shift Malam</td><td style='padding:3px 8px;border:1px solid #E5E7EB'>Y / Ya / 1 / true</td></tr>"
                + "</table></div></html>");
        text.setFont(Constants.FONT_BODY);
        card.add(text, BorderLayout.CENTER);
        return card;
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
        card.setText("<html><div style='padding:4px 6px'>"
                + "<span style='color:#111827;font-size:22px;font-family:" + Constants.FONT_FAMILY + "'><b>" + value + "</b></span><br>"
                + "<span style='color:#6B7280;font-size:11px;font-family:" + Constants.FONT_FAMILY + "'>" + label + "</span>"
                + "</div></html>");
    }
}
