package isep.aisafe.app.backoffice.console.presentation.pilot;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.pilot.*;
import isep.aisafe.pilot.domain.Pilot;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class AddPilotUI {

    private final RegisterPilotService controller;
    private final AddPilotController addPilotController;
    private final Scanner scanner;

    public AddPilotUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner the scanner used to read user input
     */
    public AddPilotUI(final Scanner scanner) {
        this.scanner = scanner;
        this.controller = new RegisterPilotController();
        this.addPilotController = new AddPilotController();

    }

    public void show() {
        try {
            ConsoleStyle.printSection("ASSOCIATE PILOT WITH AIR TRANSPORT COMPANY");

            final Iterable<Pilot> pilotsIterable = controller.getAllPilots();

            Set<Pilot> pilots = new HashSet<>();
            pilotsIterable.forEach(pilots::add);

            if (pilots.isEmpty()) {
                ConsoleStyle.printWarning("No pilots are registered.");
                return;
            }

            ConsoleStyle.printInfo("Registered Pilots:");

            System.out.println();

            System.out.println(" ID - Name (Status) ");

            for (Pilot pilot : pilots) {

                if (pilot.companyIATACode() == null ) {
                    System.out.println(pilot.identity() + " - " + pilot.firstName() + " " + pilot.lastName() + " (" + pilot.pilotStatus() + ")");
                }
            }

            System.out.println();

            ConsoleStyle.printPrompt("Enter the pilot's ID: ");
            final String pilotID = scanner.nextLine();
            addPilotController.addPilot(pilotID);

            System.out.println();
            ConsoleStyle.printInfo("Pilot " + pilotID.trim().toUpperCase() + " associated to ATC successfully!");
            System.out.println();

        } catch (Exception e) {
            ConsoleStyle.printError("An error occurred while associating the pilot to the air transport company.");
            e.printStackTrace();
        }
    }

}
