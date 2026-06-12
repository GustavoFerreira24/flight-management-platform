package isep.aisafe.app.backoffice.console.presentation.flightmanagement;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.flightmanagement.ConsultFlightPlanTestReportsController;
import isep.aisafe.flightplan.domain.FlightPlanTestReport;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * User interface for consulting persisted flight plan validation reports.
 */
public class ConsultFlightPlanTestReportsUI {

    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ConsultFlightPlanTestReportsController controller = new ConsultFlightPlanTestReportsController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public ConsultFlightPlanTestReportsUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner scanner used to read user input
     */
    public ConsultFlightPlanTestReportsUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the persisted validation reports for the authenticated pilot.
     */
    public void show() {
        System.out.println();
        ConsoleStyle.printSection("Flight Plan Test Reports");

        try {
            final List<FlightPlanTestReport> reports = controller.authenticatedPilotReports();

            if (reports.isEmpty()) {
                ConsoleStyle.printWarning("There are no validation reports for the authenticated pilot.");
                return;
            }

            printReports(reports);
            printReportDetails(selectReport(reports));
        } catch (IllegalArgumentException e) {
            ConsoleStyle.printError(e.getMessage());
        }
    }

    private void printReports(final List<FlightPlanTestReport> reports) {
        ConsoleStyle.printInfo("Validation reports:");

        for (int index = 0; index < reports.size(); index++) {
            final FlightPlanTestReport report = reports.get(index);
            System.out.printf(
                    "%d - %s | Flight Plan: %s | Result: %s | Final Status: %s | Tested At: %s%n",
                    index + 1,
                    report.identity(),
                    report.flightPlan().identity(),
                    report.successful() ? "PASSED" : "FAILED",
                    report.finalStatus(),
                    DISPLAY_DATE_TIME.format(report.testedAt())
            );
        }
    }

    private FlightPlanTestReport selectReport(final List<FlightPlanTestReport> reports) {
        while (true) {
            final String optionText = readRequiredText("Select report: ");

            try {
                final int option = Integer.parseInt(optionText);

                if (option >= 1 && option <= reports.size()) {
                    return reports.get(option - 1);
                }
            } catch (NumberFormatException ignored) {
                // handled below with a user-friendly message
            }

            ConsoleStyle.printWarning("Select a valid report option.");
        }
    }

    private void printReportDetails(final FlightPlanTestReport report) {
        ConsoleStyle.printSection("Validation Report Details");
        ConsoleStyle.printInfo("Report: " + report.identity());
        ConsoleStyle.printInfo("Flight plan: " + report.flightPlan().identity());
        ConsoleStyle.printInfo("Flight: " + report.flightPlan().flightDesignator());
        ConsoleStyle.printInfo("Aircraft: " + report.flightPlan().aircraftRegistration());
        ConsoleStyle.printInfo("Pilot: " + report.flightPlan().pilotIdentifier());
        ConsoleStyle.printInfo("Tested by: " + report.testedBy());
        ConsoleStyle.printInfo("Tested at: " + DISPLAY_DATE_TIME.format(report.testedAt()));
        ConsoleStyle.printInfo("Initial status: " + report.initialStatus());
        ConsoleStyle.printInfo("Validation status: " + report.validationStatus());
        ConsoleStyle.printInfo("Final status: " + report.finalStatus());
        ConsoleStyle.printInfo("Result: " + (report.successful() ? "PASSED" : "FAILED"));
        ConsoleStyle.printInfo("Messages:");

        for (String message : report.messages()) {
            System.out.println("- " + message);
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
