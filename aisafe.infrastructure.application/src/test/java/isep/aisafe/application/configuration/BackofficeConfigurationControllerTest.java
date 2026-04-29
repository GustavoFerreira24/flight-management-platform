package isep.aisafe.application.configuration;

import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BackofficeConfigurationControllerTest {

    /**
     * Purpose: verify that the production constructor remains available.
     * Scenario: create the controller without injecting a fake authorization service.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new BackofficeConfigurationController());
    }

    /**
     * Purpose: verify the permission required for backoffice configuration.
     * Scenario: execute the controller with a capturing authorization fake.
     * Expected output: BACKOFFICE_CONFIGURATION permission is requested.
     */
    @Test
    void ensureExecuteRequiresBackofficeConfigurationPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final BackofficeConfigurationController controller = new BackofficeConfigurationController(authz);

        // Act
        controller.execute();

        // Assert
        assertArrayEquals(AisafePermissions.BACKOFFICE_CONFIGURATION, authz.requestedRoles());
    }
}
