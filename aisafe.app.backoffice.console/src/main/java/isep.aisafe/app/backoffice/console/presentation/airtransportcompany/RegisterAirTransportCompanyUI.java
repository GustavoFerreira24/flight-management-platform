package isep.aisafe.app.backoffice.console.presentation.airtransportcompany;

import isep.aisafe.application.aircompany.AirCompanyConfigurationController;
import isep.aisafe.app.common.console.ConsoleStyle;

import java.util.Scanner;

/**
 * User interface for registering an air transport company.
 */
public class RegisterAirTransportCompanyUI {

    private final AirCompanyConfigurationController controller = new AirCompanyConfigurationController();
    private final Scanner scanner;

    /**
     * Creates the UI using the standard input scanner.
     */
    public RegisterAirTransportCompanyUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI using a custom scanner.
     *
     * @param scanner the scanner used to read user input
     */
    public RegisterAirTransportCompanyUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the UI and registers an air transport company.
     */
    public void show() {
        try {
            System.out.println();
            ConsoleStyle.printSection("Register Air Transport Company");

            final String name = readRequiredText("Company name: ", "Company name is required.");
            final String country = readCountry();
            final String iataCode = readIataCode();
            final String icaoCode = readIcaoCode();

            controller.registerAirTransportCompany(
                    name,
                    country,
                    iataCode,
                    icaoCode
            );

            ConsoleStyle.printSuccess("Air transport company registered successfully.");
        } catch (RuntimeException e) {
            ConsoleStyle.printError("Could not register air transport company: " + e.getMessage());
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

    private String readCountry() {
        while (true) {
            ConsoleStyle.printMenu(
                    "Country",
                    ConsoleStyle.option(1, "Portugal"),
                    ConsoleStyle.option(2, "Spain"),
                    ConsoleStyle.option(3, "France"),
                    ConsoleStyle.option(4, "Germany"),
                    ConsoleStyle.option(5, "Ireland"),
                    ConsoleStyle.option(6, "United Kingdom"),
                    ConsoleStyle.option(7, "United States"),
                    ConsoleStyle.option(8, "Brazil"),
                    ConsoleStyle.option(9, "Other")
            );
            ConsoleStyle.printPrompt("Option: ");

            final String option = scanner.nextLine().trim();

            if ("1".equals(option)) {
                return "Portugal";
            }
            if ("2".equals(option)) {
                return "Spain";
            }
            if ("3".equals(option)) {
                return "France";
            }
            if ("4".equals(option)) {
                return "Germany";
            }
            if ("5".equals(option)) {
                return "Ireland";
            }
            if ("6".equals(option)) {
                return "United Kingdom";
            }
            if ("7".equals(option)) {
                return "United States";
            }
            if ("8".equals(option)) {
                return "Brazil";
            }
            if ("9".equals(option)) {
                return readRequiredText("Other country: ", "Country is required.");
            }

            ConsoleStyle.printWarning("Invalid option. Choose a number between 1 and 9.");
        }
    }

    private String readIataCode() {
        while (true) {
            final String value = readRequiredText("IATA code (2 letters): ", "IATA code is required.")
                    .trim()
                    .toUpperCase();

            if (value.matches("[A-Z]{2}")) {
                return value;
            }

            ConsoleStyle.printWarning("Invalid IATA code. It must have exactly 2 letters.");
        }
    }

    private String readIcaoCode() {
        while (true) {
            final String value = readRequiredText("ICAO code (2 or 3 letters): ", "ICAO code is required.")
                    .trim()
                    .toUpperCase();

            if (value.matches("[A-Z]{2,3}")) {
                return value;
            }

            ConsoleStyle.printWarning("Invalid ICAO code. It must have 2 or 3 letters.");
        }
    }
}
