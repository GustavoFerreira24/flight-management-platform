package isep.aisafe.application.usermanagement;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

public class ManageUsersController {

    private final AisafeAuthorizationService authz = new AisafeAuthorizationService();

    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.USER_MANAGEMENT);

        System.out.println("Manage users use case authorized.");
    }
}