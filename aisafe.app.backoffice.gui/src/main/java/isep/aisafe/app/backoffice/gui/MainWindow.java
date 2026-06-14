package isep.aisafe.app.backoffice.gui;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.application.aircompany.AirCompanyConfigurationController;
import isep.aisafe.application.aircontrolarea.RegisterAirControlAreaController;
import isep.aisafe.application.aircraft.AircraftFleetEntry;
import isep.aisafe.application.aircraft.AircraftFleetFilter;
import isep.aisafe.application.aircraft.DecommissionAircraftController;
import isep.aisafe.application.aircraft.ListAircraftFleetController;
import isep.aisafe.application.aircraft.RegisterAircraftController;
import isep.aisafe.application.aircraftmodel.ManageAircraftModelController;
import isep.aisafe.application.airport.RegisterAirportController;
import isep.aisafe.application.customercollaborator.RegisterCustomerCollaboratorController;
import isep.aisafe.application.enginemodel.RegisterEngineModelController;
import isep.aisafe.application.flightmanagement.ConsultFlightPlanTestReportsController;
import isep.aisafe.application.flightmanagement.CreateFlightController;
import isep.aisafe.application.flightmanagement.CreateFlightPlanController;
import isep.aisafe.application.flightmanagement.InsertWeatherDataInFlightPlanController;
import isep.aisafe.application.flightmanagement.TestFlightPlanController;
import isep.aisafe.application.flightmanagement.TestFlightPlanResult;
import isep.aisafe.application.flightroute.RegisterFlightRouteController;
import isep.aisafe.application.listcustomercollaborators.ListCustomerCollaboratorsController;
import isep.aisafe.application.pilot.AddPilotController;
import isep.aisafe.application.pilot.DeactivatePilotController;
import isep.aisafe.application.pilot.ListPilotRosterController;
import isep.aisafe.application.pilot.RegisterPilotController;
import isep.aisafe.application.report.GenerateMonthlyReportController;
import isep.aisafe.application.report.GenerateSimulationReportController;
import isep.aisafe.application.usermanagement.RegisterBackofficeUserController;
import isep.aisafe.application.weather.BulkWeatherImportError;
import isep.aisafe.application.weather.BulkWeatherImportResult;
import isep.aisafe.application.weather.ConsultWeatherDataController;
import isep.aisafe.application.weather.ImportBulkWeatherDataController;
import isep.aisafe.application.weather.RegisterWeatherDataController;
import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanTestReport;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.authz.AuthenticationCredentialHandler;
import isep.aisafe.pilot.domain.Pilot;
import isep.aisafe.report.domain.SimulationFlightResult;
import isep.aisafe.report.domain.SimulationSafetyViolation;
import isep.aisafe.usermanagement.domain.AisafeRoles;
import isep.aisafe.weatherdata.domain.WeatherData;
import isep.aisafe.weatherdata.domain.WeatherMeasurement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.Icon;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.function.Supplier;

public final class MainWindow extends JFrame {

    private static final Color NAVY = new Color(9, 25, 49);
    private static final Color NAVY_2 = new Color(15, 39, 73);
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color BLUE_SOFT = new Color(219, 234, 254);
    private static final Color PAGE = new Color(244, 247, 251);
    private static final Color CARD = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT = new Color(15, 23, 42);
    private static final Color MUTED = new Color(100, 116, 139);

    private final JPanel sidebar = new SidebarPanel();
    private final JPanel cards = new JPanel(new CardLayout());
    private final JPanel content = new JPanel(new BorderLayout());
    private final NotificationBanner notificationBanner = new NotificationBanner();
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();

