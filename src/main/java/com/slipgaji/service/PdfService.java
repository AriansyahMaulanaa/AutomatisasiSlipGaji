package com.slipgaji.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.slipgaji.model.Payslip;
import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfService {

    private static final Color HEADER_BG = new Color(79, 70, 229);
    private static final Color ROW_ALT = new Color(245, 247, 250);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color TEXT_GRAY = new Color(100, 116, 139);
    private static final Color NET_BG = new Color(16, 185, 129);

    private final String companyName;
    private final String companyAddress;

    public PdfService(String companyName, String companyAddress) {
        this.companyName = companyName;
        this.companyAddress = companyAddress;
    }

    public String generatePayslip(Payslip payslip) throws IOException, DocumentException {
        Constants.ensureDirectories();

        String periodDir = Constants.PDF_DIR + File.separator + payslip.getPeriod();
        new File(periodDir).mkdirs();

        String fileName = "SlipGaji_" + payslip.getEmployeeIdCode() + "_" + payslip.getPeriod() + ".pdf";
        String filePath = periodDir + File.separator + fileName;

        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Fonts
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.WHITE);
        Font subtitleFont = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(200, 200, 230));
        Font headingFont = new Font(Font.HELVETICA, 12, Font.BOLD, TEXT_DARK);
        Font labelFont = new Font(Font.HELVETICA, 10, Font.NORMAL, TEXT_GRAY);
        Font valueFont = new Font(Font.HELVETICA, 10, Font.BOLD, TEXT_DARK);
        Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL, TEXT_DARK);
        Font totalLabelFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
        Font totalValueFont = new Font(Font.HELVETICA, 14, Font.BOLD, Color.WHITE);
        Font footerFont = new Font(Font.HELVETICA, 8, Font.NORMAL, TEXT_GRAY);

        // ===== HEADER =====
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);

        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(HEADER_BG);
        headerCell.setPadding(20);
        headerCell.setBorder(Rectangle.NO_BORDER);

        Paragraph companyPara = new Paragraph(companyName, titleFont);
        companyPara.setAlignment(Element.ALIGN_LEFT);
        headerCell.addElement(companyPara);

        Paragraph addressPara = new Paragraph(companyAddress, subtitleFont);
        addressPara.setAlignment(Element.ALIGN_LEFT);
        headerCell.addElement(addressPara);

        Paragraph slipTitle = new Paragraph("SLIP GAJI KARYAWAN", new Font(Font.HELVETICA, 10, Font.BOLD, new Color(180, 180, 230)));
        slipTitle.setSpacingBefore(8);
        slipTitle.setAlignment(Element.ALIGN_LEFT);
        headerCell.addElement(slipTitle);

        headerTable.addCell(headerCell);
        document.add(headerTable);
        document.add(new Paragraph(" "));

        // ===== EMPLOYEE INFO =====
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{15, 35, 15, 35});

        addInfoRow(infoTable, "Nama", payslip.getEmployeeName(), "ID Karyawan", payslip.getEmployeeIdCode(), labelFont, valueFont);
        addInfoRow(infoTable, "Posisi", payslip.getPosition(), "Departemen", payslip.getDepartment(), labelFont, valueFont);
        addInfoRow(infoTable, "Periode", formatPeriod(payslip.getPeriod()), "Tgl. Cetak", java.time.LocalDate.now().toString(), labelFont, valueFont);

        document.add(infoTable);
        document.add(new Paragraph(" "));

        // ===== ATTENDANCE =====
        Paragraph attTitle = new Paragraph("KEHADIRAN", headingFont);
        attTitle.setSpacingBefore(5);
        document.add(attTitle);
        document.add(createLine());

        PdfPTable attTable = new PdfPTable(3);
        attTable.setWidthPercentage(100);
        attTable.setWidths(new float[]{33, 33, 34});

        addAttendanceCell(attTable, "Hari Hadir", String.valueOf(payslip.getDaysPresent()) + " hari", labelFont, valueFont);
        addAttendanceCell(attTable, "Hari Absen", String.valueOf(payslip.getDaysAbsent()) + " hari", labelFont, valueFont);
        addAttendanceCell(attTable, "Jam Lembur", String.valueOf(payslip.getOvertimeHours()) + " jam", labelFont, valueFont);

        document.add(attTable);
        document.add(new Paragraph(" "));

        // ===== SALARY DETAILS =====
        Paragraph salaryTitle = new Paragraph("RINCIAN GAJI", headingFont);
        salaryTitle.setSpacingBefore(5);
        document.add(salaryTitle);
        document.add(createLine());

        PdfPTable salaryTable = new PdfPTable(2);
        salaryTable.setWidthPercentage(100);
        salaryTable.setWidths(new float[]{60, 40});

        // Pendapatan
        addSalaryHeader(salaryTable, "PENDAPATAN");
        addSalaryRow(salaryTable, "Gaji Pokok", UIHelper.formatCurrency(payslip.getBaseSalary()), normalFont, valueFont, false);
        addSalaryRow(salaryTable, "Uang Lembur (" + payslip.getOvertimeHours() + " jam)", UIHelper.formatCurrency(payslip.getOvertimePay()), normalFont, valueFont, true);
        addSalaryRow(salaryTable, "Tunjangan (Transport + Makan)", UIHelper.formatCurrency(payslip.getAllowances()), normalFont, valueFont, false);

        // Potongan
        addSalaryHeader(salaryTable, "POTONGAN");
        addSalaryRow(salaryTable, "Potongan Absen (" + payslip.getDaysAbsent() + " hari)", UIHelper.formatCurrency(payslip.getDeductions()), normalFont, valueFont, true);

        document.add(salaryTable);

        // ===== NET SALARY =====
        PdfPTable netTable = new PdfPTable(2);
        netTable.setWidthPercentage(100);
        netTable.setWidths(new float[]{60, 40});
        netTable.setSpacingBefore(5);

        PdfPCell netLabelCell = new PdfPCell(new Phrase("GAJI BERSIH (TAKE HOME PAY)", totalLabelFont));
        netLabelCell.setBackgroundColor(NET_BG);
        netLabelCell.setPadding(12);
        netLabelCell.setBorder(Rectangle.NO_BORDER);
        netLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        netTable.addCell(netLabelCell);

        PdfPCell netValueCell = new PdfPCell(new Phrase(UIHelper.formatCurrency(payslip.getNetSalary()), totalValueFont));
        netValueCell.setBackgroundColor(NET_BG);
        netValueCell.setPadding(12);
        netValueCell.setBorder(Rectangle.NO_BORDER);
        netValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        netValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        netTable.addCell(netValueCell);

        document.add(netTable);

        // ===== FOOTER =====
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        Paragraph disclaimer = new Paragraph(
                "Dokumen ini digenerate secara otomatis oleh sistem SlipGaji Pro dan sah tanpa tanda tangan.",
                footerFont
        );
        disclaimer.setAlignment(Element.ALIGN_CENTER);
        document.add(disclaimer);

        Paragraph confidential = new Paragraph(
                "CONFIDENTIAL - Hanya untuk penerima yang dituju.",
                new Font(Font.HELVETICA, 8, Font.BOLD, TEXT_GRAY)
        );
        confidential.setAlignment(Element.ALIGN_CENTER);
        confidential.setSpacingBefore(4);
        document.add(confidential);

        Paragraph watermark = new Paragraph(
                "Dibuat oleh: Mahasiswa Universitas Pamulang",
                new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(180, 180, 180))
        );
        watermark.setAlignment(Element.ALIGN_CENTER);
        watermark.setSpacingBefore(20);
        document.add(watermark);

        document.close();
        return filePath;
    }

    private void addInfoRow(PdfPTable table, String label1, String value1, String label2, String value2, Font labelFont, Font valueFont) {
        PdfPCell l1 = new PdfPCell(new Phrase(label1, labelFont));
        l1.setBorder(Rectangle.NO_BORDER);
        l1.setPadding(4);
        table.addCell(l1);

        PdfPCell v1 = new PdfPCell(new Phrase(": " + (value1 != null ? value1 : "-"), valueFont));
        v1.setBorder(Rectangle.NO_BORDER);
        v1.setPadding(4);
        table.addCell(v1);

        PdfPCell l2 = new PdfPCell(new Phrase(label2, labelFont));
        l2.setBorder(Rectangle.NO_BORDER);
        l2.setPadding(4);
        table.addCell(l2);

        PdfPCell v2 = new PdfPCell(new Phrase(": " + (value2 != null ? value2 : "-"), valueFont));
        v2.setBorder(Rectangle.NO_BORDER);
        v2.setPadding(4);
        table.addCell(v2);
    }

    private void addAttendanceCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(BORDER);
        cell.setPadding(10);
        cell.setBackgroundColor(ROW_ALT);

        Paragraph lbl = new Paragraph(label, labelFont);
        cell.addElement(lbl);
        Paragraph val = new Paragraph(value, valueFont);
        val.setSpacingBefore(2);
        cell.addElement(val);

        table.addCell(cell);
    }

    private void addSalaryHeader(PdfPTable table, String title) {
        Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD, TEXT_GRAY);
        PdfPCell cell = new PdfPCell(new Phrase(title, headerFont));
        cell.setColspan(2);
        cell.setBackgroundColor(ROW_ALT);
        cell.setPadding(8);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(BORDER);
        table.addCell(cell);
    }

    private void addSalaryRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont, boolean alt) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(8);
        labelCell.setBorder(Rectangle.BOTTOM);
        labelCell.setBorderColor(BORDER);
        if (alt) labelCell.setBackgroundColor(ROW_ALT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(8);
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColor(BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        if (alt) valueCell.setBackgroundColor(ROW_ALT);
        table.addCell(valueCell);
    }

    private Paragraph createLine() {
        Paragraph line = new Paragraph(" ");
        line.setSpacingAfter(2);
        return line;
    }

    private String formatPeriod(String period) {
        if (period == null || period.length() < 7) return period;
        String[] parts = period.split("-");
        String[] months = {"", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        try {
            int monthIdx = Integer.parseInt(parts[1]);
            return months[monthIdx] + " " + parts[0];
        } catch (Exception e) {
            return period;
        }
    }
}
