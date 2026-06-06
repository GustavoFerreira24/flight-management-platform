package isep.aisafe.application.pilot;

import eapli.framework.general.domain.model.EmailAddress;
import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import eapli.framework.infrastructure.authz.domain.model.SystemUser;
import isep.aisafe.aircontrolarea.domain.*;
import isep.aisafe.airtransportcompany.domain.*;
import isep.aisafe.application.usermanagement.RegisterBackofficeUserController;
import isep.aisafe.application.usermanagement.UserRegistrationValidator;
import isep.aisafe.customercollaborator.domain.*;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.pilot.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ListPilotRosterControllerTest {

    private InMemoryPilotRepository pilotRepo;
    private InMemoryCustomerCollaboratorRepository collaboratorRepo;
    private InMemoryAirTransportCompanyRepository companyRepo;
    private AisafeAuthorizationService mockAuthz;
    private ListPilotRosterController controller;

    private AirTransportCompany tapCompany;

    @BeforeEach
    void setUp() {
        pilotRepo = new InMemoryPilotRepository();
        pilotRepo.clear();

        collaboratorRepo = new InMemoryCustomerCollaboratorRepository();
        collaboratorRepo.clear();

        companyRepo = new InMemoryAirTransportCompanyRepository();

        tapCompany = new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        );
        companyRepo.save(tapCompany);

        collaboratorRepo.save(new CustomerCollaborator(
                "Test", "User",
                CollaboratorEmail.valueOf("test@test.com"),
                tapCompany,
                SystemUsername.valueOf("test@test.com")
        ));

        mockAuthz = new AisafeAuthorizationService() {
            @Override
            public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {}

            @Override
            public String authenticatedUsername() {
                return "test@test.com";
            }
        };

        controller = new ListPilotRosterController(
                mockAuthz, pilotRepo, collaboratorRepo, companyRepo,
                new UserRegistrationValidator() {
                    @Override
                    public void validate(String u, String p, String f,
                                         String l, String e, Set<Role> r) {}
                },
                new RegisterBackofficeUserController() {
                    @Override
                    public void registerUser(String u, String p, String f,
                                             String l, String e, Set<Role> r) {}
                }
        );
    }

    private Pilot validPilot(String id, String iataCode) {
        final PilotID pilotID = PilotID.valueOf(id);
        final Certification cert = new Certification("01-01-2020", "01-01-2099");
        final PilotStatus status = PilotStatus.valueOf(cert);
        return new Pilot(pilotID, cert, status,
                IATACode.valueOf(iataCode),
                EmailAddress.valueOf("pilot@test.com"),
                PhoneNumber.valueOf("+351912345678"),
                "John", "Doe", new HashSet<>());
    }

    @Test
    void ensureListPilotRosterReturnsEmptyWhenNoPilots() {
        // Act
        final Set<Pilot> result = controller.listPilotRoster();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void ensureListPilotRosterReturnsPilotsFromSameCompany() {
        // Arrange
        pilotRepo.save(validPilot("ABC", "TP"));

        // Act
        final Set<Pilot> result = controller.listPilotRoster();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void ensureListPilotRosterDoesNotReturnPilotsFromOtherCompany() {
        // Arrange
        pilotRepo.save(validPilot("ABC", "FR"));

        // Act
        final Set<Pilot> result = controller.listPilotRoster();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void ensureListPilotRosterReturnsOnlyPilotsFromAuthenticatedCompany() {
        // Arrange
        pilotRepo.save(validPilot("ABC", "TP"));
        pilotRepo.save(validPilot("DEF", "FR"));

        // Act
        final Set<Pilot> result = controller.listPilotRoster();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void ensureListPilotRosterRejectsCollaboratorNotFromAirTransportCompany() {
        // Arrange
        final InMemoryCustomerCollaboratorRepository areaRepo =
                new InMemoryCustomerCollaboratorRepository();
        areaRepo.save(new CustomerCollaborator(
                "Area", "User",
                CollaboratorEmail.valueOf("area@test.com"),
                new AirControlArea(
                        AirControlAreaCode.valueOf("LISACC"),
                        "Lisbon Area",
                        GeographicBoundary.valueOf(35.0, 45.0, -15.0, -5.0),
                        MinimumFuelRequirement.valueOf(1000, FuelUnit.valueOf("KG"))
                ),
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

        final ListPilotRosterController areaController = new ListPilotRosterController(
                areaAuthz, pilotRepo, areaRepo, companyRepo,
                new UserRegistrationValidator() {
                    @Override
                    public void validate(String u, String p, String f,
                                         String l, String e, Set<Role> r) {}
                },
                new RegisterBackofficeUserController() {
                    @Override
                    public void registerUser(String u, String p, String f,
                                             String l, String e, Set<Role> r) {}
                }
        );

        // Act & Assert
        assertThrows(IllegalStateException.class, areaController::listPilotRoster);
    }

    @Test
    void ensureControllerRejectsNullAuthz() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new ListPilotRosterController(
                        null, pilotRepo, collaboratorRepo, companyRepo,
                        new UserRegistrationValidator(),
                        new RegisterBackofficeUserController()
                ));
    }

    @Test
    void ensureControllerRejectsNullPilotRepo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new ListPilotRosterController(
                        mockAuthz, null, collaboratorRepo, companyRepo,
                        new UserRegistrationValidator(),
                        new RegisterBackofficeUserController()
                ));
    }

    @Test
    void ensureControllerRejectsNullCollaboratorRepo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new ListPilotRosterController(
                        mockAuthz, pilotRepo, null, companyRepo,
                        new UserRegistrationValidator(),
                        new RegisterBackofficeUserController()
                ));
    }

    @Test
    void ensureControllerRejectsNullCompanyRepo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new ListPilotRosterController(
                        mockAuthz, pilotRepo, collaboratorRepo, null,
                        new UserRegistrationValidator(),
                        new RegisterBackofficeUserController()
                ));
    }
}