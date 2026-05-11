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

            System.out.print("Aircraft registration number: ");
            final String registration = scanner.nextLine();

            System.out.print("Registration country: ");
            final String registrationCountry = scanner.nextLine();

            final int crewSize = readInt("Crew size: ");

            final int economySeats = readInt("Economy seats: ");
            final int businessSeats = readInt("Business seats: ");
            final int firstClassSeats = readInt("First class seats: ");

            System.out.print("Aircraft model name: ");
            final String aircraftModelName = scanner.nextLine();

            System.out.print("Aircraft model manufacturer: ");
            final String aircraftModelManufacturer = scanner.nextLine();

            System.out.print("Engine model name: ");
            final String engineModelName = scanner.nextLine();

            System.out.print("Engine model manufacturer: ");
            final String engineModelManufacturer = scanner.nextLine();

            controller.registerAircraft(
                    registration,
                    registrationCountry,
                    crewSize,
                    economySeats,
                    businessSeats,
                    firstClassSeats,
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

    private int readInt(final String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(scanner.nextLine());
    }
}