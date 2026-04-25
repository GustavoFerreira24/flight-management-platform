package isep.aisafe.app.backoffice.console;

import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.application.UserManagementService;
import isep.aisafe.usermanagement.domain.AisafeRoles;

import java.util.Set;

public class AisafeBootstrapper {

    public static void bootstrap() {

        final UserManagementService userService = AuthzRegistry.userService();

        register(userService, "admin", "Password1",
                "Admin", "User", "admin@aisafe.com",
                Set.of(AisafeRoles.ADMIN));

        register(userService, "backoffice", "Password1",
                "Backoffice", "User", "backoffice@aisafe.com",
                Set.of(AisafeRoles.BACKOFFICE_OPERATOR));

        register(userService, "weather", "Password1",
                "Weather", "Person", "weather@aisafe.com",
                Set.of(AisafeRoles.WEATHER_PERSON));

        register(userService, "fco", "Password1",
                "Flight", "Control", "fco@aisafe.com",
                Set.of(AisafeRoles.FLIGHT_CONTROL_OPERATOR));
    }

    private static void register(
            final UserManagementService userService,
            final String username,
            final String password,
            final String firstName,
            final String lastName,
            final String email,
            final Set<?> roles) {

        try {
            userService.registerNewUser(
                    username,
                    password,
                    firstName,
                    lastName,
                    email,
                    (Set) roles
            );
            System.out.println("User created: " + username);
        } catch (Exception e) {
            System.out.println("User already exists: " + username);
        }
    }
}