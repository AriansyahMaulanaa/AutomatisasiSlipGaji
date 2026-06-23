package com.slipgaji.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import com.slipgaji.util.Constants;
import java.io.File;
import java.util.Properties;

public class EmailService {

    private String smtpHost;
    private String smtpPort;
    private String senderEmail;
    private String senderPassword;

    public EmailService(String smtpHost, String smtpPort, String senderEmail, String senderPassword) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }

    public void sendPayslip(String recipientEmail, String recipientName, String period, String pdfPath) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.ssl.trust", smtpHost);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail, false));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Slip Gaji - Periode " + formatPeriod(period) + " | " + recipientName);

        String periodFormatted = formatPeriod(period);

        String bodyHtml = """
                <html>
                <body style="font-family: 'Segoe UI', Arial, sans-serif; color: #334155; max-width: 600px; margin: 0 auto;">
                    <div style="background: linear-gradient(135deg, #2563EB, #3B82F6); padding: 28px; border-radius: 10px 10px 0 0;">
                        <h2 style="color: white; margin: 0; font-size: 20px;">Slip Gaji Karyawan</h2>
                        <p style="color: #BFDBFE; margin: 6px 0 0 0; font-size: 13px;">Periode: %s</p>
                    </div>
                    <div style="background: #F8FAFC; padding: 28px; border: 1px solid #E2E8F0; border-top: none; border-radius: 0 0 10px 10px;">
                        <p style="margin: 0 0 16px 0;">Yth. <strong>%s</strong>,</p>
                        <p style="margin: 0 0 12px 0;">Bersama email ini kami sampaikan slip gaji Anda untuk periode <strong>%s</strong>.</p>
                        <p style="margin: 0 0 12px 0;">Silakan buka file PDF terlampir untuk melihat rincian gaji Anda secara lengkap, meliputi:</p>
                        <ul style="color: #475569; padding-left: 20px; margin: 0 0 20px 0; line-height: 1.8;">
                            <li>Gaji pokok dan tunjangan</li>
                            <li>Perhitungan lembur dan insentif</li>
                            <li>Potongan absensi (jika ada)</li>
                            <li>Total gaji bersih yang diterima</li>
                        </ul>
                        <hr style="border: none; border-top: 1px solid #E2E8F0; margin: 20px 0;">
                        <p style="color: #64748B; font-size: 12px; margin: 0 0 4px 0;">
                            Email ini dikirim secara otomatis oleh sistem %s.
                        </p>
                        <p style="color: #64748B; font-size: 12px; margin: 0;">
                            Jika ada pertanyaan, silakan hubungi bagian HRD atau keuangan perusahaan.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(periodFormatted, recipientName, periodFormatted, Constants.APP_NAME);

        MimeMultipart multipart = new MimeMultipart();

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(bodyHtml, "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);

        if (pdfPath != null && new File(pdfPath).exists()) {
            try {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(new File(pdfPath));
                attachmentPart.setFileName(new File(pdfPath).getName());
                multipart.addBodyPart(attachmentPart);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        message.setContent(multipart);
        Transport.send(message);
    }

    public boolean testConnection() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.ssl.trust", smtpHost);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");

        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });
            Transport transport = session.getTransport("smtp");
            transport.connect(smtpHost, Integer.parseInt(smtpPort), senderEmail, senderPassword);
            transport.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
