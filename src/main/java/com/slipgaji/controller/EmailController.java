package com.slipgaji.controller;

import com.slipgaji.model.Payslip;
import com.slipgaji.model.SendHistory;
import com.slipgaji.service.DatabaseService;
import com.slipgaji.service.EmailService;

import java.util.List;

public class EmailController {
    private final DatabaseService db;

    public EmailController() {
        this.db = DatabaseService.getInstance();
    }

    private EmailService createEmailService() {
        String host = db.getSetting("smtp_host");
        String port = db.getSetting("smtp_port");
        String email = db.getSetting("smtp_email");
        String password = db.getSetting("smtp_password");

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalStateException("Konfigurasi SMTP belum lengkap. Silakan atur di Settings.");
        }

        return new EmailService(host, port, email, password);
    }

    public void sendSingle(Payslip payslip) throws Exception {
        EmailService emailService = createEmailService();
        String sentBy = AuthController.getCurrentUser() != null ?
                AuthController.getCurrentUser().getUsername() : "system";

        try {
            // Generate PDF if not exists
            if (payslip.getPdfPath() == null || payslip.getPdfPath().isEmpty()) {
                PayslipController pc = new PayslipController();
                pc.generatePdf(payslip);
            }

            emailService.sendPayslip(
                    payslip.getEmployeeEmail(),
                    payslip.getEmployeeName(),
                    payslip.getPeriod(),
                    payslip.getPdfPath()
            );

            // Log success
            SendHistory history = new SendHistory(
                    payslip.getId(), payslip.getEmployeeName(), payslip.getEmployeeEmail(),
                    payslip.getPeriod(), "SUCCESS", null, sentBy
            );
            db.saveSendHistory(history);

        } catch (Exception e) {
            // Log failure
            SendHistory history = new SendHistory(
                    payslip.getId(), payslip.getEmployeeName(), payslip.getEmployeeEmail(),
                    payslip.getPeriod(), "FAILED", e.getMessage(), sentBy
            );
            db.saveSendHistory(history);
            throw e;
        }
    }

    public void sendBatch(List<Payslip> payslips, BatchCallback callback) {
        EmailService emailService = createEmailService();
        String sentBy = AuthController.getCurrentUser() != null ?
                AuthController.getCurrentUser().getUsername() : "system";

        PayslipController pc = new PayslipController();
        int success = 0, failed = 0;

        for (int i = 0; i < payslips.size(); i++) {
            Payslip payslip = payslips.get(i);
            try {
                // Generate PDF if needed
                if (payslip.getPdfPath() == null || payslip.getPdfPath().isEmpty()) {
                    pc.generatePdf(payslip);
                }

                emailService.sendPayslip(
                        payslip.getEmployeeEmail(),
                        payslip.getEmployeeName(),
                        payslip.getPeriod(),
                        payslip.getPdfPath()
                );

                SendHistory history = new SendHistory(
                        payslip.getId(), payslip.getEmployeeName(), payslip.getEmployeeEmail(),
                        payslip.getPeriod(), "SUCCESS", null, sentBy
                );
                db.saveSendHistory(history);
                success++;

                if (callback != null) callback.onProgress(i + 1, payslips.size(),
                        payslip.getEmployeeName(), "SUCCESS", null);

            } catch (Exception e) {
                SendHistory history = new SendHistory(
                        payslip.getId(), payslip.getEmployeeName(), payslip.getEmployeeEmail(),
                        payslip.getPeriod(), "FAILED", e.getMessage(), sentBy
                );
                db.saveSendHistory(history);
                failed++;

                if (callback != null) callback.onProgress(i + 1, payslips.size(),
                        payslip.getEmployeeName(), "FAILED", e.getMessage());
            }
        }

        if (callback != null) callback.onComplete(success, failed);
    }

    public boolean testSmtpConnection() {
        EmailService emailService = createEmailService();
        return emailService.testConnection();
    }

    public interface BatchCallback {
        void onProgress(int current, int total, String name, String status, String error);
        void onComplete(int success, int failed);
    }
}
