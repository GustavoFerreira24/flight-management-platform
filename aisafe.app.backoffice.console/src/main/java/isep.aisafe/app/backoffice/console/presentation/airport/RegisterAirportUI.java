package isep.aisafe.app.backoffice.console.presentation.airport;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.airport.RegisterAirportController;
import java.util.List;
import java.util.Scanner;

/**
 * User interface for registering a new airport in the backoffice application.
 * * <p>This class is responsible for interacting with the operator via console,
 * collecting necessary data for airport identification, location, and
 * association with an air control area.</p>
 * * @author Gemini
 */
public class RegisterAirportUI {

    private final RegisterAirportController controller = new RegisterAirportController();
    private final Scanner scanner;

    /**
     * Constructs the UI using the standard system input.
     */
    public RegisterAirportUI() {
        this(new Scanner(System.in));
    }

    /**
     * Constructs the UI using a specific scanner, allowing for easier unit testing.
     *
     * @param scanner the scanner used to read user input.
     */
    public RegisterAirportUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Executes the airport registration flow.
     * * <p>Presents a series of prompts to the user, collects data, and delegates
     * the business logic to the {@link RegisterAirportController}. Handles
     * any domain or runtime exceptions by displaying a friendly error message.</p>
     */
    public void show() {
        try {
            ConsoleStyle.printSection("Register Airport");

            ConsoleStyle.printPrompt("Country to add the airport: ");
            final String country = scanner.nextLine();

            ConsoleStyle.printPrompt("Town to add the airport: ");
            final String town = scanner.nextLine();

            ConsoleStyle.printPrompt("Airport name: ");
            final String name = scanner.nextLine();

            ConsoleStyle.printPrompt("IATA code (3 letters): ");
            final String iata = scanner.nextLine();

            ConsoleStyle.printPrompt("ICAO code (4 letters): ");
            final String icao = scanner.nextLine();

            final double lat = readDouble("Latitude (-90 to 90): ");
            final double lon = readDouble("Longitude (-180 to 180): ");
            final double elev = readDouble("Elevation (meters): ");

            final String areaCode = selectAirControlArea(lat, lon);

            if (areaCode == null) {
                return;
            }

            controller.registerAirport(
                    icao, iata, name, country, town, lat, lon, elev, areaCode
            );

            ConsoleStyle.printSuccess("Airport registered successfully.");

        } catch (Exception e) {
            ConsoleStyle.printError("Could not register airport: " + e.getMessage());
        }
    }

    /**
     * Safely reads a double value from the console.
     * * <p>Ensures that the input is a valid numeric value, preventing
     * application crashes due to invalid formatting and handling scanner
     * buffer issues.</p>
     * * @param prompt the message displayed to the user.
     * @return the valid double value entered by the user.
     */
    private double readDouble(String prompt) {
        while (true) {
            try {
                ConsoleStyle.printPrompt(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                ConsoleStyle.printWarning("Invalid number format. Please use '.' for decimals.");
            }
        }
    }

    private String selectAirControlArea(final double latitude, final double longitude) {
        final List<String> matchingAreas = controller.airControlAreasContaining(latitude, longitude);

        if (matchingAreas.isEmpty()) {
            ConsoleStyle.printWarning("No air control area contains the airport coordinates.");
            ConsoleStyle.printWarning("The airport cannot be registered until a matching air control area exists.");
            return null;
        }

        if (matchingAreas.size() == 1) {
            final String selectedArea = matchingAreas.getFirst();
            ConsoleStyle.printSuccess("Matching air control area: " + selectedArea);
            return areaCodeFrom(selectedArea);
        }

        ConsoleStyle.printSection("Matching air control areas");

        for (int i = 0; i < matchingAreas.size(); i++) {
            System.out.println(ConsoleStyle.option(i + 1, matchingAreas.get(i)));
        }

        while (true) {
            ConsoleStyle.printPrompt("Choose air control area: ");
            final String input = scanner.nextLine().trim();

            try {
                final int selected = Integer.parseInt(input);
                if (selected >= 1 && selected <= matchingAreas.size()) {
                    return areaCodeFrom(matchingAreas.get(selected - 1));
                }
            } catch (NumberFormatException ignored) {
                // Fall through to the validation message below.
            }

            ConsoleStyle.printWarning("Invalid option. Please choose a listed number.");
        }
    }

    private String areaCodeFrom(final String areaOption) {
        final int separator = areaOption.indexOf('|');

        if (separator < 0) {
            return areaOption.trim();
        }

        return areaOption.substring(0, separator).trim();
    }
}
