package isep.aisafe.application.usermanagement;

import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

import java.util.Set;

/**
 * Coordinates the backoffice user registration use case.
 *
 * <p>The controller enforces authorization, validates the submitted account data and then
 * delegates actual user creation to the framework user management service. Domain-specific
 * user details that are not yet modeled in code should be added here when that aggregate exists.</p>
 */
public class RegisterBackofficeUserController {

    private final AisafeAuthorizationService authz;
    private final UserManagementService userService;
    private final UserRegistrationValidator validator;

    /**
     * Creates the controller with production authorization, validation and user management services.
     */
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

    /**
     * Registers a new backoffice user after checking permissions and validating input.
     *
     * @param username unique username for the new user
     * @param password raw password to be validated and encoded by the framework
     * @param firstName user's first name
     * @param lastName user's last name
     * @param email user's email address
     * @param roles roles assigned to the new user
     */
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
