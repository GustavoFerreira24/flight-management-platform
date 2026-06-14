package isep.aisafe.app.backoffice.gui;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
        final JLabel subtitle = new JLabel("Here's what's happening with AISafe backoffice operations.");
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
                { "Airports", "Available", "map" },
                { "Aircraft", "Open", "plane" },
                { "Companies", "Available", "user" },
                { "Users", "Managed", "shield" },
                { "Flight Plans", "Ready", "grid" },
                { "Reports", "Available", "chart" }
        };
        for (int i = 0; i < cards.length; i++) {
            c.gridx = i % 3;
            c.gridy = i / 3;
            c.insets = new Insets(0, 0, 16, c.gridx == 2 ? 0 : 16);
            grid.add(summaryCard(cards[i][0], cards[i][1], cards[i][2], "System module active"), c);
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
        lc.weightx = 0.58;
        lc.weighty = 1;
        lc.insets = new Insets(0, 0, 0, 16);
        lower.add(activityCard(), lc);
        lc.gridx++;
        lc.weightx = 0.24;
        lower.add(statusCard(), lc);
        lc.gridx++;
        lc.weightx = 0.18;
        lc.insets = new Insets(0, 0, 0, 0);
        lower.add(quickActionsCard(window), lc);
        return lower;
    }

    private JPanel activityCard() {
        final JPanel card = dashboardCard("Backoffice Activity", "grid");
        final String[] rows = {
                "Airport and airspace registration available",
                "Aircraft models and engine models ready",
                "Company and collaborator management active",
                "User registration and security controls ready"
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

    private JPanel quickActionsCard(final MainWindow window) {
        final JPanel card = dashboardCard("Quick Actions", "plane");
        addQuickAction(card, 1, "Airports", window);
        addQuickAction(card, 2, "Aircraft", window);
        addQuickAction(card, 3, "Companies", window);
        addQuickAction(card, 4, "Users", window);
        return card;
    }

    private static JPanel summaryCard(final String label, final String value, final String icon, final String hint) {
        final JPanel card = createCard();
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 16, 18));
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 12, 0);
        card.add(iconBadge(icon, BLUE, BLUE_SOFT), c);
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
        final JLabel hintLabel = new JLabel(hint);
        hintLabel.setForeground(new Color(22, 163, 74));
        hintLabel.setFont(hintLabel.getFont().deriveFont(12f));
        card.add(hintLabel, c);
        return card;
    }

    private static JPanel iconBadge(final String icon, final Color color, final Color background) {
        final JPanel badge = new RoundedPanel(12, background, null, false);
        badge.setLayout(new GridBagLayout());
        badge.setPreferredSize(new Dimension(42, 42));
        badge.add(new JLabel(IconFactory.module(icon, 22, color)));
        return badge;
    }

    private static void addInfoRow(final JPanel card, final int rowIndex, final String text, final String icon, final Color color) {
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

    private static void addQuickAction(final JPanel card, final int row, final String label, final MainWindow window) {
        final JButton button = secondaryButton(label);
        button.setPreferredSize(new Dimension(190, 36));
        button.addActionListener(e -> window.navigateTo(label));
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(6, 0, 6, 0);
        card.add(button, c);
    }

    private static JPanel dashboardCard(final String title, final String icon) {
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

final class PilotDashboardPanel extends JPanel {
    private final JLabel clock = new JLabel();

    PilotDashboardPanel(final MainWindow window, final boolean canInsertWeather) {
        super(new GridBagLayout());
        setBackground(PAGE);
        setBorder(BorderFactory.createEmptyBorder(30, 34, 30, 34));

        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        add(header("Pilot Cockpit", "Create, validate and review your flight plans."), c);

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
        add(lowerGrid(window, canInsertWeather), c);
    }

    private JPanel header(final String titleText, final String subtitleText) {
        final JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        final String username = new AisafeAuthorizationService().authenticatedUsername();
        final JPanel copy = new JPanel(new GridBagLayout());
        copy.setOpaque(false);
        final GridBagConstraints hc = new GridBagConstraints();
        hc.gridx = 0;
        hc.gridy = 0;
        hc.anchor = GridBagConstraints.WEST;
        final JLabel title = new JLabel(titleText + ", " + username);
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        copy.add(title, hc);
        hc.gridy++;
        final JLabel subtitle = new JLabel(subtitleText);
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(14f));
        copy.add(subtitle, hc);
        header.add(copy, BorderLayout.WEST);
        clock.setForeground(MUTED);
        clock.setIcon(new LineIcon("plane", 16, MUTED));
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
                { "Draft Plans", "Create", "grid", "Prepare flight plan drafts" },
                { "Validation", "Ready", "check", "Run operational checks" },
                { "Reports", "Review", "chart", "Open generated test reports" },
                { "Weather", "Optional", "cloud", "Attach area weather data" },
                { "Fuel Check", "Required", "shield", "Fuel is validated in test flow" },
                { "Status Flow", "Active", "plane", "Draft to validated process" }
        };
        for (int i = 0; i < cards.length; i++) {
            c.gridx = i % 3;
            c.gridy = i / 3;
            c.insets = new Insets(0, 0, 16, c.gridx == 2 ? 0 : 16);
            grid.add(dashboardSummaryCard(cards[i][0], cards[i][1], cards[i][2], cards[i][3]), c);
        }
        return grid;
    }

    private JPanel lowerGrid(final MainWindow window, final boolean canInsertWeather) {
        final JPanel lower = new JPanel(new GridBagLayout());
        lower.setOpaque(false);
        final GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0;
        lc.gridy = 0;
        lc.fill = GridBagConstraints.BOTH;
        lc.weightx = 0.58;
        lc.weighty = 1;
        lc.insets = new Insets(0, 0, 0, 16);
        lower.add(workflowCard(), lc);
        lc.gridx++;
        lc.weightx = 0.22;
        lower.add(statusCard(), lc);
        lc.gridx++;
        lc.weightx = 0.20;
        lc.insets = new Insets(0, 0, 0, 0);
        lower.add(quickActionsCard(window, canInsertWeather), lc);
        return lower;
    }

    private JPanel workflowCard() {
        final JPanel card = dashboardCard("Pilot Workflow", "plane");
        addInfoRow(card, 1, "Create a new draft flight plan", "grid", BLUE);
        addInfoRow(card, 2, "Insert weather data when required", "cloud", new Color(2, 132, 199));
        addInfoRow(card, 3, "Run validation before operational use", "check", new Color(22, 163, 74));
        addInfoRow(card, 4, "Review validation reports and warnings", "chart", new Color(245, 158, 11));
        return card;
    }

    private JPanel statusCard() {
        final JPanel card = dashboardCard("Validation Focus", "shield");
        addInfoRow(card, 1, "Route status", "check", new Color(22, 163, 74));
        addInfoRow(card, 2, "Aircraft readiness", "plane", BLUE);
        addInfoRow(card, 3, "Pilot certification", "user", new Color(15, 118, 110));
        addInfoRow(card, 4, "Fuel quantity", "shield", new Color(245, 158, 11));
        return card;
    }

    private JPanel quickActionsCard(final MainWindow window, final boolean canInsertWeather) {
        final JPanel card = dashboardCard("Quick Actions", "check");
        addQuickAction(card, 1, "Create Flight Plan", window);
        if (canInsertWeather) {
            addQuickAction(card, 2, "Insert Weather in Flight Plan", window);
            addQuickAction(card, 3, "Test Flight Plan", window);
            addQuickAction(card, 4, "Consult Flight Plan Reports", window);
        } else {
            addQuickAction(card, 2, "Test Flight Plan", window);
            addQuickAction(card, 3, "Consult Flight Plan Reports", window);
        }
        return card;
    }

    private static JPanel dashboardSummaryCard(final String label, final String value, final String icon, final String hint) {
        return DashboardWidgets.summaryCard(label, value, icon, hint);
    }

    private static JPanel dashboardCard(final String title, final String icon) {
        return DashboardWidgets.dashboardCard(title, icon);
    }

    private static void addInfoRow(final JPanel card, final int rowIndex, final String text, final String icon, final Color color) {
        DashboardWidgets.addInfoRow(card, rowIndex, text, icon, color);
    }

    private static void addQuickAction(final JPanel card, final int row, final String label, final MainWindow window) {
        DashboardWidgets.addQuickAction(card, row, label, window);
    }
}

