package isep.aisafe.app.backoffice.console.presentation.report;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.report.GenerateSimulationReportController;
import isep.aisafe.report.domain.SimulationFlightResult;
import isep.aisafe.report.domain.SimulationSafetyViolation;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * User interface for generating simulation reports.
 */
public class GenerateSimulationReportUI {

    private final GenerateSimulationReportController controller = new GenerateSimulationReportController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public GenerateSimulationReportUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner scanner used to read input
     */
    public GenerateSimulationReportUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the simulation report generation form.
     */
    public void show() {
        System.out.println();
        ConsoleStyle.printSection("Generate Simulation Report");

        final String simulationIdentifier = readRequiredText("Simulation identifier: ");
        final boolean passedValidation = readYesNo("Did the scheduled flight plan pass validation? (y/n): ");
        final List<SimulationFlightResult> flightResults = readFlightResults();
        final List<SimulationSafetyViolation> safetyViolations = readSafetyViolations();
        final Path outputDirectory = Path.of(readTextWithDefault("Output directory [reports]: ", "reports"));

        final Path reportFile = controller.generateSimulationReportFile(
                simulationIdentifier,
                passedValidation,
                flightResults,
                safetyViolations,
                outputDirectory
        );

        ConsoleStyle.printInfo("Simulation report saved to: " + reportFile.toAbsolutePath());
    }

    private List<SimulationFlightResult> readFlightResults() {
        final int totalFlights = readNonNegativeInt("Number of simulated flights: ");
        final List<SimulationFlightResult> flightResults = new ArrayList<>();

        for (int index = 1; index <= totalFlights; index++) {
            ConsoleStyle.printInfo("Flight " + index + " of " + totalFlights);
            final String flightIdentifier = readRequiredText("Flight identifier: ");
            final String executionStatus = readRequiredText("Execution status: ");
            flightResults.add(new SimulationFlightResult(flightIdentifier, executionStatus));
        }

        return flightResults;
    }

    private List<SimulationSafetyViolation> readSafetyViolations() {
        final int totalViolations = readNonNegativeInt("Number of safety violations: ");
        final List<SimulationSafetyViolation> safetyViolations = new ArrayList<>();

        for (int index = 1; index <= totalViolations; index++) {
            ConsoleStyle.printInfo("Safety violation " + index + " of " + totalViolations);
            final LocalDateTime timestamp = readDateTime("Timestamp (YYYY-MM-DDTHH:MM): ");
            final String position = readRequiredText("Position: ");
            final String description = readRequiredText("Description: ");
            safetyViolations.add(new SimulationSafetyViolation(timestamp, position, description));
        }

        return safetyViolations;
    }

    private boolean readYesNo(final String prompt) {
        while (true) {
            final String value = readRequiredText(prompt).trim().toLowerCase();
            if ("y".equals(value) || "yes".equals(value)) {
                return true;
            }
            if ("n".equals(value) || "no".equals(value)) {
                return false;
            }
            ConsoleStyle.printWarning("Enter y or n.");
        }
    }

    private int readNonNegativeInt(final String prompt) {
        while (true) {
            final String value = readRequiredText(prompt);
            try {
                final int parsedValue = Integer.parseInt(value);
                if (parsedValue >= 0) {
                    return parsedValue;
                }
            } catch (NumberFormatException ignored) {
                // Prompt again below.
            }
            ConsoleStyle.printWarning("Enter a non-negative integer value.");
        }
    }

    private LocalDateTime readDateTime(final String prompt) {
        while (true) {
            final String value = readRequiredText(prompt);
            try {
                return LocalDateTime.parse(value);
            } catch (DateTimeParseException ignored) {
                ConsoleStyle.printWarning("Enter an ISO date-time, for example 2027-01-10T10:30.");
            }
        }
    }

    private String readTextWithDefault(final String prompt, final String defaultValue) {
        ConsoleStyle.printPrompt(prompt);
        final String value = scanner.nextLine().trim();
        if (value.isBlank()) {
            return defaultValue;
        }
        return value;
    }

    private String readRequiredText(final String prompt) {
        while (true) {
            ConsoleStyle.printPrompt(prompt);
            final String value = scanner.nextLine().trim();
            if (!value.isBlank()) {
                return value;
            }
            ConsoleStyle.printWarning("Value is required.");
        }
    }
}
