package isep.aisafe.app.backoffice.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import static isep.aisafe.app.backoffice.gui.MainWindow.*;
import static isep.aisafe.app.backoffice.gui.UiTheme.*;

abstract class FormPanel extends RoundedPanel {
    private final GridBagConstraints c = new GridBagConstraints();
    private int row = 0;

    FormPanel(final String title) {
        super(18, CARD, BORDER, true);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(26, 30, 26, 30));
        setMaximumSize(new Dimension(980, Integer.MAX_VALUE));
        c.insets = new Insets(8, 4, 8, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;

        final JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(false);
        final JPanel badge = new RoundedPanel(12, BLUE_SOFT, null, false);
        badge.setLayout(new GridBagLayout());
        badge.setPreferredSize(new Dimension(48, 48));
        badge.add(new JLabel(new LineIcon(iconName(title), 24, BLUE)));
        header.add(badge, BorderLayout.WEST);

        final JPanel copy = new JPanel(new GridBagLayout());
        copy.setOpaque(false);
        final GridBagConstraints hc = new GridBagConstraints();
        hc.gridx = 0;
        hc.gridy = 0;
        hc.anchor = GridBagConstraints.WEST;
        final JLabel heading = new JLabel(title);
        heading.setForeground(TEXT);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 21f));
        copy.add(heading, hc);
        hc.gridy++;
        hc.insets = new Insets(4, 0, 0, 0);
        final JLabel subtitle = new JLabel("Use the same flow and validations as the console application.");
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(13f));
        copy.add(subtitle, hc);
        header.add(copy, BorderLayout.CENTER);
        add(header, c);

        c.gridy = row++;
        c.insets = new Insets(6, 4, 18, 4);
        final JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(1, 1));
        divider.setBackground(new Color(226, 232, 240));
        add(divider, c);
        c.gridwidth = 1;
        c.insets = new Insets(8, 4, 8, 4);
    }

    JTextField text(final String label) {
        final JTextField field = new JTextField(28);
        styleField(field);
        addField(label, field);
        return field;
    }

    JPasswordField password(final String label) {
        final JPasswordField field = new JPasswordField(28);
        styleField(field);
        addField(label, field);
        return field;
    }

    JComboBox<String> combo(final String label, final String... values) {
        final JComboBox<String> combo = new JComboBox<>(values);
        combo.setPreferredSize(new Dimension(320, 40));
        combo.putClientProperty("FlatLaf.style", "arc: 10");
        addField(label, combo);
        return combo;
    }

    JSpinner integer(final String label, final int value, final int min, final int max) {
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, 1));
        spinner.setPreferredSize(new Dimension(320, 40));
        addField(label, spinner);
        return spinner;
    }

    JSpinner decimal(final String label, final double value, final double min, final double max) {
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, 1.0));
        spinner.setPreferredSize(new Dimension(320, 40));
        addField(label, spinner);
        return spinner;
    }

    void addField(final String label, final Component component) {
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        c.insets = new Insets(8, 4, 8, 22);
        final JLabel labelComponent = new JLabel(label);
        labelComponent.setForeground(new Color(51, 65, 85));
        labelComponent.setFont(labelComponent.getFont().deriveFont(Font.BOLD, 13f));
        add(labelComponent, c);
        c.gridx = 1;
        c.weightx = 1;
        c.insets = new Insets(8, 4, 8, 4);
        component.setFont(component.getFont().deriveFont(14f));
        add(component, c);
        row++;
    }

    JButton action(final String label, final ThrowingRunnable runnable) {
        final JButton button = primaryButton(label);
        button.setPreferredSize(new Dimension(150, 40));
        button.addActionListener(e -> runSafely(runnable));
        final JButton cancel = secondaryButton("Cancel");
        cancel.setPreferredSize(new Dimension(110, 40));
        cancel.addActionListener(e -> clearInputs(this));
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(16, 4, 12, 4);
        final JPanel rowPanel = new RoundedPanel(14, new Color(248, 250, 252), BORDER, false);
        rowPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rowPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rowPanel.add(cancel);
        rowPanel.add(button);
        add(rowPanel, c);
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        c.insets = new Insets(8, 4, 8, 4);
        return button;
    }

    private void clearInputs(final Component component) {
        if (component instanceof JTextField) {
            ((JTextField) component).setText("");
            return;
        }
        if (component instanceof JPasswordField) {
            ((JPasswordField) component).setText("");
            return;
        }
        if (component instanceof JTextArea) {
            ((JTextArea) component).setText("");
            return;
        }
        if (component instanceof JTable) {
            ((JTable) component).setModel(tableModel());
            return;
        }
        if (component instanceof java.awt.Container) {
            for (Component child : ((java.awt.Container) component).getComponents()) {
                clearInputs(child);
            }
        }
    }

    JScrollPane output(final JTextArea area) {
        area.setEditable(false);
        area.setBackground(new Color(248, 250, 252));
        area.setForeground(TEXT);
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        area.setFont(area.getFont().deriveFont(13f));
        final JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(760, 260));
        final JPanel panel = resultPanel("Output", new LineIcon("grid", 18, BLUE), scroll);
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(18, 4, 8, 4);
        add(panel, c);
        c.gridwidth = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 4, 8, 4);
        return scroll;
    }

    void table(final JTable table) {
        table.setRowHeight(36);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 13f));
        table.setGridColor(new Color(226, 232, 240));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        final JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(820, 320));
        final JPanel panel = resultPanel("Results", new LineIcon("chart", 18, BLUE), scroll);
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(18, 4, 8, 4);
        add(panel, c);
        c.gridwidth = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 4, 8, 4);
    }

    private void styleField(final JTextField field) {
        field.setPreferredSize(new Dimension(360, 42));
        field.putClientProperty("FlatLaf.style", "arc: 10; borderWidth: 1; focusWidth: 1");
    }

    private JPanel resultPanel(final String title, final javax.swing.Icon icon, final Component content) {
        final JPanel panel = new RoundedPanel(16, new Color(248, 250, 252), BORDER, false);
        panel.setLayout(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        final JLabel label = new JLabel(title, icon, JLabel.LEFT);
        label.setIconTextGap(9);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(label, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
}
