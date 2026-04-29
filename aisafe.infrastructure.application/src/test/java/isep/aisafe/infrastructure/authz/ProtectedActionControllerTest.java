package isep.aisafe.infrastructure.authz;

import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtectedActionControllerTest {

    /**
     * Purpose: verify that the production constructor remains available.
     * Scenario: create the protected action controller without injecting a fake authorization service.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new ProtectedActionController());
    }

    /**
     * Purpose: verify the permission required by the protected admin action.
     * Scenario: execute the action with a capturing authorization fake.
     * Expected output: USER_MANAGEMENT permission is requested before the action completes.
     */
    @Test
    void ensureAdminOnlyActionRequiresUserManagementPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final ProtectedActionController controller = new ProtectedActionController(authz);

        // Act
        controller.executeAdminOnlyAction();

        // Assert
        assertArrayEquals(AisafePermissions.USER_MANAGEMENT, authz.requestedRoles());
    }
}
