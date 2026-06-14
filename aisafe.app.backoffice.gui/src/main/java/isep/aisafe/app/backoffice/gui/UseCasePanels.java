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
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import static isep.aisafe.app.backoffice.gui.MainWindow.*;

final class RegisterBackofficeUserPanel extends JPanel {
    RegisterBackofficeUserPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final RegisterBackofficeUserController controller = new RegisterBackofficeUserController();

        final JTextField username = PanelUi.text("e.g., backoffice01");
        final JPasswordField password = PanelUi.password("Initial password");
        final JTextField firstName = PanelUi.text("e.g., Maria");
        final JTextField lastName = PanelUi.text("e.g., Silva");
        final JTextField email = PanelUi.text("e.g., maria.silva@aisafe.com");
        final JComboBox<String> role = new JComboBox<>(new String[] { "ADMIN", "BACKOFFICE_OPERATOR" });
        PanelUi.combo(role);

        final JPanel form = PanelUi.formCard("User Account", "Create an authorized backoffice user.");
        PanelUi.addField(form, 0, 0, "Username *", username);
        PanelUi.addField(form, 0, 1, "Role *", role);
        PanelUi.addField(form, 1, 0, "First name *", firstName);
        PanelUi.addField(form, 1, 1, "Last name *", lastName);
        PanelUi.addField(form, 2, 0, "Email *", email);
        PanelUi.addField(form, 2, 1, "Password *", password);

        final JPanel tips = PanelUi.sideCard("Security Notes", "shield",
                new PanelUi.Tip("check", "Role-based access", "Permissions are applied according to the selected role.", PanelUi.SUCCESS),
                new PanelUi.Tip("warning", "Initial password", "The user should change it after first access.", PanelUi.WARNING),
                new PanelUi.Tip("info", "Valid email", "Use a valid account for identification.", UiTheme.BLUE));

