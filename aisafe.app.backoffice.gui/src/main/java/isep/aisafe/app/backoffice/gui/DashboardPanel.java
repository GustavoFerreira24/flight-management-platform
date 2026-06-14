package isep.aisafe.app.backoffice.gui;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static isep.aisafe.app.backoffice.gui.MainWindow.secondaryButton;
import static isep.aisafe.app.backoffice.gui.UiTheme.*;

final class DashboardPanel extends JPanel {
    private final JLabel clock = new JLabel();

    DashboardPanel(final MainWindow window) {
        super(new GridBagLayout());
        setBackground(PAGE);
        setBorder(BorderFactory.createEmptyBorder(30, 34, 30, 34));

        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        add(header(), c);

        final Timer timer = new Timer(1000, e -> updateClock());
        timer.setRepeats(true);
        timer.start();
        updateClock();

        c.gridy++;
        c.insets = new Insets(24, 0, 0, 0);
        add(summaryGrid(), c);

        c.gridy++;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        add(lowerGrid(window), c);
    }

    private JPanel header() {
        final JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        final String username = new AisafeAuthorizationService().authenticatedUsername();
        final JPanel copy = new JPanel(new GridBagLayout());
        copy.setOpaque(false);
        final GridBagConstraints hc = new GridBagConstraints();
        hc.gridx = 0;
        hc.gridy = 0;
        hc.anchor = GridBagConstraints.WEST;
        final JLabel title = new JLabel("Welcome back, " + username + " 👋");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        copy.add(title, hc);
        hc.gridy++;
        final JLabel subtitle = new JLabel("Here's what's happening with AISafe.");
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(14f));
        copy.add(subtitle, hc);
        header.add(copy, BorderLayout.WEST);
        clock.setForeground(MUTED);
        clock.setIcon(new LineIcon("grid", 16, MUTED));
        clock.setIconTextGap(8);
        header.add(clock, BorderLayout.EAST);
        return header;
    }

    private void updateClock() {
        clock.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }

    private JPanel summaryGrid() {
        final JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        final String[][] cards = {
                { "Weather Records", "Ready", "cloud" },
                { "Airports", "Available", "map" },
                { "Aircraft", "Open", "plane" },
                { "Flight Plans", "Ready", "grid" },
                { "Companies", "Available", "user" },
                { "Reports Generated", "—", "chart" }
        };
        for (int i = 0; i < cards.length; i++) {
            c.gridx = i % 3;
            c.gridy = i / 3;
            c.insets = new Insets(0, 0, 16, c.gridx == 2 ? 0 : 16);
            grid.add(summaryCard(cards[i][0], cards[i][1], cards[i][2]), c);
        }
        return grid;
    }

    private JPanel lowerGrid(final MainWindow window) {
        final JPanel lower = new JPanel(new GridBagLayout());
        lower.setOpaque(false);
        final GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0;
        lc.gridy = 0;
        lc.fill = GridBagConstraints.BOTH;
        lc.weightx = 0.55;
        lc.weighty = 1;
        lc.insets = new Insets(0, 0, 0, 16);
        lower.add(activityCard(), lc);
        lc.gridx++;
        lc.weightx = 0.25;
        lower.add(statusCard(), lc);
        lc.gridx++;
        lc.weightx = 0.20;
        lc.insets = new Insets(0, 0, 0, 0);
        lower.add(quickActionsCard(window), lc);
        return lower;
    }

    private JPanel summaryCard(final String label, final String value, final String icon) {
        final JPanel card = createCard();
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 16, 18));
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 12, 0);
        card.add(iconBadge(icon), c);
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
        c.gridy++;
        final JLabel hint = new JLabel("System module active");
        hint.setForeground(new Color(22, 163, 74));
        hint.setFont(hint.getFont().deriveFont(12f));
        card.add(hint, c);
        return card;
    }

    private JPanel iconBadge(final String icon) {
        final JPanel badge = new RoundedPanel(12, BLUE_SOFT, null, false);
        badge.setLayout(new GridBagLayout());
        badge.setPreferredSize(new Dimension(42, 42));
        badge.add(new JLabel(IconFactory.module(icon, 22, BLUE)));
        return badge;
    }

    private JPanel activityCard() {
        final JPanel card = dashboardCard("Recent Activity", "grid");
        final String[] rows = {
                "Weather data importer is available",
                "Flight plan validation ready",
                "Airport and aircraft registries loaded",
                "Reports module ready"
        };
        for (int i = 0; i < rows.length; i++) {
            addInfoRow(card, i + 1, rows[i], "grid", BLUE);
        }
        return card;
    }

    private JPanel statusCard() {
        final JPanel card = dashboardCard("System Status", "shield");
        final String[] rows = { "Database: Connected", "Security: Active", "Layout: Ready", "Audit: Available" };
        for (int i = 0; i < rows.length; i++) {
            addInfoRow(card, i + 1, rows[i], "shield", new Color(22, 163, 74));
        }
        return card;
    }

    private void addInfoRow(final JPanel card, final int rowIndex, final String text, final String icon, final Color color) {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = rowIndex;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(7, 0, 7, 0);
        final JLabel row = new JLabel(text, new LineIcon(icon, 15, color), SwingConstants.LEFT);
        row.setIconTextGap(10);
        row.setForeground(new Color(51, 65, 85));
        card.add(row, c);
    }

    private JPanel quickActionsCard(final MainWindow window) {
        final JPanel card = dashboardCard("Quick Actions", "plane");
        addQuickAction(card, 1, "Import Weather Data", "Weather", window);
        addQuickAction(card, 2, "Register Airport", "Airports", window);
        addQuickAction(card, 3, "Create Flight Plan", "Flight Plans", window);
        addQuickAction(card, 4, "Generate Monthly Report", "Reports", window);
        return card;
    }

    private void addQuickAction(final JPanel card, final int row, final String label, final String module, final MainWindow window) {
        final JButton button = secondaryButton(label);
        button.setPreferredSize(new Dimension(190, 36));
        button.addActionListener(e -> window.navigateTo(module));
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(6, 0, 6, 0);
        card.add(button, c);
    }

    private JPanel dashboardCard(final String title, final String icon) {
        final JPanel card = createCard();
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        final JLabel label = new JLabel(title, IconFactory.module(icon, 18, BLUE), SwingConstants.LEFT);
        label.setIconTextGap(10);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        card.add(label, c);
        return card;
    }

    private static JPanel createCard() {
        final JPanel card = new RoundedPanel(18, CARD, BORDER, true);
        card.setLayout(new GridBagLayout());
        card.setBackground(CARD);
        return card;
    }
}
