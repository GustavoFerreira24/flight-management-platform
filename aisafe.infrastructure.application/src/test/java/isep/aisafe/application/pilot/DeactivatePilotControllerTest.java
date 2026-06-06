package isep.aisafe.application.pilot;

import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import eapli.framework.infrastructure.authz.domain.model.SystemUser;
import eapli.framework.general.domain.model.EmailAddress;
import isep.aisafe.aircontrolarea.domain.*;
import isep.aisafe.aircraft.domain.*;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelMotorization;
import isep.aisafe.aircraftmodel.domain.AircraftModelType;
import isep.aisafe.airport.domain.*;
import isep.aisafe.airtransportcompany.domain.*;
import isep.aisafe.application.usermanagement.RegisterBackofficeUserController;
import isep.aisafe.application.usermanagement.UserRegistrationValidator;
import isep.aisafe.customercollaborator.domain.*;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import isep.aisafe.flight.domain.Flight;
import isep.aisafe.flight.domain.FlightDesignator;
import isep.aisafe.flightplan.domain.*;
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.flightroute.domain.RouteDesignator;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.pilot.domain.*;
import isep.aisafe.weatherdata.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeactivatePilotControllerTest {

    private InMemoryPilotRepository pilotRepo;
    private InMemoryCustomerCollaboratorRepository collaboratorRepo;
    private AisafeAuthorizationService mockAuthz;
    private UserRegistrationValidator mockValidator;
    private UserManagementService mockUserService;
    private InMemoryFlightPlanRepository mockFlightPlanRepo;
    private DeactivatePilotController controller;
    private AirTransportCompany tapCompany;

    @BeforeEach
    void setUp() {
        pilotRepo = new InMemoryPilotRepository();
        pilotRepo.clear();

        collaboratorRepo = new InMemoryCustomerCollaboratorRepository();
        collaboratorRepo.clear();

        mockFlightPlanRepo = new InMemoryFlightPlanRepository();
        mockFlightPlanRepo.clear();

        tapCompany = new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        );

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

        mockValidator = new UserRegistrationValidator() {
            @Override
            public void validate(String u, String p, String f, String l, String e, Set<Role> r) {}
        };

        mockUserService = new UserManagementService(null, null, null) {
            @Override
            public SystemUser registerNewUser(String u, String p, String f,
                                              String l, String e, Set<Role> r) {
                return null;
            }
        };

        controller = new DeactivatePilotController(
                mockAuthz, pilotRepo, mockFlightPlanRepo, collaboratorRepo,
                mockValidator,
                new RegisterBackofficeUserController() {
                    @Override
                    public void registerUser(String u, String p, String f,
                                             String l, String e, Set<Role> r) {}
                }
        );
    }

    private Pilot validPilot(String id) {
        final PilotID pilotID = PilotID.valueOf(id);
        final Certification cert = new Certification("01-01-2020", "01-01-2099");
        final PilotStatus status = PilotStatus.valueOf(cert);
        return new Pilot(pilotID, cert, status,
                IATACode.valueOf("TP"),
                EmailAddress.valueOf("pilot@test.com"),
                PhoneNumber.valueOf("+351912345678"),
                "John", "Doe", new HashSet<>());
    }

    @Test
    void ensurePilotIsDeactivatedSuccessfully() {
        // Arrange
        pilotRepo.save(validPilot("ABC"));

        // Act
        controller.deactivatePilot("ABC");

        // Assert
        final Pilot updated = pilotRepo.ofIdentity(PilotID.valueOf("ABC")).get();
        assertFalse(updated.pilotStatus().isValid());
    }

    @Test
    void ensurePilotWithFlightPlanIsNotDeactivated() {
        // Arrange
        Pilot p = validPilot("ABC");
        pilotRepo.save(p);

        mockFlightPlanRepo.save(new FlightPlan(
                FlightPlanIdentifier.valueOf("XYZ"),
                flight(),
                aircraft(),
                p, // Associado ao piloto "ABC"
                fuelQuantity()
        ));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            controller.deactivatePilot("ABC");
        });

        Pilot updated = pilotRepo.ofIdentity(PilotID.valueOf("ABC")).get();
        assertTrue(updated.pilotStatus().isValid(), "O piloto deveria continuar ativo!");
    }

    @Test
    void ensureDeactivatePilotRejectsNullID() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.deactivatePilot(null));
    }

    @Test
    void ensureDeactivatePilotRejectsNonExistentPilot() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.deactivatePilot("ZZZ"));
    }

    @Test
    void ensureDeactivatePilotRejectsPilotFromDifferentCompany() {
        // Arrange
        final Pilot pilot = new Pilot(
                PilotID.valueOf("ABC"),
                new Certification("01-01-2020", "01-01-2099"),
                PilotStatus.valueOf(new Certification("01-01-2020", "01-01-2099")),
                IATACode.valueOf("FR"),
                EmailAddress.valueOf("pilot@test.com"),
                PhoneNumber.valueOf("+351912345678"),
                "John", "Doe", new HashSet<>()
        );
        pilotRepo.save(pilot);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> controller.deactivatePilot("ABC"));
    }

    @Test
    void ensureActivatePilotWorksSuccessfully() {
        // Arrange
        final Pilot pilot = new Pilot(
                PilotID.valueOf("ABC"),
                new Certification("01-01-2020", "01-01-2021"),
                PilotStatus.valueOf(new Certification("01-01-2020", "01-01-2021")),
                IATACode.valueOf("TP"),
                EmailAddress.valueOf("pilot@test.com"),
                PhoneNumber.valueOf("+351912345678"),
                "John", "Doe", new HashSet<>()
        );
        pilotRepo.save(pilot);

        // Act
        controller.activatePilot(PilotID.valueOf("ABC"));

        // Assert
        final Pilot updated = pilotRepo.ofIdentity(PilotID.valueOf("ABC")).get();
        assertTrue(updated.pilotStatus().isValid());
    }

    @Test
    void ensureControllerRejectsNullAuthz() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new DeactivatePilotController(
                        null, pilotRepo, mockFlightPlanRepo,
                        collaboratorRepo, mockValidator,
                        new RegisterBackofficeUserController()
                ));
    }

    @Test
    void ensureControllerRejectsNullPilotRepo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new DeactivatePilotController(
                        mockAuthz, null, mockFlightPlanRepo,
                        collaboratorRepo, mockValidator,
                        new RegisterBackofficeUserController()
                ));
    }

    @Test
    void ensureControllerRejectsNullFlightPlanRepo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new DeactivatePilotController(
                        mockAuthz, pilotRepo, null,
                        collaboratorRepo, mockValidator,
                        new RegisterBackofficeUserController()
                ));
    }

    @Test
    void ensureControllerRejectsNullCollaboratorRepo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new DeactivatePilotController(
                        mockAuthz, pilotRepo, mockFlightPlanRepo,
                        null, mockValidator,
                        new RegisterBackofficeUserController()
                ));
    }

    private FlightPlan flightPlan() {
        return new FlightPlan(
                FlightPlanIdentifier.valueOf("FP001"),
                flight(),
                aircraft(),
                pilot(),
                fuelQuantity()
        );
    }

    private Flight flight() {
        return new Flight(
                FlightDesignator.valueOf("TP123"),
                route(),
                isep.aisafe.flight.domain.Departure.valueOf(LocalDateTime.of(2027, 1, 10, 10, 0))
        );
    }

    private FlightRoute route() {
        return new FlightRoute(
                RouteDesignator.valueOf("TP123"),
                company(),
                airport("LPPT", "LIS", "Lisbon Airport", "Lisbon"),
                airport("LPPR", "OPO", "Porto Airport", "Porto")
        );
    }

    private Aircraft aircraft() {
        return aircraft(company());
    }

    private Aircraft aircraft(final AirTransportCompany company) {
        final EngineModel engineModel = engineModel();
        return new Aircraft(
                AircraftRegistration.valueOf("CS-TXA"),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(100, 10, 0),
                AircraftOperationalStatus.active(),
                6,
                company,
                aircraftModel(engineModel),
                engineModel
        );
    }

    private Pilot pilot() {
        return pilot("TP");
    }

    private Pilot pilot(final String companyIataCode) {
        final Certification certification = new Certification("01-01-2026", "01-01-2028");
        return new Pilot(
                PilotID.valueOf("ABC"),
                certification,
                PilotStatus.valueOf(certification),
                IATACode.valueOf(companyIataCode),
                EmailAddress.valueOf("pilot@example.com"),
                PhoneNumber.valueOf("+351912345678"),
                "Ben",
                "Dover",
                Set.of(AircraftModelDesignator.valueOf("A320"))
        );
    }

    private Pilot inactivePilot() {
        final Certification certification = new Certification("01-01-2020", "01-01-2021");
        return new Pilot(
                PilotID.valueOf("DEF"),
                certification,
                PilotStatus.valueOf(certification),
                IATACode.valueOf("TP"),
                EmailAddress.valueOf("pilot@example.com"),
                PhoneNumber.valueOf("+351912345678"),
                "Ben",
                "Dover",
                Set.of(AircraftModelDesignator.valueOf("A320"))
        );
    }

    private FuelQuantity fuelQuantity() {
        return FuelQuantity.valueOf(5000, FuelUnit.valueOf("KG"));
    }

    private Airport airport(
            final String icao,
            final String iata,
            final String name,
            final String town) {

        return new Airport(
                AirportICAOCode.valueOf(icao),
                AirportIATACode.valueOf(iata),
                name,
                Country.valueOf("Portugal"),
                town,
                new GpsCoordinates(38.7, -9.1),
                new Elevation(114.0),
                airControlArea(icao, name)
        );
    }

    private AirControlArea airControlArea(final String icao, final String name) {
        return new AirControlArea(
                AirControlAreaCode.valueOf(icao + "FIR"),
                name + " Control",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );
    }

    private AirTransportCompany company() {
        return company("TAP Air Portugal", "Portugal", "TP", "TAP");
    }

    private AirTransportCompany company(
            final String name,
            final String country,
            final String iata,
            final String icao) {

        return new AirTransportCompany(
                name,
                Country.valueOf(country),
                IATACode.valueOf(iata),
                ICAOCode.valueOf(icao)
        );
    }

    private EngineModel engineModel() {
        return new EngineModel(
                EngineModelDesignator.valueOf("LEAP-X", "CFM International"),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                120.0,
                140.0,
                0.60
        );
    }

    private AircraftModel aircraftModel(final EngineModel certifiedEngineModel) {
        return new AircraftModel(
                AircraftModelDesignator.valueOf("A320", "Airbus"),
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
                180,
                List.of(certifiedEngineModel)
        );
    }
}