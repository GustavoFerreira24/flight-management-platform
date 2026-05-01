package isep.aisafe.app.backoffice.console.presentation.enginemodel;

import isep.aisafe.application.enginemodel.RegisterEngineModelController;

import java.util.Scanner;

public class RegisterEngineModelUI {

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
            System.out.println();
            System.out.println("Register Engine Model");

            System.out.print("Model name: ");
            final String modelName = scanner.nextLine();

            System.out.print("Manufacturer: ");
            final String manufacturer = scanner.nextLine();

            System.out.print("Engine type (e.g. TURBOFAN): ");
            final String engineType = scanner.nextLine();

            System.out.print("Fuel type (e.g. JET A-1): ");
            final String fuelType = scanner.nextLine();

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

            System.out.println("Engine model registered successfully.");
        } catch (RuntimeException e) {
            System.out.println("Could not register engine model: " + e.getMessage());
        }
    }

    private double readDouble(final String prompt) {
        System.out.print(prompt);
        return Double.parseDouble(scanner.nextLine());
    }
}
