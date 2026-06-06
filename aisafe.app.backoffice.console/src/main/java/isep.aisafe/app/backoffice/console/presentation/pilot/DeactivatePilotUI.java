package isep.aisafe.app.backoffice.console.presentation.pilot;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.pilot.DeactivatePilotController;
import isep.aisafe.application.pilot.ListPilotRosterController;
import isep.aisafe.application.pilot.RegisterPilotController;
import isep.aisafe.application.pilot.RegisterPilotService;
import isep.aisafe.pilot.domain.Pilot;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class DeactivatePilotUI {

    private final RegisterPilotService controller;
    private final DeactivatePilotController deactivatePilotController;
    private final ListPilotRosterController listPilotRosterController;
    private final Scanner scanner;

    public DeactivatePilotUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner the scanner used to read user input
     */
    public DeactivatePilotUI(final Scanner scanner) {
        this.scanner = scanner;
        this.controller = new RegisterPilotController();
        this.deactivatePilotController = new DeactivatePilotController();
        this.listPilotRosterController = new ListPilotRosterController();

    }

    public void show() {
        try {
            ConsoleStyle.printSection("DEACTIVATE PILOT");

            final Set<Pilot> pilots = listPilotRosterController.listPilotRoster();

            if (pilots.isEmpty()) {
                ConsoleStyle.printWarning("No pilots are registered.");
                return;
            }

            ConsoleStyle.printInfo("Registered Pilots:");

            for (Pilot pilot : pilots) {
                System.out.println(pilot.identity() + " - " + pilot.firstName() + " " + pilot.lastName() + " (" + pilot.pilotStatus() + ")");
            }

            ConsoleStyle.printPrompt("Enter the pilot's ID: ");
            final String pilotID = scanner.nextLine();
            deactivatePilotController.deactivatePilot(pilotID);

            System.out.println();
            ConsoleStyle.printInfo("Pilot " + pilotID.trim().toUpperCase() + " deactivated successfully!");
            System.out.println();

        } catch (Exception e) {
            ConsoleStyle.printError("An error occurred while deactivating the pilot.");
        }
    }

}
