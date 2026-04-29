package isep.aisafe.application.usermanagement;

import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import eapli.framework.infrastructure.authz.domain.model.SystemUser;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import isep.aisafe.usermanagement.domain.AisafeRoles;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterBackofficeUserControllerTest {

    /**
     * Purpose: verify that the production constructor can still be used by the application.
     * Scenario: create the controller with no injected test doubles.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new RegisterBackofficeUserController());
    }

    /**
     * Purpose: verify the complete successful registration flow for a backoffice user.
     * Scenario: an authorized actor submits valid registration data and a valid role.
     * Expected output: authorization is requested, data is validated, and the user service receives the same data once.
     */
    @Test
    void ensureRegisterUserAuthorizesValidatesAndDelegatesToUserService() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final FakeUserManagementService userService = new FakeUserManagementService();
        final RegisterBackofficeUserController controller = new RegisterBackofficeUserController(
                authz,
                userService,
                new UserRegistrationValidator()
        );
        final Set<Role> roles = Set.of(AisafeRoles.BACKOFFICE_OPERATOR);

        // Act
        controller.registerUser(
                "backoffice1",
                "Password1",
                "Backoffice",
                "User",
                "backoffice1@aisafe.com",
                roles
        );

        // Assert
        assertArrayEquals(AisafePermissions.USER_REGISTRATION, authz.requestedRoles());
        assertEquals("backoffice1", userService.username);
        assertEquals("Password1", userService.password);
        assertEquals("Backoffice", userService.firstName);
        assertEquals("User", userService.lastName);
        assertEquals("backoffice1@aisafe.com", userService.email);
        assertEquals(roles, userService.roles);
        assertEquals(1, userService.calls);
    }

    /**
     * Purpose: ensure invalid registration data does not create users.
     * Scenario: the username is blank while the remaining fields are valid.
     * Expected output: an IllegalArgumentException is thrown and the user service is not called.
     */
    @Test
    void ensureInvalidUserDataIsNotRegistered() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final FakeUserManagementService userService = new FakeUserManagementService();
        final RegisterBackofficeUserController controller = new RegisterBackofficeUserController(
                authz,
                userService,
                new UserRegistrationValidator()
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerUser(
                        "",
                        "Password1",
                        "Backoffice",
                        "User",
                        "backoffice1@aisafe.com",
                        Set.of(AisafeRoles.BACKOFFICE_OPERATOR)
                )
        );
        assertArrayEquals(AisafePermissions.USER_REGISTRATION, authz.requestedRoles());
        assertEquals(0, userService.calls);
    }

    /**
     * Purpose: ensure authorization is enforced before registering users.
     * Scenario: the authorization service rejects the current actor.
     * Expected output: the authorization exception is propagated and no user is registered.
     */
    @Test
    void ensureAuthorizationFailureStopsRegistration() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final RuntimeException failure = new RuntimeException("not authorized");
        authz.throwOnAuthorization(failure);
        final FakeUserManagementService userService = new FakeUserManagementService();
        final RegisterBackofficeUserController controller = new RegisterBackofficeUserController(
                authz,
                userService,
                new UserRegistrationValidator()
        );

        // Act / Assert
        assertSame(failure, assertThrows(RuntimeException.class, () ->
                controller.registerUser(
                        "backoffice1",
                        "Password1",
                        "Backoffice",
                        "User",
                        "backoffice1@aisafe.com",
                        Set.of(AisafeRoles.BACKOFFICE_OPERATOR)
                )
        ));
        assertEquals(0, userService.calls);
    }

    private static final class FakeUserManagementService extends UserManagementService {

        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String email;
        private Set<Role> roles;
        private int calls;

        private FakeUserManagementService() {
            super(null, null, null);
        }

        @Override
        public SystemUser registerNewUser(
                final String username,
                final String rawPassword,
                final String firstName,
                final String lastName,
                final String email,
                final Set<Role> roles) {
            calls++;
            this.username = username;
            password = rawPassword;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.roles = roles;
            return null;
        }
    }
}
