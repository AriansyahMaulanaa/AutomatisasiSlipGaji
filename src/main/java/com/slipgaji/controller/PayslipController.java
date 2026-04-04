package com.slipgaji.controller;

import com.slipgaji.model.Payslip;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.service.PdfService;

import java.util.List;

public class PayslipController {
    private final DatabaseService db;

    public PayslipController() {
        this.db = DatabaseService.getInstance();
    }

    public List<Payslip> getPayslips(String period) {
        return db.getPayslipsByPeriod(period);
    }

    public List<String> getPeriods() {
        return db.getPayslipPeriods();
    }

    public Payslip getPayslipById(int id) {
        return db.getPayslipById(id);
    }

    public String generatePdf(Payslip payslip) throws Exception {
        String companyName = db.getSetting("company_name");
        String companyAddr = db.getSetting("company_address");
        if (companyName == null || companyName.isEmpty()) companyName = "PT. Maju Bersama Sejahtera";
        if (companyAddr == null || companyAddr.isEmpty()) companyAddr = "Jakarta, Indonesia";

        PdfService pdfService = new PdfService(companyName, companyAddr);
        String path = pdfService.generatePayslip(payslip);
        db.updatePayslipPdfPath(payslip.getId(), path);
        payslip.setPdfPath(path);
        return path;
    }

    public void generateAllPdfs(List<Payslip> payslips, ProgressCallback callback) throws Exception {
        String companyName = db.getSetting("company_name");
        String companyAddr = db.getSetting("company_address");
        if (companyName == null || companyName.isEmpty()) companyName = "PT. Maju Bersama Sejahtera";
        if (companyAddr == null || companyAddr.isEmpty()) companyAddr = "Jakarta, Indonesia";

        PdfService pdfService = new PdfService(companyName, companyAddr);

        for (int i = 0; i < payslips.size(); i++) {
            Payslip p = payslips.get(i);
            String path = pdfService.generatePayslip(p);
            db.updatePayslipPdfPath(p.getId(), path);
            p.setPdfPath(path);
            if (callback != null) callback.onProgress(i + 1, payslips.size(), p.getEmployeeName());
        }
    }

    public interface ProgressCallback {
        void onProgress(int current, int total, String name);
    }
}