final class AirCompanyDashboardPanel extends JPanel {
    private final JLabel clock = new JLabel();

    AirCompanyDashboardPanel(final MainWindow window) {
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
        final JLabel title = new JLabel("Air Company Operations");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        copy.add(title, hc);
        hc.gridy++;
        final JLabel subtitle = new JLabel("Fleet, pilots, routes and flight planning for " + username + ".");
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(14f));
        copy.add(subtitle, hc);
        header.add(copy, BorderLayout.WEST);
        clock.setForeground(MUTED);
        clock.setIcon(new LineIcon("plane", 16, MUTED));
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
                { "Fleet", "Manage", "plane", "Register and list aircraft" },
                { "Pilots", "Roster", "user", "Assign and deactivate pilots" },
                { "Flight Routes", "Create", "map", "Company routes between airports" },
                { "Flights", "Schedule", "grid", "Create flights from routes" },
                { "Flight Plans", "Draft", "check", "Prepare company operations" },
                { "Operational Status", "Active", "shield", "Fleet rules enforced" }
        };
        for (int i = 0; i < cards.length; i++) {
            c.gridx = i % 3;
            c.gridy = i / 3;
            c.insets = new Insets(0, 0, 16, c.gridx == 2 ? 0 : 16);
            grid.add(DashboardWidgets.summaryCard(cards[i][0], cards[i][1], cards[i][2], cards[i][3]), c);
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
        lc.weightx = 0.58;
        lc.weighty = 1;
        lc.insets = new Insets(0, 0, 0, 16);
        lower.add(operationsCard(), lc);
        lc.gridx++;
        lc.weightx = 0.22;
        lower.add(complianceCard(), lc);
        lc.gridx++;
        lc.weightx = 0.20;
        lc.insets = new Insets(0, 0, 0, 0);
        lower.add(quickActionsCard(window), lc);
        return lower;
    }

    private JPanel operationsCard() {
        final JPanel card = DashboardWidgets.dashboardCard("Company Workflow", "plane");
        DashboardWidgets.addInfoRow(card, 1, "Register aircraft and maintain fleet availability", "plane", BLUE);
        DashboardWidgets.addInfoRow(card, 2, "Keep the pilot roster active and certified", "user", new Color(15, 118, 110));
        DashboardWidgets.addInfoRow(card, 3, "Create routes before scheduling flights", "map", new Color(2, 132, 199));
        DashboardWidgets.addInfoRow(card, 4, "Prepare flights and flight plans for pilots", "check", new Color(22, 163, 74));
        return card;
    }

    private JPanel complianceCard() {
        final JPanel card = DashboardWidgets.dashboardCard("Operational Rules", "shield");
        DashboardWidgets.addInfoRow(card, 1, "Aircraft must use certified engines", "check", new Color(22, 163, 74));
        DashboardWidgets.addInfoRow(card, 2, "Pilots require active assignments", "user", new Color(15, 118, 110));
        DashboardWidgets.addInfoRow(card, 3, "Routes depend on registered airports", "map", BLUE);
        DashboardWidgets.addInfoRow(card, 4, "Decommission only eligible aircraft", "warning", new Color(245, 158, 11));
        return card;
    }

    private JPanel quickActionsCard(final MainWindow window) {
        final JPanel card = DashboardWidgets.dashboardCard("Quick Actions", "grid");
        DashboardWidgets.addQuickAction(card, 1, "Aircraft", window);
        DashboardWidgets.addQuickAction(card, 2, "Pilots", window);
        DashboardWidgets.addQuickAction(card, 3, "Flight Routes", window);
        DashboardWidgets.addQuickAction(card, 4, "Flight Plans", window);
        return card;
    }
}

