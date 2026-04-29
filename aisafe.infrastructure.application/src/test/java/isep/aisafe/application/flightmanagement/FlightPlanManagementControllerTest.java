package isep.aisafe.application.flightmanagement;

import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlightPlanManagementControllerTest {

    /**
     * Purpose: verify that the production constructor remains available.
     * Scenario: create the controller without injecting a fake authorization service.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new FlightPlanManagementController());
    }

    /**
     * Purpose: verify the permission required for flight plan management.
     * Scenario: execute the controller with a capturing authorization fake.
     * Expected output: FLIGHT_PLAN_MANAGEMENT permission is requested.
     */
    @Test
    void ensureExecuteRequiresFlightPlanManagementPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final FlightPlanManagementController controller = new FlightPlanManagementController(authz);

        // Act
        controller.execute();

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_MANAGEMENT, authz.requestedRoles());
    }
}
