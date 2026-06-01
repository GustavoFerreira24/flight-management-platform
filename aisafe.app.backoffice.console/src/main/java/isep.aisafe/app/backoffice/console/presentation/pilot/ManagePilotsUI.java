package isep.aisafe.app.backoffice.console.presentation.pilot;

import isep.aisafe.app.backoffice.console.presentation.aircraft.RegisterAircraftUI;
import isep.aisafe.app.common.console.ConsoleStyle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ManagePilotsUI {

    private final Scanner scanner;

    public ManagePilotsUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner the scanner used to read user input
     */
    public ManagePilotsUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    public void show() {

        boolean keepRunning = true;

        do {
            try {
                ConsoleStyle.printMenu(
                        "Manage Pilots",
                        ConsoleStyle.option(1, "Register Pilot                                 (US75)"),
                        ConsoleStyle.option(2, "List Pilot Roster                              (US76)"),
                        ConsoleStyle.option(3, "Delete Pilot                                   (US77)"),
                        ConsoleStyle.option(0, "Back                                                 ")
                );
                ConsoleStyle.printPrompt("Option: ");

                final String option = scanner.nextLine();

                switch (option) {
                    case "1":
                        executeMenuAction(() -> new RegisterPilotUI(scanner).show());
                        break;
                    case "2":
                        System.out.println("List pilot roster (US76) not implemented yet");
                        break;
                    case "3":
                        System.out.println("Delete pilot (US77) not implemented yet");
                        break;
                    case "0":
                        keepRunning = false;
                        break;
                    default:
                        ConsoleStyle.printWarning("Unknown option.");
                        break;
                }


            } catch (final Exception e) {
                ConsoleStyle.printError("An error occurred while displaying the manage pilots menu.");
            }
        } while (keepRunning);
    }

    private void executeMenuAction(final Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException e) {
            ConsoleStyle.printError("Invalid input: " + e.getMessage());
        } catch (RuntimeException e) {
            ConsoleStyle.printError("Operation failed: " + e.getMessage());
        }
    }


}
