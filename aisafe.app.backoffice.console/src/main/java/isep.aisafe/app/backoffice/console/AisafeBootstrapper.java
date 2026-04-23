package isep.aisafe.app.backoffice.console;

import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;

import java.util.Set;

public class AisafeBootstrapper {

    public static void bootstrap() {

        final UserManagementService userService = AuthzRegistry.userService();

        final String username = "admin";
        final String password = "Password1";

        try {
            userService.registerNewUser(
                    username,
                    password,
                    "Admin",
                    "User",
                    "admin@aisafe.com",
                    Set.of(Role.valueOf("ADMIN"))
            );

            System.out.println("Admin user created.");

        } catch (Exception e) {
            // normalmente já existe
            System.out.println("Admin user already exists.");
        }
    }
}