package com.slipgaji.view;

import com.slipgaji.util.Constants;
import com.slipgaji.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.YearMonth;

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
        JPanel root = new JPanel(new BorderLayout(0, Constants.SPACING_MD)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Constants.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(Constants.SPACING_LG, Constants.SPACING_LG,
                                       Constants.SPACING_LG, Constants.SPACING_LG));

        JLabel title = new JLabel("Pilih Periode Penggajian");
        title.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 14));
        title.setForeground(Constants.TEXT_PRIMARY);
        root.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, Constants.SPACING_SM);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblBulan = new JLabel("Bulan");
        lblBulan.setFont(Constants.FONT_BODY);
        lblBulan.setForeground(Constants.TEXT_LABEL);
        fields.add(lblBulan, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);

        bulanCombo = new JComboBox<>(BULAN);
        bulanCombo.setFont(Constants.FONT_BODY);
        bulanCombo.setPreferredSize(new Dimension(140, 32));
        int curMonth = LocalDate.now().getMonthValue();
        bulanCombo.setSelectedIndex(curMonth - 1);
        fields.add(bulanCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(Constants.SPACING_SM, 0, 0, Constants.SPACING_SM);

        JLabel lblTahun = new JLabel("Tahun");
        lblTahun.setFont(Constants.FONT_BODY);
        lblTahun.setForeground(Constants.TEXT_LABEL);
        fields.add(lblTahun, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(Constants.SPACING_SM, 0, 0, 0);

        int curYear = LocalDate.now().getYear();
        tahunCombo = new JComboBox<>();
        for (int y = curYear - 1; y <= curYear + 2; y++) {
            tahunCombo.addItem(String.valueOf(y));
        }
        tahunCombo.setSelectedItem(String.valueOf(curYear));
        tahunCombo.setFont(Constants.FONT_BODY);
        tahunCombo.setPreferredSize(new Dimension(140, 32));
        fields.add(tahunCombo, gbc);

        root.add(fields, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, Constants.SPACING_SM, 0));
        btns.setOpaque(false);

        JButton btnBatal = UIHelper.createOutlineButton("Batal");
        btnBatal.addActionListener(e -> {
            result = null;
            dispose();
        });

        JButton btnGenerate = UIHelper.createStyledButton("Generate", Constants.ACCENT_ACTION);
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
        setPreferredSize(new Dimension(360, 220));
        pack();
        setLocationRelativeTo(getOwner());
    }

    public String showDialog() {
        setVisible(true);
        return result;
    }
}