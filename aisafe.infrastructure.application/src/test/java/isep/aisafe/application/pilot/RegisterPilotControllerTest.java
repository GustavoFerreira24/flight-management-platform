package isep.aisafe.application.pilot;

import isep.aisafe.aircontrolarea.domain.*;
import isep.aisafe.airtransportcompany.domain.*;
import isep.aisafe.customercollaborator.domain.*;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.pilot.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import eapli.framework.infrastructure.authz.domain.model.Role;

import static org.junit.jupiter.api.Assertions.*;

class RegisterPilotControllerTest {

    private InMemoryPilotRepository repository;
    private RegisterPilotController controller;
    private AisafeAuthorizationService mockAuthz;
    private InMemoryCustomerCollaboratorRepository mockCollaboratorRepo;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPilotRepository();
        repository.clear();

        mockAuthz = new AisafeAuthorizationService() {
            @Override
            public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {}

            @Override
            public String authenticatedUsername() {
                return "test@test.com";
            }
        };

        mockCollaboratorRepo = new InMemoryCustomerCollaboratorRepository();
        mockCollaboratorRepo.save(new CustomerCollaborator(
                "Test", "User",
                CollaboratorEmail.valueOf("test@test.com"),
                airTransportCompany("TP", "TAP"),
                SystemUsername.valueOf("test@test.com")
        ));

        controller = new RegisterPilotController(mockAuthz, repository, mockCollaboratorRepo);
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
    void ensurePilotIsRegisteredWithCorrectCompany() {
        // Arrange
        final String id = "ABC";
        final String issuedAt = "01-01-2020";
        final String validUntil = "01-01-2099";

        // Act
        final Pilot result = controller.registerPilot(id, issuedAt, validUntil);

        // Assert
        assertNotNull(result.companyIATACode());
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
        final String issuedAt = "2020-01-01";
        final String validUntil = "01-01-2099";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.registerPilot(id, issuedAt, validUntil));
    }

    @Test
    void ensureRegistrationRejectsValidUntilBeforeIssuedAt() {
        // Arrange
        final String id = "ABC";
        final String issuedAt = "01-01-2025";
        final String validUntil = "01-01-2020";

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
    void ensureRegistrationRejectsCollaboratorNotFromAirTransportCompany() {
        // Arrange
        final InMemoryCustomerCollaboratorRepository areaRepo =
                new InMemoryCustomerCollaboratorRepository();
        areaRepo.save(new CustomerCollaborator(
                "Area", "User",
                CollaboratorEmail.valueOf("area@test.com"),
                airControlArea("LPPO"),
                SystemUsername.valueOf("area@test.com")
        ));

        final AisafeAuthorizationService areaAuthz = new AisafeAuthorizationService() {
            @Override
            public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {}

            @Override
            public String authenticatedUsername() {
                return "area@test.com";
            }
        };

        final RegisterPilotController areaController =
                new RegisterPilotController(areaAuthz, repository, areaRepo);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> areaController.registerPilot("ABC", "01-01-2020", "01-01-2099"));
    }

    @Test
    void ensureRegistrationRejectsUnknownCollaborator() {
        // Arrange
        final AisafeAuthorizationService unknownAuthz = new AisafeAuthorizationService() {
            @Override
            public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {}

            @Override
            public String authenticatedUsername() {
                return "unknown@test.com";
            }
        };

        final RegisterPilotController unknownController =
                new RegisterPilotController(unknownAuthz, repository, mockCollaboratorRepo);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> unknownController.registerPilot("ABC", "01-01-2020", "01-01-2099"));
    }

    @Test
    void ensureControllerRejectsNullAuthz() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterPilotController(null, repository, mockCollaboratorRepo));
    }

    @Test
    void ensureControllerRejectsNullRepository() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterPilotController(mockAuthz, null, mockCollaboratorRepo));
    }

    @Test
    void ensureControllerRejectsNullCollaboratorRepo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterPilotController(mockAuthz, repository, null));
    }

    private AirTransportCompany airTransportCompany(final String iataCode, final String icaoCode) {

        return new AirTransportCompany(
                "Test Company " + iataCode,
                Country.valueOf("Portugal"),
                IATACode.valueOf(iataCode),
                ICAOCode.valueOf(icaoCode)
        );
    }

    private AirControlArea airControlArea(final String code) {

        return new AirControlArea(
                AirControlAreaCode.valueOf(code),
                "Test Area " + code,
                GeographicBoundary.valueOf(35.0, 45.0, -15.0, -5.0),
                MinimumFuelRequirement.valueOf(1000, FuelUnit.valueOf("KG"))
        );
    }
}
