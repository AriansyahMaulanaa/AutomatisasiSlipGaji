package com.slipgaji.view;

import com.slipgaji.ui.components.AppButton;
import com.slipgaji.ui.theme.UIColors;
import com.slipgaji.ui.theme.UIFonts;
import com.slipgaji.ui.theme.UIMetrics;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;

/**
 * PeriodePickerDialog — pilih bulan & tahun untuk generate slip.
 */
public class PeriodePickerDialog extends JDialog {

    private JComboBox<String> bulanCombo;
    private JComboBox<String> tahunCombo;
    private String result = null;

    private static final String[] BULAN = {
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };

    public PeriodePickerDialog(Window owner) {
        super(owner, "Pilih Periode Penggajian", ModalityType.APPLICATION_MODAL);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, UIMetrics.SPACE_16)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIColors.NEUTRAL_0);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                        UIMetrics.RADIUS_MODAL * 2, UIMetrics.RADIUS_MODAL * 2));
                g2.setColor(UIColors.NEUTRAL_200);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1,
                        UIMetrics.RADIUS_MODAL * 2, UIMetrics.RADIUS_MODAL * 2));
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(UIMetrics.SPACE_24, UIMetrics.SPACE_24,
                                        UIMetrics.SPACE_20, UIMetrics.SPACE_24));

        JLabel title = new JLabel("Pilih Periode Penggajian");
        title.setFont(UIFonts.H2);
        title.setForeground(UIColors.NEUTRAL_800);
        root.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 0, 4, UIMetrics.SPACE_8);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblBulan = new JLabel("Bulan");
        lblBulan.setFont(UIFonts.LABEL);
        lblBulan.setForeground(UIColors.NEUTRAL_600);
        lblBulan.setPreferredSize(new Dimension(70, 20));
        fields.add(lblBulan, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(4, 0, 4, 0);
        bulanCombo = new JComboBox<>(BULAN);
        bulanCombo.setFont(UIFonts.BODY);
        bulanCombo.setBackground(UIColors.NEUTRAL_0);
        bulanCombo.setPreferredSize(new Dimension(160, UIMetrics.INPUT_HEIGHT));
        int curMonth = LocalDate.now().getMonthValue();
        bulanCombo.setSelectedIndex(curMonth - 1);
        fields.add(bulanCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.insets = new Insets(UIMetrics.SPACE_8, 0, 4, UIMetrics.SPACE_8);
        JLabel lblTahun = new JLabel("Tahun");
        lblTahun.setFont(UIFonts.LABEL);
        lblTahun.setForeground(UIColors.NEUTRAL_600);
        lblTahun.setPreferredSize(new Dimension(70, 20));
        fields.add(lblTahun, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(UIMetrics.SPACE_8, 0, 4, 0);
        int curYear = LocalDate.now().getYear();
        tahunCombo = new JComboBox<>();
        for (int y = curYear - 1; y <= curYear + 2; y++) tahunCombo.addItem(String.valueOf(y));
        tahunCombo.setSelectedItem(String.valueOf(curYear));
        tahunCombo.setFont(UIFonts.BODY);
        tahunCombo.setBackground(UIColors.NEUTRAL_0);
        tahunCombo.setPreferredSize(new Dimension(160, UIMetrics.INPUT_HEIGHT));
        fields.add(tahunCombo, gbc);
        root.add(fields, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIMetrics.SPACE_8, 0));
        btns.setOpaque(false);

        JButton btnBatal = AppButton.secondary("Batal");
        btnBatal.addActionListener(e -> { result = null; dispose(); });

        JButton btnGenerate = AppButton.primary("Generate");
        btnGenerate.addActionListener(e -> {
            int month = bulanCombo.getSelectedIndex() + 1;
            String year = (String) tahunCombo.getSelectedItem();
            result = year + "-" + String.format("%02d", month);
            dispose();
        });

        btns.add(btnBatal);
        btns.add(btnGenerate);
        root.add(btns, BorderLayout.SOUTH);

        setContentPane(root);
        setUndecorated(true);
        setPreferredSize(new Dimension(380, 240));
        pack();
        setLocationRelativeTo(getOwner());
    }

    public String showDialog() {
        setVisible(true);
        return result;
    }
}
