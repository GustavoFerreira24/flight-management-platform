package isep.aisafe.app.backoffice.console.presentation.customercollaborator;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.customercollaborator.RegisterCustomerCollaboratorController;

import java.util.Scanner;
import java.util.regex.Pattern;

public class RegisterCustomerCollaboratorUI {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PERSON_NAME_PATTERN =
            Pattern.compile("[A-Za-z]+(?:[ '-][A-Za-z]+)*");

    private final RegisterCustomerCollaboratorController controller =
            new RegisterCustomerCollaboratorController();
    private final Scanner scanner;

    public RegisterCustomerCollaboratorUI() {
        this(new Scanner(System.in));
    }

    public RegisterCustomerCollaboratorUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    public void show() {
        ConsoleStyle.printSection("Register Customer Collaborator");

        final String customerType = askCustomerType();
        final String customerCode = "1".equals(customerType) ? askIataCode() : askAreaCode();

        final String firstName = askPersonName("First name", "First name is required.");
        final String lastName = askPersonName("Last name", "Last name is required.");
        final String email = askEmail();
        final String initialPassword = askRequiredText("Initial password", "Initial password is required.");

        try {
            if ("1".equals(customerType)) {
                controller.registerCollaboratorForAirTransportCompany(
                        firstName,
                        lastName,
                        email,
                        customerCode,
                        initialPassword
                );
            } else {
                controller.registerCollaboratorForAirControlArea(
                        firstName,
                        lastName,
                        email,
                        customerCode,
                        initialPassword
                );
            }

            ConsoleStyle.printSuccess("Customer collaborator registered successfully.");
        } catch (IllegalArgumentException e) {
            ConsoleStyle.printError("Invalid customer collaborator data: " + e.getMessage());
        } catch (Exception e) {
            ConsoleStyle.printError("Could not register customer collaborator. The email or system user may already exist.");
        }
    }

    private String askRequiredText(final String label, final String errorMessage) {
        while (true) {
            ConsoleStyle.printPrompt(label + ": ");
            final String value = scanner.nextLine();

            if (value != null && !value.isBlank()) {
                return value.trim();
            }

            ConsoleStyle.printWarning(errorMessage);
        }
    }

    private String askEmail() {
        while (true) {
            final String email = askRequiredText("Email", "Email is required.");

            if (EMAIL_PATTERN.matcher(email).matches()) {
                return email;
            }

            ConsoleStyle.printWarning("Email format is invalid.");
        }
    }

    private String askPersonName(final String label, final String errorMessage) {
        while (true) {
            final String value = askRequiredText(label, errorMessage);

            if (PERSON_NAME_PATTERN.matcher(value).matches()) {
                return value;
            }

            ConsoleStyle.printWarning(label + " must contain only letters, spaces, hyphens or apostrophes.");
        }
    }

    private String askCustomerType() {
        while (true) {
            ConsoleStyle.printMenu(
                    "Customer type",
                    ConsoleStyle.option(1, "Air Transport Company"),
                    ConsoleStyle.option(2, "Air Control Area")
            );
            ConsoleStyle.printPrompt("Option: ");

            final String option = scanner.nextLine().trim();

            if ("1".equals(option) || "2".equals(option)) {
                return option;
            }

            ConsoleStyle.printWarning("Invalid customer type option.");
        }
    }

    private String askIataCode() {
        while (true) {
            final String value = askRequiredText(
                    "Air transport company IATA code (2 letters)",
                    "Air transport company IATA code is required."
            ).toUpperCase();

            if (value.matches("[A-Z]{2}")) {
                return value;
            }

            ConsoleStyle.printWarning("Invalid IATA code. It must have exactly 2 letters.");
        }
    }

    private String askAreaCode() {
        while (true) {
            final String value = askRequiredText(
                    "Air control area code",
                    "Air control area code is required."
            ).toUpperCase();

            if (value.matches("[A-Z0-9]{2,10}")) {
                return value;
            }

            ConsoleStyle.printWarning("Invalid area code. Use 2 to 10 letters or digits.");
        }
    }
}
