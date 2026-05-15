package isep.aisafe.app.backoffice.console.presentation.aircraft;

import isep.aisafe.application.aircraft.AircraftFleetEntry;
import isep.aisafe.application.aircraft.AircraftFleetFilter;
import isep.aisafe.application.aircraft.ListAircraftFleetController;

import java.util.List;
import java.util.Scanner;

/**
 * User interface for listing the authenticated company's aircraft fleet.
 */
public class ListAircraftFleetUI {

    private final ListAircraftFleetController controller = new ListAircraftFleetController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public ListAircraftFleetUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner the scanner used to read user input
     */
    public ListAircraftFleetUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the aircraft fleet listing.
     */
    public void show() {
        try {
            System.out.println();
            System.out.println("List Aircraft Fleet");

            final AircraftFleetFilter filter = readFilter();
            final List<AircraftFleetEntry> aircraft = controller.listAircraftFleet(filter);

            if (aircraft.isEmpty()) {
                System.out.println("No aircraft found for the selected filters.");
                return;
            }

            printAircraft(aircraft);
        } catch (RuntimeException e) {
            System.out.println("Could not list aircraft fleet: " + e.getMessage());
        }
    }

    private AircraftFleetFilter readFilter() {
        if (!readYesNo("Apply filters? (Y/N): ")) {
            return AircraftFleetFilter.empty();
        }

        final String model = readOptionalText("Model contains: ");
        final String maker = readOptionalText("Maker contains: ");
        final Integer minimumCapacity = readOptionalNonNegativeInt("Minimum total seats (blank for none): ");
        final Integer maximumCapacity = readOptionalNonNegativeInt("Maximum total seats (blank for none): ");
        final Integer minimumAge = readOptionalNonNegativeInt("Minimum age in years (blank for none): ");
        final Integer maximumAge = readOptionalNonNegativeInt("Maximum age in years (blank for none): ");

        try {
            return new AircraftFleetFilter(
                    model,
                    maker,
                    minimumCapacity,
                    maximumCapacity,
                    minimumAge,
                    maximumAge
            );
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.out.println("Please enter the filters again.");
            return readFilter();
        }
    }

    private boolean readYesNo(final String prompt) {
        while (true) {
            System.out.print(prompt);
            final String value = scanner.nextLine().trim();

            if ("Y".equalsIgnoreCase(value) || "YES".equalsIgnoreCase(value)) {
                return true;
            }

            if ("N".equalsIgnoreCase(value) || "NO".equalsIgnoreCase(value)) {
                return false;
            }

            System.out.println("Invalid option. Enter Y or N.");
        }
    }

    private String readOptionalText(final String prompt) {
        System.out.print(prompt);
        final String value = scanner.nextLine().trim();

        if (value.isBlank()) {
            return null;
        }

        return value;
    }

    private Integer readOptionalNonNegativeInt(final String prompt) {
        while (true) {
            System.out.print(prompt);
            final String value = scanner.nextLine().trim();

            if (value.isBlank()) {
                return null;
            }

            try {
                final int parsed = Integer.parseInt(value);

                if (parsed >= 0) {
                    return parsed;
                }

                System.out.println("Invalid value. It cannot be negative.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter an integer value.");
            }
        }
    }

    private void printAircraft(final List<AircraftFleetEntry> aircraft) {
        System.out.println("Aircraft fleet:");
        System.out.println("Registration | Model | Maker | Seats | Model Capacity | Year | Age | Status | Engine");

        for (final AircraftFleetEntry entry : aircraft) {
            System.out.printf(
                    "%s | %s | %s | %d | %d | %d | %d | %s | %s%n",
                    entry.registration(),
                    entry.model(),
                    entry.maker(),
                    entry.totalSeats(),
                    entry.modelCapacity(),
                    entry.manufactureYear(),
                    entry.ageInYears(),
                    entry.operationalStatus(),
                    entry.engineModel()
            );
        }
    }
}
