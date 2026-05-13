package isep.aisafe.app.backoffice.console.presentation.aircraft;

import isep.aisafe.application.aircraft.RegisterAircraftController;

import java.util.Scanner;

/**
 * User interface for registering aircraft in the authenticated company's fleet.
 */
public class RegisterAircraftUI {

    private final RegisterAircraftController controller = new RegisterAircraftController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public RegisterAircraftUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner the scanner used to read user input
     */
    public RegisterAircraftUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the aircraft registration form.
     */
    public void show() {
        try {
            System.out.println();
            System.out.println("Register Aircraft");

            final String registration = readAircraftRegistration();
            final String registrationCountry = readCountry();
            final String operationalStatus = readOperationalStatus();

            final int crewSize = readPositiveInt("Crew size: ");
            final int[] cabinConfiguration = readCabinConfiguration();

            final String aircraftModelName = readRequiredText("Aircraft model name: ", "Aircraft model name is required.");
            final String aircraftModelManufacturer = readRequiredText(
                    "Aircraft model manufacturer: ",
                    "Aircraft model manufacturer is required."
            );
            final String engineModelName = readRequiredText("Engine model name: ", "Engine model name is required.");
            final String engineModelManufacturer = readRequiredText(
                    "Engine model manufacturer: ",
                    "Engine model manufacturer is required."
            );

            controller.registerAircraft(
                    registration,
                    registrationCountry,
                    operationalStatus,
                    crewSize,
                    cabinConfiguration[0],
                    cabinConfiguration[1],
                    cabinConfiguration[2],
                    aircraftModelName,
                    aircraftModelManufacturer,
                    engineModelName,
                    engineModelManufacturer
            );

            System.out.println("Aircraft registered successfully.");
        } catch (RuntimeException e) {
            System.out.println("Could not register aircraft: " + e.getMessage());
        }
    }

    private String readAircraftRegistration() {
        while (true) {
            final String registration = readRequiredText(
                    "Aircraft registration number: ",
                    "Aircraft registration number is required."
            ).trim().toUpperCase();

            if (registration.matches("[A-Z0-9][A-Z0-9-]{1,9}")) {
                return registration;
            }

            System.out.println("Invalid aircraft registration. Use 2 to 10 letters, digits or hyphens.");
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
            System.out.println("Registration country:");
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

    private String readOperationalStatus() {
        while (true) {
            System.out.println("Operational status:");
            System.out.println("1 - ACTIVE");
            System.out.println("2 - IN_MAINTENANCE");
            System.out.println("3 - DECOMMISSIONED");
            System.out.print("Option: ");

            final String option = scanner.nextLine().trim();

            if ("1".equals(option)) {
                return "ACTIVE";
            }
            if ("2".equals(option)) {
                return "IN_MAINTENANCE";
            }
            if ("3".equals(option)) {
                return "DECOMMISSIONED";
            }

            System.out.println("Invalid option. Choose 1, 2 or 3.");
        }
    }

    private int readPositiveInt(final String prompt) {
        while (true) {
            final int value = readInt(prompt);

            if (value > 0) {
                return value;
            }

            System.out.println("Invalid value. It must be greater than zero.");
        }
    }

    private int[] readCabinConfiguration() {
        while (true) {
            final int economySeats = readNonNegativeInt("Economy seats: ");
            final int businessSeats = readNonNegativeInt("Business seats: ");
            final int firstClassSeats = readNonNegativeInt("First class seats: ");

            if (economySeats + businessSeats + firstClassSeats > 0) {
                return new int[] { economySeats, businessSeats, firstClassSeats };
            }

            System.out.println("Cabin configuration must have at least one seat. Please enter the seats again.");
        }
    }

    private int readNonNegativeInt(final String prompt) {
        while (true) {
            final int value = readInt(prompt);

            if (value >= 0) {
                return value;
            }

            System.out.println("Invalid number of seats. Value cannot be negative.");
        }
    }

    private int readInt(final String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter an integer value.");
            }
        }
    }
}
