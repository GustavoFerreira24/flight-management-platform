package isep.aisafe.app.backoffice.console.presentation.flightmanagement;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.flightmanagement.CreateFlightController;
import isep.aisafe.flight.domain.Flight;

import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * User interface for creating scheduled flights.
 */
public class CreateFlightUI {

    private final CreateFlightController controller = new CreateFlightController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public CreateFlightUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner scanner used to read user input
     */
    public CreateFlightUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the flight creation form.
     */
    public void show() {
        System.out.println();
        ConsoleStyle.printSection("Create Flight");

        final String routeDesignator = readRouteDesignator();
        final String flightDesignator = readFlightDesignator();
        final String departureDay = readRequiredText("Departure day (yyyy-MM-dd): ");
        final String departureTime = readRequiredText("Departure time (HH:mm): ");

        try {
            final Flight flight = controller.createFlight(
                    routeDesignator,
                    flightDesignator,
                    departureDay,
                    departureTime
            );

            ConsoleStyle.printSuccess("Flight created successfully: " + flight.identity());
        } catch (DateTimeParseException e) {
            ConsoleStyle.printError("Invalid date/time. Use yyyy-MM-dd for the day and HH:mm for the time.");
        }
    }

    private String readRouteDesignator() {
        while (true) {
            final String routeDesignator = readRequiredText("Route designator: ").toUpperCase();

            if (routeDesignator.matches("[A-Z]{2}[0-9]{1,4}")) {
                return routeDesignator;
            }

            ConsoleStyle.printWarning("Invalid route designator. Example: TP123.");
        }
    }

    private String readFlightDesignator() {
        while (true) {
            final String flightDesignator = readRequiredText("Flight designator: ").toUpperCase();

            if (!flightDesignator.matches("[A-Z]{2}[0-9]{1,4}[A-Z]?")) {
                ConsoleStyle.printWarning("Invalid flight designator. Example: TP123 or TP123A.");
                continue;
            }

            if (controller.flightDesignatorExists(flightDesignator)) {
                ConsoleStyle.printWarning("Flight designator already exists. Please enter a different one.");
                continue;
            }

            return flightDesignator;
        }
    }

    private String readRequiredText(final String prompt) {
        while (true) {
            ConsoleStyle.printPrompt(prompt);
            final String value = scanner.nextLine().trim();

            if (!value.isBlank()) {
                return value;
            }

            ConsoleStyle.printWarning("This field is required.");
        }
    }
}