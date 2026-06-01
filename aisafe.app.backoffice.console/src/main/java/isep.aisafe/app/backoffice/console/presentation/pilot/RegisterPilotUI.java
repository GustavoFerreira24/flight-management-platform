package isep.aisafe.app.backoffice.console.presentation.pilot;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.pilot.RegisterPilotController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class RegisterPilotUI {

    private final RegisterPilotController controller = new RegisterPilotController();
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
    }

    /**
     * Executes the pilot registration flow.
     * * <p>Presents a series of prompts to the user, collects data, and delegates
     * the business logic to the {@link RegisterPilotController}. Handles
     * any domain or runtime exceptions by displaying a friendly error message.</p>
     */
    public void show() {
        try {
            ConsoleStyle.printSection("REGISTER PILOT");

            System.out.println();

            ConsoleStyle.printPrompt("Insert Pilot ID (3 letters): ");
            final String id = scanner.nextLine();

            ConsoleStyle.printPrompt("CERTIFICATION");
            System.out.println();
            ConsoleStyle.printPrompt("Certification Issued At (DD-MM-YYYY):");
            String issuedAtStr = scanner.nextLine().trim();
            ConsoleStyle.printPrompt("Certification Valid Until (DD-MM-YYYY):");
            String validUntilStr = scanner.nextLine().trim();

            controller.registerPilot(id, issuedAtStr, validUntilStr);

            System.out.println();
            ConsoleStyle.printInfo("Pilot " + id.trim().toUpperCase() + " registered successfully!");
            System.out.println();

        } catch (Exception e) {
            ConsoleStyle.printError("Could not register airport: " + e.getMessage());
        }
    }
}
