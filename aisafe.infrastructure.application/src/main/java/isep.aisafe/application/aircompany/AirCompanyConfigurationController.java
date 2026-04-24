package isep.aisafe.application.aircompany;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

public class AirCompanyConfigurationController {

    private final AisafeAuthorizationService authz = new AisafeAuthorizationService();

    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        System.out.println("Air company configuration use case authorized.");
    }
}