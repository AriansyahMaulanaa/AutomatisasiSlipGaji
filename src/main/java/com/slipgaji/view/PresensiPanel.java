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
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.time.LocalTime;
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
    private JPanel statusBadge;
    private JPanel statusDot;
    private Color badgeBg = Constants.BADGE_BG;
    private Color badgeBorder = Constants.BADGE_BORDER;
    private Color badgeDot = Constants.DOT_ACTIVE;
    private Color badgeTextColor = Constants.BADGE_TEXT;
    private ScanOverlayPanel scanOverlay;
    private boolean scanning = true;

    private String selectedTipe = "Masuk";
    private JPanel togglePanel;

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
        setBorder(new EmptyBorder(Constants.SPACING_MD, Constants.SPACING_LG - 4,
                                   Constants.SPACING_MD, Constants.SPACING_LG - 4));

        JLabel title = new JLabel("Presensi Scan");
        title.setFont(Constants.FONT_TITLE);
        title.setForeground(Constants.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 4, Constants.SPACING_MD, 4));
        add(title, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel leftCol = new JPanel(new BorderLayout(0, Constants.SPACING_SM));
        leftCol.setOpaque(false);

        togglePanel = createToggleControl();
        leftCol.add(togglePanel, BorderLayout.NORTH);

        scanOverlay = new ScanOverlayPanel();
        scanOverlay.setPreferredSize(new Dimension(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT));
        scanOverlay.setMinimumSize(new Dimension(400, 300));
        scanOverlay.setBorder(new EmptyBorder(Constants.SPACING_XS, Constants.SPACING_XS,
                                              Constants.SPACING_XS, Constants.SPACING_XS));
        leftCol.add(scanOverlay, BorderLayout.CENTER);

        statusBadge = createStatusBadge();
        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(statusBadge);
        leftCol.add(badgeWrap, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, Constants.SPACING_MD);
        mainContent.add(leftCol, gbc);

        infoPanel = createInfoCard();
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContent.add(infoPanel, gbc);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createToggleControl() {
        JPanel track = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(Constants.BADGE_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        track.setOpaque(false);
        track.setBorder(new EmptyBorder(4, 4, 4, 4));

        JButton masukBtn = createToggleButton("Absen Masuk", "Masuk");
        JButton pulangBtn = createToggleButton("Absen Pulang", "Pulang");

        track.add(masukBtn);
        track.add(pulangBtn);
        return track;
    }

    private JButton createToggleButton(String text, String tipe) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                boolean active = selectedTipe.equals(tipe);
                if (active) {
                    g2.setColor(Constants.ACCENT_ACTION);
                    g2.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 12));
        btn.setForeground(selectedTipe.equals(tipe) ? Color.WHITE : Constants.TEXT_LABEL);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.addActionListener(e -> {
            selectedTipe = tipe;
            for (Component c : togglePanel.getComponents()) {
                if (c instanceof JButton b) {
                    String t = b.getText().contains("Masuk") ? "Masuk" : "Pulang";
                    b.setForeground(t.equals(selectedTipe) ? Color.WHITE : Constants.TEXT_LABEL);
                    b.repaint();
                }
            }
            togglePanel.repaint();
        });
        return btn;
    }

    private JPanel createStatusBadge() {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(Constants.BADGE_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));
                g2.setColor(Constants.BADGE_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, h, h));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(6, 16, 6, 16));

        statusDot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Constants.DOT_ACTIVE);
                g2.fillOval(0, 0, 8, 8);
                g2.dispose();
            }
        };
        statusDot.setOpaque(false);
        statusDot.setPreferredSize(new Dimension(8, 8));

        statusScanLabel = new JLabel("Menunggu Scan...");
        statusScanLabel.setFont(Constants.FONT_BODY);
        statusScanLabel.setForeground(Constants.BADGE_TEXT);

        badge.add(statusDot);
        badge.add(statusScanLabel);
        return badge;
    }

    private void updateStatusBadge(String text, Color bg, Color border, Color dotColor, Color textColor) {
        statusScanLabel.setText(text);
        statusScanLabel.setForeground(textColor);
        statusBadge.repaint();
        statusDot.repaint();
    }

    private JPanel createInfoCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Double(0, 1, w, h - 1, 10, 10));
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 10, 10));
                g2.setColor(Constants.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 10, 10));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(Constants.SPACING_SM + 12, Constants.SPACING_LG,
                                       Constants.SPACING_SM + 12, Constants.SPACING_LG));
        card.setPreferredSize(new Dimension(280, 0));

        fotoLabel = new JLabel();
        fotoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fotoLabel.setBorder(new EmptyBorder(0, 0, Constants.SPACING_MD, 0));

        JLabel infoTitle = new JLabel("Informasi Karyawan");
        infoTitle.setFont(Constants.FONT_HEADING);
        infoTitle.setForeground(Constants.TEXT_PRIMARY);
        infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoTitle.setBorder(new EmptyBorder(0, 0, Constants.SPACING_SM + 4, 0));

        namaLabel = createInfoField("Nama");
        jabatanLabel = createInfoField("Jabatan");
        statusKaryawanLabel = createInfoField("Status");
        idLabel = createInfoField("ID Karyawan");
        tglLahirLabel = createInfoField("Tanggal Lahir");

        card.add(infoTitle);
        card.add(fotoLabel);
        card.add(namaLabel);
        card.add(Box.createVerticalStrut(Constants.SPACING_SM));
        card.add(jabatanLabel);
        card.add(Box.createVerticalStrut(Constants.SPACING_SM));
        card.add(statusKaryawanLabel);
        card.add(Box.createVerticalStrut(Constants.SPACING_SM));
        card.add(idLabel);
        card.add(Box.createVerticalStrut(Constants.SPACING_SM));
        card.add(tglLahirLabel);

        clearInfo();
        return card;
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
        String valColor = value != null ? "#1E293B" : "#94A3B8";
        String valStyle = value != null ? "" : "font-style:italic;";
        label.setText("<html><b style='color:#475569'>" + fieldName + ":</b> " +
                     "<span style='color:" + valColor + ";" + valStyle + "'>" + display + "</span></html>");
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
        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Constants.ACCENT_ACTION);
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
                statusScanLabel.setForeground(Constants.DANGER);
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
            statusScanLabel.setForeground(Constants.DANGER);
        }
    }

    private void processBarcode(String barcode) {
        scanning = false;
        scanOverlay.setBarcodeDetected(true);
        scanOverlay.repaint();

        PresensiResult result = presensiController.processBarcodeWithType(barcode, selectedTipe);

        if (result.isSuccess() && result.getEmployee() != null) {
            displayEmployeeInfo(result.getEmployee());
            String jamStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            updateStatusBadge(
                result.getMessage() + " \u2014 " + jamStr,
                Constants.SUCCESS_BG,
                Constants.SUCCESS,
                Constants.SUCCESS,
                Constants.SUCCESS
            );
            SoundUtil.beepSuccess();
            scanOverlay.setScanSuccess(true);
        } else if (!result.isSuccess() && result.getEmployee() != null) {
            displayEmployeeInfo(result.getEmployee());
            updateStatusBadge(
                result.getMessage(),
                Constants.FAILED_BG,
                Constants.DANGER,
                Constants.DANGER,
                Constants.DANGER
            );
            SoundUtil.beepError();
            scanOverlay.setScanSuccess(false);
        } else {
            updateStatusBadge(
                result.getMessage(),
                Constants.FAILED_BG,
                Constants.DANGER,
                Constants.DANGER,
                Constants.DANGER
            );
            SoundUtil.beepError();
            scanOverlay.setScanSuccess(false);
        }
        scanOverlay.repaint();

        Timer delayTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scanning = true;
                scanOverlay.setBarcodeDetected(false);
                scanOverlay.setScanSuccess(false);
                statusBadge.repaint();
                statusDot.repaint();
                statusScanLabel.setText("Menunggu Scan...");
                statusScanLabel.setForeground(Constants.BADGE_TEXT);
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

            int gw = (int) (w * 0.65);
            int gh = (int) (h * 0.30);
            int gx = (w - gw) / 2;
            int gy = (h - gh) / 2;

            if (barcodeDetected && scanSuccess != null) {
                Color fillColor = scanSuccess ? Constants.SCAN_SUCCESS_COLOR : Constants.SCAN_ERROR_COLOR;
                Color borderColor = scanSuccess ? Constants.SCAN_SUCCESS_BORDER : Constants.SCAN_ERROR_BORDER;
                g2.setColor(fillColor);
                g2.fillRoundRect(gx, gy, gw, gh, 8, 8);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(gx, gy, gw, gh, 8, 8);
            } else {
                g2.setColor(new Color(255, 255, 255, 150));
                float[] dash = {10f, 6f};
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, dash, 0f));
                g2.drawRoundRect(gx, gy, gw, gh, 8, 8);
            }

            g2.setColor(Constants.ACCENT_BLUE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);

            g2.dispose();
        }
    }
}