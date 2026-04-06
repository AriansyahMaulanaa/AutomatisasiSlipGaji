package com.slipgaji.view;

import com.slipgaji.model.Payslip;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class PayslipPreviewDialog extends JDialog {
    private final Payslip payslip;

    public PayslipPreviewDialog(Window owner, Payslip payslip) {
        super(owner, "Preview Slip Gaji — " + payslip.getEmployeeName(), ModalityType.APPLICATION_MODAL);
        this.payslip = payslip;
        initUI();
    }

    private void initUI() {
        setSize(620, 780);
        setLocationRelativeTo(getOwner());
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Constants.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Info panel at top
        JPanel infoCard = UIHelper.createCard("");
        infoCard.setBorder(new EmptyBorder(0, 0, 0, 0));

        String html = buildPreviewHtml();
        JLabel previewLabel = new JLabel(html);
        previewLabel.setFont(Constants.FONT_BODY);
        previewLabel.setVerticalAlignment(SwingConstants.TOP);

        JScrollPane scrollPane = new JScrollPane(previewLabel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        infoCard.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setBackground(Constants.BG_DARK);
        btnPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton openPdfBtn = UIHelper.createStyledButton("📂 Buka File PDF", Constants.PRIMARY);
        openPdfBtn.addActionListener(e -> openPdf());

        JButton closeBtn = UIHelper.createStyledButton("Tutup", Constants.REFRESH_BTN);
        closeBtn.addActionListener(e -> dispose());

        btnPanel.add(openPdfBtn);
        btnPanel.add(closeBtn);

        mainPanel.add(infoCard, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private String buildPreviewHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Segoe UI;padding:24px;'>");

        // Header
        sb.append("<div style='background:#6366F1;color:white;padding:20px;border-radius:8px;'>");
        sb.append("<h2 style='margin:0'>SLIP GAJI KARYAWAN</h2>");
        sb.append("<p style='margin:4px 0 0 0;color:#C7D2FE'>Periode: ").append(payslip.getPeriod()).append("</p>");
        sb.append("</div>");
        sb.append("<br>");

        // Employee info
        sb.append("<table style='width:100%'>");
        sb.append(row("Nama", payslip.getEmployeeName()));
        sb.append(row("ID Karyawan", payslip.getEmployeeIdCode()));
        sb.append(row("Posisi", payslip.getPosition()));
        sb.append(row("Departemen", payslip.getDepartment()));
        sb.append("</table>");
        sb.append("<hr style='border:none;border-top:1px solid #E5E7EB;margin:12px 0'>");

        // Attendance
        sb.append("<h3 style='color:#111827;font-size:14px'>Kehadiran</h3>");
        sb.append("<table style='width:100%'>");
        sb.append(row("Hari Hadir", payslip.getDaysPresent() + " hari"));
        sb.append(row("Hari Absen", payslip.getDaysAbsent() + " hari"));
        sb.append(row("Jam Lembur", payslip.getOvertimeHours() + " jam"));
        if (payslip.isNightShift()) {
            sb.append(row("Shift Malam", "Ya"));
        }
        sb.append("</table>");
        sb.append("<hr style='border:none;border-top:1px solid #E5E7EB;margin:12px 0'>");

        // Salary
        sb.append("<h3 style='color:#111827;font-size:14px'>Rincian Gaji</h3>");
        sb.append("<table style='width:100%'>");
        sb.append(row("Gaji Pokok", UIHelper.formatCurrency(payslip.getBaseSalary())));
        sb.append(row("Uang Lembur", UIHelper.formatCurrency(payslip.getOvertimePay())));
        sb.append(row("Tunjangan", UIHelper.formatCurrency(payslip.getAllowances())));
        if (payslip.getNightShiftIncentive() > 0) {
            sb.append(row("Insentif Shift Malam", UIHelper.formatCurrency(payslip.getNightShiftIncentive())));
        }
        sb.append(rowRed("Potongan Absen", "- " + UIHelper.formatCurrency(payslip.getDeductions())));
        sb.append("</table>");
        sb.append("<hr style='border:none;border-top:1px solid #E5E7EB;margin:12px 0'>");

        // Net salary
        sb.append("<div style='background:#10B981;color:white;padding:16px;text-align:center;border-radius:8px;'>");
        sb.append("<b style='font-size:16px'>GAJI BERSIH: ").append(UIHelper.formatCurrency(payslip.getNetSalary())).append("</b>");
        sb.append("</div>");

        sb.append("</body></html>");
        return sb.toString();
    }

    private String row(String label, String value) {
        return "<tr><td style='padding:5px 10px;color:#6B7280'>" + label
                + "</td><td style='padding:5px 10px;font-weight:bold;color:#111827'>" + (value != null ? value : "-") + "</td></tr>";
    }

    private String rowRed(String label, String value) {
        return "<tr><td style='padding:5px 10px;color:#6B7280'>" + label
                + "</td><td style='padding:5px 10px;font-weight:bold;color:#EF4444'>" + value + "</td></tr>";
    }

    private void openPdf() {
        if (payslip.getPdfPath() != null && !payslip.getPdfPath().isEmpty()) {
            try {
                Desktop.getDesktop().open(new File(payslip.getPdfPath()));
            } catch (Exception ex) {
                UIHelper.showError(this, "Gagal membuka PDF: " + ex.getMessage());
            }
        } else {
            UIHelper.showError(this, "File PDF belum digenerate.");
        }
    }
}
