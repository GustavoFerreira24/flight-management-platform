package isep.aisafe.app.backoffice.console.presentation.aircraft;

import isep.aisafe.application.aircraft.RegisterAircraftController;
import isep.aisafe.app.common.console.ConsoleStyle;

import java.time.Year;
import java.util.List;
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
            ConsoleStyle.printSection("Register Aircraft");

            final String registration = readAircraftRegistration();
            final String registrationCountry = readCountry();
            final String operationalStatus = readOperationalStatus();
            final int manufactureYear = readManufactureYear();

            final int crewSize = readPositiveInt("Crew size: ");
            String aircraftModelDesignator;
            int[] cabinConfiguration;

            while (true) {
                aircraftModelDesignator = readAircraftModelDesignator();
                final int passengerCapacity = controller.aircraftModelPassengerCapacity(aircraftModelDesignator);
                cabinConfiguration = readCabinConfiguration(aircraftModelDesignator, passengerCapacity);

                if (cabinConfiguration != null) {
                    break;
                }
            }

            final String engineModelDesignator = readCertifiedEngineModelDesignator(aircraftModelDesignator);
            final String[] aircraftModelParts = splitDesignator(aircraftModelDesignator, "aircraft model");
            final String[] engineModelParts = splitDesignator(engineModelDesignator, "engine model");

            controller.registerAircraft(
                    registration,
                    registrationCountry,
                    operationalStatus,
                    manufactureYear,
                    crewSize,
                    cabinConfiguration[0],
                    cabinConfiguration[1],
                    cabinConfiguration[2],
                    aircraftModelParts[0],
                    aircraftModelParts[1],
                    engineModelParts[0],
                    engineModelParts[1]
            );

            ConsoleStyle.printSuccess("Aircraft registered successfully.");
        } catch (RuntimeException e) {
            ConsoleStyle.printError("Could not register aircraft: " + e.getMessage());
        }
    }

    private String readAircraftRegistration() {
        while (true) {
            final String registration = readRequiredText(
                    "Aircraft registration number: ",
                    "Aircraft registration number is required."
            ).trim().toUpperCase();

            if (registration.matches("[A-Z0-9][A-Z0-9-]{1,9}")) {
                if (controller.aircraftRegistrationExists(registration)) {
                    ConsoleStyle.printWarning(
                            "Aircraft registration already exists. Please enter a different registration."
                    );
                    continue;
                }

                return registration;
            }

            ConsoleStyle.printWarning("Invalid aircraft registration. Use 2 to 10 letters, digits or hyphens.");
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
                    "Registration Country",
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

    private String readOperationalStatus() {
        while (true) {
            ConsoleStyle.printMenu(
                    "Operational Status",
                    ConsoleStyle.option(1, "ACTIVE"),
                    ConsoleStyle.option(2, "IN_MAINTENANCE"),
                    ConsoleStyle.option(3, "DECOMMISSIONED")
            );
            ConsoleStyle.printPrompt("Option: ");

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

            ConsoleStyle.printWarning("Invalid option. Choose 1, 2 or 3.");
        }
    }

    private int readPositiveInt(final String prompt) {
        while (true) {
            final int value = readInt(prompt);

            if (value > 0) {
                return value;
            }

            ConsoleStyle.printWarning("Invalid value. It must be greater than zero.");
        }
    }

    private int readManufactureYear() {
        final int currentYear = Year.now().getValue();

        while (true) {
            final int value = readInt("Manufacture year: ");

            if (value >= 1903 && value <= currentYear) {
                return value;
            }

            ConsoleStyle.printWarning("Invalid manufacture year. Enter a year between 1903 and " + currentYear + ".");
        }
    }

    private int[] readCabinConfiguration(final String aircraftModelDesignator, final int passengerCapacity) {
        while (true) {
            final int economySeats = readNonNegativeInt("Economy seats: ");
            final int businessSeats = readNonNegativeInt("Business seats: ");
            final int firstClassSeats = readNonNegativeInt("First class seats: ");
            final int totalSeats = economySeats + businessSeats + firstClassSeats;

            if (totalSeats > 0 && totalSeats <= passengerCapacity) {
                return new int[] { economySeats, businessSeats, firstClassSeats };
            }

            if (totalSeats == 0) {
                ConsoleStyle.printWarning("Cabin configuration must have at least one seat. Please enter the seats again.");
                continue;
            }

            ConsoleStyle.printWarning(
                    "Total seats (" + totalSeats + ") exceed the passenger capacity of "
                            + aircraftModelDesignator + " (" + passengerCapacity + ")."
            );

            if (readCabinCapacityAction()) {
                continue;
            }

            return null;
        }
    }

    private boolean readCabinCapacityAction() {
        while (true) {
            ConsoleStyle.printMenu(
                    "Cabin Capacity",
                    ConsoleStyle.option(1, "Enter seats again"),
                    ConsoleStyle.option(2, "Choose another aircraft model")
            );
            ConsoleStyle.printPrompt("Option: ");

            final String option = scanner.nextLine().trim();

            if ("1".equals(option)) {
                return true;
            }
            if ("2".equals(option)) {
                return false;
            }

            ConsoleStyle.printWarning("Invalid option. Choose 1 or 2.");
        }
    }

    private int readNonNegativeInt(final String prompt) {
        while (true) {
            final int value = readInt(prompt);

            if (value >= 0) {
                return value;
            }

            ConsoleStyle.printWarning("Invalid number of seats. Value cannot be negative.");
        }
    }

    private String readAircraftModelDesignator() {
        final List<String> aircraftModelDesignators = controller.aircraftModelDesignators();

        if (aircraftModelDesignators.isEmpty()) {
            throw new IllegalStateException("There are no aircraft models registered.");
        }

        return readAircraftModelOptionFromList(aircraftModelDesignators);
    }

    private String readCertifiedEngineModelDesignator(final String aircraftModelDesignator) {
        final List<String> engineModelDesignators = controller.certifiedEngineModelDesignators(aircraftModelDesignator);

        if (engineModelDesignators.isEmpty()) {
            throw new IllegalStateException("The selected aircraft model has no certified engine models.");
        }

        return readCertifiedEngineModelOptionFromList(aircraftModelDesignator, engineModelDesignators);
    }

    private String readAircraftModelOptionFromList(final List<String> options) {
        while (true) {
            ConsoleStyle.printSection("Aircraft Model");

            for (int i = 0; i < options.size(); i++) {
                final String aircraftModelDesignator = options.get(i);

                System.out.println(ConsoleStyle.option(i + 1, aircraftModelDesignator));
                System.out.println("    " + ConsoleStyle.muted(controller.aircraftModelDescription(aircraftModelDesignator)));
            }

            final int selectedOption = readInt("Option: ");

            if (selectedOption >= 1 && selectedOption <= options.size()) {
                return options.get(selectedOption - 1);
            }

            ConsoleStyle.printWarning("Invalid option. Choose a number between 1 and " + options.size() + ".");
        }
    }

    private String readCertifiedEngineModelOptionFromList(
            final String aircraftModelDesignator,
            final List<String> options) {

        while (true) {
            ConsoleStyle.printSection("Certified Engine Model");

            for (int i = 0; i < options.size(); i++) {
                final String engineModelDesignator = options.get(i);

                System.out.println(ConsoleStyle.option(i + 1, engineModelDesignator));
                System.out.println("    " + ConsoleStyle.muted(controller.certifiedEngineModelDescription(
                        aircraftModelDesignator,
                        engineModelDesignator
                )));
            }

            final int selectedOption = readInt("Option: ");

            if (selectedOption >= 1 && selectedOption <= options.size()) {
                return options.get(selectedOption - 1);
            }

            ConsoleStyle.printWarning("Invalid option. Choose a number between 1 and " + options.size() + ".");
        }
    }

    private String readOptionFromList(final String title, final List<String> options) {
        while (true) {
            ConsoleStyle.printSection(title);

            for (int i = 0; i < options.size(); i++) {
                System.out.println(ConsoleStyle.option(i + 1, options.get(i)));
            }

            final int selectedOption = readInt("Option: ");

            if (selectedOption >= 1 && selectedOption <= options.size()) {
                return options.get(selectedOption - 1);
            }

            ConsoleStyle.printWarning("Invalid option. Choose a number between 1 and " + options.size() + ".");
        }
    }

    private String[] splitDesignator(final String designator, final String label) {
        final String[] parts = designator.split("\\s*\\|\\s*", 2);

        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalStateException("Invalid " + label + " designator: " + designator);
        }

        return parts;
    }

    private int readInt(final String prompt) {
        while (true) {
            ConsoleStyle.printPrompt(prompt);

            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                ConsoleStyle.printWarning("Invalid number. Please enter an integer value.");
            }
        }
    }
}
