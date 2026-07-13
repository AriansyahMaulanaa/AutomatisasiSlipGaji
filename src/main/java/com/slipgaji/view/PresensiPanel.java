package com.slipgaji.view;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.slipgaji.controller.PresensiController;
import com.slipgaji.controller.PresensiController.PresensiResult;
import com.slipgaji.model.Employee;
import com.slipgaji.ui.components.AppCard;
import com.slipgaji.ui.components.SegmentedControl;
import com.slipgaji.ui.components.StatusBadge;
import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;
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

/**
 * PresensiPanel — scan barcode kartu karyawan.
 *
 * <p>Redesign:
 * <ul>
 *   <li>Viewfinder pakai 4 corner brackets (bukan border penuh).</li>
 *   <li>Toggle Masuk/Pulang pakai {@link SegmentedControl}.</li>
 *   <li>Info karyawan dibungkus {@link AppCard} dengan border+radius.</li>
 *   <li>Status pill pakai {@link StatusBadge} dengan tone Info/Success/Danger.</li>
 * </ul>
 */
public class PresensiPanel extends JPanel {

    private final PresensiController presensiController = new PresensiController();
    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private Timer scanTimer;

    private JLabel fotoLabel;
    private JLabel namaLabel, jabatanLabel, statusKaryawanLabel, idLabel, tglLahirLabel;
    private StatusBadge statusBadge;

    private ScanOverlayPanel scanOverlay;
    private boolean scanning = true;

    private String selectedTipe = "Masuk";
    private SegmentedControl toggle;

    private final Map<JLabel, String> fieldNames = new HashMap<>();
    private String lastBarcode = "";
    private long lastBarcodeTime = 0;

