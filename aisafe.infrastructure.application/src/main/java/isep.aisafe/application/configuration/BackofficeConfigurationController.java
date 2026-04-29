package isep.aisafe.application.configuration;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

public class BackofficeConfigurationController {

    private final AisafeAuthorizationService authz;

    public BackofficeConfigurationController() {
        this(new AisafeAuthorizationService());
    }

    BackofficeConfigurationController(final AisafeAuthorizationService authz) {
        this.authz = authz;
    }

    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        System.out.println("Backoffice configuration use case authorized.");
    }
}
