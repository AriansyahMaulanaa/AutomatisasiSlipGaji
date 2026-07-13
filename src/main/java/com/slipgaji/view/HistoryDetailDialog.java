package com.slipgaji.view;

import com.slipgaji.model.Payslip;
import com.slipgaji.model.SendHistory;
import com.slipgaji.ui.components.AppButton;
import com.slipgaji.ui.components.StatusBadge;
import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * Dialog detail pengiriman email — refactor pakai StatusBadge + AppButton.
 */
public class HistoryDetailDialog extends JDialog {
    private final SendHistory history;
    private final Payslip payslip;

    public HistoryDetailDialog(Window owner, SendHistory history, Payslip payslip) {
        super(owner, "Detail Pengiriman — " + history.getEmployeeName(), ModalityType.APPLICATION_MODAL);
        this.history = history;
        this.payslip = payslip;
        initUI();
    }

    private void initUI() {
        setSize(540, 520);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, UIMetrics.SPACE_16));
        mainPanel.setBackground(UIColors.NEUTRAL_0);
        mainPanel.setBorder(new EmptyBorder(UIMetrics.SPACE_24, UIMetrics.SPACE_24,
                                             UIMetrics.SPACE_20, UIMetrics.SPACE_24));

        // Header — title + status badge
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        JLabel title = new JLabel("Detail Pengiriman");
        title.setFont(UIFonts.H1);
        title.setForeground(UIColors.NEUTRAL_800);
        JLabel subtitle = new JLabel(history.getEmployeeName());
        subtitle.setFont(UIFonts.BODY);
        subtitle.setForeground(UIColors.NEUTRAL_600);
        left.add(title);
        left.add(Box.createVerticalStrut(2));
        left.add(subtitle);
        header.add(left, BorderLayout.WEST);

        boolean isSuccess = "SUCCESS".equals(history.getStatus());
        StatusBadge badge = new StatusBadge(
                isSuccess ? "Email Terkirim" : "Email Gagal",
                isSuccess ? StatusBadge.Tone.SUCCESS : StatusBadge.Tone.DANGER);
        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);
        header.add(badgeWrap, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        // Detail content
        JPanel detail = new JPanel();
        detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));
        detail.setOpaque(false);
        detail.setBorder(new EmptyBorder(UIMetrics.SPACE_16, 0, 0, 0));

        addDetailRow(detail, "Nama Karyawan", history.getEmployeeName());
        addDetailRow(detail, "Email", history.getEmployeeEmail());
        addDetailRow(detail, "Periode", history.getPeriod());
        addDetailRow(detail, "Waktu Kirim", history.getSentAt() != null ? history.getSentAt() : "—");
        addDetailRow(detail, "Dikirim Oleh", history.getSentBy() != null ? history.getSentBy() : "—");

        if (!isSuccess && history.getErrorMessage() != null && !history.getErrorMessage().isEmpty()) {
            addDetailRow(detail, "Error", history.getErrorMessage());
        }

        if (payslip != null) {
            detail.add(Box.createVerticalStrut(UIMetrics.SPACE_8));
            addDetailRow(detail, "Gaji Bersih", UIHelper.formatCurrency(payslip.getNetSalary()));
            String pdfStatus = (payslip.getPdfPath() != null && !payslip.getPdfPath().isEmpty()
                    && new File(payslip.getPdfPath()).exists()) ? "Tersedia" : "Tidak tersedia";
            addDetailRow(detail, "File PDF", pdfStatus);
        }

        JScrollPane sp = new JScrollPane(detail);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        mainPanel.add(sp, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIMetrics.SPACE_8, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(UIMetrics.SPACE_16, 0, 0, 0));

        JButton closeBtn = AppButton.secondary("Tutup");
        closeBtn.addActionListener(e -> dispose());
        btnPanel.add(closeBtn);

        if (payslip != null && payslip.getPdfPath() != null && !payslip.getPdfPath().isEmpty()) {
            JButton openPdfBtn = AppButton.primary("Buka Slip PDF");
            openPdfBtn.addActionListener(e -> openPdf());
            btnPanel.add(openPdfBtn);
        }

        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private void addDetailRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 0, 6, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(UIFonts.LABEL);
        lblLabel.setForeground(UIColors.NEUTRAL_600);
        lblLabel.setPreferredSize(new Dimension(140, 20));

        JLabel lblValue = new JLabel(value != null ? value : "—");
        lblValue.setFont(UIFonts.BODY_BOLD);
        lblValue.setForeground(UIColors.NEUTRAL_800);

        row.add(lblLabel, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.CENTER);
        parent.add(row);
    }

    private void openPdf() {
        if (payslip != null && payslip.getPdfPath() != null && !payslip.getPdfPath().isEmpty()) {
            File pdfFile = new File(payslip.getPdfPath());
            if (pdfFile.exists()) {
                try {
                    Desktop.getDesktop().open(pdfFile);
                } catch (Exception ex) {
                    UIHelper.showError(this, "Gagal membuka PDF: " + ex.getMessage());
                }
            } else {
                UIHelper.showError(this, "File PDF tidak ditemukan:\n" + payslip.getPdfPath());
            }
        }
    }
}
