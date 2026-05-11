package isep.aisafe.app.backoffice.console.presentation.aircraft;

import isep.aisafe.application.aircraft.DecommissionAircraftController;

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

            System.out.print("Aircraft registration number: ");
            final String registration = scanner.nextLine();

            controller.decommissionAircraft(registration);

            System.out.println("Aircraft decommissioned successfully.");
        } catch (RuntimeException e) {
            System.out.println("Could not decommission aircraft: " + e.getMessage());
        }
    }
}
