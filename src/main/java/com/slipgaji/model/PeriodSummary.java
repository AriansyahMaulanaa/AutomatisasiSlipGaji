package com.slipgaji.model;

/**
 * Summary data for a payslip period (used in folder view).
 */
public class PeriodSummary {
    private String period;
    private int slipCount;
    private double totalSalary;
    private int pdfGeneratedCount;
    private int emailSentCount;

    public PeriodSummary() {}

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public int getSlipCount() { return slipCount; }
    public void setSlipCount(int slipCount) { this.slipCount = slipCount; }
    public double getTotalSalary() { return totalSalary; }
    public void setTotalSalary(double totalSalary) { this.totalSalary = totalSalary; }
    public int getPdfGeneratedCount() { return pdfGeneratedCount; }
    public void setPdfGeneratedCount(int pdfGeneratedCount) { this.pdfGeneratedCount = pdfGeneratedCount; }
    public int getEmailSentCount() { return emailSentCount; }
    public void setEmailSentCount(int emailSentCount) { this.emailSentCount = emailSentCount; }

    /**
     * Status:
     * - "Terkirim" if all emails sent
     * - "Generated" if all PDFs generated but not all sent
     * - "Draft" otherwise
     */
    public String getStatus() {
        if (slipCount > 0 && emailSentCount >= slipCount) return "Terkirim";
        if (slipCount > 0 && pdfGeneratedCount >= slipCount) return "Generated";
        return "Draft";
    }

    /**
     * Format period string "2026-04" to "April 2026"
     */
    public String getFormattedPeriod() {
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
