package isep.aisafe.application.aircompany;

import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirCompanyConfigurationControllerTest {

    /**
     * Purpose: verify that the production constructor remains available.
     * Scenario: create the controller without injecting a fake authorization service.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new AirCompanyConfigurationController());
    }

    /**
     * Purpose: verify the permission required for air company configuration.
     * Scenario: execute the controller with a capturing authorization fake.
     * Expected output: AIR_COMPANY_CONFIGURATION permission is requested.
     */
    @Test
    void ensureExecuteRequiresAirCompanyConfigurationPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final AirCompanyConfigurationController controller = new AirCompanyConfigurationController(authz);

        // Act
        controller.execute();

        // Assert
        assertArrayEquals(AisafePermissions.AIR_COMPANY_CONFIGURATION, authz.requestedRoles());
    }
}
