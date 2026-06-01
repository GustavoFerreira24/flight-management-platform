package isep.aisafe.app.backoffice.console.presentation.enginemodel;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.enginemodel.RegisterEngineModelController;

import java.util.List;
import java.util.Scanner;

public class RegisterEngineModelUI {

    private static final List<String> ENGINE_TYPES = List.of("TURBOFAN", "TURBOPROP", "TURBOJET", "PISTON");
    private static final List<String> FUEL_TYPES = List.of("JET A-1", "AVGAS 100LL", "SAF", "DIESEL");

    private final RegisterEngineModelController controller = new RegisterEngineModelController();
    private final Scanner scanner;

    public RegisterEngineModelUI() {
        this(new Scanner(System.in));
    }

    public RegisterEngineModelUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    public void show() {
        try {
            ConsoleStyle.printSection("Register Engine Model");

            ConsoleStyle.printPrompt("Model name: ");
            final String modelName = scanner.nextLine();

            final String manufacturer = selectManufacturer();

            final String engineType = selectOption("Engine type", ENGINE_TYPES);

            final String fuelType = selectOption("Fuel type", FUEL_TYPES);

            final double power = readDouble("Power: ");
            final double thrust = readDouble("Thrust: ");
            final double tsfc = readDouble("TSFC: ");

            controller.registerEngineModel(
                    modelName,
                    manufacturer,
                    engineType,
                    fuelType,
                    power,
                    thrust,
                    tsfc
            );

            ConsoleStyle.printSuccess("Engine model registered successfully.");
        } catch (RuntimeException e) {
            ConsoleStyle.printError("Could not register engine model: " + e.getMessage());
        }
    }

    private double readDouble(final String prompt) {
        ConsoleStyle.printPrompt(prompt);
        return Double.parseDouble(scanner.nextLine());
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

    private String selectManufacturer() {
        final List<String> manufacturers = controller.engineManufacturerDesignators();

        if (manufacturers.isEmpty()) {
            throw new IllegalStateException("No engine manufacturers are registered.");
        }

        return selectOption("Manufacturer", manufacturers);
    }
}
