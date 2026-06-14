package isep.aisafe.app.backoffice.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import static isep.aisafe.app.backoffice.gui.MainWindow.secondaryButton;
import static isep.aisafe.app.backoffice.gui.UiTheme.*;

final class WeatherDashboardPanel extends JPanel {
    WeatherDashboardPanel(final MainWindow window, final boolean canManageWeather) {
        super(new GridBagLayout());
        setBackground(PAGE);
        setBorder(BorderFactory.createEmptyBorder(30, 34, 30, 34));

        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        final JLabel title = new JLabel("Weather Operations");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        add(title, c);
        c.gridy++;
        final JLabel subtitle = new JLabel("Manage weather data consultation, registration and bulk imports.");
        subtitle.setForeground(MUTED);
        add(subtitle, c);

        c.gridy++;
        c.insets = new Insets(24, 0, 0, 0);
        add(summaryGrid(), c);

        c.gridy++;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        add(lowerGrid(window, canManageWeather), c);
    }

    private JPanel summaryGrid() {
        final JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        final String[][] cards = {
                { "Weather Records", "Ready", "cloudSearch" },
                { "Last Import", "Available", "cloudUpload" },
                { "Registered Areas", "Open", "map" },
                { "Import Status", "Ready", "cloudUpload" },
                { "Data Source", "IPMA", "cloud" },
                { "Validation Status", "Active", "shield" }
        };
        for (int i = 0; i < cards.length; i++) {
            c.gridx = i % 3;
            c.gridy = i / 3;
            c.insets = new Insets(0, 0, 16, c.gridx == 2 ? 0 : 16);
            grid.add(summaryCard(cards[i][0], cards[i][1], cards[i][2]), c);
        }
        return grid;
    }

    private JPanel lowerGrid(final MainWindow window, final boolean canManageWeather) {
        final JPanel lower = new JPanel(new GridBagLayout());
        lower.setOpaque(false);
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = .65;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 16);
        lower.add(activityCard(), c);
        c.gridx++;
        c.weightx = .35;
        c.insets = new Insets(0, 0, 0, 0);
        lower.add(quickActionsCard(window, canManageWeather), c);
        return lower;
    }

    private JPanel summaryCard(final String label, final String value, final String icon) {
        final JPanel card = card();
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 16, 18));
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 12, 0);
        final JPanel badge = new RoundedPanel(12, BLUE_SOFT, null, false);
        badge.setLayout(new GridBagLayout());
        badge.setPreferredSize(new Dimension(42, 42));
        badge.add(new JLabel(new LineIcon(icon, 22, BLUE)));
        card.add(badge, c);
        c.gridy++;
        c.insets = new Insets(0, 0, 6, 0);
        final JLabel top = new JLabel(label);
        top.setForeground(MUTED);
        top.setFont(top.getFont().deriveFont(Font.BOLD, 12f));
        card.add(top, c);
        c.gridy++;
        final JLabel number = new JLabel(value);
        number.setForeground(TEXT);
        number.setFont(number.getFont().deriveFont(Font.BOLD, 25f));
        card.add(number, c);
        return card;
    }

    private JPanel activityCard() {
        final JPanel card = dashboardCard("Weather Activity", "cloudSearch");
        final String[] rows = {
                "Weather data importer available",
                "Last weather file imported",
                "Weather data registered",
                "Consultation ready"
        };
        for (int i = 0; i < rows.length; i++) {
            addRow(card, i + 1, rows[i]);
        }
        return card;
    }

    private JPanel quickActionsCard(final MainWindow window, final boolean canManageWeather) {
        final JPanel card = dashboardCard("Quick Actions", "cloudPlus");
        addQuickAction(card, 1, "Consult Weather Data", window);
        if (canManageWeather) {
            addQuickAction(card, 2, "Register Weather Data", window);
            addQuickAction(card, 3, "Import Bulk Weather Data", window);
        }
        return card;
    }

    private void addRow(final JPanel card, final int rowIndex, final String text) {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = rowIndex;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(7, 0, 7, 0);
        final JLabel row = new JLabel(text, new LineIcon("cloud", 15, BLUE), SwingConstants.LEFT);
        row.setIconTextGap(10);
        row.setForeground(new Color(51, 65, 85));
        card.add(row, c);
    }

    private void addQuickAction(final JPanel card, final int row, final String label, final MainWindow window) {
        final JButton button = secondaryButton(label);
        button.setPreferredSize(new Dimension(220, 36));
        button.addActionListener(e -> window.navigateTo(label));
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(6, 0, 6, 0);
        card.add(button, c);
    }

    private JPanel dashboardCard(final String title, final String icon) {
        final JPanel card = card();
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        final JLabel label = new JLabel(title, new LineIcon(icon, 18, BLUE), SwingConstants.LEFT);
        label.setIconTextGap(10);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        card.add(label, c);
        return card;
    }

    private JPanel card() {
        final JPanel card = new RoundedPanel(18, CARD, BORDER, true);
        card.setLayout(new GridBagLayout());
        return card;
    }
}
