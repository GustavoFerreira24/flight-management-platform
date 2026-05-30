package isep.aisafe.app.backoffice.console.presentation.flightroute;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.flightroute.RegisterFlightRouteController;

import java.util.List;
import java.util.Scanner;

/**
 * User interface for registering flight routes for the authenticated company.
 */
public class RegisterFlightRouteUI {

    private final RegisterFlightRouteController controller = new RegisterFlightRouteController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public RegisterFlightRouteUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner the scanner used to read user input
     */
    public RegisterFlightRouteUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the flight route registration form.
     */
    public void show() {
        try {
            System.out.println();
            ConsoleStyle.printSection("Register Flight Route");

            printAvailableAirports();

            final String routeDesignator = readRouteDesignator();
            final String departureAirport = readAirportIcao("Departure airport ICAO: ");
            final String destinationAirport = readAirportIcao("Destination airport ICAO: ");

            controller.registerFlightRoute(routeDesignator, departureAirport, destinationAirport);

            ConsoleStyle.printSuccess("Flight route registered successfully.");
        } catch (RuntimeException e) {
            ConsoleStyle.printError("Could not register flight route: " + e.getMessage());
        }
    }

    private void printAvailableAirports() {
        final List<String> airports = controller.airportOptions();

        if (airports.isEmpty()) {
            ConsoleStyle.printWarning("No airports are currently registered.");
            return;
        }

        ConsoleStyle.printSection("Available Airports");

        for (int i = 0; i < airports.size(); i++) {
            System.out.println(ConsoleStyle.option(i + 1, airports.get(i)));
        }
    }

    private String readRouteDesignator() {
        while (true) {
            final String routeDesignator = readRequiredText(
                    "Route designator (2 company letters + 1 to 4 digits): ",
                    "Route designator is required."
            ).toUpperCase();

            if (!routeDesignator.matches("[A-Z]{2}[0-9]{1,4}")) {
                ConsoleStyle.printWarning("Invalid route designator. Example: TP123.");
                continue;
            }

            if (controller.routeDesignatorExists(routeDesignator)) {
                ConsoleStyle.printWarning("Route designator already exists. Please enter a different one.");
                continue;
            }

            return routeDesignator;
        }
    }

    private String readAirportIcao(final String prompt) {
        while (true) {
            final String icao = readRequiredText(prompt, "Airport ICAO code is required.").toUpperCase();

            if (icao.matches("[A-Z]{4}")) {
                return icao;
            }

            ConsoleStyle.printWarning("Invalid airport ICAO code. It must have exactly 4 letters.");
        }
    }

    private String readRequiredText(final String prompt, final String errorMessage) {
        while (true) {
            ConsoleStyle.printPrompt(prompt);
            final String value = scanner.nextLine().trim();

            if (!value.isBlank()) {
                return value;
            }

            ConsoleStyle.printWarning(errorMessage);
        }
    }
}
