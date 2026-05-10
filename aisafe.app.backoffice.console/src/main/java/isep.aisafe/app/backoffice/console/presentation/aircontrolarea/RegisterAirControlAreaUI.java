package isep.aisafe.app.backoffice.console.presentation.aircontrolarea;

import isep.aisafe.application.aircontrolarea.RegisterAirControlAreaController;

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
            System.out.println("Register Air Control Area");

            System.out.print("Area code: ");
            final String code = scanner.nextLine();

            System.out.print("Name: ");
            final String name = scanner.nextLine();

            final double minLatitude = readDouble("Minimum latitude: ");
            final double maxLatitude = readDouble("Maximum latitude: ");
            final double minLongitude = readDouble("Minimum longitude: ");
            final double maxLongitude = readDouble("Maximum longitude: ");

            final double minimumFuelAmount = readDouble("Minimum fuel amount: ");

            System.out.print("Fuel unit (KG or L): ");
            final String fuelUnit = scanner.nextLine();

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

            System.out.println("Air control area registered successfully.");
        } catch (RuntimeException e) {
            System.out.println("Could not register air control area: " + e.getMessage());
        }
    }

    private double readDouble(final String prompt) {
        System.out.print(prompt);
        return Double.parseDouble(scanner.nextLine());
    }
}