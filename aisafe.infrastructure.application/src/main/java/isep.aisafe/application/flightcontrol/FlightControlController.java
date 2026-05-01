package isep.aisafe.application.flightcontrol;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

/**
 * Controller for flight control operations.
 */
public class FlightControlController {

    private final AisafeAuthorizationService authz;

    /**
     * Creates the controller with the default AISafe authorization service.
     */
    public FlightControlController() {
        this(new AisafeAuthorizationService());
    }

    FlightControlController(final AisafeAuthorizationService authz) {
        this.authz = authz;
    }

    /**
     * Authorizes access to flight control functionality.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_CONTROL);
        // TODO implement flight control behavior when the feature is developed.
    }
}
