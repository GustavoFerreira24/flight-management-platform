package isep.aisafe.application.pilot;

import eapli.framework.general.domain.model.EmailAddress;
import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.customercollaborator.domain.CollaboratorEmail;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.InMemoryCustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.SystemUsername;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.pilot.domain.Certification;
import isep.aisafe.pilot.domain.InMemoryPilotRepository;
import isep.aisafe.pilot.domain.PhoneNumber;
import isep.aisafe.pilot.domain.Pilot;
import isep.aisafe.pilot.domain.PilotID;
import isep.aisafe.pilot.domain.PilotStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class AddPilotControllerTest {

    private InMemoryPilotRepository pilotRepo;
    private InMemoryCustomerCollaboratorRepository collaboratorRepo;
    private AisafeAuthorizationService mockAuthz;
    private AddPilotController controller;
    private AirTransportCompany tapCompany;

    @BeforeEach
    void setUp() {
        pilotRepo = new InMemoryPilotRepository();
        pilotRepo.clear();

        collaboratorRepo = new InMemoryCustomerCollaboratorRepository();
        collaboratorRepo.clear();

        tapCompany = new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        );

        collaboratorRepo.save(new CustomerCollaborator(
                "Test",
                "User",
                CollaboratorEmail.valueOf("test@test.com"),
                tapCompany,
                SystemUsername.valueOf("test@test.com")
        ));

        mockAuthz = new AisafeAuthorizationService() {
            @Override
            public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {
            }

            @Override
            public String authenticatedUsername() {
                return "test@test.com";
            }
        };

        controller = new AddPilotController(
                mockAuthz,
                pilotRepo,
                collaboratorRepo
        );
    }

    private Pilot validPilot(String id) {
        final PilotID pilotID = PilotID.valueOf(id);
        final Certification cert = new Certification("01-01-2020", "01-01-2099");
        final PilotStatus status = PilotStatus.valueOf(cert);

        return new Pilot(
                pilotID,
                cert,
                status,
                null,
                EmailAddress.valueOf("pilot@test.com"),
                PhoneNumber.valueOf("+351912345678"),
                "John",
                "Doe",
                new HashSet<>()
        );
    }

    private Pilot alreadyAssociatedPilot(String id) {
        final PilotID pilotID = PilotID.valueOf(id);
        final Certification cert = new Certification("01-01-2020", "01-01-2099");
        final PilotStatus status = PilotStatus.valueOf(cert);

        return new Pilot(
                pilotID,
                cert,
                status,
                IATACode.valueOf("TP"),
                EmailAddress.valueOf("pilot@test.com"),
                PhoneNumber.valueOf("+351912345678"),
                "John",
                "Doe",
                new HashSet<>()
        );
    }

    @Test
    void shouldAssignPilotToCollaboratorCompany() {

        // Arrange
        Pilot pilot = validPilot("ABC");
        pilotRepo.save(pilot);

        // Act
        controller.addPilot("ABC");

        // Assert
        Pilot updatedPilot = pilotRepo.ofIdentity(PilotID.valueOf("ABC")).orElseThrow();

        assertEquals(
                tapCompany.iataCode(),
                updatedPilot.companyIATACode()
        );
    }

    @Test
    void shouldThrowExceptionWhenPilotIdIsNull() {

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> controller.addPilot(null)
        );

        assertEquals(
                "O ID do piloto não pode ser nulo.",
                ex.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenPilotDoesNotExist() {

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> controller.addPilot("ABC")
        );

        assertEquals(
                "Piloto não encontrado.",
                ex.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenPilotIsAlreadyAssociatedWithCompany() {

        // Arrange
        Pilot pilot = alreadyAssociatedPilot("ABC");
        pilotRepo.save(pilot);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> controller.addPilot("ABC")
        );

        assertEquals(
                "Pilot is already associated with an air transport company.",
                ex.getMessage()
        );
    }
}