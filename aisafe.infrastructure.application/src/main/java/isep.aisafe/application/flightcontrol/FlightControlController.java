package isep.aisafe.application.flightcontrol;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

public class FlightControlController {

    private final AisafeAuthorizationService authz = new AisafeAuthorizationService();

    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_CONTROL);

        System.out.println("Flight control use case authorized.");
    }
}