package com.slipgaji.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

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

        // Body text
        String bodyHtml = """
                <html>
                <body style="font-family: 'Segoe UI', Arial, sans-serif; color: #1e293b; max-width: 600px; margin: 0 auto;">
                    <div style="background: linear-gradient(135deg, #4f46e5, #7c3aed); padding: 24px; border-radius: 8px 8px 0 0;">
                        <h2 style="color: white; margin: 0;">Slip Gaji Karyawan</h2>
                        <p style="color: #c7d2fe; margin: 4px 0 0 0;">Periode: %s</p>
                    </div>
                    <div style="background: #f8fafc; padding: 24px; border: 1px solid #e2e8f0; border-top: none; border-radius: 0 0 8px 8px;">
                        <p>Yth. <strong>%s</strong>,</p>
                        <p>Bersama email ini kami lampirkan slip gaji Anda untuk periode <strong>%s</strong>.</p>
                        <p>Silakan buka file PDF terlampir untuk melihat rincian gaji Anda.</p>
                        <hr style="border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;">
                        <p style="color: #64748b; font-size: 12px;">
                            Email ini dikirim secara otomatis oleh sistem SlipGaji Pro.<br>
                            Jika ada pertanyaan, silakan hubungi HRD.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(formatPeriod(period), recipientName, formatPeriod(period));

        // Multipart message
        MimeMultipart multipart = new MimeMultipart();

        // HTML body part
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(bodyHtml, "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);

        // PDF attachment
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
