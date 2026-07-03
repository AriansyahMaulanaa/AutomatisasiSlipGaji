package com.slipgaji.view;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.slipgaji.controller.PresensiController;
import com.slipgaji.controller.PresensiController.PresensiResult;
import com.slipgaji.model.Employee;
import com.slipgaji.util.BarcodeUtil;
import com.slipgaji.util.Constants;
import com.slipgaji.util.SoundUtil;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class PresensiPanel extends JPanel {

    private final PresensiController presensiController = new PresensiController();
    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private Timer scanTimer;

    private JLabel fotoLabel;
    private JLabel namaLabel;
    private JLabel jabatanLabel;
    private JLabel statusKaryawanLabel;
    private JLabel idLabel;
    private JLabel tglLahirLabel;
    private JLabel statusScanLabel;

    private JPanel infoPanel;
    private ScanOverlayPanel scanOverlay;
    private boolean scanning = true;

    private final Map<JLabel, String> fieldNames = new HashMap<>();
    private String lastBarcode = "";
    private long lastBarcodeTime = 0;

    public PresensiPanel() {
        initUI();
        initCamera();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Presensi Scan Barcode");
        title.setFont(Constants.FONT_TITLE);
        title.setForeground(Constants.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 8, 8, 8));
        add(title, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();

        scanOverlay = new ScanOverlayPanel();
        scanOverlay.setPreferredSize(new Dimension(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT));
        scanOverlay.setMinimumSize(new Dimension(400, 300));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 16);
        mainContent.add(scanOverlay, gbc);

        infoPanel = createInfoPanel();
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContent.add(infoPanel, gbc);

        add(mainContent, BorderLayout.CENTER);

        statusScanLabel = new JLabel("Menunggu Scan...", SwingConstants.CENTER);
        statusScanLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 16));
        statusScanLabel.setForeground(Constants.TEXT_SECONDARY);
        statusScanLabel.setBorder(new EmptyBorder(8, 0, 4, 0));
        add(statusScanLabel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setPreferredSize(new Dimension(280, 0));

        fotoLabel = new JLabel();
        fotoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fotoLabel.setBorder(new EmptyBorder(0, 0, 16, 0));

        namaLabel = createInfoField("Nama");
        jabatanLabel = createInfoField("Jabatan");
        statusKaryawanLabel = createInfoField("Status");
        idLabel = createInfoField("ID Karyawan");
        tglLahirLabel = createInfoField("Tanggal Lahir");

        JLabel infoTitle = new JLabel("Informasi Karyawan");
        infoTitle.setFont(Constants.FONT_HEADING);
        infoTitle.setForeground(Constants.TEXT_PRIMARY);
        infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        panel.add(infoTitle);
        panel.add(fotoLabel);
        panel.add(namaLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(jabatanLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(statusKaryawanLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(idLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(tglLahirLabel);

        clearInfo();

        return panel;
    }

    private JLabel createInfoField(String label) {
        JLabel lbl = new JLabel();
        lbl.setFont(Constants.FONT_BODY);
        lbl.setForeground(Constants.TEXT_PRIMARY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldNames.put(lbl, label);
        setInfoField(lbl, null);
        return lbl;
    }

    private void setInfoField(JLabel label, String value) {
        String fieldName = fieldNames.get(label);
        if (fieldName == null) return;
        String display = (value != null && !value.isEmpty()) ? value : "-";
        String color = value != null ? "#111827" : "#6B7280";
        label.setText("<html><b>" + fieldName + ":</b> <span style='color:" + color + "'>" + display + "</span></html>");
    }

    private void clearInfo() {
        fotoLabel.setIcon(UIHelper.createPlaceholderIcon(120));
        for (JLabel lbl : fieldNames.keySet()) {
            setInfoField(lbl, null);
        }
    }

    private void displayEmployeeInfo(Employee emp) {
        if (emp == null) {
            clearInfo();
            return;
        }
        setInfoField(namaLabel, emp.getName());
        setInfoField(jabatanLabel, emp.getPosition());
        setInfoField(statusKaryawanLabel, emp.getStatus());
        setInfoField(idLabel, emp.getEmployeeId());
        setInfoField(tglLahirLabel, emp.getBirthDate() != null ?
                emp.getBirthDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) : "-");

        if (emp.getPhoto() != null && !emp.getPhoto().isEmpty()) {
            fotoLabel.setIcon(UIHelper.createRoundedImageFromPath(emp.getPhoto(), 120));
        } else {
            String initial = emp.getName() != null && !emp.getName().isEmpty() ?
                    String.valueOf(emp.getName().charAt(0)).toUpperCase() : "?";
            fotoLabel.setIcon(createInitialIcon(initial, 120));
        }
        fotoLabel.repaint();
    }

    private ImageIcon createInitialIcon(String initial, int size) {
        java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Constants.PRIMARY);
        g2.fill(new java.awt.geom.Ellipse2D.Double(0, 0, size, size));
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, size / 2));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (size - fm.stringWidth(initial)) / 2;
        int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(initial, tx, ty);
        g2.dispose();
        return new ImageIcon(bi);
    }

    private void initCamera() {
        try {
            webcam = Webcam.getDefault();
            if (webcam == null) {
                statusScanLabel.setText("Kamera tidak terdeteksi");
                statusScanLabel.setForeground(Constants.ACCENT_DANGER);
                return;
            }
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            webcamPanel = new WebcamPanel(webcam, false);
            webcamPanel.setPreferredSize(new Dimension(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT));
            webcamPanel.setFPSDisplayed(false);
            webcamPanel.setImageSizeDisplayed(false);
            webcamPanel.setMirrored(true);

            scanOverlay.setLayout(new BorderLayout());
            scanOverlay.add(webcamPanel, BorderLayout.CENTER);

            webcamPanel.start();

            scanTimer = new Timer(200, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!scanning || webcamPanel == null) return;
                    BufferedImage image = webcam.getImage();
                    if (image == null) return;
                    scanOverlay.setCurrentImage(image);
                    String barcode = BarcodeUtil.decode(image);
                    if (barcode != null && !barcode.isEmpty()) {
                        long now = System.currentTimeMillis();
                        if (!barcode.equals(lastBarcode) || now - lastBarcodeTime > 5000) {
                            lastBarcode = barcode;
                            lastBarcodeTime = now;
                            processBarcode(barcode);
                        }
                    }
                }
            });
            scanTimer.start();
        } catch (Exception e) {
            e.printStackTrace();
            statusScanLabel.setText("Error kamera: " + e.getMessage());
            statusScanLabel.setForeground(Constants.ACCENT_DANGER);
        }
    }

    private void processBarcode(String barcode) {
        scanning = false;
        scanOverlay.setBarcodeDetected(true);
        scanOverlay.repaint();

        PresensiResult result = presensiController.processBarcode(barcode);

        if (result.isSuccess() && result.getEmployee() != null) {
            displayEmployeeInfo(result.getEmployee());
            statusScanLabel.setText(result.getMessage());
            statusScanLabel.setForeground(Constants.ACCENT);
            SoundUtil.beepSuccess();
            scanOverlay.setScanSuccess(true);
        } else if (!result.isSuccess() && result.getEmployee() != null) {
            displayEmployeeInfo(result.getEmployee());
            statusScanLabel.setText(result.getMessage());
            statusScanLabel.setForeground(Constants.ACCENT);
            SoundUtil.beepSuccess();
            scanOverlay.setScanSuccess(true);
        } else {
            statusScanLabel.setText("Barcode Tidak Dikenal");
            statusScanLabel.setForeground(Constants.ACCENT_DANGER);
            SoundUtil.beepError();
            scanOverlay.setScanSuccess(false);
        }
        scanOverlay.repaint();

        Timer delayTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scanning = true;
                scanOverlay.setBarcodeDetected(false);
                scanOverlay.setScanSuccess(false);
                statusScanLabel.setText("Menunggu Scan...");
                statusScanLabel.setForeground(Constants.TEXT_SECONDARY);
                scanOverlay.repaint();
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    public void stopCamera() {
        if (scanTimer != null && scanTimer.isRunning()) scanTimer.stop();
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }

    public void startCamera() {
        if (webcam != null && !webcam.isOpen()) {
            webcam.open();
        }
        if (webcamPanel != null) webcamPanel.start();
        if (scanTimer != null && !scanTimer.isRunning()) scanTimer.start();
        scanning = true;
    }

    private static class ScanOverlayPanel extends JPanel {
        private BufferedImage currentImage;
        private boolean barcodeDetected = false;
        private Boolean scanSuccess = null;

        void setCurrentImage(BufferedImage image) { this.currentImage = image; }
        void setBarcodeDetected(boolean detected) { this.barcodeDetected = detected; }
        void setScanSuccess(Boolean success) { this.scanSuccess = success; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int gw = (int) (w * 0.7);
            int gh = (int) (h * 0.35);
            int gx = (w - gw) / 2;
            int gy = (h - gh) / 2;

            if (barcodeDetected && scanSuccess != null) {
                Color fillColor = scanSuccess ? Constants.SCAN_SUCCESS_COLOR : Constants.SCAN_ERROR_COLOR;
                Color borderColor = scanSuccess ? Constants.SCAN_SUCCESS_BORDER : Constants.SCAN_ERROR_BORDER;
                g2.setColor(fillColor);
                g2.fillRect(gx, gy, gw, gh);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRect(gx, gy, gw, gh);
            } else {
                g2.setColor(Constants.SCAN_GUIDE_COLOR);
                g2.fillRect(gx, gy, gw, gh);
                g2.setColor(Constants.SCAN_GUIDE_BORDER);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRect(gx, gy, gw, gh);

                // Corner markers
                int cornerSize = 20;
                g2.setStroke(new BasicStroke(3f));
                g2.setColor(Constants.SCAN_GUIDE_BORDER);
                // Top-left
                g2.drawLine(gx, gy + cornerSize, gx, gy);
                g2.drawLine(gx, gy, gx + cornerSize, gy);
                // Top-right
                g2.drawLine(gx + gw - cornerSize, gy, gx + gw, gy);
                g2.drawLine(gx + gw, gy, gx + gw, gy + cornerSize);
                // Bottom-left
                g2.drawLine(gx, gy + gh - cornerSize, gx, gy + gh);
                g2.drawLine(gx, gy + gh, gx + cornerSize, gy + gh);
                // Bottom-right
                g2.drawLine(gx + gw - cornerSize, gy + gh, gx + gw, gy + gh);
                g2.drawLine(gx + gw, gy + gh, gx + gw, gy + gh - cornerSize);
            }

            g2.dispose();
        }
    }
}
