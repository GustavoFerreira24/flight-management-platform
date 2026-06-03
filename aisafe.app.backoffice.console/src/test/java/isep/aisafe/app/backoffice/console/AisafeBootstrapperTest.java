package isep.aisafe.app.backoffice.console;

import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import eapli.framework.infrastructure.authz.domain.model.SystemUser;
import isep.aisafe.aircontrolarea.domain.*;
import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.aircraft.domain.AircraftRegistration;
import isep.aisafe.aircraft.domain.InMemoryAircraftRepository;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelMotorization;
import isep.aisafe.aircraftmodel.domain.AircraftModelType;
import isep.aisafe.airport.domain.Airport;
import isep.aisafe.airport.domain.InMemoryAirportRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.customercollaborator.domain.CollaboratorEmail;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.InMemoryCustomerCollaboratorRepository;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import isep.aisafe.usermanagement.domain.AisafeRoles;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static isep.aisafe.app.backoffice.console.AisafeBootstrapper.registerAircraftIfMissing;
import static isep.aisafe.app.backoffice.console.AisafeBootstrapper.registerAirportIfMissing;
import static org.junit.jupiter.api.Assertions.*;

class AisafeBootstrapperTest {

    /**
     * Purpose: verify customer collaborator bootstrap creation.
     * Scenario: bootstrap a collaborator that does not exist yet.
     * Expected output: a collaborator is persisted and a matching system user is requested.
     */
    @Test
    void ensureCustomerCollaboratorIsBootstrappedWhenMissing() {
        // Arrange
        final FakeUserManagementService userService = new FakeUserManagementService();
        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();
        final AirTransportCompany airTransportCompany = new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        );

        // Act
        final CustomerCollaborator collaborator = AisafeBootstrapper.registerCustomerCollaboratorIfMissing(
                userService,
                repository,
                "TAP",
                "Collaborator",
                "tap.collaborator@aisafe.com",
                "Password1",
                airTransportCompany,
                null,
                Set.of(AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR)
        );

        // Assert
        assertEquals("TAP", collaborator.firstName());
        assertEquals("Collaborator", collaborator.lastName());
        assertEquals(airTransportCompany, collaborator.airTransportCompany());
        assertNull(collaborator.airControlArea());
        assertEquals("tap.collaborator@aisafe.com", collaborator.systemUsername().toString());
        assertTrue(repository.findByEmail(
                CollaboratorEmail.valueOf("tap.collaborator@aisafe.com")
        ).isPresent());
        assertEquals("tap.collaborator@aisafe.com", userService.username);
        assertEquals("Password1", userService.password);
        assertEquals(Set.of(AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR), userService.roles);
        assertEquals(1, userService.calls);
    }

    /**
     * Purpose: verify customer collaborator bootstrap idempotency.
     * Scenario: bootstrap the same collaborator twice.
     * Expected output: the second bootstrap reuses the existing collaborator and does not create another user.
     */
    @Test
    void ensureCustomerCollaboratorBootstrapIsIdempotent() {
        // Arrange
        final FakeUserManagementService userService = new FakeUserManagementService();
        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();
        final AirControlArea airControlArea = new AirControlArea(
                AirControlAreaCode.valueOf("LISACC"),
                "Lisbon Area Control Centre",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );

        AisafeBootstrapper.registerCustomerCollaboratorIfMissing(
                userService,
                repository,
                "Lisbon",
                "Controller",
                "lisacc.collaborator@aisafe.com",
                "Password1",
                null,
                airControlArea,
                Set.of(AisafeRoles.FLIGHT_CONTROL_OPERATOR)
        );

        // Act
        final CustomerCollaborator existingCollaborator = AisafeBootstrapper.registerCustomerCollaboratorIfMissing(
                userService,
                repository,
                "Lisbon",
                "Controller",
                "lisacc.collaborator@aisafe.com",
                "Password1",
                null,
                airControlArea,
                Set.of(AisafeRoles.FLIGHT_CONTROL_OPERATOR)
        );

        // Assert
        assertEquals(airControlArea, existingCollaborator.airControlArea());
        assertNull(existingCollaborator.airTransportCompany());
        assertEquals(1, userService.calls);
    }

    private static final class FakeUserManagementService extends UserManagementService {

        private String username;
        private String password;
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
            this.roles = roles;
            return null;
        }
    }

    @Test
    void ensureAirportIsBootstrappedWhenMissing() {
        // Arrange
        final InMemoryAirControlAreaRepository areaRepo = new InMemoryAirControlAreaRepository();
        final InMemoryAirportRepository airportRepo = new InMemoryAirportRepository();

        final AirControlArea lisbonArea = new AirControlArea(
                AirControlAreaCode.valueOf("LISACC"),
                "Lisbon Area Control Centre",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );

        areaRepo.save(lisbonArea);


        final Airport airport = AisafeBootstrapper.registerAirportIfMissing(
                airportRepo,
                "LPPR",
                "OPO",
                "Francisco Sá Carneiro",
                "Portugal",
                "Porto",
                41.2355,
                -8.6703,
                69.0,
                lisbonArea
        );

        assertEquals("LPPR", airport.icaoCode().toString());
        assertEquals("OPO", airport.iataCode().toString());
        assertEquals("Francisco Sá Carneiro", airport.name());
        assertEquals("Portugal", airport.country().toString());
        assertEquals("Porto", airport.town());
        assertEquals(41.2355, airport.coordinates().latitude());
        assertEquals(-8.6703, airport.coordinates().longitude());
        assertEquals(69.0, airport.elevation().meters());
    }

    @Test
    void ensureAircraftIsBootstrappedWhenMissing() {
        final InMemoryAircraftRepository repository = new InMemoryAircraftRepository();
        final EngineModel engineModel = engineModel();
        final AircraftModel aircraftModel = aircraftModel(engineModel);
        final AirTransportCompany company = new AirTransportCompany(
                "Air Europa",
                Country.valueOf("Spain"),
                IATACode.valueOf("UX"),
                ICAOCode.valueOf("AEA")
        );

        final Aircraft aircraft = registerAircraftIfMissing(
                repository,
                "EC-UX7",
                "Spain",
                2012,
                10,
                279,
                35,
                0,
                company,
                aircraftModel,
                engineModel
        );

        final Aircraft existingAircraft = registerAircraftIfMissing(
                repository,
                "EC-UX7",
                "Spain",
                2012,
                10,
                279,
                35,
                0,
                company,
                aircraftModel,
                engineModel
        );

        assertEquals(AircraftRegistration.valueOf("EC-UX7"), aircraft.identity());
        assertEquals(314, aircraft.cabinConfiguration().totalSeats());
        assertEquals(2012, aircraft.manufactureYear());
        assertEquals(company, aircraft.company());
        assertEquals(aircraft, existingAircraft);
        assertTrue(repository.findByRegistration(AircraftRegistration.valueOf("EC-UX7")).isPresent());
    }

    private EngineModel engineModel() {
        return new EngineModel(
                EngineModelDesignator.valueOf("GE 90B", "General Electric"),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                0.84,
                489000.0,
                0.00016
        );
    }

    private AircraftModel aircraftModel(final EngineModel engineModel) {
        return new AircraftModel(
                AircraftModelDesignator.valueOf("777-200ER", "Boeing"),
                AircraftModelType.valueOf("PASSENGER"),
                AircraftModelMotorization.valueOf("TURBOFAN"),
                14200,
                145150,
                347450,
                209000,
                181280,
                13100,
                905,
                440.0,
                0.017,
                0.87,
                314,
                List.of(engineModel)
        );
    }
}
