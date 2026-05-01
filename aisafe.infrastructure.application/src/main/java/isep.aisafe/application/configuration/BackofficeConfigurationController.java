package isep.aisafe.application.configuration;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

/**
 * Controller for backoffice configuration actions.
 */
public class BackofficeConfigurationController {

    private final AisafeAuthorizationService authz;

    /**
     * Creates the controller with the default AISafe authorization service.
     */
    public BackofficeConfigurationController() {
        this(new AisafeAuthorizationService());
    }

    BackofficeConfigurationController(final AisafeAuthorizationService authz) {
        this.authz = authz;
    }

    /**
     * Authorizes access to backoffice configuration functionality.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        System.out.println("Backoffice configuration use case authorized.");
    }
}
