package isep.aisafe.application.usermanagement;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

/**
 * Controller for the user management entry point in the backoffice application.
 */
public class ManageUsersController {

    private final AisafeAuthorizationService authz;

    /**
     * Creates the controller with the default AISafe authorization service.
     */
    public ManageUsersController() {
        this(new AisafeAuthorizationService());
    }

    ManageUsersController(final AisafeAuthorizationService authz) {
        this.authz = authz;
    }

    /**
     * Authorizes access to user management functionality.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.USER_MANAGEMENT);
        // TODO implement user management behavior when the feature is developed.
    }
}
