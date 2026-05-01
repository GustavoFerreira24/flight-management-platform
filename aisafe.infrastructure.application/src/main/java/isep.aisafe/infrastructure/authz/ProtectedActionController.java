package isep.aisafe.infrastructure.authz;

/**
 * Small controller used to exercise and demonstrate protected action execution.
 */
public class ProtectedActionController {

    private final AisafeAuthorizationService authorizationService;

    /**
     * Creates the controller with the default AISafe authorization service.
     */
    public ProtectedActionController() {
        this(new AisafeAuthorizationService());
    }

    ProtectedActionController(final AisafeAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    /**
     * Executes an action that is only available to user-management roles.
     */
    public void executeAdminOnlyAction() {
        authorizationService.ensureAuthenticatedUserHasAnyOf(AisafePermissions.USER_MANAGEMENT);

        System.out.println("Admin-only action executed.");
    }
}
