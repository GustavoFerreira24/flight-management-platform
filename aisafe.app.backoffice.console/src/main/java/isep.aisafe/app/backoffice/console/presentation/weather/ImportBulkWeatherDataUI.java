package isep.aisafe.app.backoffice.console.presentation.weather;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.weather.BulkWeatherImportError;
import isep.aisafe.application.weather.BulkWeatherImportResult;
import isep.aisafe.application.weather.ImportBulkWeatherDataController;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * User interface for importing bulk weather data from supported weather files.
 */
public class ImportBulkWeatherDataUI {

    private final ImportBulkWeatherDataController controller = new ImportBulkWeatherDataController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public ImportBulkWeatherDataUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner the scanner used to read user input
     */
    public ImportBulkWeatherDataUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the bulk weather data import form.
     */
    public void show() {
        try {
            ConsoleStyle.printSection("Import Bulk Weather Data");
            printFormatInformation();

            final Path file = askImportFilePath();
            final BulkWeatherImportResult result = controller.importFromFile(file);

            printResult(result);
        } catch (IllegalArgumentException e) {
            ConsoleStyle.printError("Invalid weather data import: " + safeMessage(e));
        } catch (IOException e) {
            ConsoleStyle.printError("Could not read weather data import file: " + safeMessage(e));
        } catch (RuntimeException e) {
            ConsoleStyle.printError("Could not import weather data: " + safeMessage(e));
        }
    }

    private void printFormatInformation() {
        ConsoleStyle.printInfo("Supported formats: CSV, XLSX");
        ConsoleStyle.printInfo("Expected CSV header: areaCode,latitude,longitude,providerName,providerType,parameter,value,unit,altitude,day");
        ConsoleStyle.printInfo("XLSX weather files use the supplied WeatherData_v0 Wind sheet format.");
    }

    private Path askImportFilePath() {
        while (true) {
            ConsoleStyle.printPrompt("Weather data import file path: ");
            final String input = scanner.nextLine().trim();

            if (input.isBlank()) {
                ConsoleStyle.printWarning("Weather data import file path is required.");
                continue;
            }

            if (!hasSupportedExtension(input)) {
                ConsoleStyle.printWarning("Only CSV and XLSX weather data files are supported.");
                continue;
            }

            try {
                return Path.of(input);
            } catch (InvalidPathException e) {
                ConsoleStyle.printWarning("Invalid file path. Please enter a valid import file path.");
            }
        }
    }

    private boolean hasSupportedExtension(final String input) {
        final String lowerCaseInput = input.toLowerCase();
        return lowerCaseInput.endsWith(".csv") || lowerCaseInput.endsWith(".xlsx");
    }

    private void printResult(final BulkWeatherImportResult result) {
        if (result.isFullyImported()) {
            ConsoleStyle.printSuccess("Weather data import completed successfully.");
        } else if (result.importedRecords() > 0) {
            ConsoleStyle.printWarning("Weather data import completed with rejected records.");
        } else {
            ConsoleStyle.printError("No weather data records were imported.");
        }

        ConsoleStyle.printInfo("Records read: " + result.readRecords());
        ConsoleStyle.printInfo("Records imported: " + result.importedRecords());
        ConsoleStyle.printInfo("Records rejected: " + result.errors().size());

        if (result.hasErrors()) {
            printRejectedRecords(result);
        }
    }

    private void printRejectedRecords(final BulkWeatherImportResult result) {
        ConsoleStyle.printSection("Rejected records");
        for (final BulkWeatherImportError error : result.errors()) {
            System.out.println("Line " + error.sourceLine() + " | " + error.message());
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
