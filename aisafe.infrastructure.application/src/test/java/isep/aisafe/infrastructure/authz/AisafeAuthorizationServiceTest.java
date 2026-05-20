package isep.aisafe.infrastructure.authz;

import eapli.framework.infrastructure.authz.application.AuthorizationService;
import eapli.framework.infrastructure.authz.application.UserSession;
import eapli.framework.infrastructure.authz.domain.model.Role;
import eapli.framework.infrastructure.authz.domain.model.SystemUser;
import eapli.framework.infrastructure.authz.domain.model.SystemUserBuilder;
import isep.aisafe.usermanagement.domain.AisafePasswordPolicy;
import isep.aisafe.usermanagement.domain.AisafeRoles;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AisafeAuthorizationServiceTest {

    /**
     * Purpose: verify that the production constructor remains available.
     * Scenario: create the AISafe authorization service without injecting the framework service.
     * Expected output: a non-null service instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new AisafeAuthorizationService());
    }

    /**
     * Purpose: ensure the injectable constructor fails fast for invalid setup.
     * Scenario: create the service with a null framework authorization service.
     * Expected output: a NullPointerException is thrown before the service can be used.
     */
    @Test
    void ensureInjectedAuthorizationServiceCannotBeNull() {
        // Arrange / Act / Assert
        assertThrows(NullPointerException.class, () -> new AisafeAuthorizationService(null));
    }

    /**
     * Purpose: verify that AISafe authorization delegates role checks to the framework service.
     * Scenario: request authorization for ADMIN and BACKOFFICE_OPERATOR roles.
     * Expected output: the wrapped framework service receives exactly those roles.
     */
    @Test
    void ensureAuthorizationRequestIsDelegated() {
        // Arrange
        final CapturingFrameworkAuthorizationService frameworkAuthz = new CapturingFrameworkAuthorizationService();
        final AisafeAuthorizationService service = new AisafeAuthorizationService(frameworkAuthz);

        // Act
        service.ensureAuthenticatedUserHasAnyOf(AisafeRoles.ADMIN, AisafeRoles.BACKOFFICE_OPERATOR);

        // Assert
        assertArrayEquals(
                new Role[] { AisafeRoles.ADMIN, AisafeRoles.BACKOFFICE_OPERATOR },
                frameworkAuthz.requestedRoles
        );
    }

    /**
     * Purpose: verify the generic authenticated-user check.
     * Scenario: call ensureAuthenticatedUser without passing explicit roles.
     * Expected output: the framework service is called with an empty role array.
     */
    @Test
    void ensureAuthenticatedUserIsDelegatedWithoutRoles() {
        // Arrange
        final CapturingFrameworkAuthorizationService frameworkAuthz = new CapturingFrameworkAuthorizationService();
        final AisafeAuthorizationService service = new AisafeAuthorizationService(frameworkAuthz);

        // Act
        service.ensureAuthenticatedUser();

        // Assert
        assertNotNull(frameworkAuthz.requestedRoles);
        assertEquals(0, frameworkAuthz.requestedRoles.length);
    }

    /**
     * Purpose: verify that the authenticated username is exposed to application controllers.
     * Scenario: the framework authorization service has an authenticated user session.
     * Expected output: the AISafe service returns the session user's username.
     */
    @Test
    void ensureAuthenticatedUsernameIsReturnedFromCurrentSession() {
        // Arrange
        final CapturingFrameworkAuthorizationService frameworkAuthz = new CapturingFrameworkAuthorizationService();
        frameworkAuthz.session = Optional.of(new UserSession(systemUser("collaborator@aisafe.com")));
        final AisafeAuthorizationService service = new AisafeAuthorizationService(frameworkAuthz);

        // Act / Assert
        assertEquals("collaborator@aisafe.com", service.authenticatedUsername());
    }

    /**
     * Purpose: verify fail-fast behavior when no user is authenticated.
     * Scenario: the framework authorization service has no session.
     * Expected output: an IllegalStateException is thrown.
     */
    @Test
    void ensureAuthenticatedUsernameRequiresCurrentSession() {
        // Arrange
        final CapturingFrameworkAuthorizationService frameworkAuthz = new CapturingFrameworkAuthorizationService();
        final AisafeAuthorizationService service = new AisafeAuthorizationService(frameworkAuthz);

        // Act / Assert
        assertThrows(IllegalStateException.class, service::authenticatedUsername);
    }

    /**
     * Purpose: verify role checks can be made from the current session without calling authorization enforcement.
     * Scenario: the authenticated user has the air company collaborator role.
     * Expected output: role lookup returns true for that role and false for unrelated roles.
     */
    @Test
    void ensureAuthenticatedUserRolesCanBeCheckedWithoutAuthorizationAttempt() {
        // Arrange
        final CapturingFrameworkAuthorizationService frameworkAuthz = new CapturingFrameworkAuthorizationService();
        frameworkAuthz.session = Optional.of(new UserSession(systemUser("collaborator@aisafe.com")));
        final AisafeAuthorizationService service = new AisafeAuthorizationService(frameworkAuthz);

        // Act / Assert
        assertTrue(service.authenticatedUserHasAnyOf(AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR));
        assertFalse(service.authenticatedUserHasAnyOf(AisafeRoles.ADMIN, AisafeRoles.BACKOFFICE_OPERATOR));
        assertNull(frameworkAuthz.requestedRoles);
    }

    private static final class CapturingFrameworkAuthorizationService extends AuthorizationService {

        private Role[] requestedRoles;
        private Optional<UserSession> session = Optional.empty();

        private CapturingFrameworkAuthorizationService() {
            super();
        }

        @Override
        public void ensureAuthenticatedUserHasAnyOf(final Role... actions) {
            requestedRoles = actions;
        }

        @Override
        public Optional<UserSession> session() {
            return session;
        }
    }

    private SystemUser systemUser(final String username) {
        return new SystemUserBuilder(new AisafePasswordPolicy(), NoOpPasswordEncoder.getInstance())
                .with(username, "Password1", "Customer", "Collaborator", username)
                .withRoles(AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR)
                .build();
    }
}
