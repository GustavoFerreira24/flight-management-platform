package isep.aisafe.app.backoffice.console.presentation.usermanagement;

import eapli.framework.infrastructure.authz.domain.model.Role;
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
        System.out.println();
        System.out.println("Register Backoffice User");

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
            System.out.println("Invalid user data: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Could not register user. The username or email may already exist.");
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

    private String askEmail() {
        while (true) {
            final String email = askRequiredText("Email", "Email is required.");

            if (EMAIL_PATTERN.matcher(email).matches()) {
                return email;
            }

            System.out.println("Email format is invalid.");
        }
    }

    private Set<Role> askRole(final Scanner scanner) {
        while (true) {
            System.out.println("Role:");
            System.out.println("1 - Admin");
            System.out.println("2 - Backoffice Operator");
            System.out.print("Option: ");

            final String option = scanner.nextLine();

            if ("1".equals(option)) {
                return Set.of(AisafeRoles.ADMIN);
            }

            if ("2".equals(option)) {
                return Set.of(AisafeRoles.BACKOFFICE_OPERATOR);
            }

            System.out.println("Invalid role option.");
        }
    }
}
