package isep.aisafe.application.aircompany;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

public class AirCompanyConfigurationController {

    private final AisafeAuthorizationService authz;

    public AirCompanyConfigurationController() {
        this(new AisafeAuthorizationService());
    }

    AirCompanyConfigurationController(final AisafeAuthorizationService authz) {
        this.authz = authz;
    }

    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        System.out.println("Air company configuration use case authorized.");
    }
}
