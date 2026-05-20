package isep.aisafe.app.backoffice.console.presentation.weather;

import isep.aisafe.application.weather.RegisterWeatherDataController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RegisterWeatherDataUI {

    private static final Map<String, Map<String, String>> PARAMETER_UNITS = new LinkedHashMap<>();

    static {
        Map<String, String> tempUnits = new LinkedHashMap<>();
        tempUnits.put("1", "C");
        PARAMETER_UNITS.put("TEMPERATURE", tempUnits);

        Map<String, String> windSpeedUnits = new LinkedHashMap<>();
        windSpeedUnits.put("1", "M/S");
        windSpeedUnits.put("2", "KT");
        PARAMETER_UNITS.put("WIND_SPEED", windSpeedUnits);

        Map<String, String> windDirUnits = new LinkedHashMap<>();
        windDirUnits.put("1", "DEG");
        PARAMETER_UNITS.put("WIND_DIRECTION", windDirUnits);

        Map<String, String> pressureUnits = new LinkedHashMap<>();
        pressureUnits.put("1", "HPA");
        PARAMETER_UNITS.put("PRESSURE", pressureUnits);

        Map<String, String> humidityUnits = new LinkedHashMap<>();
        humidityUnits.put("1", "PERCENT");
        PARAMETER_UNITS.put("HUMIDITY", humidityUnits);
    }

    private final RegisterWeatherDataController controller = new RegisterWeatherDataController();
    private final Scanner scanner;

    public RegisterWeatherDataUI() {
        this(new Scanner(System.in));
    }

    public RegisterWeatherDataUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    public void show() {
        System.out.println();
        System.out.println("Register Weather Data");

        final String day = askDate();
        final String areaCode = selectAirControlArea();

        if (areaCode == null) {
            return;
        }

        final double[] coordinates = askCoordinatesInside(areaCode);
        final double latitude = coordinates[0];
        final double longitude = coordinates[1];

        final String parameter = askParameter();
        final double value = askValue(parameter);
        final String unit = askUnit(parameter);
        final double altitude = askAltitude();

        try {
            controller.registerWeatherMeasurement(
                    day,
                    areaCode,
                    parameter,
                    value,
                    unit,
                    altitude,
                    latitude,
                    longitude
            );

            System.out.println("Weather measurement registered successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid weather data: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Could not register weather data. " + e.getMessage());
        }
    }

    private String askDate() {
        final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        while (true) {
            System.out.print("Date (YYYY-MM-DD): ");
            final String input = scanner.nextLine().trim();

            if (input.isBlank()) {
                System.out.println("Date is required.");
                continue;
            }

            try {
                LocalDate.parse(input, formatter);
                return input;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD (e.g., 2026-05-17).");
            }
        }
    }

    private String askRequiredText(final String label, final String errorMessage) {
        while (true) {
            System.out.print(label + ": ");
            final String value = scanner.nextLine();

            if (value != null && !value.isBlank()) {
                return value.trim();
            }

            System.out.println(errorMessage);
        }
    }

    private String askParameter() {
        while (true) {
            System.out.println("Weather parameter:");
            System.out.println("1 - Temperature");
            System.out.println("2 - Wind Speed");
            System.out.println("3 - Wind Direction");
            System.out.println("4 - Pressure");
            System.out.println("5 - Humidity");
            System.out.print("Option: ");

            final String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    return "TEMPERATURE";
                case "2":
                    return "WIND_SPEED";
                case "3":
                    return "WIND_DIRECTION";
                case "4":
                    return "PRESSURE";
                case "5":
                    return "HUMIDITY";
                default:
                    System.out.println("Invalid parameter option.");
            }
        }
    }

    private double askValue(final String parameter) {
        return switch (parameter) {
            case "TEMPERATURE" -> askDouble(
                    "Temperature (-100 to 70 C)",
                    "Temperature must be a number between -100 and 70.",
                    -100,
                    70
            );
            case "WIND_SPEED" -> askDouble(
                    "Wind speed (>= 0)",
                    "Wind speed must be a non-negative number.",
                    0,
                    Double.MAX_VALUE
            );
            case "WIND_DIRECTION" -> askDouble(
                    "Wind direction (0 to 360 degrees)",
                    "Wind direction must be a number between 0 and 360.",
                    0,
                    360
            );
            case "PRESSURE" -> askDouble(
                    "Pressure (800 to 1100 hPa)",
                    "Pressure must be a number between 800 and 1100.",
                    800,
                    1100
            );
            case "HUMIDITY" -> askDouble(
                    "Humidity (0 to 100 %)",
                    "Humidity must be a number between 0 and 100.",
                    0,
                    100
            );
            default -> throw new IllegalArgumentException("Unknown parameter: " + parameter);
        };
    }

    private String askUnit(final String parameter) {
        final Map<String, String> units = PARAMETER_UNITS.get(parameter);

        while (true) {
            System.out.println("Unit:");
            for (final Map.Entry<String, String> entry : units.entrySet()) {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }
            System.out.print("Option: ");

            final String option = scanner.nextLine().trim();
            if (units.containsKey(option)) {
                return units.get(option);
            }

            System.out.println("Invalid unit option.");
        }
    }

    private double askAltitude() {
        return askDouble(
                "Altitude (meters, >= 0)",
                "Altitude must be a non-negative number.",
                0,
                Double.MAX_VALUE
        );
    }

    private double askLatitude() {
        return askDouble(
                "Latitude (-90 to 90)",
                "Latitude must be a number between -90 and 90.",
                -90,
                90
        );
    }

    private double askLongitude() {
        return askDouble(
                "Longitude (-180 to 180)",
                "Longitude must be a number between -180 and 180.",
                -180,
                180
        );
    }

    private double askDouble(final String label, final String errorMessage, final double min, final double max) {
        while (true) {
            System.out.print(label + ": ");
            final String input = scanner.nextLine().trim();

            if (input.isBlank()) {
                System.out.println(label + " is required.");
                continue;
            }

            try {
                final double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println(errorMessage);
            } catch (NumberFormatException e) {
                System.out.println(errorMessage);
            }
        }
    }

    private String selectAirControlArea() {
        final List<String> areas = controller.airControlAreas();

        if (areas.isEmpty()) {
            System.out.println("No air control areas are registered.");
            System.out.println("The weather measurement cannot be registered until an air control area exists.");
            return null;
        }

        if (areas.size() == 1) {
            final String selectedArea = areas.getFirst();
            System.out.println("Selected air control area: " + selectedArea);
            return areaCodeFrom(selectedArea);
        }

        System.out.println("Air control areas:");

        for (int i = 0; i < areas.size(); i++) {
            System.out.printf("%d - %s%n", i + 1, areas.get(i));
        }

        while (true) {
            System.out.print("Choose air control area: ");
            final String input = scanner.nextLine().trim();

            try {
                final int selected = Integer.parseInt(input);
                if (selected >= 1 && selected <= areas.size()) {
                    return areaCodeFrom(areas.get(selected - 1));
                }
            } catch (NumberFormatException ignored) {
                // Fall through to the validation message below.
            }

            System.out.println("Invalid option. Please choose a listed number.");
        }
    }

    private String areaCodeFrom(final String areaOption) {
        final int separator = areaOption.indexOf('|');

        if (separator < 0) {
            return areaOption.trim();
        }

        return areaOption.substring(0, separator).trim();
    }

    private double[] askCoordinatesInside(final String areaCode) {
        while (true) {
            final double latitude = askLatitude();
            final double longitude = askLongitude();

            if (controller.airControlAreaContains(areaCode, latitude, longitude)) {
                return new double[]{latitude, longitude};
            }

            System.out.println("Coordinates are outside the selected air control area.");
            System.out.println("Please enter coordinates inside " + areaCode + ".");
        }
    }
}
