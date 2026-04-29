package isep.aisafe.app.backoffice.console.presentation.usermanagement;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.application.usermanagement.RegisterBackofficeUserController;
import isep.aisafe.usermanagement.domain.AisafeRoles;

import java.util.Scanner;
import java.util.Set;

public class RegisterBackofficeUserUI {

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

        System.out.print("Username: ");
        final String username = scanner.nextLine();

        System.out.print("Password: ");
        final String password = scanner.nextLine();

        System.out.print("First name: ");
        final String firstName = scanner.nextLine();

        System.out.print("Last name: ");
        final String lastName = scanner.nextLine();

        System.out.print("Email: ");
        final String email = scanner.nextLine();

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

    private Set<Role> askRole(final Scanner scanner) {
        System.out.println("Role:");
        System.out.println("1 - Admin");
        System.out.println("2 - Backoffice Operator");
        System.out.print("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            return Set.of(AisafeRoles.ADMIN);
        }

        return Set.of(AisafeRoles.BACKOFFICE_OPERATOR);
    }
}
