package isep.aisafe.application.aircompany;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

/**
 * Controller for air company configuration actions.
 */
public class AirCompanyConfigurationController {

    private final AisafeAuthorizationService authz;

    /**
     * Creates the controller with the default AISafe authorization service.
     */
    public AirCompanyConfigurationController() {
        this(new AisafeAuthorizationService());
    }

    AirCompanyConfigurationController(final AisafeAuthorizationService authz) {
        this.authz = authz;
    }

    /**
     * Authorizes access to air company configuration functionality.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        System.out.println("Air company configuration use case authorized.");
    }
}
