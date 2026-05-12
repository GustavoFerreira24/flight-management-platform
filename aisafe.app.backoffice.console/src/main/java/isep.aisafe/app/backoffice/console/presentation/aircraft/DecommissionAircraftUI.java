package isep.aisafe.app.backoffice.console.presentation.aircraft;

import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.application.aircraft.DecommissionAircraftController;

import java.util.List;
import java.util.Scanner;

/**
 * User interface for decommissioning aircraft from the authenticated company's fleet.
 */
public class DecommissionAircraftUI {

    private final DecommissionAircraftController controller = new DecommissionAircraftController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public DecommissionAircraftUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner the scanner used to read user input
     */
    public DecommissionAircraftUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the aircraft decommission form.
     */
    public void show() {
        try {
            System.out.println();
            System.out.println("Decommission Aircraft");

            final List<Aircraft> aircraft = controller.decommissionableAircraft();

            if (aircraft.isEmpty()) {
                System.out.println("No active aircraft are available for decommissioning.");
                return;
            }

            System.out.println("Active aircraft available for decommissioning:");
            for (int i = 0; i < aircraft.size(); i++) {
                final Aircraft candidate = aircraft.get(i);
                System.out.printf(
                        "%d - %s | %s | %s%n",
                        i + 1,
                        candidate.registration(),
                        candidate.aircraftModel().designator(),
                        candidate.operationalStatus()
                );
            }

            System.out.print("Aircraft registration number: ");
            final String registration = scanner.nextLine();

            controller.decommissionAircraft(registration);

            System.out.println("Aircraft decommissioned successfully.");
        } catch (RuntimeException e) {
            System.out.println("Could not decommission aircraft: " + e.getMessage());
        }
    }
}
