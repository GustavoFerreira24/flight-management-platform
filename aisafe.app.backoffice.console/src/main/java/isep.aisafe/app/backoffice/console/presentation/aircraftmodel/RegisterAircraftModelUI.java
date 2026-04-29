package isep.aisafe.app.backoffice.console.presentation.aircraftmodel;

import isep.aisafe.application.aircraftmodel.ManageAircraftModelController;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RegisterAircraftModelUI {

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
            System.out.println();
            System.out.println("Register Aircraft Model");

            System.out.print("Model name: ");
            final String modelName = scanner.nextLine();

            System.out.print("Manufacturer: ");
            final String manufacturer = scanner.nextLine();

            System.out.print("Type (PASSENGER/CARGO/MIXED): ");
            final String type = scanner.nextLine();

            System.out.print("Motorization (e.g. TURBOFAN): ");
            final String motorization = scanner.nextLine();

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

            System.out.println("Format for each engine designator: MODEL | MANUFACTURER");
            System.out.print("Certified engine designators (comma-separated): ");
            final List<String> certifiedEngineDesignators = Arrays.stream(scanner.nextLine().split(","))
                    .map(String::trim)
                    .filter(value -> !value.isEmpty())
                    .toList();

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

            System.out.println("Aircraft model registered successfully.");
        } catch (RuntimeException e) {
            System.out.println("Could not register aircraft model: " + e.getMessage());
        }
    }

    private static double readDouble(final Scanner scanner, final String prompt) {
        System.out.print(prompt);
        return Double.parseDouble(scanner.nextLine());
    }

    private static int readInt(final Scanner scanner, final String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(scanner.nextLine());
    }
}
