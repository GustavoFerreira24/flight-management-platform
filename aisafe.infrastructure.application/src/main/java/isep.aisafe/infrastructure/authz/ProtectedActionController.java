package isep.aisafe.infrastructure.authz;

public class ProtectedActionController {

    private final AisafeAuthorizationService authorizationService =
            new AisafeAuthorizationService();

    public void executeAdminOnlyAction() {
        authorizationService.ensureAuthenticatedUserHasAnyOf(AisafePermissions.USER_MANAGEMENT);

        System.out.println("Admin-only action executed.");
    }
}