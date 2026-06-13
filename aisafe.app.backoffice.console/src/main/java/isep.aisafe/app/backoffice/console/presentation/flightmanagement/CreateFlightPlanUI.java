package isep.aisafe.app.backoffice.console.presentation.flightmanagement;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.flightmanagement.CreateFlightPlanController;
import isep.aisafe.flightplan.domain.FlightPlan;

import java.util.Scanner;

/**
 * User interface for creating flight plans.
 */
public class CreateFlightPlanUI {

    private final CreateFlightPlanController controller = new CreateFlightPlanController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public CreateFlightPlanUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner scanner used to read user input
     */
    public CreateFlightPlanUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the flight plan creation form.
     */
    public void show() {
        System.out.println();
        ConsoleStyle.printSection("Create Flight Plan");

        final String flightPlanIdentifier = readRequiredText("Flight plan identifier: ");
        final String flightDesignator = readRequiredText("Flight designator: ").toUpperCase();
        final String aircraftRegistration = readRequiredText("Aircraft registration: ").toUpperCase();
        final double fuelAmount = readPositiveDouble("Fuel amount: ");
        final String fuelUnit = readRequiredText("Fuel unit (e.g. KG): ").toUpperCase();

        final FlightPlan flightPlan = controller.createFlightPlan(
                flightPlanIdentifier,
                flightDesignator,
                aircraftRegistration,
                fuelAmount,
                fuelUnit
        );

        ConsoleStyle.printSuccess("Flight plan created successfully: " + flightPlan.identity());
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

    private double readPositiveDouble(final String prompt) {
        while (true) {
            ConsoleStyle.printPrompt(prompt);
            final String value = scanner.nextLine().trim();

            try {
                final double number = Double.parseDouble(value);
                if (number > 0) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
                // Prompt again below.
            }

            ConsoleStyle.printWarning("Enter a number greater than zero.");
        }
    }
}
