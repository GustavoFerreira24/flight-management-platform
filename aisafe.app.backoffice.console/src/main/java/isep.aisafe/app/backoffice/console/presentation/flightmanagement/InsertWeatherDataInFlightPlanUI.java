package isep.aisafe.app.backoffice.console.presentation.flightmanagement;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.flightmanagement.InsertWeatherDataInFlightPlanController;
import isep.aisafe.flightplan.domain.FlightPlan;

import java.util.Scanner;

/**
 * User interface for adding existing weather data to a pilot flight plan.
 */
public class InsertWeatherDataInFlightPlanUI {

    private final InsertWeatherDataInFlightPlanController controller = new InsertWeatherDataInFlightPlanController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public InsertWeatherDataInFlightPlanUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner scanner used to read user input
     */
    public InsertWeatherDataInFlightPlanUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the weather data insertion form.
     */
    public void show() {
        try {
            ConsoleStyle.printSection("Insert Weather Data in Flight Plan");

            final String flightPlanIdentifier = readRequiredText("Flight plan identifier: ");
            final String weatherDataDay = readRequiredText("Weather data day (YYYY-MM-DD): ");
            final String airControlAreaCode = readRequiredText("Air control area code: ");

            final FlightPlan flightPlan = controller.insertWeatherData(
                    flightPlanIdentifier,
                    weatherDataDay,
                    airControlAreaCode
            );

            ConsoleStyle.printSuccess("Weather data inserted in flight plan successfully.");
            ConsoleStyle.printInfo("Flight plan: " + flightPlan.identity());
            ConsoleStyle.printInfo("Status: " + flightPlan.status());
            ConsoleStyle.printInfo("Weather data records associated: " + flightPlan.weatherDataCount());
        } catch (IllegalArgumentException e) {
            ConsoleStyle.printError("Invalid weather data insertion: " + safeMessage(e));
        } catch (RuntimeException e) {
            ConsoleStyle.printError("Could not insert weather data in flight plan: " + safeMessage(e));
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

    private String safeMessage(final Exception exception) {
        final String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            return exception.getClass().getSimpleName();
        }
        return message;
    }
}
