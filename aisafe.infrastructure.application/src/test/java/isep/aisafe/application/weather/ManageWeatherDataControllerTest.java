package isep.aisafe.application.weather;

import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManageWeatherDataControllerTest {

    /**
     * Purpose: verify that the production constructor remains available.
     * Scenario: create the controller without injecting a fake authorization service.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new ManageWeatherDataController());
    }

    /**
     * Purpose: verify the permission required to manage weather data.
     * Scenario: execute the controller with a capturing authorization fake.
     * Expected output: WEATHER_DATA_MANAGEMENT permission is requested.
     */
    @Test
    void ensureExecuteRequiresWeatherDataManagementPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final ManageWeatherDataController controller = new ManageWeatherDataController(authz);

        // Act
        controller.execute();

        // Assert
        assertArrayEquals(AisafePermissions.WEATHER_DATA_MANAGEMENT, authz.requestedRoles());
    }
}
