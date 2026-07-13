package com.slipgaji.view;

import com.slipgaji.model.Payslip;
import com.slipgaji.ui.components.AppButton;
import com.slipgaji.ui.components.AppCard;
import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * PayslipPreviewDialog — HTML preview slip gaji dengan warna muted.
 */
public class PayslipPreviewDialog extends JDialog {
    private final Payslip payslip;

    public PayslipPreviewDialog(Window owner, Payslip payslip) {
        super(owner, "Preview Slip Gaji — " + payslip.getEmployeeName(), ModalityType.APPLICATION_MODAL);
        this.payslip = payslip;
        initUI();
    }

    private void initUI() {
        setSize(640, 780);
        setLocationRelativeTo(getOwner());
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout(0, UIMetrics.SPACE_16));
        mainPanel.setBackground(UIColors.NEUTRAL_50);
        mainPanel.setBorder(new EmptyBorder(UIMetrics.SPACE_24, UIMetrics.SPACE_24,
                                             UIMetrics.SPACE_20, UIMetrics.SPACE_24));

        AppCard card = new AppCard();

        JLabel previewLabel = new JLabel(buildPreviewHtml());
        previewLabel.setFont(UIFonts.BODY);
        previewLabel.setVerticalAlignment(SwingConstants.TOP);

        JScrollPane scrollPane = new JScrollPane(previewLabel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIColors.NEUTRAL_0);
        card.addBody(scrollPane);
        mainPanel.add(card, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIMetrics.SPACE_8, 0));
        btnPanel.setOpaque(false);
        JButton closeBtn = AppButton.secondary("Tutup");
        closeBtn.addActionListener(e -> dispose());
        JButton openPdfBtn = AppButton.primary("Buka File PDF");
        openPdfBtn.addActionListener(e -> openPdf());
        btnPanel.add(closeBtn);
        btnPanel.add(openPdfBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private String buildPreviewHtml() {
        String primaryHex = hex(UIColors.PRIMARY_600);
        String primary100Hex = hex(UIColors.PRIMARY_100);
        String primary700Hex = hex(UIColors.PRIMARY_700);
        String neutral800 = hex(UIColors.NEUTRAL_800);
        String neutral600 = hex(UIColors.NEUTRAL_600);
        String neutral200 = hex(UIColors.NEUTRAL_200);
        String successBg = hex(UIColors.SUCCESS_BG);
        String successFg = hex(UIColors.SUCCESS_FG);
        String dangerFg = hex(UIColors.DANGER_FG);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:").append(UIFonts.FAMILY).append(";padding:20px;'>");

        // Header — soft primary bg, not neon
        sb.append("<div style='background:").append(primary100Hex).append(";color:").append(primary700Hex)
          .append(";padding:18px;border-radius:8px;'>");
        sb.append("<h2 style='margin:0;font-size:16px'>SLIP GAJI KARYAWAN</h2>");
        sb.append("<p style='margin:4px 0 0 0;color:").append(primaryHex).append(";font-size:12px'>Periode: ")
          .append(payslip.getPeriod()).append("</p>");
        sb.append("</div><br>");

        // Employee info
        sb.append("<table style='width:100%;color:").append(neutral800).append("'>");
        sb.append(row("Nama", payslip.getEmployeeName(), neutral600, neutral800));
        sb.append(row("ID Karyawan", payslip.getEmployeeIdCode(), neutral600, neutral800));
        sb.append(row("Posisi", payslip.getPosition(), neutral600, neutral800));
        sb.append(row("Departemen", payslip.getDepartment(), neutral600, neutral800));
        sb.append("</table>");
        sb.append("<hr style='border:none;border-top:1px solid ").append(neutral200).append(";margin:12px 0'>");

        // Attendance
        sb.append("<h3 style='color:").append(neutral800).append(";font-size:13px'>Kehadiran</h3>");
        sb.append("<table style='width:100%'>");
        sb.append(row("Hari Hadir", payslip.getDaysPresent() + " hari", neutral600, neutral800));
        sb.append(row("Hari Absen", payslip.getDaysAbsent() + " hari", neutral600, neutral800));
        sb.append(row("Jam Lembur", payslip.getOvertimeHours() + " jam", neutral600, neutral800));
        if (payslip.isNightShift()) sb.append(row("Shift Malam", "Ya", neutral600, neutral800));
        sb.append("</table>");
        sb.append("<hr style='border:none;border-top:1px solid ").append(neutral200).append(";margin:12px 0'>");

        // Salary breakdown
        sb.append("<h3 style='color:").append(neutral800).append(";font-size:13px'>Rincian Gaji</h3>");
        sb.append("<table style='width:100%'>");
        sb.append(row("Gaji Pokok", UIHelper.formatCurrency(payslip.getBaseSalary()), neutral600, neutral800));
        sb.append(row("Uang Lembur", UIHelper.formatCurrency(payslip.getOvertimePay()), neutral600, neutral800));
        sb.append(row("Tunjangan", UIHelper.formatCurrency(payslip.getAllowances()), neutral600, neutral800));
        if (payslip.getNightShiftIncentive() > 0) {
            sb.append(row("Insentif Shift Malam", UIHelper.formatCurrency(payslip.getNightShiftIncentive()), neutral600, neutral800));
        }
        sb.append(row("Potongan Absen", "- " + UIHelper.formatCurrency(payslip.getDeductions()), neutral600, dangerFg));
        sb.append("</table>");
        sb.append("<hr style='border:none;border-top:1px solid ").append(neutral200).append(";margin:12px 0'>");

        // Net salary — muted success
        sb.append("<div style='background:").append(successBg).append(";color:").append(successFg)
          .append(";padding:14px;text-align:center;border-radius:8px;'>");
        sb.append("<b style='font-size:15px'>GAJI BERSIH: ").append(UIHelper.formatCurrency(payslip.getNetSalary())).append("</b>");
        sb.append("</div>");

        sb.append("</body></html>");
        return sb.toString();
    }

    private String row(String label, String value, String labelColor, String valueColor) {
        return "<tr><td style='padding:5px 10px;color:" + labelColor + "'>" + label
                + "</td><td style='padding:5px 10px;font-weight:bold;color:" + valueColor + "'>"
                + (value != null ? value : "-") + "</td></tr>";
    }

    private static String hex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
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
