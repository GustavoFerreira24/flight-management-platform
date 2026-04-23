package isep.aisafe.application.flightmanagement;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

public class FlightPlanManagementController {

    private final AisafeAuthorizationService authz = new AisafeAuthorizationService();

    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT);

        System.out.println("Flight plan management use case authorized.");
    }
}