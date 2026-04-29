package isep.aisafe.application.usermanagement;

import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManageUsersControllerTest {

    /**
     * Purpose: verify that the production constructor remains available.
     * Scenario: create the controller without injecting a fake authorization service.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new ManageUsersController());
    }

    /**
     * Purpose: verify the permission required to execute user management.
     * Scenario: execute the use case with a capturing authorization fake.
     * Expected output: USER_MANAGEMENT permission is requested.
     */
    @Test
    void ensureExecuteRequiresUserManagementPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final ManageUsersController controller = new ManageUsersController(authz);

        // Act
        controller.execute();

        // Assert
        assertArrayEquals(AisafePermissions.USER_MANAGEMENT, authz.requestedRoles());
    }

    /**
     * Purpose: ensure authorization failures are not swallowed by the controller.
     * Scenario: the authorization fake throws an exception during execution.
     * Expected output: the same exception is propagated to the caller.
     */
    @Test
    void ensureAuthorizationFailureIsPropagated() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final RuntimeException failure = new RuntimeException("not authorized");
        authz.throwOnAuthorization(failure);
        final ManageUsersController controller = new ManageUsersController(authz);

        // Act / Assert
        assertSame(failure, assertThrows(RuntimeException.class, controller::execute));
    }
}
