package isep.aisafe.app.backoffice.console.presentation.usermanagement;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.usermanagement.RegisterBackofficeUserController;
import isep.aisafe.usermanagement.domain.AisafeRoles;

import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class RegisterBackofficeUserUI {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final RegisterBackofficeUserController controller =
            new RegisterBackofficeUserController();
    private final Scanner scanner;

    public RegisterBackofficeUserUI() {
        this(new Scanner(System.in));
    }

    public RegisterBackofficeUserUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    public void show() {
        ConsoleStyle.printSection("Register Backoffice User");

        final String username = askRequiredText("Username", "Username is required.");
        final String password = askRequiredText("Password", "Password is required.");
        final String firstName = askRequiredText("First name", "First name is required.");
        final String lastName = askRequiredText("Last name", "Last name is required.");
        final String email = askEmail();

        final Set<Role> roles = askRole(scanner);

        try {
            controller.registerUser(
                    username,
                    password,
                    firstName,
                    lastName,
                    email,
                    roles
            );
        } catch (IllegalArgumentException e) {
            ConsoleStyle.printError("Invalid user data: " + e.getMessage());
        } catch (Exception e) {
            ConsoleStyle.printError("Could not register user. The username or email may already exist.");
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

    private Set<Role> askRole(final Scanner scanner) {
        while (true) {
            ConsoleStyle.printMenu(
                    "Role",
                    ConsoleStyle.option(1, "Admin"),
                    ConsoleStyle.option(2, "Backoffice Operator")
            );
            ConsoleStyle.printPrompt("Option: ");

            final String option = scanner.nextLine();

            if ("1".equals(option)) {
                return Set.of(AisafeRoles.ADMIN);
            }

            if ("2".equals(option)) {
                return Set.of(AisafeRoles.BACKOFFICE_OPERATOR);
            }

            ConsoleStyle.printWarning("Invalid role option.");
        }
    }
}
