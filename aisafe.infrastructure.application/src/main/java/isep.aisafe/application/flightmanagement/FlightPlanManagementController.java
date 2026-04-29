package isep.aisafe.application.flightmanagement;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

public class FlightPlanManagementController {

    private final AisafeAuthorizationService authz;

    public FlightPlanManagementController() {
        this(new AisafeAuthorizationService());
    }

    FlightPlanManagementController(final AisafeAuthorizationService authz) {
        this.authz = authz;
    }

    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT);

        System.out.println("Flight plan management use case authorized.");
    }
}
