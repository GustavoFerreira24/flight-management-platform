package isep.aisafe.application.flightcontrol;

import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlightControlControllerTest {

    /**
     * Purpose: verify that the production constructor remains available.
     * Scenario: create the controller without injecting a fake authorization service.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new FlightControlController());
    }

    /**
     * Purpose: verify the permission required for flight control operations.
     * Scenario: execute the controller with a capturing authorization fake.
     * Expected output: FLIGHT_CONTROL permission is requested.
     */
    @Test
    void ensureExecuteRequiresFlightControlPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final FlightControlController controller = new FlightControlController(authz);

        // Act
        controller.execute();

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_CONTROL, authz.requestedRoles());
    }
}
