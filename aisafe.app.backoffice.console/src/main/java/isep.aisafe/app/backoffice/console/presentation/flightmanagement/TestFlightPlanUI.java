package isep.aisafe.app.backoffice.console.presentation.flightmanagement;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.flightmanagement.TestFlightPlanController;
import isep.aisafe.application.flightmanagement.TestFlightPlanResult;
import isep.aisafe.flightplan.domain.FlightPlan;

import java.util.List;
import java.util.Scanner;

/**
 * User interface for testing and validating flight plans.
 */
public class TestFlightPlanUI {

    private final TestFlightPlanController controller = new TestFlightPlanController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public TestFlightPlanUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner scanner used to read user input
     */
    public TestFlightPlanUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the flight plan test form.
     */
    public void show() {
        System.out.println();
        ConsoleStyle.printSection("Test / Validate Flight Plan");

        try {
            final List<FlightPlan> flightPlans = controller.authenticatedPilotFlightPlans();

            if (flightPlans.isEmpty()) {
                ConsoleStyle.printWarning("There are no flight plans available for testing for the authenticated pilot.");
                return;
            }

            printFlightPlans(flightPlans);

            final FlightPlan selectedFlightPlan = selectFlightPlan(flightPlans);
            final TestFlightPlanResult result = controller.testFlightPlan(
                    selectedFlightPlan.identity().toString()
            );

            printResult(result);
        } catch (IllegalArgumentException e) {
            ConsoleStyle.printError(e.getMessage());
        }
    }

    private void printFlightPlans(final List<FlightPlan> flightPlans) {
        ConsoleStyle.printInfo("Flight plans available for testing:");

        for (int index = 0; index < flightPlans.size(); index++) {
            final FlightPlan flightPlan = flightPlans.get(index);

            System.out.printf(
                    "%d - %s | Flight: %s | Aircraft: %s | Departure: %s | Status: %s%n",
                    index + 1,
                    flightPlan.identity(),
                    flightPlan.flightDesignator(),
                    flightPlan.aircraftRegistration(),
                    flightPlan.flightDepartureDateTime(),
                    flightPlan.status()
            );
        }
    }

    private FlightPlan selectFlightPlan(final List<FlightPlan> flightPlans) {
        while (true) {
            final String optionText = readRequiredText("Select flight plan: ");

            try {
                final int option = Integer.parseInt(optionText);

                if (option >= 1 && option <= flightPlans.size()) {
                    return flightPlans.get(option - 1);
                }
            } catch (NumberFormatException ignored) {
                // handled below with a user-friendly message
            }

            ConsoleStyle.printWarning("Select a valid flight plan option.");
        }
    }

    private void printResult(final TestFlightPlanResult result) {
        if (result.isSuccessful()) {
            ConsoleStyle.printSuccess(result.messages().getFirst());
            ConsoleStyle.printInfo("Status: " + result.status());

            for (int i = 1; i < result.messages().size(); i++) {
                ConsoleStyle.printInfo(result.messages().get(i));
            }

            return;
        }

        ConsoleStyle.printWarning("Flight plan validation failed. Current status: " + result.status());

        for (String message : result.messages()) {
            ConsoleStyle.printWarning(message);
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
