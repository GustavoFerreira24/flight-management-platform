package isep.aisafe.app.backoffice.console.presentation.pilot;

import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.pilot.RegisterPilotController;
import isep.aisafe.application.pilot.RegisterPilotService;

import java.util.Scanner;

public class RegisterPilotUI {

    private final RegisterPilotService controller;
    private final Scanner scanner;

    public RegisterPilotUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner the scanner used to read user input
     */
    public RegisterPilotUI(final Scanner scanner) {
        this.scanner = scanner;
        this.controller = new RegisterPilotController();

    }

    /**
     * Executes the pilot registration flow.
     * * <p>Presents a series of prompts to the user, collects data, and delegates
     * the business logic to the {@link RegisterPilotController}. Handles
     * any domain or runtime exceptions by displaying a friendly error message.</p>
     */
    public void show() {
        try {
            ConsoleStyle.printSection("--------- PILOT REGISTRATION ----------");

            System.out.println();
            System.out.println("NEW USER'S INFORMATION:");
            ConsoleStyle.printPrompt("Username: ");
            final String username = scanner.nextLine();
            ConsoleStyle.printPrompt("First Name: ");
            final String firstName = scanner.nextLine();
            ConsoleStyle.printPrompt("Last Name: ");
            final String lastName = scanner.nextLine();
            ConsoleStyle.printPrompt("Email: ");
            final String email = scanner.nextLine();
            ConsoleStyle.printPrompt("Password: ");
            final String password = scanner.nextLine();

            System.out.println();

            System.out.println("NEW PILOT'S INFORMATION: ");
            ConsoleStyle.printPrompt("Insert Pilot ID (3 letters): ");
            final String id = scanner.nextLine();
            ConsoleStyle.printPrompt("Select Country for Pilot Prefix: ");
            final String countryPrefix = selectCountryPrefix();
            ConsoleStyle.printPrompt("Pilot's Phone Number: ");
            final String phoneNumber = countryPrefix + scanner.nextLine();

            System.out.println();

            ConsoleStyle.printPrompt("Certification");
            System.out.println();
            ConsoleStyle.printPrompt("Certification Issued At (DD-MM-YYYY): ");
            String issuedAtStr = scanner.nextLine().trim();
            ConsoleStyle.printPrompt("Certification Valid Until (DD-MM-YYYY): ");
            String validUntilStr = scanner.nextLine().trim();

            System.out.println();

            ConsoleStyle.printPrompt("How many Certified Aircraft Models does the Pilot have?: ");
            int numberOfAircraftModels = Integer.parseInt(scanner.nextLine().trim());

            Iterable<AircraftModel> aircraftModels = controller.getAllAircraftModels();
            String aircraftModelDesignator = null;

            for (int i = 0; i < numberOfAircraftModels; i++) {

                int number = 1;

                for (AircraftModel aircraftModel : aircraftModels) {

                    ConsoleStyle.printPrompt(number + ". " + aircraftModel.designator());
                    System.out.println();
                    number++;
                }

                ConsoleStyle.printPrompt("Insert Aircraft Model Designator: ");
                final String designator = scanner.nextLine().trim();

                if (aircraftModelDesignator.isEmpty()) {
                    aircraftModelDesignator += designator;
                } else {
                    aircraftModelDesignator += "," + designator;
                }
            }

            controller.registerPilot(
                    id,
                    issuedAtStr,
                    validUntilStr,
                    username,
                    firstName,
                    lastName,
                    email,
                    password,
                    phoneNumber,
                    aircraftModelDesignator
            );

            System.out.println();
            ConsoleStyle.printInfo("Pilot " + id.trim().toUpperCase() + " registered successfully!");
            System.out.println();

        } catch (Exception e) {
            System.out.println();
            ConsoleStyle.printError("Error registering pilot: " + e.getMessage());
            System.out.println();
        }

    }

    /**
     * Simple country prefix selection - no validation here (DDD)
     */
    private String selectCountryPrefix() {

        System.out.println();
        System.out.println("   1 → PT (Portugal - +351)");
        System.out.println("   2 → ES (Spain - +34)");
        System.out.println("   3 → FR (France - +33)");
        System.out.println("   4 → DE (Germany - +49)");
        System.out.println("   5 → UK (United Kingdom - +44)");
        System.out.println("   6 → US (United States - +1)");
        System.out.println("   7 → BR (Brazil - +55)");

        ConsoleStyle.printPrompt("Choose option (1-7): ");
        String option = scanner.nextLine().trim();

        switch (option) {
            case "1": return "+351";
            case "2": return "+34";
            case "3": return "+33";
            case "4": return "+49";
            case "5": return "+44";
            case "6": return "+1";
            case "7": return "BR +55";
            default:
                return "";
        }
    }
}
