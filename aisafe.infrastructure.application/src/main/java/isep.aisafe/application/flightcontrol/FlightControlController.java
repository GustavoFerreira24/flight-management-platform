package isep.aisafe.application.flightcontrol;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

public class FlightControlController {

    private final AisafeAuthorizationService authz;

    public FlightControlController() {
        this(new AisafeAuthorizationService());
    }

    FlightControlController(final AisafeAuthorizationService authz) {
        this.authz = authz;
    }

    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_CONTROL);

        System.out.println("Flight control use case authorized.");
    }
}
