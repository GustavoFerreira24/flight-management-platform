package isep.aisafe.app.backoffice.console.presentation.aircontrolarea;

import isep.aisafe.application.aircontrolarea.RegisterAirControlAreaController;
import isep.aisafe.app.common.console.ConsoleStyle;

import java.util.Scanner;

/**
 * User interface for registering air control areas.
 */
public class RegisterAirControlAreaUI {

    private final RegisterAirControlAreaController controller = new RegisterAirControlAreaController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public RegisterAirControlAreaUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner the scanner used to read user input
     */
    public RegisterAirControlAreaUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the air control area registration form.
     */
    public void show() {
        try {
            System.out.println();
            ConsoleStyle.printSection("Register Air Control Area");

            final String code = readAreaCode();
            final String name = readRequiredText("Name: ", "Air control area name is required.");

            final double minLatitude = readDoubleInRange(
                    "Minimum latitude: ",
                    -90,
                    90,
                    "Latitude must be between -90 and 90."
            );
            final double maxLatitude = readDoubleGreaterThan(
                    "Maximum latitude: ",
                    minLatitude,
                    90,
                    "Maximum latitude must be greater than minimum latitude and at most 90."
            );
            final double minLongitude = readDoubleInRange(
                    "Minimum longitude: ",
                    -180,
                    180,
                    "Longitude must be between -180 and 180."
            );
            final double maxLongitude = readDoubleGreaterThan(
                    "Maximum longitude: ",
                    minLongitude,
                    180,
                    "Maximum longitude must be greater than minimum longitude and at most 180."
            );

            final double minimumFuelAmount = readPositiveDouble("Minimum fuel amount: ");
            final String fuelUnit = readFuelUnit();

            controller.registerAirControlArea(
                    code,
                    name,
                    minLatitude,
                    maxLatitude,
                    minLongitude,
                    maxLongitude,
                    minimumFuelAmount,
                    fuelUnit
            );

            ConsoleStyle.printSuccess("Air control area registered successfully.");
        } catch (RuntimeException e) {
            ConsoleStyle.printError("Could not register air control area: " + e.getMessage());
        }
    }

    private String readAreaCode() {
        while (true) {
            final String code = readRequiredText("Area code: ", "Air control area code is required.")
                    .trim()
                    .toUpperCase();

            if (code.matches("[A-Z0-9]{2,10}")) {
                return code;
            }

            ConsoleStyle.printWarning("Invalid area code. Use 2 to 10 letters or digits.");
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

    private double readDoubleInRange(
            final String prompt,
            final double minimum,
            final double maximum,
            final String errorMessage) {
        while (true) {
            final double value = readDouble(prompt);

            if (value >= minimum && value <= maximum) {
                return value;
            }

            ConsoleStyle.printWarning(errorMessage);
        }
    }

    private double readDoubleGreaterThan(
            final String prompt,
            final double lowerBound,
            final double maximum,
            final String errorMessage) {
        while (true) {
            final double value = readDouble(prompt);

            if (value > lowerBound && value <= maximum) {
                return value;
            }

            ConsoleStyle.printWarning(errorMessage);
        }
    }

    private double readPositiveDouble(final String prompt) {
        while (true) {
            final double value = readDouble(prompt);

            if (value > 0) {
                return value;
            }

            ConsoleStyle.printWarning("Invalid amount. Value must be greater than zero.");
        }
    }

    private double readDouble(final String prompt) {
        while (true) {
            ConsoleStyle.printPrompt(prompt);

            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                ConsoleStyle.printWarning("Invalid number. Please enter a numeric value.");
            }
        }
    }

    private String readFuelUnit() {
        while (true) {
            ConsoleStyle.printMenu(
                    "Fuel Unit",
                    ConsoleStyle.option(1, "KG"),
                    ConsoleStyle.option(2, "L")
            );
            ConsoleStyle.printPrompt("Option: ");

            final String option = scanner.nextLine().trim();

            if ("1".equals(option)) {
                return "KG";
            }

            if ("2".equals(option)) {
                return "L";
            }

            ConsoleStyle.printWarning("Invalid option. Choose 1 or 2.");
        }
    }
}
