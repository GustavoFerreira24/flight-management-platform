package isep.aisafe.app.backoffice.console.presentation.pilot;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.pilot.DeactivatePilotController;
import isep.aisafe.application.pilot.ListPilotRosterController;
import isep.aisafe.application.pilot.RegisterPilotController;
import isep.aisafe.application.pilot.RegisterPilotService;
import isep.aisafe.pilot.domain.Pilot;

import java.util.Scanner;
import java.util.Set;

public class ListPilotRosterUI {

    private final RegisterPilotService controller;
    private final ListPilotRosterController listPilotRosterController;
    private final Scanner scanner;

    public ListPilotRosterUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner the scanner used to read user input
     */
    public ListPilotRosterUI(final Scanner scanner) {
        this.scanner = scanner;
        this.controller = new RegisterPilotController();
        this.listPilotRosterController = new ListPilotRosterController();
    }

    public void show() {
        try {
            ConsoleStyle.printSection("PILOT ROSTER");

            final Set<Pilot> pilotRoster = listPilotRosterController.listPilotRoster();

            if (pilotRoster.isEmpty()) {
                ConsoleStyle.printWarning("No pilots are registered.");
                return;
            }

            for (Pilot pilot : pilotRoster) {
                System.out.println(pilot.toString());
                System.out.println();
            }

            ConsoleStyle.printPrompt("Press Enter to continue...");
            scanner.nextLine();

        } catch (Exception e) {
            ConsoleStyle.printError("An error occurred while listing the pilot roster.");
        }
    }
}
