package isep.aisafe.app.backoffice.console.presentation.pilot;

import isep.aisafe.app.common.console.ConsoleStyle;

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
                        ConsoleStyle.option(1, "Add a Pilot                                    (US75)"),
                        ConsoleStyle.option(2, "List Pilot Roster                              (US76)"),
                        ConsoleStyle.option(3, "Delete / Deactivate Pilot                      (US77)"),
                        ConsoleStyle.option(0, "Back                                                 ")
                );
                ConsoleStyle.printPrompt("Option: ");

                final String option = scanner.nextLine();

                switch (option) {
                    case "1":
                        executeMenuAction(() -> new AddPilotUI(scanner).show());
                        break;
                    case "2":
                        executeMenuAction(() -> new ListPilotRosterUI(scanner).show());
                        break;
                    case "3":
                        executeMenuAction(() -> new DeactivatePilotUI(scanner).show());
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
