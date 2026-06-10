package isep.aisafe.app.backoffice.console.presentation.flightmanagement;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.flightmanagement.TestFlightPlanController;
import isep.aisafe.application.flightmanagement.TestFlightPlanResult;
import isep.aisafe.flightplan.importer.dsl.FlightPlanDslError;

import java.io.IOException;
import java.nio.file.Path;
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

        final String flightPlanIdentifier = readRequiredText("Flight plan identifier: ");
        final Path dslFile = Path.of(readRequiredText("Flight DSL file path: "));

        try {
            final TestFlightPlanResult result = controller.testFlightPlan(flightPlanIdentifier, dslFile);
            printResult(result);
        } catch (IOException e) {
            ConsoleStyle.printError("Could not read Flight DSL file: " + e.getMessage());
        }
    }

    private void printResult(final TestFlightPlanResult result) {
        if (result.isSuccessful()) {
            ConsoleStyle.printSuccess(result.messages().getFirst());
            ConsoleStyle.printInfo("Test status: " + result.testStatus());
            return;
        }

        ConsoleStyle.printWarning("Flight plan test failed. Current status: " + result.testStatus());
        for (String message : result.messages()) {
            ConsoleStyle.printWarning(message);
        }
        for (FlightPlanDslError error : result.dslErrors()) {
            ConsoleStyle.printWarning(
                    error.type() + " at line " + error.line() + ", column " + error.column() + ": "
                            + error.message()
            );
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
