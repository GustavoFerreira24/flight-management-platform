package isep.aisafe.infrastructure.authz;

public class ProtectedActionController {

    private final AisafeAuthorizationService authorizationService;

    public ProtectedActionController() {
        this(new AisafeAuthorizationService());
    }

    ProtectedActionController(final AisafeAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public void executeAdminOnlyAction() {
        authorizationService.ensureAuthenticatedUserHasAnyOf(AisafePermissions.USER_MANAGEMENT);

        System.out.println("Admin-only action executed.");
    }
}
