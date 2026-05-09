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

            System.out.print("Company name: ");
            final String name = scanner.nextLine();

            System.out.print("Country: ");
            final String country = scanner.nextLine();

            System.out.print("IATA code (2 letters): ");
            final String iataCode = scanner.nextLine();

            System.out.print("ICAO code (2 or 3 letters): ");
            final String icaoCode = scanner.nextLine();

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
}