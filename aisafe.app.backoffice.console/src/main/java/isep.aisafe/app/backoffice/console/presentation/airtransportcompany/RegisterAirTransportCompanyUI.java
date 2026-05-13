package isep.aisafe.app.backoffice.console.presentation.airtransportcompany;

import isep.aisafe.application.aircompany.AirCompanyConfigurationController;

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
            System.out.println("Register Air Transport Company");

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

            System.out.println("Air transport company registered successfully.");
        } catch (RuntimeException e) {
            System.out.println("Could not register air transport company: " + e.getMessage());
        }
    }

    private String readRequiredText(final String prompt, final String errorMessage) {
        while (true) {
            System.out.print(prompt);
            final String value = scanner.nextLine().trim();

            if (!value.isBlank()) {
                return value;
            }

            System.out.println(errorMessage);
        }
    }

    private String readCountry() {
        while (true) {
            System.out.println("Country:");
            System.out.println("1 - Portugal");
            System.out.println("2 - Spain");
            System.out.println("3 - France");
            System.out.println("4 - Germany");
            System.out.println("5 - Ireland");
            System.out.println("6 - United Kingdom");
            System.out.println("7 - United States");
            System.out.println("8 - Brazil");
            System.out.println("9 - Other");
            System.out.print("Option: ");

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

            System.out.println("Invalid option. Choose a number between 1 and 9.");
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

            System.out.println("Invalid IATA code. It must have exactly 2 letters.");
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

            System.out.println("Invalid ICAO code. It must have 2 or 3 letters.");
        }
    }
}
