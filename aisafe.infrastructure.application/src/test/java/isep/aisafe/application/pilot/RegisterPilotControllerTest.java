package isep.aisafe.application.pilot;

import eapli.framework.general.domain.model.EmailAddress;
import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import eapli.framework.infrastructure.authz.domain.model.SystemUser;
import isep.aisafe.aircontrolarea.domain.*;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelMotorization;
import isep.aisafe.aircraftmodel.domain.AircraftModelType;
import isep.aisafe.aircraftmodel.domain.InMemoryAircraftModelRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.application.aircraftmodel.ManageAircraftModelController;
import isep.aisafe.application.usermanagement.UserRegistrationValidator;
import isep.aisafe.customercollaborator.domain.*;
import isep.aisafe.enginemodel.domain.*;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.pilot.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterPilotControllerTest {

    private InMemoryPilotRepository repository;
    private RegisterPilotController controller;
    private AisafeAuthorizationService mockAuthz;
    private InMemoryCustomerCollaboratorRepository mockCollaboratorRepo;
    private UserRegistrationValidator mockValidator;
    private UserManagementService mockUserController;
    private ManageAircraftModelController mockAircraftModelController;
    private InMemoryEngineModelRepository mockEngineModelRepo;
    private InMemoryAircraftModelRepository mockAircraftModelRepo;


    @BeforeEach
    void setUp() {

        repository = new InMemoryPilotRepository();
        repository.clear();
        mockEngineModelRepo = new InMemoryEngineModelRepository();
        mockAircraftModelRepo = new InMemoryAircraftModelRepository(); // Mova para aqui!
        mockAircraftModelRepo.save(aircraftModel(engineModel()));
        mockCollaboratorRepo = new InMemoryCustomerCollaboratorRepository();

        mockAuthz = new AisafeAuthorizationService() {
            @Override
            public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {}

            @Override
            public String authenticatedUsername() {
                return "test@test.com";
            }
        };

        mockCollaboratorRepo.save(new CustomerCollaborator(
                "Test", "User",
                CollaboratorEmail.valueOf("test@test.com"),
                airTransportCompany("TP", "TAP"),
                SystemUsername.valueOf("test@test.com")
        ));

        mockValidator = new UserRegistrationValidator() {
            @Override
            public void validate(String username, String password, String firstName,
                                 String lastName, String email, Set<Role> roles) {
            }
        };

        mockUserController = new UserManagementService(null, null, null) {
            @Override
            public SystemUser registerNewUser(String username, String password, String firstName,
                                              String lastName, String email, Set<Role> roles) {
                return null; // Apenas um valor dummy
            }
        };

        controller = new RegisterPilotController(
                mockAuthz, repository, mockCollaboratorRepo,
                mockValidator, mockUserController, mockAircraftModelRepo
        );

        mockAircraftModelController = new ManageAircraftModelController(
                mockAuthz, new InMemoryAircraftModelRepository(), new InMemoryEngineModelRepository()
        );

    }

    @Test
    void ensurePilotIsRegisteredSuccessfully() {
        // Arrange
        final String id = "ABC";
        final String issuedAt = "01-01-2020";
        final String validUntil = "01-01-2099";

        // Act
        final Pilot result = controller.registerPilot(
                id, issuedAt, validUntil,
                "pilot.user", "John", "Doe",
                "john.doe@test.com", "Password1!", "+351912345678",
                "A320");

        // Assert
        assertNotNull(result);
        assertEquals(PilotID.valueOf(id), result.identity());
    }

    @Test
    void ensurePilotIsRegisteredWithCorrectEmail() {
        // Arrange
        final String id = "ABC";
        final String email = "john.doe@test.com";

        // Act
        final Pilot result = controller.registerPilot(
                id, "01-01-2020", "01-01-2099",
                "pilot.user", "John", "Doe",
                email, "Password1!", "+351912345678",
                "A320");

        // Assert
        assertEquals(EmailAddress.valueOf(email), result.email());
    }

    @Test
    void ensureDuplicatePilotRegistrationIsRejected() {
        // Arrange
        final String id = "ABC";
        controller.registerPilot(
                id, "01-01-2020", "01-01-2099",
                "pilot.user", "John", "Doe",
                "john.doe@test.com", "Password1!", "+351912345678",
                "A320");

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.registerPilot(
                        id, "01-01-2020", "01-01-2099",
                        "pilot.user2", "Jane", "Doe",
                        "jane.doe@test.com", "Password1!", "+351912345679",
                        "A320"));
    }

    @Test
    void ensurePilotIsPersistedInRepository() {
        // Arrange
        final String id = "ABC";

        // Act
        controller.registerPilot(
                id, "01-01-2020", "01-01-2099",
                "pilot.user", "John", "Doe",
                "john.doe@test.com", "Password1!", "+351912345678",
                "A320");

        // Assert
        assertTrue(repository.ofIdentity(PilotID.valueOf(id)).isPresent());
    }

    @Test
    void ensureRegistrationRejectsInvalidDateFormat() {
        // Arrange
        final String id = "ABC";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.registerPilot(
                        id, "2020-01-01", "01-01-2099",
                        "pilot.user", "John", "Doe",
                        "john.doe@test.com", "Password1!", "+351912345678",
                        "A320"));
    }

    @Test
    void ensureRegistrationRejectsValidUntilBeforeIssuedAt() {
        // Arrange
        final String id = "ABC";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.registerPilot(
                        id, "01-01-2025", "01-01-2020",
                        "pilot.user", "John", "Doe",
                        "john.doe@test.com", "Password1!", "+351912345678",
                        "A320"));
    }

    @Test
    void ensureRegistrationRejectsInvalidPilotID() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.registerPilot(
                        "ABC123", "01-01-2020", "01-01-2099",
                        "pilot.user", "John", "Doe",
                        "john.doe@test.com", "Password1!", "+351912345678",
                        "A320"));
    }

    @Test
    void ensureRegistrationRejectsCollaboratorNotFromAirTransportCompany() {

        // Arrange

        final InMemoryCustomerCollaboratorRepository areaRepo = new InMemoryCustomerCollaboratorRepository();

        areaRepo.save(new CustomerCollaborator(
                "Area", "User",
                CollaboratorEmail.valueOf("area@test.com"),
                airControlArea("LISACC"),
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

        final RegisterPilotController areaController = new RegisterPilotController(
                areaAuthz, repository, areaRepo, mockValidator, mockUserController, mockAircraftModelRepo
        );

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> areaController.registerPilot(
                        "ABC", "01-01-2020", "01-01-2099",
                        "pilot.user", "John", "Doe",
                        "john.doe@test.com", "Password1!", "+351912345678",
                        "A320"));
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

        final RegisterPilotController unknownController = new RegisterPilotController(
                unknownAuthz, repository, mockCollaboratorRepo, mockValidator, mockUserController, mockAircraftModelRepo
        );

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> unknownController.registerPilot(
                        "ABC", "01-01-2020", "01-01-2099",
                        "pilot.user", "John", "Doe",
                        "john.doe@test.com", "Password1!", "+351912345678",
                        "A320"));
    }

    @Test
    void ensureControllerRejectsNullAuthz() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterPilotController(
                        null, repository, mockCollaboratorRepo,
                        mockValidator, mockUserController, mockAircraftModelRepo
                ));
    }

    @Test
    void ensureControllerRejectsNullRepository() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterPilotController(
                        mockAuthz, null, mockCollaboratorRepo,
                        mockValidator, mockUserController, mockAircraftModelRepo
                ));
    }

    @Test
    void ensureControllerRejectsNullCollaboratorRepo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterPilotController(
                        mockAuthz, repository, null,
                        mockValidator, mockUserController, mockAircraftModelRepo
                ));
    }

    @Test
    void ensureControllerRejectsNullValidator() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterPilotController(
                        mockAuthz, repository, mockCollaboratorRepo,
                        null, mockUserController, mockAircraftModelRepo
                ));
    }

    @Test
    void ensureControllerRejectsNullUserController() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterPilotController(
                        mockAuthz, repository, mockCollaboratorRepo,
                        mockValidator, null, mockAircraftModelRepo
                ));
    }

    @Test
    void ensureControllerRejectsNullAircraftModelRepo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterPilotController(
                        mockAuthz, repository, mockCollaboratorRepo,
                        mockValidator, mockUserController, null
                ));
    }



    @Test
    void ensureGetAllAircraftModelsReturnsListWithElements() {

        EngineModel engineModel = new EngineModel(
                EngineModelDesignator.valueOf("LEAP-1A", "CFM International"),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                120.0,
                140.0,
                0.60
        );

        mockEngineModelRepo.save(engineModel);

        mockAircraftModelController.registerAircraftModel(
                "A320", "Airbus", "PASSENGER", "TURBOFAN",
                6300, 44500, 79000, 64500, 27200, 12000,
                833, 122.6, 0.024, 0.62, 186,
                List.of(engineModel.designator().toString())
        );

        mockAircraftModelRepo = new InMemoryAircraftModelRepository();

        final Iterable<AircraftModel> result = controller.getAllAircraftModels();

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
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

    private AircraftModel aircraftModel(final EngineModel engineModel) {
        return new AircraftModel(
                AircraftModelDesignator.valueOf("A320"),
                AircraftModelType.valueOf("PASSENGER"),
                AircraftModelMotorization.valueOf("TURBOFAN"),
                6300,
                44500,
                79000,
                64500,
                27200,
                12000,
                833,
                122.6,
                0.024,
                0.62,
                186,
                List.of(engineModel)
        );
    }

    private EngineModel engineModel() {
        return new EngineModel(
                EngineModelDesignator.valueOf("LEAP-1A", "CFM International"),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                120.0,
                140.0,
                0.60
        );
    }
}