        add(PanelUi.split(form, tips), BorderLayout.CENTER);
        add(PanelUi.actions(
                PanelUi.secondary("Clear", e -> PanelUi.clear(username, password, firstName, lastName, email)),
                PanelUi.primary("Register User", e -> runSafely(() -> {
                    final Role selectedRole = "ADMIN".equals(selected(role)) ? AisafeRoles.ADMIN : AisafeRoles.BACKOFFICE_OPERATOR;
                    controller.registerUser(required(username, "Username"), passwordText(password, "Password"), required(firstName, "First name"), required(lastName, "Last name"), required(email, "Email"), Set.of(selectedRole));
                    JOptionPane.showMessageDialog(this, "User registered successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class RegisterAirControlAreaPanel extends JPanel {
    RegisterAirControlAreaPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextField code = PanelUi.text("e.g., LPR-CONT01");
        final JTextField name = PanelUi.text("e.g., Lisbon Control Area");
        final JSpinner minLat = PanelUi.spinner(0, -90, 90, 0.1);
        final JSpinner maxLat = PanelUi.spinner(0, -90, 90, 0.1);
        final JSpinner minLon = PanelUi.spinner(0, -180, 180, 0.1);
        final JSpinner maxLon = PanelUi.spinner(0, -180, 180, 0.1);
        final JSpinner fuel = PanelUi.spinner(1, 0, 1_000_000, 1);
        final JComboBox<String> unit = new JComboBox<>(new String[] { "KG", "L" });
        PanelUi.combo(unit);

        final JPanel form = PanelUi.formCard("Boundary Details", "Define a new controlled airspace and operating limits.");
        PanelUi.addField(form, 0, 0, "Code *", code);
        PanelUi.addField(form, 0, 1, "Minimum fuel amount *", fuel);
        PanelUi.addField(form, 1, 0, "Name *", name);
        PanelUi.addField(form, 1, 1, "Fuel unit *", unit);
        PanelUi.addSection(form, 2, "Coordinate Limits", "map");
        PanelUi.addField(form, 3, 0, "Minimum latitude *", minLat);
        PanelUi.addField(form, 3, 1, "Maximum latitude *", maxLat);
        PanelUi.addField(form, 4, 0, "Minimum longitude *", minLon);
        PanelUi.addField(form, 4, 1, "Maximum longitude *", maxLon);

        final JPanel side = PanelUi.sideCard("Validation Tips", "shield",
                new PanelUi.Tip("check", "Latitude range", "Latitude values must be between -90 and 90.", PanelUi.SUCCESS),
                new PanelUi.Tip("warning", "Longitude range", "Longitude values must be between -180 and 180.", PanelUi.WARNING),
                new PanelUi.Tip("info", "Boundary order", "Maximum values must be greater than minimum values.", UiTheme.BLUE));

        add(PanelUi.split(form, side), BorderLayout.CENTER);
        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> PanelUi.clear(code, name)),
                PanelUi.primary("Register", e -> runSafely(() -> {
                    new RegisterAirControlAreaController().registerAirControlArea(required(code, "Code"), required(name, "Name"), doubleValue(minLat), doubleValue(maxLat), doubleValue(minLon), doubleValue(maxLon), doubleValue(fuel), selected(unit));
                    JOptionPane.showMessageDialog(this, "Air control area registered successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class RegisterAirportPanel extends JPanel {
    RegisterAirportPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);
        final RegisterAirportController controller = new RegisterAirportController();

        final JTextField name = PanelUi.text("e.g., Francisco Sa Carneiro Airport");
        final JTextField iata = PanelUi.text("e.g., OPO");
        final JTextField icao = PanelUi.text("e.g., LPPR");
        final JSpinner elev = PanelUi.spinner(0, -500, 10000, 1);
        final JTextField country = PanelUi.text("e.g., Portugal");
        final JSpinner lat = PanelUi.spinner(0, -90, 90, 0.1);
        final JTextField town = PanelUi.text("e.g., Porto");
        final JSpinner lon = PanelUi.spinner(0, -180, 180, 0.1);
        final JComboBox<String> area = new JComboBox<>(new String[] { "Load after coordinates" });
        PanelUi.combo(area);

        final JPanel form = PanelUi.formCard("General Information", "Add a new airport to the system.");
        PanelUi.addField(form, 0, 0, "Airport name *", name);
        PanelUi.addField(form, 0, 1, "IATA code *", iata);
        PanelUi.addField(form, 1, 0, "ICAO code *", icao);
        PanelUi.addField(form, 1, 1, "Elevation *", elev);
        PanelUi.addField(form, 2, 0, "Country *", country);
        PanelUi.addField(form, 2, 1, "Latitude *", lat);
        PanelUi.addField(form, 3, 0, "Town / City *", town);
        PanelUi.addField(form, 3, 1, "Longitude *", lon);
        PanelUi.addSection(form, 4, "Air Control Area", "map");
        PanelUi.addField(form, 5, 0, "Associated area *", area);

        final JPanel preview = PanelUi.previewCard("Location Preview", "Enter coordinates to preview the airport location and find containing air control areas.",
                new String[][] { { "Latitude", "0.000000°" }, { "Longitude", "0.000000°" }, { "Elevation", "0" }, { "Status", "No coordinates" } });

        add(PanelUi.wizard("Register Airport", form, preview,
                new PanelUi.Step("General Information", "Basic airport details", true),
                new PanelUi.Step("Location", "Coordinates and elevation", false),
                new PanelUi.Step("Air Control Area", "Associated control area", false),
                new PanelUi.Step("Review", "Validate before register", false)), BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> PanelUi.clear(name, iata, icao, country, town)),
                PanelUi.secondary("Find containing areas", e -> runSafely(() -> {
                    area.removeAllItems();
                    for (String option : controller.airControlAreasContaining(doubleValue(lat), doubleValue(lon))) {
                        area.addItem(option);
                    }
                    if (area.getItemCount() == 0) {
                        throw new IllegalArgumentException("No air control area contains these coordinates.");
                    }
                })),
                PanelUi.primary("Register", e -> runSafely(() -> {
                    controller.registerAirport(required(icao, "ICAO code"), required(iata, "IATA code"), required(name, "Name"), required(country, "Country"), required(town, "Town"), doubleValue(lat), doubleValue(lon), doubleValue(elev), firstPart(selected(area)));
                    JOptionPane.showMessageDialog(this, "Airport registered successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class RegisterAirTransportCompanyPanel extends JPanel {
    RegisterAirTransportCompanyPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextField name = PanelUi.text("Enter company full legal name");
        final JTextField country = PanelUi.text("Select or enter country");
        final JTextField iata = PanelUi.text("e.g., TP");
        final JTextField icao = PanelUi.text("e.g., TAP");

        final JPanel form = PanelUi.formCard("Company Details", "Add a new air transport company to the system.");
        PanelUi.addField(form, 0, 0, "Company name *", name);
        PanelUi.addField(form, 0, 1, "Country *", country);
        PanelUi.addField(form, 1, 0, "IATA code *", iata);
        PanelUi.addField(form, 1, 1, "ICAO code *", icao);
        PanelUi.addInfoStrip(form, 2, "All fields marked with * are required.");

        final JPanel side = PanelUi.sideCard("Registration Tips", "info",
                new PanelUi.Tip("check", "Unique codes", "Ensure IATA and ICAO codes are valid and unique.", PanelUi.SUCCESS),
                new PanelUi.Tip("shield", "Official name", "Use the official company name as registered.", UiTheme.BLUE),
                new PanelUi.Tip("warning", "Routes", "An active company can be used in routes and operations.", PanelUi.WARNING));

        add(PanelUi.split(form, side), BorderLayout.CENTER);
        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> PanelUi.clear(name, country, iata, icao)),
                PanelUi.primary("Register Company", e -> runSafely(() -> {
                    new AirCompanyConfigurationController().registerAirTransportCompany(required(name, "Name"), required(country, "Country"), required(iata, "IATA code"), required(icao, "ICAO code"));
                    JOptionPane.showMessageDialog(this, "Air transport company registered successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class RegisterCustomerCollaboratorPanel extends JPanel {
    RegisterCustomerCollaboratorPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JComboBox<String> type = new JComboBox<>(new String[] { "Air Transport Company", "Air Control Area" });
        PanelUi.combo(type);
        final JTextField customerCode = PanelUi.text("IATA code or area code");
        final JTextField firstName = PanelUi.text("First name");
        final JTextField lastName = PanelUi.text("Last name");
        final JTextField email = PanelUi.text("email@example.com");
        final JPasswordField password = PanelUi.password("Initial password");

        final JPanel form = PanelUi.formCard("Collaborator Details", "Create an access account linked to a customer.");
        PanelUi.addField(form, 0, 0, "Customer type *", type);
        PanelUi.addField(form, 0, 1, "Customer code *", customerCode);
        PanelUi.addField(form, 1, 0, "First name *", firstName);
        PanelUi.addField(form, 1, 1, "Last name *", lastName);
        PanelUi.addField(form, 2, 0, "Email *", email);
        PanelUi.addField(form, 2, 1, "Initial password *", password);

        final JPanel side = PanelUi.sideCard("Access Notes", "user",
                new PanelUi.Tip("shield", "System user", "Each collaborator is also a system user.", UiTheme.BLUE),
                new PanelUi.Tip("check", "Authorization", "Access is restricted to allowed customer operations.", PanelUi.SUCCESS),
                new PanelUi.Tip("warning", "Disable when needed", "Inactive collaborators should be disabled.", PanelUi.WARNING));

        add(PanelUi.split(form, side), BorderLayout.CENTER);
        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> PanelUi.clear(customerCode, firstName, lastName, email, password)),
                PanelUi.primary("Register Collaborator", e -> runSafely(() -> {
                    final RegisterCustomerCollaboratorController controller = new RegisterCustomerCollaboratorController();
                    if ("Air Transport Company".equals(selected(type))) {
                        controller.registerCollaboratorForAirTransportCompany(required(firstName, "First name"), required(lastName, "Last name"), required(email, "Email"), required(customerCode, "IATA code"), passwordText(password, "Initial password"));
                    } else {
                        controller.registerCollaboratorForAirControlArea(required(firstName, "First name"), required(lastName, "Last name"), required(email, "Email"), required(customerCode, "Area code"), passwordText(password, "Initial password"));
                    }
                    JOptionPane.showMessageDialog(this, "Customer collaborator registered successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class ListCustomerCollaboratorsPanel extends JPanel {
    ListCustomerCollaboratorsPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JComboBox<String> type = new JComboBox<>(new String[] { "Air Transport Company", "Air Control Area" });
        PanelUi.combo(type);
        final JTextField code = PanelUi.text("IATA code or area code");
        final JTable table = new JTable(tableModel("Collaborators"));
        PanelUi.table(table);

        final JPanel filters = PanelUi.formCard("Search Collaborators", "List active collaborators for a given customer.");
        PanelUi.addField(filters, 0, 0, "Customer type *", type);
        PanelUi.addField(filters, 0, 1, "Customer code *", code);

        final JPanel card = PanelUi.card(new BorderLayout(0, 16));
        card.add(filters, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("Clear", e -> {
                    code.setText("");
                    table.setModel(tableModel("Collaborators"));
                }),
                PanelUi.primary("List Collaborators", e -> runSafely(() -> {
                    final ListCustomerCollaboratorsController controller = new ListCustomerCollaboratorsController();
                    final List<?> result = "Air Transport Company".equals(selected(type))
                            ? controller.listForAirTransportCompany(required(code, "IATA code"))
                            : controller.listForAirControlArea(required(code, "Area code"));

                    final DefaultTableModel model = tableModel("Collaborator");
                    for (Object value : result) {
                        model.addRow(new Object[] { value });
                    }
                    table.setModel(model);
                }))
        ), BorderLayout.SOUTH);
    }
}

final class RegisterEngineModelPanel extends JPanel {
    RegisterEngineModelPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final RegisterEngineModelController controller = new RegisterEngineModelController();
        final JTextField model = PanelUi.text("e.g., GE90B");
        final JComboBox<String> manufacturer = comboFrom(controller::engineManufacturerDesignators);
        PanelUi.combo(manufacturer);
        final JComboBox<String> engineType = new JComboBox<>(new String[] { "TURBOFAN", "TURBOPROP", "TURBOJET", "PISTON" });
        PanelUi.combo(engineType);
        final JComboBox<String> fuelType = new JComboBox<>(new String[] { "JET A-1", "AVGAS 100LL", "SAF", "DIESEL" });
        PanelUi.combo(fuelType);
        final JSpinner power = PanelUi.spinner(1, 0, 1_000_000, 1);
        final JSpinner thrust = PanelUi.spinner(1, 0, 1_000_000, 1);
        final JSpinner tsfc = PanelUi.spinner(0.1, 0, 1_000_000, 0.1);

        final JPanel form = PanelUi.formCard("Engine Model Details", "Register an engine model and its performance characteristics.");
        PanelUi.addField(form, 0, 0, "Model name *", model);
        PanelUi.addField(form, 0, 1, "Manufacturer *", manufacturer);
        PanelUi.addField(form, 1, 0, "Engine type *", engineType);
        PanelUi.addField(form, 1, 1, "Fuel type *", fuelType);
        PanelUi.addSection(form, 2, "Performance", "chart");
        PanelUi.addField(form, 3, 0, "Power *", power);
        PanelUi.addField(form, 3, 1, "Thrust *", thrust);
        PanelUi.addField(form, 4, 0, "TSFC *", tsfc);

        final JPanel side = PanelUi.sideCard("Engine Tips", "plane",
                new PanelUi.Tip("check", "Unique pair", "Model name and manufacturer must be unique.", PanelUi.SUCCESS),
                new PanelUi.Tip("info", "Compatibility", "Engine type must match aircraft motorization.", UiTheme.BLUE),
                new PanelUi.Tip("warning", "Efficiency", "Use consistent units for TSFC values.", PanelUi.WARNING));

        add(PanelUi.split(form, side), BorderLayout.CENTER);
        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> PanelUi.clear(model)),
                PanelUi.primary("Register Engine", e -> runSafely(() -> {
                    controller.registerEngineModel(required(model, "Model name"), selected(manufacturer), selected(engineType), selected(fuelType), doubleValue(power), doubleValue(thrust), doubleValue(tsfc));
                    JOptionPane.showMessageDialog(this, "Engine model registered successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class RegisterAircraftModelPanel extends JPanel {
    RegisterAircraftModelPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final ManageAircraftModelController controller = new ManageAircraftModelController();
        final JTextField model = PanelUi.text("e.g., A320neo");
        final JComboBox<String> manufacturer = comboFrom(controller::aircraftManufacturerDesignators);
        PanelUi.combo(manufacturer);
        final JComboBox<String> type = new JComboBox<>(new String[] { "PASSENGER", "CARGO", "MIXED" });
        PanelUi.combo(type);
        final JComboBox<String> motorization = new JComboBox<>(new String[] { "TURBOFAN", "TURBOPROP", "TURBOJET", "PISTON" });
        PanelUi.combo(motorization);
        final JSpinner range = PanelUi.spinner(1, 0, 1_000_000, 1);
        final JSpinner emptyWeight = PanelUi.spinner(1, 0, 1_000_000, 1);
        final JSpinner mtow = PanelUi.spinner(1, 0, 1_000_000, 1);
        final JSpinner mzfw = PanelUi.spinner(1, 0, 1_000_000, 1);
        final JSpinner fuel = PanelUi.spinner(1, 0, 1_000_000, 1);
        final JSpinner ceiling = PanelUi.spinner(1, 0, 100_000, 1);
        final JSpinner cruise = PanelUi.spinner(1, 0, 10_000, 1);
        final JSpinner wing = PanelUi.spinner(1, 0, 10_000, 1);
        final JSpinner drag = PanelUi.spinner(0.01, 0, 10, 0.01);
        final JSpinner lift = PanelUi.spinner(0.1, 0, 10, 0.1);
        final JSpinner passengers = PanelUi.intSpinner(1, 0, 1000);
        final JList<String> engines = new JList<>();
        engines.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        engines.setVisibleRowCount(5);
        motorization.addActionListener(e -> engines.setListData(new Vector<>(controller.compatibleEngineModelDesignators(selected(motorization)))));
        engines.setListData(new Vector<>(controller.compatibleEngineModelDesignators(selected(motorization))));

        final JPanel form = PanelUi.formCard("Aircraft Model Details", "Define technical and operational characteristics.");
        PanelUi.addField(form, 0, 0, "Model name *", model);
        PanelUi.addField(form, 0, 1, "Manufacturer *", manufacturer);
        PanelUi.addField(form, 1, 0, "Type *", type);
        PanelUi.addField(form, 1, 1, "Motorization *", motorization);
        PanelUi.addSection(form, 2, "Limits and performance", "chart");
        PanelUi.addField(form, 3, 0, "Max range *", range);
        PanelUi.addField(form, 3, 1, "Cruise speed *", cruise);
        PanelUi.addField(form, 4, 0, "Empty weight *", emptyWeight);
        PanelUi.addField(form, 4, 1, "MTOW *", mtow);
        PanelUi.addField(form, 5, 0, "MZFW *", mzfw);
        PanelUi.addField(form, 5, 1, "Max fuel capacity *", fuel);
        PanelUi.addField(form, 6, 0, "Service ceiling *", ceiling);
        PanelUi.addField(form, 6, 1, "Passenger capacity *", passengers);
        PanelUi.addField(form, 7, 0, "Wing area *", wing);
        PanelUi.addField(form, 7, 1, "Drag coefficient *", drag);
        PanelUi.addField(form, 8, 0, "Lift coefficient *", lift);
        PanelUi.addField(form, 8, 1, "Certified engines *", new JScrollPane(engines));

        final JPanel side = PanelUi.sideCard("Model Checks", "plane",
                new PanelUi.Tip("check", "At least one engine", "Aircraft models must retain certified engines.", PanelUi.SUCCESS),
                new PanelUi.Tip("warning", "Weight limits", "Check MTOW, MZFW and fuel capacity consistency.", PanelUi.WARNING),
                new PanelUi.Tip("info", "Unique model", "Model and manufacturer combination must be unique.", UiTheme.BLUE));

        final JScrollPane scroll = new JScrollPane(PanelUi.split(form, side));
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> PanelUi.clear(model)),
                PanelUi.primary("Register Model", e -> runSafely(() -> {
                    final List<String> selectedEngines = engines.getSelectedValuesList();
                    controller.registerAircraftModel(required(model, "Model name"), selected(manufacturer), selected(type), selected(motorization), doubleValue(range), doubleValue(emptyWeight), doubleValue(mtow), doubleValue(mzfw), doubleValue(fuel), doubleValue(ceiling), doubleValue(cruise), doubleValue(wing), doubleValue(drag), doubleValue(lift), intValue(passengers), selectedEngines);
                    JOptionPane.showMessageDialog(this, "Aircraft model registered successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class RegisterAircraftPanel extends JPanel {
    RegisterAircraftPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final RegisterAircraftController controller = new RegisterAircraftController();
        final JTextField registration = PanelUi.text("e.g., CS-TUP");
        final JTextField country = PanelUi.text("Registration country");
        final JComboBox<String> status = new JComboBox<>(new String[] { "ACTIVE", "IN_MAINTENANCE", "DECOMMISSIONED" });
        PanelUi.combo(status);
        final JSpinner year = PanelUi.intSpinner(2026, 1903, 3000);
        final JSpinner crew = PanelUi.intSpinner(1, 1, 100);
        final JSpinner economy = PanelUi.intSpinner(0, 0, 1000);
        final JSpinner business = PanelUi.intSpinner(0, 0, 1000);
        final JSpinner first = PanelUi.intSpinner(0, 0, 1000);
        final JComboBox<String> model = comboFrom(controller::aircraftModelDesignators);
        PanelUi.combo(model);
        final JComboBox<String> engine = new JComboBox<>();
        PanelUi.combo(engine);
        final Runnable reloadEngines = () -> {
            engine.removeAllItems();
            for (String item : controller.certifiedEngineModelDesignators(selected(model))) {
                engine.addItem(item);
            }
        };
        model.addActionListener(e -> reloadEngines.run());
        if (model.getItemCount() > 0) {
            reloadEngines.run();
        }

        final JPanel form = PanelUi.formCard("Aircraft Registration", "Add an aircraft to the authenticated company's fleet.");
        PanelUi.addField(form, 0, 0, "Registration *", registration);
        PanelUi.addField(form, 0, 1, "Registration country *", country);
        PanelUi.addField(form, 1, 0, "Operational status *", status);
        PanelUi.addField(form, 1, 1, "Manufacture year *", year);
        PanelUi.addSection(form, 2, "Configuration", "plane");
        PanelUi.addField(form, 3, 0, "Aircraft model *", model);
        PanelUi.addField(form, 3, 1, "Certified engine *", engine);
        PanelUi.addField(form, 4, 0, "Crew size *", crew);
        PanelUi.addField(form, 4, 1, "Economy seats", economy);
        PanelUi.addField(form, 5, 0, "Business seats", business);
        PanelUi.addField(form, 5, 1, "First class seats", first);

        final JPanel side = PanelUi.sideCard("Fleet Rules", "check",
                new PanelUi.Tip("check", "Unique registration", "Each aircraft registration must be unique.", PanelUi.SUCCESS),
                new PanelUi.Tip("warning", "Seat capacity", "Total seats cannot exceed model capacity.", PanelUi.WARNING),
                new PanelUi.Tip("info", "Certified engine", "Only certified engines are available for the selected model.", UiTheme.BLUE));

        add(PanelUi.split(form, side), BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> PanelUi.clear(registration, country)),
                PanelUi.primary("Register Aircraft", e -> runSafely(() -> {
                    final int totalSeats = intValue(economy) + intValue(business) + intValue(first);
                    if (totalSeats == 0) {
                        throw new IllegalArgumentException("Cabin configuration must have at least one seat.");
                    }
                    final int capacity = controller.aircraftModelPassengerCapacity(selected(model));
                    if (totalSeats > capacity) {
                        throw new IllegalArgumentException("Total seats exceed aircraft model passenger capacity: " + capacity);
                    }
                    final String[] modelParts = splitDesignator(selected(model));
                    final String[] engineParts = splitDesignator(selected(engine));
                    controller.registerAircraft(required(registration, "Registration"), required(country, "Registration country"), selected(status), intValue(year), intValue(crew), intValue(economy), intValue(business), intValue(first), modelParts[0], modelParts[1], engineParts[0], engineParts[1]);
                    JOptionPane.showMessageDialog(this, "Aircraft registered successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class ListAircraftFleetPanel extends JPanel {
    ListAircraftFleetPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextField model = PanelUi.text("Model");
        final JTextField maker = PanelUi.text("Maker");
        final JTextField minCapacity = PanelUi.text("Min capacity");
        final JTextField maxCapacity = PanelUi.text("Max capacity");
        final JTextField minAge = PanelUi.text("Min age");
        final JTextField maxAge = PanelUi.text("Max age");
        final JTable table = new JTable(tableModel("Registration", "Model", "Maker", "Seats", "Capacity", "Year", "Age", "Status", "Engine"));
        PanelUi.table(table);

        final JPanel filters = PanelUi.card(new GridBagLayout());
        PanelUi.addCompactFilter(filters, 0, model);
        PanelUi.addCompactFilter(filters, 1, maker);
        PanelUi.addCompactFilter(filters, 2, minCapacity);
        PanelUi.addCompactFilter(filters, 3, maxCapacity);
        PanelUi.addCompactFilter(filters, 4, minAge);
        PanelUi.addCompactFilter(filters, 5, maxAge);

        final JPanel card = PanelUi.card(new BorderLayout(0, 16));
        card.add(filters, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("Clear", e -> PanelUi.clear(model, maker, minCapacity, maxCapacity, minAge, maxAge)),
                PanelUi.primary("Search Fleet", e -> runSafely(() -> {
                    final AircraftFleetFilter filter = new AircraftFleetFilter(model.getText(), maker.getText(), optionalInt(minCapacity), optionalInt(maxCapacity), optionalInt(minAge), optionalInt(maxAge));
                    final List<AircraftFleetEntry> entries = new ListAircraftFleetController().listAircraftFleet(filter);
                    final DefaultTableModel m = tableModel("Registration", "Model", "Maker", "Seats", "Capacity", "Year", "Age", "Status", "Engine");
                    for (AircraftFleetEntry item : entries) {
                        m.addRow(new Object[] { item.registration(), item.model(), item.maker(), item.totalSeats(), item.modelCapacity(), item.manufactureYear(), item.ageInYears(), item.operationalStatus(), item.engineModel() });
                    }
                    table.setModel(m);
                }))
        ), BorderLayout.SOUTH);
    }
}

final class DecommissionAircraftPanel extends JPanel {
    DecommissionAircraftPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final DecommissionAircraftController controller = new DecommissionAircraftController();
        final JComboBox<String> aircraft = new JComboBox<>();
        PanelUi.combo(aircraft);

        final JPanel form = PanelUi.formCard("Decommission Aircraft", "Retire an aircraft from the company fleet.");
        PanelUi.addField(form, 0, 0, "Aircraft *", aircraft);
        PanelUi.addInfoStrip(form, 1, "You cannot decommission aircraft with pending flights.");

        final JPanel side = PanelUi.sideCard("Safety Check", "warning",
                new PanelUi.Tip("warning", "Pending flights", "Aircraft with pending flights cannot be retired.", PanelUi.WARNING),
                new PanelUi.Tip("check", "Operational status", "Decommissioning changes future availability.", PanelUi.SUCCESS));

        add(PanelUi.split(form, side), BorderLayout.CENTER);
        add(PanelUi.actions(
                PanelUi.secondary("Refresh", e -> runSafely(() -> {
                    aircraft.removeAllItems();
                    for (Aircraft a : controller.decommissionableAircraft()) {
                        aircraft.addItem(a.identity().toString());
                    }
                })),
                PanelUi.primary("Decommission", e -> runSafely(() -> {
                    controller.decommissionAircraft(selected(aircraft));
                    JOptionPane.showMessageDialog(this, "Aircraft decommissioned successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class RegisterFlightRoutePanel extends JPanel {
    RegisterFlightRoutePanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final RegisterFlightRouteController controller = new RegisterFlightRouteController();
        final JTextField designator = PanelUi.text("e.g., TP123");
        final JComboBox<String> departure = comboFrom(controller::airportOptions);
        PanelUi.combo(departure);
        final JComboBox<String> destination = comboFrom(controller::airportOptions);
        PanelUi.combo(destination);

        final JPanel form = PanelUi.formCard("Route Details", "Create a company route between two airports.");
        PanelUi.addField(form, 0, 0, "Route designator *", designator);
        PanelUi.addField(form, 1, 0, "Departure airport *", departure);
        PanelUi.addField(form, 1, 1, "Destination airport *", destination);

        final JPanel side = PanelUi.sideCard("Route Rules", "map",
                new PanelUi.Tip("check", "Unique designator", "The route designator must be unique.", PanelUi.SUCCESS),
                new PanelUi.Tip("warning", "Origin/Destination", "Departure and destination should be different.", PanelUi.WARNING));

        add(PanelUi.split(form, side), BorderLayout.CENTER);
        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> designator.setText("")),
                PanelUi.primary("Register Route", e -> runSafely(() -> {
                    controller.registerFlightRoute(required(designator, "Route designator"), firstPart(selected(departure)), firstPart(selected(destination)));
                    JOptionPane.showMessageDialog(this, "Flight route registered successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class CreateFlightPanel extends JPanel {
    CreateFlightPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextField route = PanelUi.text("Route designator");
        final JTextField flight = PanelUi.text("Flight designator");
        final JTextField day = PanelUi.text("yyyy-MM-dd");
        final JTextField time = PanelUi.text("HH:mm");

        final JPanel form = PanelUi.formCard("Flight Schedule", "Create a scheduled flight from a route.");
        PanelUi.addField(form, 0, 0, "Route designator *", route);
        PanelUi.addField(form, 0, 1, "Flight designator *", flight);
        PanelUi.addField(form, 1, 0, "Departure day *", day);
        PanelUi.addField(form, 1, 1, "Departure time *", time);

        final JPanel side = PanelUi.sideCard("Schedule Tips", "plane",
                new PanelUi.Tip("check", "Flight designator", "Use the company code and flight number.", PanelUi.SUCCESS),
                new PanelUi.Tip("info", "Date and time", "Use the format accepted by the console flow.", UiTheme.BLUE));

        add(PanelUi.split(form, side), BorderLayout.CENTER);
        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> PanelUi.clear(route, flight, day, time)),
                PanelUi.primary("Create Flight", e -> runSafely(() -> {
                    new CreateFlightController().createFlight(required(route, "Route designator"), required(flight, "Flight designator"), required(day, "Departure day"), required(time, "Departure time"));
                    JOptionPane.showMessageDialog(this, "Flight created successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class CreateFlightPlanPanel extends JPanel {
    CreateFlightPlanPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextField id = PanelUi.text("Flight plan identifier");
        final JTextField flight = PanelUi.text("Flight designator");
        final JTextField aircraft = PanelUi.text("Aircraft registration");
        final JTextField pilot = PanelUi.text("Pilot ID");
        final JSpinner fuel = PanelUi.spinner(1, 0, 1_000_000, 1);
        final JComboBox<String> unit = new JComboBox<>(new String[] { "KG", "L" });
        PanelUi.combo(unit);

        final JPanel form = PanelUi.formCard("Flight Plan Details", "Create a draft flight plan for validation.");
        PanelUi.addField(form, 0, 0, "Flight plan identifier *", id);
        PanelUi.addField(form, 0, 1, "Flight designator *", flight);
        PanelUi.addField(form, 1, 0, "Aircraft registration *", aircraft);
        PanelUi.addField(form, 1, 1, "Pilot ID *", pilot);
        PanelUi.addField(form, 2, 0, "Fuel amount *", fuel);
        PanelUi.addField(form, 2, 1, "Fuel unit *", unit);

        final JPanel side = PanelUi.sideCard("Validation Flow", "check",
                new PanelUi.Tip("info", "Draft status", "New flight plans start as draft.", UiTheme.BLUE),
                new PanelUi.Tip("warning", "Weather data", "Adding weather data voids previous tests.", PanelUi.WARNING),
                new PanelUi.Tip("check", "Pilot", "Pilot must belong to the route company.", PanelUi.SUCCESS));

        add(PanelUi.split(form, side), BorderLayout.CENTER);
        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> PanelUi.clear(id, flight, aircraft, pilot)),
                PanelUi.primary("Create Plan", e -> runSafely(() -> {
                    new CreateFlightPlanController().createFlightPlan(required(id, "Flight plan identifier"), required(flight, "Flight designator"), required(aircraft, "Aircraft registration"), required(pilot, "Pilot ID"), doubleValue(fuel), selected(unit));
                    JOptionPane.showMessageDialog(this, "Flight plan created successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class InsertWeatherDataInFlightPlanPanel extends JPanel {
    InsertWeatherDataInFlightPlanPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextField id = PanelUi.text("Flight plan identifier");
        final JTextField day = PanelUi.text("yyyy-MM-dd");
        final JTextField area = PanelUi.text("Air control area code");

        final JPanel form = PanelUi.formCard("Weather Association", "Attach weather data to an existing flight plan.");
        PanelUi.addField(form, 0, 0, "Flight plan identifier *", id);
        PanelUi.addField(form, 0, 1, "Weather day *", day);
        PanelUi.addField(form, 1, 0, "Air control area code *", area);

        final JPanel side = PanelUi.sideCard("Important", "warning",
                new PanelUi.Tip("warning", "Retesting required", "If the flight plan was already tested, the test becomes void.", PanelUi.WARNING),
                new PanelUi.Tip("info", "Area-specific", "Weather data is loaded for the selected control area.", UiTheme.BLUE));

        add(PanelUi.split(form, side), BorderLayout.CENTER);
        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> PanelUi.clear(id, day, area)),
                PanelUi.primary("Insert Weather", e -> runSafely(() -> {
                    final FlightPlan plan = new InsertWeatherDataInFlightPlanController().insertWeatherData(required(id, "Flight plan identifier"), required(day, "Weather day"), required(area, "Air control area code"));
                    JOptionPane.showMessageDialog(this, "Weather data inserted. Status: " + plan.status() + ", weather entries: " + plan.weatherDataIds().size());
                }))
        ), BorderLayout.SOUTH);
    }
}

final class TestFlightPlanPanel extends JPanel {
    TestFlightPlanPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final TestFlightPlanController controller = new TestFlightPlanController();
        final JComboBox<String> plans = new JComboBox<>();
        PanelUi.combo(plans);
        final JTextArea out = PanelUi.outputArea("Test results will appear here.");

        final JPanel form = PanelUi.formCard("Flight Plan Test", "Run the validation process for an authenticated pilot flight plan.");
        PanelUi.addField(form, 0, 0, "Flight plan *", plans);

        final JPanel result = PanelUi.resultPanel("Validation Result", out);
        final JPanel content = PanelUi.card(new BorderLayout(0, 18));
        content.add(form, BorderLayout.NORTH);
        content.add(result, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("Refresh", e -> runSafely(() -> {
                    plans.removeAllItems();
                    for (FlightPlan p : controller.authenticatedPilotFlightPlans()) {
                        plans.addItem(p.identity().toString());
                    }
                })),
                PanelUi.primary("Test Plan", e -> new SwingWorker<TestFlightPlanResult, Void>() {
                    @Override
                    protected TestFlightPlanResult doInBackground() {
                        return controller.testFlightPlan(selected(plans));
                    }

                    @Override
                    protected void done() {
                        runSafely(() -> {
                            final TestFlightPlanResult result = get();
                            out.setText("Successful: " + result.isSuccessful()
                                    + "\nStatus: " + result.status()
                                    + "\nReport: " + result.reportIdentifier().orElse("-")
                                    + "\n\n" + String.join("\n", result.messages()));
                        });
                    }
                }.execute())
        ), BorderLayout.SOUTH);
    }
}

final class ConsultFlightPlanTestReportsPanel extends JPanel {
    ConsultFlightPlanTestReportsPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextArea out = PanelUi.outputArea("Reports will appear here after loading.");
        final JPanel card = PanelUi.card(new BorderLayout(0, 18));
        card.add(PanelUi.sectionHeader("Test Reports", "Review validation reports for the authenticated pilot.", "chart"), BorderLayout.NORTH);
        card.add(PanelUi.resultPanel("Reports", out), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("Clear", e -> out.setText("")),
                PanelUi.primary("Load Reports", e -> runSafely(() -> {
                    final StringBuilder sb = new StringBuilder();
                    for (FlightPlanTestReport r : new ConsultFlightPlanTestReportsController().authenticatedPilotReports()) {
                        sb.append("Report: ").append(r.identity()).append('\n')
                                .append("Flight plan: ").append(r.flightPlan().identity()).append('\n')
                                .append("Successful: ").append(r.successful()).append('\n')
                                .append("Final status: ").append(r.finalStatus()).append('\n')
                                .append("Tested at: ").append(r.testedAt()).append('\n')
                                .append("Tested by: ").append(r.testedBy()).append('\n')
                                .append(String.join("\n", r.messages())).append("\n\n");
                    }
                    out.setText(sb.toString());
                }))
        ), BorderLayout.SOUTH);
    }
}

final class RegisterWeatherDataPanel extends JPanel {
    RegisterWeatherDataPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final RegisterWeatherDataController controller = new RegisterWeatherDataController();
        final JTextField day = PanelUi.text("yyyy-MM-dd");
        final JComboBox<String> area = comboFrom(controller::airControlAreas);
        PanelUi.combo(area);
        final JSpinner lat = PanelUi.spinner(0, -90, 90, 0.1);
        final JSpinner lon = PanelUi.spinner(0, -180, 180, 0.1);
        final JComboBox<String> parameter = new JComboBox<>(new String[] { "TEMPERATURE", "WIND_SPEED", "WIND_DIRECTION", "PRESSURE", "HUMIDITY" });
        PanelUi.combo(parameter);
        parameter.setRenderer(new WeatherParameterRenderer());
        final JSpinner value = PanelUi.spinner(0, -1000, 100000, 0.1);
        final JTextField unit = PanelUi.text("C, kt, hPa...");
        final JSpinner altitude = PanelUi.spinner(0, 0, 100000, 1);

        final JPanel form = PanelUi.formCard("Measurement Details", "Register a new weather observation in the system.");
        PanelUi.addField(form, 0, 0, "Day *", day);
        PanelUi.addField(form, 0, 1, "Air control area *", area);
        PanelUi.addField(form, 1, 0, "Latitude *", lat);
        PanelUi.addField(form, 1, 1, "Longitude *", lon);
        PanelUi.addField(form, 2, 0, "Parameter *", parameter);
        PanelUi.addField(form, 2, 1, "Value *", value);
        PanelUi.addField(form, 3, 0, "Unit *", unit);
        PanelUi.addField(form, 3, 1, "Altitude", altitude);

        final JPanel tips = PanelUi.sideCard("Validation Tips", "info",
                new PanelUi.Tip("check", "Use correct format", "Ensure the day and coordinates are accurate.", PanelUi.SUCCESS),
                new PanelUi.Tip("warning", "Choose parameter", "Select the parameter that best matches the measurement.", PanelUi.WARNING),
                new PanelUi.Tip("cloud", "Verify source", "Only trusted providers should insert weather data.", UiTheme.BLUE));
        add(PanelUi.split(form, tips), BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> PanelUi.clear(day, unit)),
                PanelUi.primary("Register", e -> runSafely(() -> {
                    controller.registerWeatherMeasurement(required(day, "Day"), firstPart(selected(area)), selected(parameter), doubleValue(value), required(unit, "Unit"), doubleValue(altitude), doubleValue(lat), doubleValue(lon));
                    JOptionPane.showMessageDialog(this, "Weather data registered successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class ConsultWeatherDataPanel extends JPanel {
    ConsultWeatherDataPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextField search = PanelUi.text("Search by location, area or ICAO...");
        final JTextField area = PanelUi.text("Air control area code");
        final JTextField day = PanelUi.text("yyyy-MM-dd");
        final JTable table = new JTable(tableModel("Results"));
        PanelUi.table(table);
        ((DefaultTableModel) table.getModel()).addRow(new Object[] { "Results will appear here after consultation." });

        final JPanel filters = PanelUi.card(new GridBagLayout());
        PanelUi.addCompactFilter(filters, 0, search);
        PanelUi.addCompactFilter(filters, 1, area);
        PanelUi.addCompactFilter(filters, 2, day);

        final JPanel results = PanelUi.card(new BorderLayout(0, 16));
        results.add(filters, BorderLayout.NORTH);
        results.add(new JScrollPane(table), BorderLayout.CENTER);

        add(results, BorderLayout.CENTER);
        add(PanelUi.actions(
                PanelUi.secondary("Clear", e -> {
                    PanelUi.clear(search, area, day);
                    final DefaultTableModel placeholder = tableModel("Results");
                    placeholder.addRow(new Object[] { "Results will appear here after consultation." });
                    table.setModel(placeholder);
                }),
                PanelUi.primary("Consult", e -> runSafely(() -> {
                    final WeatherData data = new ConsultWeatherDataController().consultWeatherData(required(day, "Day"), required(area, "Air control area code"))
                            .orElseThrow(() -> new IllegalArgumentException("No weather data found."));
                    final DefaultTableModel model = tableModel("Day", "Area", "Provider", "Parameter", "Value", "Unit", "Altitude", "Coordinates");
                    for (WeatherMeasurement m : data.measurements()) {
                        model.addRow(new Object[] { data.day(), data.airControlAreaCode(), m.providerType(), m.parameter(), m.value(), m.unit(), m.altitude(), m.coordinates() });
                    }
                    table.setModel(model);
                }))
        ), BorderLayout.SOUTH);
    }
}

final class ImportBulkWeatherDataPanel extends JPanel {
    ImportBulkWeatherDataPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextField file = PanelUi.text("Choose a CSV or XLSX file");
        final JLabel read = PanelUi.bigValue("0");
        final JLabel imported = PanelUi.bigValue("0");
        final JLabel errors = PanelUi.bigValue("0");
        final JTable table = new JTable(tableModel("Row", "Status", "Message", "Details"));
        PanelUi.table(table);

        final JPanel importFile = PanelUi.card(new BorderLayout(0, 18));
        importFile.add(PanelUi.sectionHeader("Import File", "Upload a CSV or XLSX file with weather observations.", "upload"), BorderLayout.NORTH);
        final JPanel chooser = PanelUi.dropZone(file);
        importFile.add(chooser, BorderLayout.CENTER);

        final JPanel summary = PanelUi.card(new GridBagLayout());
        final GridBagConstraints sc = new GridBagConstraints();
        sc.gridx = 0;
        sc.gridy = 0;
        sc.gridwidth = 2;
        sc.anchor = GridBagConstraints.WEST;
        sc.insets = new Insets(0, 0, 18, 0);
        summary.add(PanelUi.sectionHeader("Import Summary", "Preview validation results after import.", "chart"), sc);
        sc.gridwidth = 1;
        sc.fill = GridBagConstraints.BOTH;
        sc.weightx = 1;
        sc.gridy++;
        sc.insets = new Insets(0, 0, 14, 14);
        summary.add(PanelUi.summaryCard("Read records", read, "grid", UiTheme.BLUE, "Total rows read"), sc);
        sc.gridx = 1;
        sc.insets = new Insets(0, 0, 14, 0);
        summary.add(PanelUi.summaryCard("Imported", imported, "check", PanelUi.SUCCESS, "Successfully imported"), sc);
        sc.gridx = 0;
        sc.gridy++;
        sc.insets = new Insets(0, 0, 0, 14);
        summary.add(PanelUi.summaryCard("Errors", errors, "warning", PanelUi.ERROR, "Rows skipped"), sc);

        final JPanel top = new JPanel(new GridLayout(1, 2, 22, 0));
        top.setOpaque(false);
        top.add(importFile);
        top.add(summary);

        final JPanel card = PanelUi.card(new BorderLayout(0, 18));
        card.add(top, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("Choose file", e -> {
                    final JFileChooser chooserFile = new JFileChooser();
                    chooserFile.setFileFilter(new FileNameExtensionFilter("CSV/XLSX files", "csv", "xlsx"));
                    if (chooserFile.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        file.setText(chooserFile.getSelectedFile().toPath().toString());
                    }
                }),
                PanelUi.primary("Import", e -> runSafely(() -> {
                    final BulkWeatherImportResult result;
                    try {
                        result = new ImportBulkWeatherDataController().importFromFile(Path.of(required(file, "File")));
                    } catch (IOException ex) {
                        throw new IllegalArgumentException(ex.getMessage(), ex);
                    }
                    read.setText(String.valueOf(result.readRecords()));
                    imported.setText(String.valueOf(result.importedRecords()));
                    errors.setText(String.valueOf(result.errors().size()));

                    final DefaultTableModel model = tableModel("Row", "Status", "Message", "Details");
                    for (BulkWeatherImportError error : result.errors()) {
                        model.addRow(new Object[] { error.sourceLine(), "Error", error.message(), "Check imported file row" });
                    }
                    if (result.errors().isEmpty()) {
                        model.addRow(new Object[] { "-", "Imported", "Weather data imported successfully.", "No validation errors" });
                    }
                    table.setModel(model);
                }))
        ), BorderLayout.SOUTH);
    }
}

final class GenerateMonthlyReportPanel extends JPanel {
    GenerateMonthlyReportPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JSpinner year = PanelUi.intSpinner(2026, 1900, 3000);
        final JSpinner month = PanelUi.intSpinner(1, 1, 12);
        final JTextArea out = PanelUi.outputArea("Report preview will appear here.");

        final JPanel form = PanelUi.formCard("Report Parameters", "Select the reporting period.");
        PanelUi.addField(form, 0, 0, "Year *", year);
        PanelUi.addField(form, 0, 1, "Month *", month);

        final JPanel card = PanelUi.card(new BorderLayout(0, 18));
        card.add(PanelUi.split(form, PanelUi.sideCard("Report Types", "chart",
                new PanelUi.Tip("check", "Operational summary", "Monthly operational overview.", PanelUi.SUCCESS),
                new PanelUi.Tip("info", "Consistent layout", "Foundational format for future reports.", UiTheme.BLUE))), BorderLayout.NORTH);
        card.add(PanelUi.resultPanel("Generated Report", out), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> out.setText("")),
                PanelUi.primary("Generate", e -> runSafely(() -> out.setText(new GenerateMonthlyReportController().generateMonthlyReportText(intValue(year), intValue(month)))))
        ), BorderLayout.SOUTH);
    }
}

final class FlightPlansOverviewPanel extends JPanel {
    FlightPlansOverviewPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextField search = PanelUi.text("Search flight plans, aircraft or pilot...");
        final JTable table = new JTable(tableModel("Flight Plan", "Flight", "Route", "Aircraft", "Status", "Actions"));
        PanelUi.table(table);

        final JPanel card = PanelUi.card(new BorderLayout(0, 16));
        final JPanel filters = PanelUi.card(new GridBagLayout());
        PanelUi.addCompactFilter(filters, 0, search);
        card.add(filters, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("Clear", e -> search.setText("")),
                PanelUi.primary("Refresh", e -> JOptionPane.showMessageDialog(this, "Flight plan data is available in the Create/Test tabs according to your role."))
        ), BorderLayout.SOUTH);
    }
}

final class GenerateSimulationReportPanel extends JPanel {
    GenerateSimulationReportPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextField id = PanelUi.text("Simulation identifier");
        final JCheckBox passed = new JCheckBox("Passed validation");
        passed.setOpaque(false);
        final JTextArea flights = PanelUi.outputArea("FL001,COMPLETED");
        final JTextArea violations = PanelUi.outputArea("2026-01-01T10:00,41.15 -8.61,Description");
        final JTextField dir = PanelUi.text("reports");
        dir.setText("reports");

        final JPanel form = PanelUi.formCard("Simulation Report", "Generate and save a simulation report.");
        PanelUi.addField(form, 0, 0, "Simulation identifier *", id);
        PanelUi.addField(form, 0, 1, "Output directory *", dir);
        PanelUi.addField(form, 1, 0, "Result", passed);
        PanelUi.addField(form, 2, 0, "Flight results", new JScrollPane(flights));
        PanelUi.addField(form, 2, 1, "Safety violations", new JScrollPane(violations));

        final JPanel side = PanelUi.sideCard("Report Contents", "chart",
                new PanelUi.Tip("check", "Flight statuses", "Include each flight execution status.", PanelUi.SUCCESS),
                new PanelUi.Tip("warning", "Safety violations", "List timestamps, positions and details.", PanelUi.WARNING),
                new PanelUi.Tip("info", "Saved file", "The complete report is saved to a file.", UiTheme.BLUE));

        add(PanelUi.split(form, side), BorderLayout.CENTER);
        add(PanelUi.actions(
                PanelUi.secondary("Choose directory", e -> {
                    final JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        dir.setText(chooser.getSelectedFile().toPath().toString());
                    }
                }),
                PanelUi.primary("Generate", e -> runSafely(() -> {
                    final Path path = new GenerateSimulationReportController().generateSimulationReportFile(required(id, "Simulation identifier"), passed.isSelected(), parseFlights(flights.getText()), parseViolations(violations.getText()), Path.of(required(dir, "Output directory")));
                    JOptionPane.showMessageDialog(this, "Report created: " + path);
                }))
        ), BorderLayout.SOUTH);
    }
}

final class RegisterPilotPanel extends JPanel {
    RegisterPilotPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final RegisterPilotController controller = new RegisterPilotController();
        final JTextField id = PanelUi.text("Pilot ID");
        final JTextField issuedAt = PanelUi.text("DD-MM-YYYY");
        final JTextField validUntil = PanelUi.text("DD-MM-YYYY");
        final JTextField username = PanelUi.text("Username");
        final JTextField firstName = PanelUi.text("First name");
        final JTextField lastName = PanelUi.text("Last name");
        final JTextField email = PanelUi.text("Email");
        final JPasswordField password = PanelUi.password("Password");
        final JTextField phone = PanelUi.text("Phone number");
        final JList<String> models = new JList<>();
        final List<String> options = new ArrayList<>();
        for (AircraftModel model : controller.getAllAircraftModels()) {
            options.add(model.identity().toString());
        }
        models.setListData(new Vector<>(options));
        models.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        models.setVisibleRowCount(5);

        final JPanel form = PanelUi.formCard("Pilot Certification", "Register a certified pilot and system user.");
        PanelUi.addField(form, 0, 0, "Pilot ID *", id);
        PanelUi.addField(form, 0, 1, "Phone number *", phone);
        PanelUi.addField(form, 1, 0, "Certification issued at *", issuedAt);
        PanelUi.addField(form, 1, 1, "Certification valid until *", validUntil);
        PanelUi.addSection(form, 2, "User Account", "user");
        PanelUi.addField(form, 3, 0, "Username *", username);
        PanelUi.addField(form, 3, 1, "Password *", password);
        PanelUi.addField(form, 4, 0, "First name *", firstName);
        PanelUi.addField(form, 4, 1, "Last name *", lastName);
        PanelUi.addField(form, 5, 0, "Email *", email);
        PanelUi.addField(form, 5, 1, "Certified aircraft models *", new JScrollPane(models));

        final JPanel side = PanelUi.sideCard("Pilot Rules", "plane",
                new PanelUi.Tip("check", "Certification", "Pilot must be certified for at least one aircraft model.", PanelUi.SUCCESS),
                new PanelUi.Tip("info", "System user", "The pilot can authenticate into the system.", UiTheme.BLUE),
                new PanelUi.Tip("warning", "Validity", "Certification dates must be valid.", PanelUi.WARNING));

        final JScrollPane scroll = new JScrollPane(PanelUi.split(form, side));
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("Cancel", e -> PanelUi.clear(id, issuedAt, validUntil, username, firstName, lastName, email, password, phone)),
                PanelUi.primary("Register Pilot", e -> runSafely(() -> {
                    final List<String> selectedModels = models.getSelectedValuesList();
                    if (selectedModels.isEmpty()) {
                        throw new IllegalArgumentException("At least one aircraft model is required.");
                    }
                    controller.registerPilot(required(id, "Pilot ID"), required(issuedAt, "Certification issued at"), required(validUntil, "Certification valid until"), required(username, "Username"), required(firstName, "First name"), required(lastName, "Last name"), required(email, "Email"), passwordText(password, "Password"), required(phone, "Phone number"), String.join(",", selectedModels));
                    JOptionPane.showMessageDialog(this, "Pilot registered successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class AddPilotPanel extends JPanel {
    AddPilotPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextField id = PanelUi.text("Pilot ID");
        final JTextArea out = PanelUi.outputArea("Available pilots will appear here.");

        final JPanel form = PanelUi.formCard("Add Pilot to Roster", "Associate an existing pilot to the company.");
        PanelUi.addField(form, 0, 0, "Pilot ID *", id);

        final JPanel card = PanelUi.card(new BorderLayout(0, 18));
        card.add(form, BorderLayout.NORTH);
        card.add(PanelUi.resultPanel("Available Pilots", out), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("List available pilots", e -> runSafely(() -> out.setText(joinLines(new RegisterPilotController().getAllPilots())))),
                PanelUi.primary("Add Pilot", e -> runSafely(() -> {
                    new AddPilotController().addPilot(required(id, "Pilot ID"));
                    JOptionPane.showMessageDialog(this, "Pilot added successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class ListPilotRosterPanel extends JPanel {
    ListPilotRosterPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextArea out = PanelUi.outputArea("Pilot roster will appear here.");
        add(PanelUi.resultPanel("Pilot Roster", out), BorderLayout.CENTER);
        add(PanelUi.actions(
                PanelUi.secondary("Clear", e -> out.setText("")),
                PanelUi.primary("List Roster", e -> runSafely(() -> out.setText(joinLines(new ListPilotRosterController().listPilotRoster()))))
        ), BorderLayout.SOUTH);
    }
}

final class DeactivatePilotPanel extends JPanel {
    DeactivatePilotPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);

        final JTextField id = PanelUi.text("Pilot ID");
        final JTextArea out = PanelUi.outputArea("Roster preview will appear here.");

        final JPanel form = PanelUi.formCard("Deactivate Pilot", "Make a pilot inactive in the company roster.");
        PanelUi.addField(form, 0, 0, "Pilot ID *", id);

        final JPanel card = PanelUi.card(new BorderLayout(0, 18));
        card.add(PanelUi.split(form, PanelUi.sideCard("Restriction", "warning",
                new PanelUi.Tip("warning", "Assigned plans", "Cannot deactivate a pilot with flight plans assigned.", PanelUi.WARNING),
                new PanelUi.Tip("info", "Roster status", "Inactive pilots no longer appear in the active roster.", UiTheme.BLUE))), BorderLayout.NORTH);
        card.add(PanelUi.resultPanel("Current Roster", out), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        add(PanelUi.actions(
                PanelUi.secondary("List roster", e -> runSafely(() -> out.setText(joinLines(new ListPilotRosterController().listPilotRoster())))),
                PanelUi.primary("Deactivate", e -> runSafely(() -> {
                    new DeactivatePilotController().deactivatePilot(required(id, "Pilot ID"));
                    JOptionPane.showMessageDialog(this, "Pilot deactivated successfully.");
                }))
        ), BorderLayout.SOUTH);
    }
}

final class WeatherParameterRenderer extends javax.swing.DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(final javax.swing.JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        final String parameter = String.valueOf(value);
        final java.awt.Color color = parameterColor(parameter);
        label.setIcon(new LineIcon(parameterIcon(parameter), 18, color));
        label.setIconTextGap(8);
        label.setForeground(isSelected ? java.awt.Color.WHITE : color.darker());
        if (!isSelected) {
            label.setBackground(parameterBackground(parameter));
        }
        label.setOpaque(true);
        return label;
    }

    private String parameterIcon(final String parameter) {
        if ("TEMPERATURE".equals(parameter)) return "thermometer";
        if ("WIND_SPEED".equals(parameter)) return "wind";
        if ("WIND_DIRECTION".equals(parameter)) return "compass";
        if ("PRESSURE".equals(parameter)) return "gauge";
        if ("HUMIDITY".equals(parameter)) return "droplet";
        return "cloud";
    }

    private java.awt.Color parameterColor(final String parameter) {
        if ("TEMPERATURE".equals(parameter)) return new java.awt.Color(185, 28, 28);
        if ("WIND_SPEED".equals(parameter)) return new java.awt.Color(37, 99, 235);
        if ("WIND_DIRECTION".equals(parameter)) return new java.awt.Color(79, 70, 229);
        if ("PRESSURE".equals(parameter)) return new java.awt.Color(15, 118, 110);
        if ("HUMIDITY".equals(parameter)) return new java.awt.Color(2, 132, 199);
        return UiTheme.BLUE;
    }

    private java.awt.Color parameterBackground(final String parameter) {
        if ("TEMPERATURE".equals(parameter)) return new java.awt.Color(254, 226, 226);
        if ("WIND_SPEED".equals(parameter)) return new java.awt.Color(219, 234, 254);
        if ("WIND_DIRECTION".equals(parameter)) return new java.awt.Color(224, 231, 255);
        if ("PRESSURE".equals(parameter)) return new java.awt.Color(204, 251, 241);
        if ("HUMIDITY".equals(parameter)) return new java.awt.Color(224, 242, 254);
        return UiTheme.BLUE_SOFT;
    }
}

final class PanelUi {
    static final java.awt.Color SUCCESS = new java.awt.Color(22, 163, 74);
    static final java.awt.Color WARNING = new java.awt.Color(245, 158, 11);
    static final java.awt.Color ERROR = new java.awt.Color(220, 38, 38);

    private PanelUi() {}

    static JPanel card(final java.awt.LayoutManager layout) {
        final JPanel p = new RoundedPanel(18, UiTheme.CARD, UiTheme.BORDER, true);
        p.setLayout(layout);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        return p;
    }

    static JPanel formCard(final String title, final String subtitle) {
        final JPanel p = card(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        p.add(sectionHeader(title, subtitle, "grid"), c);
        return p;
    }

    static JLabel sectionHeader(final String title, final String subtitle, final String icon) {
        final JLabel label = new JLabel("<html><div style='font-size:18px'><b>" + title + "</b></div><div style='font-size:12px;color:#64748B;padding-top:4px'>" + subtitle + "</div></html>", new LineIcon(icon, 28, UiTheme.BLUE), SwingConstants.LEFT);
        label.setIconTextGap(14);
        label.setForeground(UiTheme.TEXT);
        return label;
    }

    static JTextField text(final String placeholder) {
        final JTextField field = new JTextField();
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("FlatLaf.style", "arc: 10");
        field.setPreferredSize(new Dimension(260, 42));
        field.setMinimumSize(new Dimension(120, 42));
        return field;
    }

    static JPasswordField password(final String placeholder) {
        final JPasswordField field = new JPasswordField();
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("FlatLaf.style", "arc: 10");
        field.setPreferredSize(new Dimension(260, 42));
        return field;
    }

    static JSpinner spinner(final double value, final double min, final double max, final double step) {
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        spinner.putClientProperty("FlatLaf.style", "arc: 10");
        spinner.setPreferredSize(new Dimension(260, 42));
        return spinner;
    }

    static JSpinner intSpinner(final int value, final int min, final int max) {
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, 1));
        spinner.putClientProperty("FlatLaf.style", "arc: 10");
        spinner.setPreferredSize(new Dimension(260, 42));
        return spinner;
    }

    static void combo(final JComboBox<?> combo) {
        combo.putClientProperty("FlatLaf.style", "arc: 10");
        combo.setPreferredSize(new Dimension(260, 42));
        combo.setMinimumSize(new Dimension(120, 42));
    }

    static void addField(final JPanel form, final int row, final int col, final String label, final Component component) {
        final JPanel wrap = new JPanel(new BorderLayout(0, 7));
        wrap.setOpaque(false);
        wrap.add(label(label), BorderLayout.NORTH);
        wrap.add(component, BorderLayout.CENTER);

        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row + 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(row == 0 ? 24 : 0, col == 0 ? 0 : 24, 16, 0);
        form.add(wrap, c);
    }

    static void addSection(final JPanel form, final int row, final String title, final String icon) {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row + 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(10, 0, 18, 0);
        final JLabel section = new JLabel(title, new LineIcon(icon, 18, UiTheme.BLUE), SwingConstants.LEFT);
        section.setIconTextGap(10);
        section.setForeground(UiTheme.TEXT);
        section.setFont(section.getFont().deriveFont(Font.BOLD, 15f));
        form.add(section, c);
    }

    static void addInfoStrip(final JPanel form, final int row, final String text) {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row + 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(10, 0, 0, 0);
        final JPanel info = new RoundedPanel(10, new java.awt.Color(239, 246, 255), new java.awt.Color(147, 197, 253), false);
        info.setLayout(new BorderLayout(10, 0));
        info.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        final JLabel label = new JLabel(text, new LineIcon("info", 18, UiTheme.BLUE), SwingConstants.LEFT);
        label.setIconTextGap(10);
        label.setForeground(UiTheme.MUTED);
        info.add(label, BorderLayout.CENTER);
        form.add(info, c);
    }

    static JLabel label(final String text) {
        final boolean required = text.contains("*");
        final String clean = text.replace("*", "").trim();
        final JLabel label = new JLabel(required ? "<html>" + clean + " <span style='color:#EF4444'>*</span></html>" : clean);
        label.setForeground(UiTheme.TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        return label;
    }

    static JPanel split(final JPanel main, final JPanel side) {
        final JPanel p = new JPanel(new BorderLayout(22, 0));
        p.setOpaque(false);
        p.add(main, BorderLayout.CENTER);
        p.add(side, BorderLayout.EAST);
        return p;
    }

    static JPanel wizard(final String title, final JPanel form, final JPanel preview, final Step... steps) {
        final JPanel outer = card(new BorderLayout(0, 0));
        final JPanel body = new JPanel(new BorderLayout(24, 0));
        body.setOpaque(false);
        body.add(stepsPanel(steps), BorderLayout.WEST);
        body.add(form, BorderLayout.CENTER);
        body.add(preview, BorderLayout.EAST);
        outer.add(body, BorderLayout.CENTER);
        return outer;
    }

    static JPanel stepsPanel(final Step... steps) {
        final StepRailPanel p = new StepRailPanel();
        p.setPreferredSize(new Dimension(210, 430));
        p.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 18));

        for (Step step : steps) {
            p.addStep(step);
        }

        return p;
    }

    private static final class StepRailPanel extends JPanel {
        private final java.util.List<StepRow> rows = new ArrayList<>();

        StepRailPanel() {
            super(new GridBagLayout());
            setOpaque(false);
        }

        void addStep(final Step step) {
            final StepRow row = new StepRow(step);
            rows.add(row);

            final GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = rows.size() - 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.weightx = 1;
            c.insets = new Insets(rows.size() == 1 ? 0 : 30, 0, 0, 0);

            add(row, c);
        }

        @Override
        protected void paintComponent(final java.awt.Graphics graphics) {
            super.paintComponent(graphics);

            if (rows.size() < 2) {
                return;
            }

            final java.awt.Graphics2D g = (java.awt.Graphics2D) graphics.create();
            g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

            final int x = 24;
            final int firstY = rows.get(0).getY() + 23;
            final int lastY = rows.get(rows.size() - 1).getY() + 23;

            g.setColor(new java.awt.Color(203, 213, 225));
            g.setStroke(new java.awt.BasicStroke(
                    1.3f,
                    java.awt.BasicStroke.CAP_ROUND,
                    java.awt.BasicStroke.JOIN_ROUND,
                    1f,
                    new float[] { 4f, 8f },
                    0f
            ));

            g.drawLine(x, firstY, x, lastY);
            g.dispose();
        }
    }

    private static final class StepRow extends JPanel {
        StepRow(final Step step) {
            super(new BorderLayout(12, 0));
            setOpaque(false);

            final JLabel badge = new JLabel(new StepBadgeIcon(iconForStep(step.title), step.active));
            badge.setPreferredSize(new Dimension(48, 48));
            badge.setHorizontalAlignment(SwingConstants.CENTER);
            badge.setVerticalAlignment(SwingConstants.TOP);

            final JPanel textPanel = new JPanel(new GridBagLayout());
            textPanel.setOpaque(false);

            final GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;

            final JLabel title = new JLabel("<html>" + step.title + "</html>");
            title.setForeground(step.active ? UiTheme.BLUE : UiTheme.TEXT);
            title.setFont(title.getFont().deriveFont(Font.BOLD, step.active ? 13f : 12.5f));
            textPanel.add(title, c);

            c.gridy++;
            c.insets = new Insets(4, 0, 0, 0);

            final JLabel subtitle = new JLabel("<html><span style='color:#64748B'>" + step.subtitle + "</span></html>");
            subtitle.setForeground(UiTheme.MUTED);
            subtitle.setFont(subtitle.getFont().deriveFont(11f));
            textPanel.add(subtitle, c);

            add(badge, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        private static String iconForStep(final String title) {
            final String value = title.toLowerCase();

            if (value.contains("general")) {
                return "grid";
            }
            if (value.contains("location")) {
                return "map";
            }
            if (value.contains("control") || value.contains("area")) {
                return "cloud";
            }
            if (value.contains("review")) {
                return "check";
            }

            return "grid";
        }
    }

    private static final class StepBadgeIcon implements javax.swing.Icon {
        private static final int SIZE = 44;

        private final String icon;
        private final boolean active;

        StepBadgeIcon(final String icon, final boolean active) {
            this.icon = icon;
            this.active = active;
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }

        @Override
        public void paintIcon(final Component c, final java.awt.Graphics graphics, final int x, final int y) {
            final java.awt.Graphics2D g = (java.awt.Graphics2D) graphics.create();

            g.translate(x, y);
            g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

            final int circleSize = 38;
            final int circleX = 3;
            final int circleY = 3;

            if (active) {
                g.setColor(UiTheme.BLUE);
                g.fillOval(circleX, circleY, circleSize, circleSize);

                g.setColor(new java.awt.Color(37, 99, 235, 50));
                g.setStroke(new java.awt.BasicStroke(4f));
                g.drawOval(circleX + 1, circleY + 1, circleSize - 2, circleSize - 2);
            } else {
                g.setColor(UiTheme.CARD);
                g.fillOval(circleX, circleY, circleSize, circleSize);

                g.setColor(UiTheme.BORDER);
                g.setStroke(new java.awt.BasicStroke(1.3f));
                g.drawOval(circleX, circleY, circleSize, circleSize);
            }

            final int iconSize = 17;
            final java.awt.Color iconColor = active ? java.awt.Color.WHITE : UiTheme.MUTED;

            final LineIcon lineIcon = new LineIcon(icon, iconSize, iconColor);
            final int iconX = circleX + (circleSize - iconSize) / 2;
            final int iconY = circleY + (circleSize - iconSize) / 2;

            lineIcon.paintIcon(c, g, iconX, iconY);

            g.dispose();
        }
    }

    static JPanel sideCard(final String title, final String icon, final Tip... tips) {
        final JPanel p = new RoundedPanel(16, new java.awt.Color(248, 250, 252), UiTheme.BORDER, true);
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        p.setPreferredSize(new Dimension(310, 420));

        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;

        final JLabel head = new JLabel(title, new LineIcon(icon, 22, UiTheme.BLUE), SwingConstants.LEFT);
        head.setIconTextGap(12);
        head.setForeground(UiTheme.TEXT);
        head.setFont(head.getFont().deriveFont(Font.BOLD, 16f));
        p.add(head, c);

        for (Tip tip : tips) {
            c.gridy++;
            c.insets = new Insets(22, 0, 0, 0);
            p.add(tip(tip), c);
        }

        c.gridy++;
        c.weighty = 1;
        p.add(Box.createVerticalGlue(), c);
        return p;
    }

    static JPanel tip(final Tip tip) {
        final JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        final JLabel icon = new JLabel(new LineIcon(tip.icon, 20, tip.color));
        icon.setPreferredSize(new Dimension(30, 30));
        final JLabel copy = new JLabel("<html><b>" + tip.title + "</b><br><span style='color:#64748B'>" + tip.description + "</span></html>");
        copy.setForeground(UiTheme.TEXT);
        p.add(icon, BorderLayout.WEST);
        p.add(copy, BorderLayout.CENTER);
        return p;
    }

    static JPanel previewCard(final String title, final String help, final String[][] rows) {
        final JPanel p = new RoundedPanel(16, new java.awt.Color(248, 250, 252), UiTheme.BORDER, true);
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.setPreferredSize(new Dimension(300, 420));

        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        final JLabel head = new JLabel(title, new LineIcon("map", 22, UiTheme.BLUE), SwingConstants.LEFT);
        head.setIconTextGap(10);
        head.setForeground(UiTheme.BLUE);
        head.setFont(head.getFont().deriveFont(Font.BOLD, 15f));
        p.add(head, c);

        c.gridy++;
        c.insets = new Insets(10, 0, 16, 0);
        final JLabel text = new JLabel("<html>" + help + "</html>");
        text.setForeground(UiTheme.MUTED);
        p.add(text, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 20, 0);
        final JLabel map = new JLabel(new LineIcon("map", 86, new java.awt.Color(147, 197, 253)), SwingConstants.CENTER);
        map.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(map, c);

        for (String[] row : rows) {
            c.gridy++;
            c.insets = new Insets(6, 0, 0, 0);
            p.add(row(row[0], row[1]), c);
        }

        c.gridy++;
        c.weighty = 1;
        p.add(Box.createVerticalGlue(), c);
        return p;
    }

    static JPanel row(final String label, final String value) {
        final JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        final JLabel l = new JLabel(label);
        l.setForeground(UiTheme.MUTED);
        final JLabel v = new JLabel(value);
        v.setForeground(UiTheme.TEXT);
        v.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    static JPanel actions(final JButton... buttons) {
        final JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 18));
        p.setOpaque(false);
        for (JButton b : buttons) {
            p.add(b);
        }
        return p;
    }

    static JButton primary(final String text, final java.awt.event.ActionListener listener) {
        final JButton b = primaryButton(text);
        b.setPreferredSize(new Dimension(Math.max(135, text.length() * 12), 42));
        b.addActionListener(listener);
        return b;
    }

    static JButton secondary(final String text, final java.awt.event.ActionListener listener) {
        final JButton b = secondaryButton(text);
        b.setPreferredSize(new Dimension(Math.max(120, text.length() * 10), 42));
        b.addActionListener(listener);
        return b;
    }

    static void addCompactFilter(final JPanel panel, final int col, final Component component) {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = col == 0 ? 1.2 : 0.8;
        c.insets = new Insets(0, col == 0 ? 0 : 14, 0, 0);
        panel.add(component, c);
    }

    static void table(final JTable table) {
        table.setRowHeight(38);
        table.setShowVerticalLines(false);
        table.setGridColor(UiTheme.BORDER);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new java.awt.Color(248, 250, 252));
        table.getTableHeader().setForeground(UiTheme.TEXT);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 13f));
    }

    static JTextArea outputArea(final String placeholder) {
        final JTextArea area = new JTextArea(placeholder);
        area.setEditable(true);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(UiTheme.TEXT);
        area.setBackground(java.awt.Color.WHITE);
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return area;
    }

    static JPanel resultPanel(final String title, final JTextArea out) {
        final JPanel p = card(new BorderLayout(0, 0));
        final JLabel head = new JLabel(title);
        head.setForeground(UiTheme.TEXT);
        head.setFont(head.getFont().deriveFont(Font.BOLD, 16f));
        head.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        p.add(head, BorderLayout.NORTH);
        final JScrollPane scroll = new JScrollPane(out);
        scroll.setPreferredSize(new Dimension(720, 260));
        scroll.setBorder(BorderFactory.createLineBorder(UiTheme.BORDER));
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    static JPanel dropZone(final JTextField file) {
        final JPanel p = new RoundedPanel(14, new java.awt.Color(248, 250, 252), UiTheme.BORDER, false);
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        p.add(file, c);
        c.gridy++;
        c.insets = new Insets(16, 0, 0, 0);
        final JLabel hint = new JLabel("Supported formats: .csv, .xlsx");
        hint.setForeground(UiTheme.MUTED);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(hint, c);
        return p;
    }

    static JLabel bigValue(final String text) {
        final JLabel l = new JLabel(text);
        l.setForeground(UiTheme.TEXT);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 26f));
        return l;
    }

    static JPanel summaryCard(final String label, final JLabel value, final String icon, final java.awt.Color color, final String subtext) {
        final JPanel p = new RoundedPanel(14, UiTheme.CARD, UiTheme.BORDER, true);
        p.setLayout(new BorderLayout(14, 0));
        p.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        final JLabel iconLabel = new JLabel(new LineIcon(icon, 28, color), SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(50, 50));
        p.add(iconLabel, BorderLayout.WEST);

        final JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        final JLabel top = new JLabel(label);
        top.setForeground(UiTheme.MUTED);
        top.setFont(top.getFont().deriveFont(Font.BOLD, 13f));
        final JLabel bottom = new JLabel(subtext);
        bottom.setForeground(color);
        copy.add(top);
        copy.add(Box.createVerticalStrut(6));
        copy.add(value);
        copy.add(Box.createVerticalStrut(4));
        copy.add(bottom);
        p.add(copy, BorderLayout.CENTER);
        return p;
    }

    static void clear(final JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    static final class Tip {
        final String icon;
        final String title;
        final String description;
        final java.awt.Color color;

        Tip(final String icon, final String title, final String description, final java.awt.Color color) {
            this.icon = icon;
            this.title = title;
            this.description = description;
            this.color = color;
        }
    }

    static final class Step {
        final String title;
        final String subtitle;
        final boolean active;

        Step(final String title, final String subtitle, final boolean active) {
            this.title = title;
            this.subtitle = subtitle;
            this.active = active;
        }
    }
}