final class FlightControlDashboardPanel extends JPanel {
    private final JLabel clock = new JLabel();

    FlightControlDashboardPanel(final MainWindow window) {
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
        final JLabel title = new JLabel("Flight Control Dashboard");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        copy.add(title, hc);
        hc.gridy++;
        final JLabel subtitle = new JLabel("Monitor flight plans, weather data and operational reports for " + username + ".");
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(14f));
        copy.add(subtitle, hc);
        header.add(copy, BorderLayout.WEST);
        clock.setForeground(MUTED);
        clock.setIcon(new LineIcon("shield", 16, MUTED));
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
                { "Flight Plans", "Review", "grid", "Consult operational plans" },
                { "Reports", "Generate", "chart", "Monthly and simulation reports" },
                { "Weather Data", "Consult", "cloud", "Search area weather records" },
                { "Safety", "Monitor", "shield", "Review validation outcomes" },
                { "Simulation", "Available", "plane", "Generate simulation reports" },
                { "Operational Flow", "Ready", "check", "Control area workflow active" }
        };
        for (int i = 0; i < cards.length; i++) {
            c.gridx = i % 3;
            c.gridy = i / 3;
            c.insets = new Insets(0, 0, 16, c.gridx == 2 ? 0 : 16);
            grid.add(DashboardWidgets.summaryCard(cards[i][0], cards[i][1], cards[i][2], cards[i][3]), c);
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
        lc.weightx = 0.60;
        lc.weighty = 1;
        lc.insets = new Insets(0, 0, 0, 16);
        lower.add(controlCard(), lc);
        lc.gridx++;
        lc.weightx = 0.22;
        lower.add(weatherCard(), lc);
        lc.gridx++;
        lc.weightx = 0.18;
        lc.insets = new Insets(0, 0, 0, 0);
        lower.add(quickActionsCard(window), lc);
        return lower;
    }

    private JPanel controlCard() {
        final JPanel card = DashboardWidgets.dashboardCard("Control Activity", "shield");
        DashboardWidgets.addInfoRow(card, 1, "Review flight plan information", "grid", BLUE);
        DashboardWidgets.addInfoRow(card, 2, "Consult reports for operational safety", "chart", new Color(79, 70, 229));
        DashboardWidgets.addInfoRow(card, 3, "Use weather data for decision support", "cloud", new Color(2, 132, 199));
        DashboardWidgets.addInfoRow(card, 4, "Generate simulations and monthly reports", "plane", new Color(22, 163, 74));
        return card;
    }

    private JPanel weatherCard() {
        final JPanel card = DashboardWidgets.dashboardCard("Control Checks", "check");
        DashboardWidgets.addInfoRow(card, 1, "Weather records available", "cloud", new Color(2, 132, 199));
        DashboardWidgets.addInfoRow(card, 2, "Reports module active", "chart", BLUE);
        DashboardWidgets.addInfoRow(card, 3, "Flight plan consultation ready", "grid", new Color(22, 163, 74));
        DashboardWidgets.addInfoRow(card, 4, "Safety alerts visible", "warning", new Color(245, 158, 11));
        return card;
    }

    private JPanel quickActionsCard(final MainWindow window) {
        final JPanel card = DashboardWidgets.dashboardCard("Quick Actions", "grid");
        DashboardWidgets.addQuickAction(card, 1, "Flight Plans", window);
        DashboardWidgets.addQuickAction(card, 2, "Reports", window);
        DashboardWidgets.addQuickAction(card, 3, "Consult Weather Data", window);
        return card;
    }
}

