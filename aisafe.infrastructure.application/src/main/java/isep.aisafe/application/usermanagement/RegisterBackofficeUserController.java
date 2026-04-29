package isep.aisafe.application.usermanagement;

import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

import java.util.Set;

public class RegisterBackofficeUserController {

    private final AisafeAuthorizationService authz;
    private final UserManagementService userService;
    private final UserRegistrationValidator validator;

    public RegisterBackofficeUserController() {
        this(new AisafeAuthorizationService(), null, new UserRegistrationValidator());
    }

    RegisterBackofficeUserController(
            final AisafeAuthorizationService authz,
            final UserManagementService userService,
            final UserRegistrationValidator validator) {
        this.authz = authz;
        this.userService = userService;
        this.validator = validator;
    }

    public void registerUser(
            final String username,
            final String password,
            final String firstName,
            final String lastName,
            final String email,
            final Set<Role> roles) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.USER_REGISTRATION);

        validator.validate(username, password, firstName, lastName, email, roles);

        userService().registerNewUser(
                username,
                password,
                firstName,
                lastName,
                email,
                roles
        );

        System.out.println("User registered successfully.");
    }

    private UserManagementService userService() {
        if (userService != null) {
            return userService;
        }
        return AuthzRegistry.userService();
    }
}
