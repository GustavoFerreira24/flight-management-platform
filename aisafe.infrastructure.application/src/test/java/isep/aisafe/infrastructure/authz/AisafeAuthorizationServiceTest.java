package isep.aisafe.infrastructure.authz;

import eapli.framework.infrastructure.authz.application.AuthorizationService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.usermanagement.domain.AisafeRoles;
import org.junit.jupiter.api.Test;

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

    private static final class CapturingFrameworkAuthorizationService extends AuthorizationService {

        private Role[] requestedRoles;

        private CapturingFrameworkAuthorizationService() {
            super();
        }

        @Override
        public void ensureAuthenticatedUserHasAnyOf(final Role... actions) {
            requestedRoles = actions;
        }
    }
}