final class DashboardWidgets {
    private DashboardWidgets() {
    }

    static JPanel summaryCard(final String label, final String value, final String icon, final String hint) {
        final JPanel card = createCard();
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 16, 18));
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 12, 0);
        card.add(iconBadge(icon, BLUE, BLUE_SOFT), c);
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
        final JLabel hintLabel = new JLabel(hint);
        hintLabel.setForeground(new Color(22, 163, 74));
        hintLabel.setFont(hintLabel.getFont().deriveFont(12f));
        card.add(hintLabel, c);
        return card;
    }

    static JPanel dashboardCard(final String title, final String icon) {
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

    static void addInfoRow(final JPanel card, final int rowIndex, final String text, final String icon, final Color color) {
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

    static void addQuickAction(final JPanel card, final int row, final String label, final MainWindow window) {
        final JButton button = secondaryButton(label);
        button.setPreferredSize(new Dimension(190, 36));
        button.addActionListener(e -> window.navigateTo(label));
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(6, 0, 6, 0);
        card.add(button, c);
    }

    static JPanel iconBadge(final String icon, final Color color, final Color background) {
        final JPanel badge = new RoundedPanel(12, background, null, false);
        badge.setLayout(new GridBagLayout());
        badge.setPreferredSize(new Dimension(42, 42));
        badge.add(new JLabel(IconFactory.module(icon, 22, color)));
        return badge;
    }

    static JPanel createCard() {
        final JPanel card = new RoundedPanel(18, CARD, BORDER, true);
        card.setLayout(new GridBagLayout());
        card.setBackground(CARD);
        return card;
    }
}
