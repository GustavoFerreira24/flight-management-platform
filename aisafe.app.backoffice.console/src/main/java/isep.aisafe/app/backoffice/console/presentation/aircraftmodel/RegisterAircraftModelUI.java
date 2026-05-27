package isep.aisafe.app.backoffice.console.presentation.aircraftmodel;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.aircraftmodel.ManageAircraftModelController;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class RegisterAircraftModelUI {

    private static final List<String> MANUFACTURERS = List.of("AIRBUS", "BOEING", "EMBRAER", "ATR", "BOMBARDIER", "OTHER");
    private static final List<String> AIRCRAFT_TYPES = List.of("PASSENGER", "CARGO", "MIXED");
    private static final List<String> MOTORIZATIONS = List.of("TURBOFAN", "TURBOPROP", "TURBOJET", "PISTON");

    private final ManageAircraftModelController controller = new ManageAircraftModelController();
    private final Scanner scanner;

    public RegisterAircraftModelUI() {
        this(new Scanner(System.in));
    }

    public RegisterAircraftModelUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    public void show() {
        try {
            ConsoleStyle.printSection("Register Aircraft Model");

            ConsoleStyle.printPrompt("Model name: ");
            final String modelName = scanner.nextLine();

            final String manufacturer = selectOption("Manufacturer", MANUFACTURERS);

            final String type = selectOption("Type", AIRCRAFT_TYPES);

            final String motorization = selectOption("Motorization", MOTORIZATIONS);

            final double maxRange = readDouble(scanner, "Maximum range: ");
            final double emptyWeight = readDouble(scanner, "Empty weight: ");
            final double mtow = readDouble(scanner, "MTOW: ");
            final double mzfw = readDouble(scanner, "MZFW: ");
            final double maxFuelCapacity = readDouble(scanner, "Maximum fuel capacity: ");
            final double serviceCeiling = readDouble(scanner, "Service ceiling: ");
            final double cruiseSpeed = readDouble(scanner, "Cruise speed: ");
            final double wingArea = readDouble(scanner, "Wing area: ");
            final double dragCoefficient = readDouble(scanner, "Drag coefficient: ");
            final double liftCoefficient = readDouble(scanner, "Lift coefficient: ");
            final int passengerCapacity = readInt(scanner, "Passenger capacity: ");

            final List<String> certifiedEngineDesignators = selectCertifiedEngineModels(motorization);

            controller.registerAircraftModel(
                    modelName,
                    manufacturer,
                    type,
                    motorization,
                    maxRange,
                    emptyWeight,
                    mtow,
                    mzfw,
                    maxFuelCapacity,
                    serviceCeiling,
                    cruiseSpeed,
                    wingArea,
                    dragCoefficient,
                    liftCoefficient,
                    passengerCapacity,
                    certifiedEngineDesignators
            );

            ConsoleStyle.printSuccess("Aircraft model registered successfully.");
        } catch (RuntimeException e) {
            ConsoleStyle.printError("Could not register aircraft model: " + e.getMessage());
        }
    }

    private static double readDouble(final Scanner scanner, final String prompt) {
        ConsoleStyle.printPrompt(prompt);
        return Double.parseDouble(scanner.nextLine());
    }

    private static int readInt(final Scanner scanner, final String prompt) {
        ConsoleStyle.printPrompt(prompt);
        return Integer.parseInt(scanner.nextLine());
    }

    private String selectOption(final String label, final List<String> options) {
        ConsoleStyle.printSection(label);

        for (int i = 0; i < options.size(); i++) {
            System.out.println(ConsoleStyle.option(i + 1, options.get(i)));
        }

        while (true) {
            ConsoleStyle.printPrompt("Option: ");
            final String input = scanner.nextLine().trim();

            try {
                final int selected = Integer.parseInt(input);
                if (selected >= 1 && selected <= options.size()) {
                    final String value = options.get(selected - 1);
                    if ("OTHER".equals(value)) {
                        ConsoleStyle.printPrompt("Enter " + label.toLowerCase() + ": ");
                        return scanner.nextLine();
                    }

                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Fall through to the validation message below.
            }

            ConsoleStyle.printWarning("Invalid option. Please choose a listed number.");
        }
    }

    private List<String> selectCertifiedEngineModels(final String motorization) {
        final List<String> compatibleEngineDesignators = controller.compatibleEngineModelDesignators(motorization);

        if (compatibleEngineDesignators.isEmpty()) {
            throw new IllegalStateException("No compatible engine models are registered for motorization " + motorization + ".");
        }

        ConsoleStyle.printSection("Certified engine models");

        for (int i = 0; i < compatibleEngineDesignators.size(); i++) {
            System.out.println(ConsoleStyle.option(i + 1, compatibleEngineDesignators.get(i)));
        }

        while (true) {
            ConsoleStyle.printPrompt("Options (comma-separated): ");
            final String input = scanner.nextLine().trim();
            final Set<String> selectedEngineDesignators = new LinkedHashSet<>();
            boolean invalidOption = false;

            for (String rawOption : input.split(",")) {
                final String option = rawOption.trim();
                if (option.isEmpty()) {
                    invalidOption = true;
                    continue;
                }

                try {
                    final int selected = Integer.parseInt(option);
                    if (selected >= 1 && selected <= compatibleEngineDesignators.size()) {
                        selectedEngineDesignators.add(compatibleEngineDesignators.get(selected - 1));
                    } else {
                        invalidOption = true;
                    }
                } catch (NumberFormatException ignored) {
                    invalidOption = true;
                }
            }

            if (!invalidOption && !selectedEngineDesignators.isEmpty()) {
                return new ArrayList<>(selectedEngineDesignators);
            }

            ConsoleStyle.printWarning("Invalid option. Please choose at least one listed number.");
        }
    }
}
