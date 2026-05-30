package isep.aisafe.application.pilot;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.pilot.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import eapli.framework.infrastructure.authz.domain.model.Role;

class RegisterPilotControllerTest {

    private InMemoryPilotRepository repository;
    private RegisterPilotController controller;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPilotRepository();
        repository.clear();

        AisafeAuthorizationService mockAuthz = new AisafeAuthorizationService() {
            @Override
            public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {
                // não faz nada nos testes
            }
        };

        controller = new RegisterPilotController(mockAuthz, repository);
    }

    @Test
    void ensurePilotIsRegisteredSuccessfully() {
        // Arrange
        final String id = "ABC";
        final String issuedAt = "01-01-2020";
        final String validUntil = "01-01-2099";

        // Act
        final Pilot result = controller.registerPilot(id, issuedAt, validUntil);

        // Assert
        assertNotNull(result);
        assertEquals(PilotID.valueOf(id), result.identity());
    }

    @Test
    void ensureDuplicatePilotRegistrationIsRejected() {
        // Arrange
        final String id = "ABC";
        final String issuedAt = "01-01-2020";
        final String validUntil = "01-01-2099";
        controller.registerPilot(id, issuedAt, validUntil);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.registerPilot(id, issuedAt, validUntil));
    }

    @Test
    void ensurePilotIsPersistedInRepository() {
        // Arrange
        final String id = "ABC";
        final String issuedAt = "01-01-2020";
        final String validUntil = "01-01-2099";

        // Act
        controller.registerPilot(id, issuedAt, validUntil);

        // Assert
        assertTrue(repository.ofIdentity(PilotID.valueOf(id)).isPresent());
    }

    @Test
    void ensureRegistrationRejectsInvalidDateFormat() {
        // Arrange
        final String id = "ABC";
        final String issuedAt = "2020-01-01"; // formato errado
        final String validUntil = "01-01-2099";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.registerPilot(id, issuedAt, validUntil));
    }

    @Test
    void ensureRegistrationRejectsInvalidPilotID() {
        // Arrange
        final String invalidId = "ABC123";
        final String issuedAt = "01-01-2020";
        final String validUntil = "01-01-2099";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.registerPilot(invalidId, issuedAt, validUntil));
    }

    @Test
    void ensureControllerRejectsNullAuthz() {
        // Arrange & Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterPilotController(null, repository));
    }

    @Test
    void ensureControllerRejectsNullRepository() {
        // Arrange
        AisafeAuthorizationService mockAuthz = new AisafeAuthorizationService() {
            @Override
            public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {
            }
        };

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterPilotController(mockAuthz, null));
    }
}