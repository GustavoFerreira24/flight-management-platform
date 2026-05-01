package isep.aisafe.application.flightmanagement;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

/**
 * Controller for flight plan management operations.
 */
public class FlightPlanManagementController {

    private final AisafeAuthorizationService authz;

    /**
     * Creates the controller with the default AISafe authorization service.
     */
    public FlightPlanManagementController() {
        this(new AisafeAuthorizationService());
    }

    FlightPlanManagementController(final AisafeAuthorizationService authz) {
        this.authz = authz;
    }

    /**
     * Authorizes access to flight plan management functionality.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT);

        System.out.println("Flight plan management use case authorized.");
    }
}
