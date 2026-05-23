package isep.aisafe.app.backoffice.console.presentation.aircraft;

import isep.aisafe.app.common.console.ConsoleStyle;
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
            ConsoleStyle.printSection("List Aircraft Fleet");

            final AircraftFleetFilter filter = readFilter();
            final List<AircraftFleetEntry> aircraft = controller.listAircraftFleet(filter);

            if (aircraft.isEmpty()) {
                ConsoleStyle.printWarning("No aircraft found for the selected filters.");
                return;
            }

            printAircraft(aircraft);
        } catch (RuntimeException e) {
            ConsoleStyle.printError("Could not list aircraft fleet: " + e.getMessage());
        }
    }

    private AircraftFleetFilter readFilter() {
        if (!readYesNo("Apply filters? (Y/N): ")) {
            return AircraftFleetFilter.empty();
        }

        final String model = readOptionalText("Model ");
        final String maker = readOptionalText("Maker: ");
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
            ConsoleStyle.printWarning(e.getMessage());
            ConsoleStyle.printWarning("Please enter the filters again.");
            return readFilter();
        }
    }

    private boolean readYesNo(final String prompt) {
        while (true) {
            ConsoleStyle.printPrompt(prompt);
            final String value = scanner.nextLine().trim();

            if ("Y".equalsIgnoreCase(value) || "YES".equalsIgnoreCase(value)) {
                return true;
            }

            if ("N".equalsIgnoreCase(value) || "NO".equalsIgnoreCase(value)) {
                return false;
            }

            ConsoleStyle.printWarning("Invalid option. Enter Y or N.");
        }
    }

    private String readOptionalText(final String prompt) {
        ConsoleStyle.printPrompt(prompt);
        final String value = scanner.nextLine().trim();

        if (value.isBlank()) {
            return null;
        }

        return value;
    }

    private Integer readOptionalNonNegativeInt(final String prompt) {
        while (true) {
            ConsoleStyle.printPrompt(prompt);
            final String value = scanner.nextLine().trim();

            if (value.isBlank()) {
                return null;
            }

            try {
                final int parsed = Integer.parseInt(value);

                if (parsed >= 0) {
                    return parsed;
                }

                ConsoleStyle.printWarning("Invalid value. It cannot be negative.");
            } catch (NumberFormatException e) {
                ConsoleStyle.printWarning("Invalid number. Please enter an integer value.");
            }
        }
    }

    private void printAircraft(final List<AircraftFleetEntry> aircraft) {
        ConsoleStyle.printSection("Aircraft fleet");
        System.out.println(ConsoleStyle.muted("Registration | Model | Maker | Seats | Model Capacity | Year | Age | Status | Engine"));

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
