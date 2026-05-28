package isep.aisafe.app.backoffice.console.presentation.weather;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.weather.ConsultWeatherDataController;
import isep.aisafe.weatherdata.domain.WeatherData;
import isep.aisafe.weatherdata.domain.WeatherMeasurement;

import java.util.Optional;
import java.util.Scanner;

/**
 * User interface for consulting weather data by day and air control area.
 */
public class ConsultWeatherDataUI {

    private final ConsultWeatherDataController controller = new ConsultWeatherDataController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public ConsultWeatherDataUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner scanner used to read user input
     */
    public ConsultWeatherDataUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the weather data consultation form.
     */
    public void show() {
        try {
            ConsoleStyle.printSection("Consult Weather Data");

            final String day = askRequiredText("Weather data day (YYYY-MM-DD): ");
            final String areaCode = askRequiredText("Air control area code: ");

            final Optional<WeatherData> weatherData = controller.consultWeatherData(day, areaCode);
            if (weatherData.isEmpty()) {
                ConsoleStyle.printWarning("No weather data found for the selected day and air control area.");
                return;
            }

            printWeatherData(weatherData.get());
        } catch (IllegalArgumentException e) {
            ConsoleStyle.printError("Invalid weather data consultation: " + safeMessage(e));
        } catch (RuntimeException e) {
            ConsoleStyle.printError("Could not consult weather data: " + safeMessage(e));
        }
    }

    private String askRequiredText(final String prompt) {
        while (true) {
            ConsoleStyle.printPrompt(prompt);
            final String input = scanner.nextLine().trim();
            if (!input.isBlank()) {
                return input;
            }

            ConsoleStyle.printWarning("This value is required.");
        }
    }

    private void printWeatherData(final WeatherData weatherData) {
        ConsoleStyle.printSuccess("Weather data found.");
        ConsoleStyle.printInfo("Day: " + weatherData.day());
        ConsoleStyle.printInfo("Air control area: " + weatherData.airControlAreaCode());
        ConsoleStyle.printInfo("Measurements: " + weatherData.measurements().size());

        int index = 1;
        for (final WeatherMeasurement measurement : weatherData.measurements()) {
            System.out.println();
            System.out.println("Measurement " + index++);
            System.out.println("Provider: " + measurement.provider().name());
            System.out.println("Provider type: " + measurement.provider().type());
            System.out.println("Parameter: " + measurement.parameter());
            System.out.println("Value: " + measurement.value() + " " + measurement.unit());
            System.out.println("Altitude: " + measurement.altitude());
            System.out.println("Latitude: " + measurement.coordinates().latitude());
            System.out.println("Longitude: " + measurement.coordinates().longitude());
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
