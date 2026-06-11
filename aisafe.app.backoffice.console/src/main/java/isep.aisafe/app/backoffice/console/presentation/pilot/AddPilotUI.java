package isep.aisafe.app.backoffice.console.presentation.pilot;

import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.pilot.*;
import isep.aisafe.pilot.domain.Pilot;

import java.util.Scanner;
import java.util.Set;

public class AddPilotUI {

    private final RegisterPilotService controller;
    private final AddPilotController addPilotController;
    private final ListPilotRosterController listPilotRosterController;
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

            String airTransportCompany = null;

            for (Pilot pilot : pilots) {

                if (pilot.pilotStatus().equals("ACTIVE") ) {

                    if (pilot.companyIATACode() == null ) {
                        airTransportCompany = "------";
                    } else {
                        airTransportCompany = pilot.companyIATACode().toString();
                    }
                    System.out.println(pilot.identity() + " - " + pilot.firstName() + " " + pilot.lastName() + " Current Air Transport Company IATA Code: " + airTransportCompany + " (" + pilot.pilotStatus() + ")");
                }

            }

            ConsoleStyle.printPrompt("Enter the pilot's ID: ");
            final String pilotID = scanner.nextLine();
            addPilotController.addPilot(pilotID);

            System.out.println();
            ConsoleStyle.printInfo("Pilot " + pilotID.trim().toUpperCase() + " associate to " + airTransportCompany + " successfully!");
            System.out.println();

        } catch (Exception e) {
            ConsoleStyle.printError("An error occurred while associating the pilot to the air transport company.");
        }
    }

}
