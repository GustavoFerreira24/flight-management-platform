package isep.aisafe.application.usermanagement;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

public class ManageUsersController {

    private final AisafeAuthorizationService authz;

    public ManageUsersController() {
        this(new AisafeAuthorizationService());
    }

    ManageUsersController(final AisafeAuthorizationService authz) {
        this.authz = authz;
    }

    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.USER_MANAGEMENT);

        System.out.println("Manage users use case authorized.");
    }
}