    public PresensiPanel() {
        initUI();
        initCamera();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, UIMetrics.SPACE_16));
        setBackground(UIColors.NEUTRAL_50);
        setBorder(new EmptyBorder(UIMetrics.SPACE_24, UIMetrics.SPACE_24,
                                   UIMetrics.SPACE_24, UIMetrics.SPACE_24));

        JLabel title = new JLabel("Presensi Scan");
        title.setFont(UIFonts.H1);
        title.setForeground(UIColors.NEUTRAL_800);
        title.setBorder(new EmptyBorder(0, 0, UIMetrics.SPACE_8, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();

        // ============ LEFT: Toggle + Camera + StatusBadge ============
        JPanel leftCol = new JPanel(new BorderLayout(0, UIMetrics.SPACE_12));
        leftCol.setOpaque(false);

        // Segmented toggle wrapped in card-like container for elevation feel
        JPanel toggleWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        toggleWrap.setOpaque(false);
        toggle = new SegmentedControl();
        toggle.addSegment("Absen Masuk", "Masuk");
        toggle.addSegment("Absen Pulang", "Pulang");
        toggle.setSelected("Masuk");
        toggle.onChange(key -> selectedTipe = key);
        toggleWrap.add(toggle);
        leftCol.add(toggleWrap, BorderLayout.NORTH);

        scanOverlay = new ScanOverlayPanel();
        scanOverlay.setPreferredSize(new Dimension(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT));
        scanOverlay.setMinimumSize(new Dimension(400, 300));
        leftCol.add(scanOverlay, BorderLayout.CENTER);

        statusBadge = StatusBadge.infoWithDot("Menunggu Scan...");
        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(statusBadge);
        leftCol.add(badgeWrap, BorderLayout.SOUTH);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, UIMetrics.SPACE_16);
        mainContent.add(leftCol, gbc);

        // ============ RIGHT: Info card ============
        gbc.gridx = 1; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContent.add(createInfoCard(), gbc);

        add(mainContent, BorderLayout.CENTER);
    }

    // ============================================================
    // Info card (right column)
    // ============================================================
    private AppCard createInfoCard() {
        AppCard card = new AppCard("Informasi Karyawan");
        card.setPreferredSize(new Dimension(300, 0));

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        fotoLabel = new JLabel();
        fotoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fotoLabel.setBorder(new EmptyBorder(0, 0, UIMetrics.SPACE_16, 0));

        namaLabel = createInfoField("Nama");
        jabatanLabel = createInfoField("Jabatan");
        statusKaryawanLabel = createInfoField("Status");
        idLabel = createInfoField("ID Karyawan");
        tglLahirLabel = createInfoField("Tanggal Lahir");

        body.add(fotoLabel);
        body.add(namaLabel);
        body.add(Box.createVerticalStrut(UIMetrics.SPACE_8));
        body.add(jabatanLabel);
        body.add(Box.createVerticalStrut(UIMetrics.SPACE_8));
        body.add(statusKaryawanLabel);
        body.add(Box.createVerticalStrut(UIMetrics.SPACE_8));
        body.add(idLabel);
        body.add(Box.createVerticalStrut(UIMetrics.SPACE_8));
        body.add(tglLahirLabel);

        card.addBody(body);
        clearInfo();
        return card;
    }

    private JLabel createInfoField(String label) {
        JLabel lbl = new JLabel();
        lbl.setFont(UIFonts.BODY);
        lbl.setForeground(UIColors.NEUTRAL_800);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldNames.put(lbl, label);
        setInfoField(lbl, null);
        return lbl;
    }

    private void setInfoField(JLabel label, String value) {
        String fieldName = fieldNames.get(label);
        if (fieldName == null) return;
        String display = (value != null && !value.isEmpty()) ? value : "—";
        String labelColor = colorHex(UIColors.NEUTRAL_600);
        String valColor = value != null ? colorHex(UIColors.NEUTRAL_800) : colorHex(UIColors.NEUTRAL_400);
        label.setText("<html><span style='color:" + labelColor + ";font-size:11px'>" + fieldName + "</span>"
                    + "<br><span style='color:" + valColor + ";font-size:13px'><b>" + display + "</b></span></html>");
    }

    private static String colorHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private void clearInfo() {
        fotoLabel.setIcon(createAvatarPlaceholder(96));
        for (JLabel lbl : fieldNames.keySet()) setInfoField(lbl, null);
    }

    private void displayEmployeeInfo(Employee emp) {
        if (emp == null) { clearInfo(); return; }
        setInfoField(namaLabel, emp.getName());
        setInfoField(jabatanLabel, emp.getPosition());
        setInfoField(statusKaryawanLabel, emp.getStatus());
        setInfoField(idLabel, emp.getEmployeeId());
        setInfoField(tglLahirLabel, emp.getBirthDate() != null
                ? emp.getBirthDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) : "—");

        if (emp.getPhoto() != null && !emp.getPhoto().isEmpty()) {
            fotoLabel.setIcon(UIHelper.createRoundedImageFromPath(emp.getPhoto(), 96));
        } else {
            String initial = emp.getName() != null && !emp.getName().isEmpty()
                    ? String.valueOf(emp.getName().charAt(0)).toUpperCase() : "?";
            fotoLabel.setIcon(createInitialIcon(initial, 96));
        }
        fotoLabel.repaint();
    }

    /** Avatar placeholder — soft primary bg dengan silhouette line-style. */
    private ImageIcon createAvatarPlaceholder(int size) {
        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UIColors.PRIMARY_50);
        g2.fillOval(0, 0, size, size);
        g2.setColor(UIColors.PRIMARY_500);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = size / 2, cy = size / 2 - size / 10;
        int hr = size / 7;
        g2.drawOval(cx - hr, cy - hr, hr * 2, hr * 2);
        int shoulderW = size / 3;
        int bodyTop = cy + hr + size / 20;
        g2.drawArc(cx - shoulderW, bodyTop, shoulderW * 2, shoulderW * 2, 0, 180);
        g2.dispose();
        return new ImageIcon(bi);
    }

    private ImageIcon createInitialIcon(String initial, int size) {
        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UIColors.PRIMARY_500);
        g2.fillOval(0, 0, size, size);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(UIFonts.FAMILY, Font.BOLD, size / 2));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (size - fm.stringWidth(initial)) / 2;
        int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(initial, tx, ty);
        g2.dispose();
        return new ImageIcon(bi);
    }

    // ============================================================
    // Camera
    // ============================================================
    private void initCamera() {
        try {
            webcam = Webcam.getDefault();
            if (webcam == null) {
                statusBadge.update("Kamera tidak terdeteksi", StatusBadge.Tone.DANGER);
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
                @Override public void actionPerformed(ActionEvent e) {
                    if (!scanning || webcamPanel == null) return;
                    BufferedImage image = webcam.getImage();
                    if (image == null) return;
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
            statusBadge.update("Error kamera: " + e.getMessage(), StatusBadge.Tone.DANGER);
        }
    }

    private void processBarcode(String barcode) {
        scanning = false;
        scanOverlay.setState(ScanOverlayPanel.State.PROCESSING);
        scanOverlay.repaint();

        PresensiResult result = presensiController.processBarcodeWithType(barcode, selectedTipe);

        if (result.isSuccess() && result.getEmployee() != null) {
            displayEmployeeInfo(result.getEmployee());
            String jamStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            statusBadge.update(result.getMessage() + " — " + jamStr, StatusBadge.Tone.SUCCESS);
            SoundUtil.beepSuccess();
            scanOverlay.setState(ScanOverlayPanel.State.SUCCESS);
        } else if (!result.isSuccess() && result.getEmployee() != null) {
            displayEmployeeInfo(result.getEmployee());
            statusBadge.update(result.getMessage(), StatusBadge.Tone.DANGER);
            SoundUtil.beepError();
            scanOverlay.setState(ScanOverlayPanel.State.ERROR);
        } else {
            statusBadge.update(result.getMessage(), StatusBadge.Tone.DANGER);
            SoundUtil.beepError();
            scanOverlay.setState(ScanOverlayPanel.State.ERROR);
        }
        scanOverlay.repaint();

        Timer delayTimer = new Timer(3000, new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                scanning = true;
                scanOverlay.setState(ScanOverlayPanel.State.IDLE);
                scanOverlay.repaint();
                statusBadge.update("Menunggu Scan...", StatusBadge.Tone.INFO);
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    public void stopCamera() {
        if (scanTimer != null && scanTimer.isRunning()) scanTimer.stop();
        if (webcam != null && webcam.isOpen()) webcam.close();
    }

    public void startCamera() {
        if (webcam != null && !webcam.isOpen()) webcam.open();
        if (webcamPanel != null) webcamPanel.start();
        if (scanTimer != null && !scanTimer.isRunning()) scanTimer.start();
        scanning = true;
    }

    // ============================================================
    // Overlay with corner brackets + edge outline
    // ============================================================
    private static class ScanOverlayPanel extends JPanel {
        enum State { IDLE, PROCESSING, SUCCESS, ERROR }
        private State state = State.IDLE;

        void setState(State s) { this.state = s; }

        ScanOverlayPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Outer subtle rounded frame
            g2.setColor(UIColors.NEUTRAL_200);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 12, 12));

            // Guide box — center 65% width, 35% height
            int gw = (int) (w * 0.65);
            int gh = (int) (h * 0.35);
            int gx = (w - gw) / 2;
            int gy = (h - gh) / 2;

            Color bracketColor;
            switch (state) {
                case SUCCESS -> bracketColor = UIColors.SUCCESS_FG;
                case ERROR   -> bracketColor = UIColors.DANGER_FG;
                case PROCESSING -> bracketColor = UIColors.PRIMARY_600;
                default -> bracketColor = UIColors.PRIMARY_500;
            }

            // Semi-transparent tint bg saat sukses/error/processing
            if (state != State.IDLE) {
                Color tint = new Color(bracketColor.getRed(), bracketColor.getGreen(),
                                       bracketColor.getBlue(), 30);
                g2.setColor(tint);
                g2.fill(new RoundRectangle2D.Double(gx, gy, gw, gh, 12, 12));
            }

            // Corner brackets
            drawCorners(g2, gx, gy, gw, gh, bracketColor);

            g2.dispose();
        }

        private void drawCorners(Graphics2D g2, int x, int y, int w, int h, Color color) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int len = Math.min(28, Math.min(w, h) / 4);
            int r = 8; // radius corner
            // top-left
            g2.drawArc(x, y, r * 2, r * 2, 90, 90);
            g2.drawLine(x + r, y, x + len, y);
            g2.drawLine(x, y + r, x, y + len);
            // top-right
            g2.drawArc(x + w - r * 2, y, r * 2, r * 2, 0, 90);
            g2.drawLine(x + w - len, y, x + w - r, y);
            g2.drawLine(x + w, y + r, x + w, y + len);
            // bottom-left
            g2.drawArc(x, y + h - r * 2, r * 2, r * 2, 180, 90);
            g2.drawLine(x, y + h - len, x, y + h - r);
            g2.drawLine(x + r, y + h, x + len, y + h);
            // bottom-right
            g2.drawArc(x + w - r * 2, y + h - r * 2, r * 2, r * 2, 270, 90);
            g2.drawLine(x + w, y + h - len, x + w, y + h - r);
            g2.drawLine(x + w - len, y + h, x + w - r, y + h);
        }
    }
}