    public MainWindow() {
        super("AISafe Back Office");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1480, 860));
        setPreferredSize(new Dimension(1480, 860));
        setIconImage(IconFactory.appImage());
        getRootPane().putClientProperty("JRootPane.titleBarBackground", NAVY);
        getRootPane().putClientProperty("JRootPane.titleBarForeground", Color.WHITE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(PAGE);
        cards.setBackground(PAGE);
        sidebar.setPreferredSize(new Dimension(248, 760));

        notificationBanner.setVisible(false);

        content.setBackground(PAGE);
        content.add(notificationBanner, BorderLayout.NORTH);
        content.add(cards, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        showLogin();
    }

    private void showLogin() {
        if (sidebar.getParent() != null) {
            remove(sidebar);
        }
        sidebar.removeAll();
        sidebar.setBackground(NAVY);
        sidebar.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));
        notificationBanner.hideMessage();
        cards.removeAll();
        cards.add(new LoginPanel(() -> {
            buildMenu();
            setActive("Dashboard");
        }), "login");
        showCard("login");
        revalidate();
        repaint();
    }

    private void buildMenu() {
        if (sidebar.getParent() == null) {
            add(sidebar, BorderLayout.WEST);
        }
        sidebar.removeAll();
        notificationBanner.hideMessage();
        navButtons.clear();
        cards.removeAll();
        sidebar.setLayout(new BorderLayout(0, 0));
        sidebar.setBackground(NAVY);
        sidebar.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));

        final JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        final AisafeAuthorizationService authz = new AisafeAuthorizationService();
        final JLabel brand = new JLabel("AISafe");
        brand.setIcon(IconFactory.brand(30, Color.WHITE));
        brand.setIconTextGap(10);
        brand.setForeground(Color.WHITE);
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 22f));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(brand);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        final JLabel module = new JLabel(profileSubtitle(authz));
        module.setForeground(isWeatherProfile(authz) ? new Color(96, 165, 250) : new Color(148, 163, 184));
        module.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(module);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 26)));
        sidebar.add(headerPanel, BorderLayout.NORTH);

        final JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        addModule(navPanel, "Dashboard", dashboardPanel(authz));
        addProfileNavigation(navPanel, authz);
        sidebar.add(navPanel, BorderLayout.CENTER);

        final JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        addModule(bottomPanel, "Settings", settingsPanel(), 8);
        final JPanel profile = new RoundedPanel(14, new Color(15, 39, 73), new Color(30, 64, 112), false);
        profile.setLayout(new BorderLayout(10, 0));
        profile.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        profile.setAlignmentX(Component.LEFT_ALIGNMENT);
        profile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        final JLabel avatar = new JLabel(IconFactory.brand(26, new Color(147, 197, 253)));
        profile.add(avatar, BorderLayout.WEST);
        final JLabel profileText = new JLabel("<html><b>" + new AisafeAuthorizationService().authenticatedUsername() + "</b><br><span style='color:#94A3B8'>AISafe session</span></html>");
        profileText.setForeground(Color.WHITE);
        profile.add(profileText, BorderLayout.CENTER);
        bottomPanel.add(profile);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        final JButton logout = new JButton("Logout");
        styleNavButton(logout, false);
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        logout.addActionListener(e -> showLogin());
        bottomPanel.add(logout);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);
        setActive("Dashboard");
        revalidate();
        repaint();
    }

    private GridBagConstraints fullCell() {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        return c;
    }

    private boolean hasAny(final AisafeAuthorizationService authz, final Role... roles) {
        try {
            return authz.authenticatedUserHasAnyOf(roles);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private JPanel dashboardPanel(final AisafeAuthorizationService authz) {
        if (isWeatherProfile(authz)) {
            return new WeatherDashboardPanel(this, hasAny(authz, AisafePermissions.WEATHER_DATA_MANAGEMENT));
        }

        if (hasAny(authz, AisafeRoles.ADMIN) || hasAny(authz, AisafeRoles.BACKOFFICE_OPERATOR)) {
            return new DashboardPanel(this);
        }

        if (hasAny(authz, AisafePermissions.AIR_COMPANY_CONFIGURATION)) {
            return new AirCompanyDashboardPanel(this);
        }

        if (hasAny(authz, AisafePermissions.FLIGHT_CONTROL)) {
            return new FlightControlDashboardPanel(this);
        }

        if (hasAny(authz, AisafePermissions.FLIGHT_PLAN_MANAGEMENT)) {
            return new PilotDashboardPanel(this, hasAny(authz, AisafePermissions.FLIGHT_PLAN_WEATHER_DATA_INSERTION));
        }

        return new DashboardPanel(this);
    }

    private boolean isWeatherProfile(final AisafeAuthorizationService authz) {
        return (hasAny(authz, AisafePermissions.WEATHER_DATA_MANAGEMENT) || hasAny(authz, AisafePermissions.WEATHER_DATA_CONSULTATION))
                && !hasAny(authz, AisafeRoles.ADMIN)
                && !hasAny(authz, AisafeRoles.BACKOFFICE_OPERATOR)
                && !hasAny(authz, AisafePermissions.AIR_COMPANY_CONFIGURATION)
                && !hasAny(authz, AisafePermissions.FLIGHT_PLAN_MANAGEMENT)
                && !hasAny(authz, AisafePermissions.FLIGHT_CONTROL);
    }

    private String profileSubtitle(final AisafeAuthorizationService authz) {
        if (isWeatherProfile(authz)) return "Weather Person";
        if (hasAny(authz, AisafeRoles.BACKOFFICE_OPERATOR)) return "Backoffice Operator";
        if (hasAny(authz, AisafeRoles.ADMIN)) return "Admin";
        if (hasAny(authz, AisafePermissions.FLIGHT_CONTROL)) return "Flight Control";
        if (hasAny(authz, AisafePermissions.FLIGHT_PLAN_MANAGEMENT)) return "Pilot";
        if (hasAny(authz, AisafePermissions.AIR_COMPANY_CONFIGURATION)) return "Air Company";
        return "Backoffice";
    }

    private void addProfileNavigation(final JPanel navPanel, final AisafeAuthorizationService authz) {
        if (hasAny(authz, AisafeRoles.ADMIN)) {
            addModule(navPanel, "Users", usersPanel(authz));
            return;
        }
        if (hasAny(authz, AisafeRoles.BACKOFFICE_OPERATOR)) {
            addModule(navPanel, "Airports", airportsPanel());
            addModule(navPanel, "Aircraft", aircraftPanel(authz));
            addModule(navPanel, "Companies", companiesPanel(authz));
            addModule(navPanel, "Users", usersPanel(authz));
            return;
        }
        if (hasAny(authz, AisafePermissions.AIR_COMPANY_CONFIGURATION)) {
            addModule(navPanel, "Aircraft", aircraftPanel(authz));
            addModule(navPanel, "Pilots", pilotsPanel(authz));
            addModule(navPanel, "Flight Routes", flightRoutesPanel(authz));
            addModule(navPanel, "Flight Plans", flightPlansPanel(authz));
            return;
        }
        if (hasAny(authz, AisafePermissions.FLIGHT_CONTROL)) {
            addModule(navPanel, "Flight Plans", flightPlansPanel(authz));
            addModule(navPanel, "Reports", reportsPanel());
            addConsultWeatherData(navPanel);
            return;
        }
        if (hasAny(authz, AisafePermissions.FLIGHT_PLAN_MANAGEMENT)) {
            addPilotFlightPlanUseCases(navPanel, authz);
            return;
        }
        if (hasAny(authz, AisafePermissions.WEATHER_DATA_MANAGEMENT)) {
            addWeatherManagementUseCases(navPanel);
            return;
        }
        if (hasAny(authz, AisafePermissions.WEATHER_DATA_CONSULTATION)) {
            addConsultWeatherData(navPanel);
        }
    }

    private void addWeatherManagementUseCases(final JPanel navPanel) {
        addConsultWeatherData(navPanel);
        addModule(navPanel, "Register Weather Data", directUseCasePage("Register Weather Data", "Add weather measurements for an air control area.", new RegisterWeatherDataPanel()));
        addModule(navPanel, "Import Bulk Weather Data", directUseCasePage("Import Bulk Weather Data", "Import weather data from CSV or XLSX files.", new ImportBulkWeatherDataPanel()));
    }

    private void addPilotFlightPlanUseCases(final JPanel navPanel, final AisafeAuthorizationService authz) {
        addModule(navPanel, "Create Flight Plan", directUseCasePage("Create Flight Plan", "Create a flight plan for an assigned aircraft and pilot.", new CreateFlightPlanPanel()));
        if (hasAny(authz, AisafePermissions.FLIGHT_PLAN_WEATHER_DATA_INSERTION)) {
            addModule(navPanel, "Insert Weather in Flight Plan", directUseCasePage("Insert Weather in Flight Plan", "Attach weather data to an existing flight plan.", new InsertWeatherDataInFlightPlanPanel()));
        }
        addModule(navPanel, "Test Flight Plan", directUseCasePage("Test Flight Plan", "Validate an authenticated pilot flight plan.", new TestFlightPlanPanel()));
        addModule(navPanel, "Consult Flight Plan Reports", directUseCasePage("Consult Flight Plan Reports", "Review generated flight plan validation reports.", new ConsultFlightPlanTestReportsPanel()));
    }

    private void addConsultWeatherData(final JPanel navPanel) {
        addModule(navPanel, "Consult Weather Data", directUseCasePage("Consult Weather Data", "Search weather records by day and control area.", new ConsultWeatherDataPanel()));
    }

    private void addModule(final JPanel target, final String title, final JPanel panel) {
        addModule(target, title, panel, 4);
    }

    private void addModule(final JPanel target, final String title, final JPanel panel, final int bottomGap) {
        if (navButtons.containsKey(title)) {
            return;
        }
        cards.add(panel, title);
        final JButton button = new JButton(title);
        styleNavButton(button, false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.addActionListener(e -> {
            setActive(title);
            showCard(title);
        });
        navButtons.put(title, button);
        target.add(button);
        if (bottomGap > 0) {
            target.add(Box.createRigidArea(new Dimension(0, bottomGap)));
        }
    }

    private void setActive(final String title) {
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            styleNavButton(entry.getValue(), entry.getKey().equals(title));
        }
        showCard(title);
    }

    void navigateTo(final String title) {
        if (navButtons.containsKey(title)) {
            setActive(title);
        }
    }

    private JPanel weatherPanel(final AisafeAuthorizationService authz) {
        final JTabbedPane tabs = moduleTabs();
        if (hasAny(authz, AisafePermissions.WEATHER_DATA_CONSULTATION)
                || hasAny(authz, AisafePermissions.WEATHER_DATA_MANAGEMENT)
                || hasAny(authz, AisafePermissions.FLIGHT_PLAN_MANAGEMENT)
                || hasAny(authz, AisafePermissions.FLIGHT_CONTROL)) {
            tabs.addTab("Consult Data", new ConsultWeatherDataPanel());
        }
        if (hasAny(authz, AisafePermissions.WEATHER_DATA_MANAGEMENT)) {
            tabs.addTab("Register Data", new RegisterWeatherDataPanel());
            tabs.addTab("Import Bulk Data", new ImportBulkWeatherDataPanel());
        }
        return modulePage("Weather Data", "Consult, register and import weather information.", tabs);
    }

    private JPanel airportsPanel() {
        final JTabbedPane tabs = moduleTabs();
        tabs.addTab("Register Airport", new RegisterAirportPanel());
        tabs.addTab("Register Air Control Area", new RegisterAirControlAreaPanel());
        return modulePage("Airports", "Manage airports and air control areas.", tabs);
    }

    private JPanel aircraftPanel(final AisafeAuthorizationService authz) {
        final JTabbedPane tabs = moduleTabs();
        if (hasAny(authz, AisafePermissions.AIR_COMPANY_CONFIGURATION)) {
            tabs.addTab("Register Aircraft", new RegisterAircraftPanel());
            tabs.addTab("List Fleet", new ListAircraftFleetPanel());
            tabs.addTab("Decommission", new DecommissionAircraftPanel());
        }
        if (hasAny(authz, AisafeRoles.BACKOFFICE_OPERATOR)) {
            tabs.addTab("Aircraft Model", new RegisterAircraftModelPanel());
            tabs.addTab("Engine Model", new RegisterEngineModelPanel());
        }
        return modulePage("Aircraft", "Manage aircraft, models, engines and fleet status.", tabs);
    }

    private JPanel pilotsPanel(final AisafeAuthorizationService authz) {
        final JTabbedPane tabs = moduleTabs();
        if (hasAny(authz, AisafePermissions.AIR_COMPANY_CONFIGURATION)) {
            tabs.addTab("Add Pilot", new AddPilotPanel());
            tabs.addTab("Pilot Roster", new ListPilotRosterPanel());
            tabs.addTab("Deactivate Pilot", new DeactivatePilotPanel());
        }
        return modulePage("Pilots", "Manage company pilot roster and pilot availability.", tabs);
    }

    private JPanel flightRoutesPanel(final AisafeAuthorizationService authz) {
        final JTabbedPane tabs = moduleTabs();
        if (hasAny(authz, AisafePermissions.AIR_COMPANY_CONFIGURATION)) {
            tabs.addTab("Register Flight Route", new RegisterFlightRoutePanel());
        }
        return modulePage("Flight Routes", "Create and manage company flight routes.", tabs);
    }

    private JPanel companiesPanel(final AisafeAuthorizationService authz) {
        final JTabbedPane tabs = moduleTabs();

        if (hasAny(authz, AisafeRoles.BACKOFFICE_OPERATOR)) {
            tabs.addTab("Register Company", new RegisterAirTransportCompanyPanel());
            tabs.addTab("Register Collaborator", new RegisterCustomerCollaboratorPanel());
            tabs.addTab("List Collaborators", new ListCustomerCollaboratorsPanel());
        }

        if (hasAny(authz, AisafePermissions.AIR_COMPANY_CONFIGURATION)) {
            tabs.addTab("Add Pilot", new AddPilotPanel());
            tabs.addTab("Pilot Roster", new ListPilotRosterPanel());
            tabs.addTab("Deactivate Pilot", new DeactivatePilotPanel());
        }

        return modulePage("Companies", "Manage companies, collaborators and pilot roster.", tabs);
    }

    private JPanel flightPlansPanel(final AisafeAuthorizationService authz) {
        final JTabbedPane tabs = moduleTabs();
        tabs.addTab("All Flight Plans", new FlightPlansOverviewPanel());
        if (hasAny(authz, AisafePermissions.AIR_COMPANY_CONFIGURATION)) {
            tabs.addTab("Create Flight", new CreateFlightPanel());
        }
        if (hasAny(authz, AisafePermissions.FLIGHT_PLAN_MANAGEMENT) || hasAny(authz, AisafePermissions.FLIGHT_CONTROL)) {
            tabs.addTab("Create Flight Plan", new CreateFlightPlanPanel());
        }
        if (hasAny(authz, AisafePermissions.FLIGHT_PLAN_MANAGEMENT)) {
            tabs.addTab("Insert Weather Data", new InsertWeatherDataInFlightPlanPanel());
            tabs.addTab("Test / Validate", new TestFlightPlanPanel());
            tabs.addTab("Test Reports", new ConsultFlightPlanTestReportsPanel());
        }
        return modulePage("Flight Plans", "Create, validate and review flight plans.", tabs);
    }

    private JPanel reportsPanel() {
        final JTabbedPane tabs = moduleTabs();
        tabs.addTab("Monthly Report", new GenerateMonthlyReportPanel());
        tabs.addTab("Simulation Report", new GenerateSimulationReportPanel());
        return modulePage("Reports", "Generate operational and simulation reports.", tabs);
    }

    private JPanel usersPanel(final AisafeAuthorizationService authz) {
        final JTabbedPane tabs = moduleTabs();
        if (hasAny(authz, AisafeRoles.ADMIN)) {
            tabs.addTab("Register Backoffice User", new RegisterBackofficeUserPanel());
        }
        if (hasAny(authz, AisafeRoles.BACKOFFICE_OPERATOR)) {
            tabs.addTab("Register Pilot", new RegisterPilotPanel());
        }
        return modulePage("Users", "Manage authorized users and pilots.", tabs);
    }

    private JPanel settingsPanel() {
        final JPanel page = createPage();
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        final JPanel card = createCard();
        final GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0;
        cc.gridy = 0;
        cc.anchor = GridBagConstraints.WEST;
        final JLabel title = new JLabel("Settings", new LineIcon("grid", 24, BLUE), SwingConstants.LEFT);
        title.setIconTextGap(10);
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        card.add(title, cc);
        cc.gridy++;
        cc.insets = new Insets(10, 0, 0, 0);
        final JLabel message = new JLabel("Application settings are managed by the existing AISafe configuration.");
        message.setForeground(MUTED);
        card.add(message, cc);
        page.add(card, c);
        return page;
    }

    private static JTabbedPane moduleTabs() {
        final JTabbedPane tabs = new ModernTabs();
        tabs.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        tabs.putClientProperty("JTabbedPane.tabType", "card");
        tabs.putClientProperty("JTabbedPane.showTabSeparators", false);
        tabs.putClientProperty("JTabbedPane.tabHeight", 38);
        return tabs;
    }

    private JPanel modulePage(final String title, final String subtitle, final JTabbedPane tabs) {
        final JPanel page = createPage();
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        final JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        final JLabel heading = new JLabel(title);
        heading.setIcon(new LineIcon(iconName(title), 28, BLUE));
        heading.setIconTextGap(12);
        heading.setForeground(TEXT);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 26f));
        final JLabel sub = new JLabel(subtitle);
        sub.setForeground(MUTED);
        final JPanel copy = new JPanel(new GridBagLayout());
        copy.setOpaque(false);
        final GridBagConstraints hc = new GridBagConstraints();
        hc.gridx = 0;
        hc.gridy = 0;
        hc.anchor = GridBagConstraints.WEST;
        copy.add(heading, hc);
        hc.gridy++;
        copy.add(sub, hc);
        header.add(copy, BorderLayout.WEST);
        final JLabel notify = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), new LineIcon("grid", 16, MUTED), SwingConstants.RIGHT);
        notify.setIconTextGap(8);
        notify.setForeground(MUTED);
        header.add(notify, BorderLayout.EAST);
        page.add(header, c);

        c.gridy++;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        page.add(tabs, c);
        return page;
    }

    private JPanel singleUseCasePage(final String title, final String subtitle, final JPanel panel) {
        final JTabbedPane tabs = moduleTabs();
        tabs.addTab(title, panel);
        return modulePage(title, subtitle, tabs);
    }

    private JPanel directUseCasePage(final String title, final String subtitle, final JPanel panel) {
        final JPanel page = createPage();
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        final JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        final JLabel heading = new JLabel(title, new LineIcon(iconName(title), 28, BLUE), SwingConstants.LEFT);
        heading.setIconTextGap(12);
        heading.setForeground(TEXT);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 26f));
        final JLabel sub = new JLabel(subtitle);
        sub.setForeground(MUTED);
        final JPanel copy = new JPanel(new GridBagLayout());
        copy.setOpaque(false);
        final GridBagConstraints hc = new GridBagConstraints();
        hc.gridx = 0;
        hc.gridy = 0;
        hc.anchor = GridBagConstraints.WEST;
        copy.add(heading, hc);
        hc.gridy++;
        copy.add(sub, hc);
        header.add(copy, BorderLayout.WEST);
        page.add(header, c);
        c.gridy++;
        c.insets = new Insets(24, 0, 0, 0);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        page.add(panel, c);
        return page;
    }

    private static void styleNavButton(final JButton button, final boolean active) {
        button.setHorizontalAlignment(JButton.LEFT);
        button.setIcon(new LineIcon(iconName(button.getText()), 20, active ? Color.WHITE : new Color(148, 163, 184)));
        button.setIconTextGap(13);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));
        button.setForeground(active ? Color.WHITE : new Color(203, 213, 225));
        button.setBackground(active ? BLUE : new Color(0, 0, 0, 0));
        button.setOpaque(active);
        button.setContentAreaFilled(active);
        button.putClientProperty("JButton.buttonType", "borderless");
        button.putClientProperty("FlatLaf.style", "arc: 16; font: 13 $semibold.font");
    }

    static String iconName(final String text) {
        final String value = text.toLowerCase();
        if (value.contains("consult weather")) return "search";
        if (value.contains("register weather")) return "plus";
        if (value.contains("import bulk weather")) return "upload";
        if (value.contains("weather")) return "cloud";
        if (value.contains("aircraft") || value.contains("flight")) return "plane";
        if (value.contains("report")) return "chart";
        if (value.contains("user") || value.contains("pilot") || value.contains("collaborator")) return "user";
        if (value.contains("airport") || value.contains("area") || value.contains("route")) return "map";
        if (value.contains("settings")) return "settings";
        if (value.contains("logout")) return "logout";
        return "grid";
    }

    private void showCard(final String key) {
        ((CardLayout) cards.getLayout()).show(cards, key);
    }

    interface ThrowingRunnable {
        void run() throws Exception;
    }

    static void runSafely(final ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception ex) {
            showError(null, ex.getMessage());
        }
    }

    static String required(final JTextField field, final String label) {
        final String value = field.getText().trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value;
    }

    static String passwordText(final JPasswordField field, final String label) {
        final String value = new String(field.getPassword()).trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value;
    }

    static String selected(final JComboBox<String> combo) {
        final Object value = combo.getSelectedItem();
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Selection is required.");
        }
        return value.toString();
    }

    static int intValue(final JSpinner spinner) {
        return ((Number) spinner.getValue()).intValue();
    }

    static double doubleValue(final JSpinner spinner) {
        return ((Number) spinner.getValue()).doubleValue();
    }

    static String firstPart(final String value) {
        return value.split("\\s*\\|\\s*", 2)[0].trim();
    }

    static String[] splitDesignator(final String value) {
        final String[] parts = value.split("\\s*\\|\\s*", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid designator: " + value);
        }
        return new String[] { parts[0].trim(), parts[1].trim() };
    }

    static JComboBox<String> comboFrom(final Supplier<List<String>> supplier) {
        return new JComboBox<>(new Vector<>(supplier.get()));
    }

    private JPanel createPage() {
        final JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(PAGE);
        page.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));
        return page;
    }

    private static JPanel createCard() {
        final JPanel card = new RoundedPanel(16, CARD);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(30, 34, 28, 34)
        ));
        return card;
    }

    private static void showError(final Component parent, final String message) {
        showNotification(
                parent,
                "Operation failed",
                cleanMessage(message, "Something went wrong."),
                "warning",
                new Color(220, 38, 38),
                new Color(254, 226, 226)
        );
    }

    static void showSuccess(final Component parent, final String message) {
        showNotification(
                parent,
                "Success",
                cleanMessage(message, "Operation completed successfully."),
                "check",
                new Color(22, 163, 74),
                new Color(220, 252, 231)
        );
    }

    static void showInfo(final Component parent, final String message) {
        showNotification(
                parent,
                "Information",
                cleanMessage(message, "Information updated."),
                "info",
                BLUE,
                BLUE_SOFT
        );
    }

    private static String cleanMessage(final String message, final String fallback) {
        if (message == null || message.isBlank()) {
            return fallback;
        }
        return message;
    }

    private static void showNotification(final Component parent,
                                         final String title,
                                         final String message,
                                         final String iconName,
                                         final Color color,
                                         final Color background) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            final MainWindow window = findMainWindow(parent);

            if (window != null) {
                window.notificationBanner.showMessage(title, message, iconName, color, background);
            }
        });
    }

    private static MainWindow findMainWindow(final Component parent) {
        if (parent != null) {
            final java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(parent);

            if (window instanceof MainWindow) {
                return (MainWindow) window;
            }
        }

        final java.awt.Window activeWindow =
                java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();

        if (activeWindow instanceof MainWindow) {
            return (MainWindow) activeWindow;
        }

        for (java.awt.Window window : java.awt.Window.getWindows()) {
            if (window instanceof MainWindow && window.isShowing()) {
                return (MainWindow) window;
            }
        }

        return null;
    }

    static JLabel fieldLabel(final String text) {
        final JLabel label = new JLabel(text);
        label.setForeground(new Color(51, 65, 85));
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        return label;
    }

    static JButton primaryButton(final String text) {
        final JButton button = new JButton(text);
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(372, 43));
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.putClientProperty("FlatLaf.style", "arc: 12");
        return button;
    }

    static JButton secondaryButton(final String text) {
        final JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(372, 43));
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.putClientProperty("FlatLaf.style", "arc: 12");
        return button;
    }

    static Integer optionalInt(final JTextField field) {
        final String value = field.getText().trim();
        return value.isBlank() ? null : Integer.valueOf(value);
    }

    static DefaultTableModel tableModel(final String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(final int row, final int column) {
                return false;
            }
        };
    }

    static String joinLines(final Iterable<?> values) {
        final StringBuilder sb = new StringBuilder();
        for (Object value : values) {
            sb.append(value).append('\n');
        }
        return sb.toString();
    }

    static List<SimulationFlightResult> parseFlights(final String text) {
        final List<SimulationFlightResult> results = new ArrayList<>();
        for (String line : text.split("\\R")) {
            if (!line.isBlank()) {
                final String[] parts = line.split(",", 2);
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid flight result line: " + line);
                }
                results.add(new SimulationFlightResult(parts[0].trim(), parts[1].trim()));
            }
        }
        return results;
    }

    static List<SimulationSafetyViolation> parseViolations(final String text) {
        final List<SimulationSafetyViolation> results = new ArrayList<>();
        for (String line : text.split("\\R")) {
            if (!line.isBlank()) {
                final String[] parts = line.split(",", 3);
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid safety violation line: " + line);
                }
                results.add(new SimulationSafetyViolation(LocalDateTime.parse(parts[0].trim()), parts[1].trim(), parts[2].trim()));
            }
        }
        return results;
    }
    private static final class NotificationBanner extends JPanel {
        private final JLabel icon = new JLabel();
        private final JLabel text = new JLabel();
        private final JButton close = new JButton("×");

        private javax.swing.Timer hideTimer;

        NotificationBanner() {
            super(new BorderLayout(12, 0));

            setOpaque(true);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                    BorderFactory.createEmptyBorder(12, 40, 12, 40)
            ));

            icon.setPreferredSize(new Dimension(28, 28));
            icon.setHorizontalAlignment(SwingConstants.CENTER);

            text.setForeground(TEXT);
            text.setFont(text.getFont().deriveFont(13f));

            close.setFocusPainted(false);
            close.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
            close.setContentAreaFilled(false);
            close.setOpaque(false);
            close.setFont(close.getFont().deriveFont(Font.BOLD, 20f));
            close.addActionListener(e -> hideMessage());

            add(icon, BorderLayout.WEST);
            add(text, BorderLayout.CENTER);
            add(close, BorderLayout.EAST);
        }

        void showMessage(final String title,
                         final String message,
                         final String iconName,
                         final Color color,
                         final Color background) {
            setBackground(background);

            icon.setIcon(new LineIcon(iconName, 22, color));
            close.setForeground(color);

            text.setText(
                    "<html>" +
                            "<div style='font-size:12px'><b>" + html(title) + "</b></div>" +
                            "<div style='font-size:11px;color:#334155;padding-top:2px'>" + html(message) + "</div>" +
                            "</html>"
            );

            setVisible(true);
            revalidate();
            repaint();

            if (hideTimer != null) {
                hideTimer.stop();
            }

            hideTimer = new javax.swing.Timer(5200, e -> hideMessage());
            hideTimer.setRepeats(false);
            hideTimer.start();
        }

        void hideMessage() {
            if (hideTimer != null) {
                hideTimer.stop();
                hideTimer = null;
            }

            setVisible(false);
            revalidate();
            repaint();
        }

        private static String html(final String value) {
            if (value == null) {
                return "";
            }

            return value
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
        }
    }

}
