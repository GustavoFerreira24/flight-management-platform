package isep.aisafe.application.aircraft;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.aircraft.domain.AircraftOperationalStatus;
import isep.aisafe.aircraft.domain.AircraftRegistration;
import isep.aisafe.aircraft.domain.AircraftRegistrationCountry;
import isep.aisafe.aircraft.domain.CabinConfiguration;
import isep.aisafe.aircraft.domain.InMemoryAircraftRepository;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelMotorization;
import isep.aisafe.aircraftmodel.domain.AircraftModelType;
import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.airtransportcompany.domain.InMemoryAirTransportCompanyRepository;
import isep.aisafe.customercollaborator.domain.CollaboratorEmail;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.InMemoryCustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.SystemUsername;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DecommissionAircraftControllerTest {

    @Test
    void ensureValidAircraftCanBeDecommissioned() {
        final Fixture fixture = new Fixture("tap.decommission@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAircraft("CS-U71");

        final Aircraft result = fixture.controller().decommissionAircraft("CS-U71");

        assertArrayEquals(AisafePermissions.AIR_COMPANY_CONFIGURATION, fixture.authz.requestedRoles());
        assertEquals(AircraftOperationalStatus.decommissioned(), result.operationalStatus());
        assertTrue(result.isDecommissioned());
        assertFalse(result.canBeUsedForFlights());
        assertTrue(fixture.aircraftRepository
                .findByRegistration(AircraftRegistration.valueOf("CS-U71"))
                .get()
                .isDecommissioned());
    }

    @Test
    void ensureUnknownAircraftIsRejected() {
        final Fixture fixture = new Fixture("tap.unknown@aisafe.com");
        fixture.bootstrapValidCompanyCollaborator();

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                fixture.controller().decommissionAircraft("CS-MIS"));

        assertEquals("Aircraft was not found.", result.getMessage());
    }

    @Test
    void ensureAircraftMustBelongToAuthenticatedCollaboratorCompany() {
        final Fixture fixture = new Fixture("tap.owner@aisafe.com");
        fixture.bootstrapValidCompanyCollaborator();

        final AirTransportCompany otherCompany = company("Ryanair", "Ireland", "FR", "RYR");
        fixture.companyRepository.save(otherCompany);
        fixture.aircraftRepository.save(aircraft("EI-U71", otherCompany));

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().decommissionAircraft("EI-U71"));

        assertEquals("Aircraft does not belong to authenticated collaborator's company.", result.getMessage());
    }

    @Test
    void ensureAircraftWithPendingFlightsCannotBeDecommissioned() {
        final Fixture fixture = new Fixture("tap.pending@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAircraft("CS-PND");
        fixture.pendingFlightsService.hasPendingFlights = true;

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().decommissionAircraft("CS-PND"));

        assertEquals("Aircraft has pending flights registered in the system.", result.getMessage());
        assertFalse(fixture.aircraftRepository
                .findByRegistration(AircraftRegistration.valueOf("CS-PND"))
                .get()
                .isDecommissioned());
    }

    @Test
    void ensureAlreadyDecommissionedAircraftIsRejected() {
        final Fixture fixture = new Fixture("tap.already@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAircraft("CS-OLD");
        fixture.controller().decommissionAircraft("CS-OLD");

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().decommissionAircraft("CS-OLD"));

        assertEquals("Aircraft is already decommissioned.", result.getMessage());
    }

    @Test
    void ensureDecommissionableAircraftListsOnlyActiveCompanyAircraftWithoutPendingFlights() {
        final Fixture fixture = new Fixture("tap.list@aisafe.com");

        fixture.company = company("List Air", "Portugal", "QZ", "QZA");
        fixture.companyRepository.save(fixture.company);
        fixture.collaboratorRepository.save(companyCollaborator(fixture.authz.username(), fixture.company));

        final Aircraft activeAircraft = aircraft("CS-LA1", fixture.company);
        final Aircraft pendingAircraft = aircraft("CS-LA2", fixture.company);
        final Aircraft decommissionedAircraft = aircraft("CS-LA3", fixture.company);
        decommissionedAircraft.decommission();

        final AirTransportCompany otherCompany = company("Ryanair", "Ireland", "FR", "RYR");
        fixture.companyRepository.save(otherCompany);

        fixture.aircraftRepository.save(activeAircraft);
        fixture.aircraftRepository.save(pendingAircraft);
        fixture.aircraftRepository.save(decommissionedAircraft);
        fixture.aircraftRepository.save(aircraft("EI-LA1", otherCompany));
        fixture.pendingFlightsService.pendingRegistration = AircraftRegistration.valueOf("CS-LA2");

        final List<Aircraft> result = fixture.controller().decommissionableAircraft();

        assertArrayEquals(AisafePermissions.AIR_COMPANY_CONFIGURATION, fixture.authz.requestedRoles());
        assertEquals(1, result.size());
        assertEquals(AircraftRegistration.valueOf("CS-LA1"), result.get(0).registration());
    }

    @Test
    void ensureAuthenticatedUserMustBeCustomerCollaborator() {
        final Fixture fixture = new Fixture("unknown@aisafe.com");
        fixture.aircraftRepository.save(aircraft("CS-X71", company("TAP Air Portugal", "Portugal", "TP", "TAP")));

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().decommissionAircraft("CS-X71"));

        assertEquals("Authenticated user is not a customer collaborator.", result.getMessage());
    }

    @Test
    void ensureCollaboratorMustBeAssociatedWithAirTransportCompany() {
        final Fixture fixture = new Fixture("area.collaborator@aisafe.com");

        fixture.aircraftRepository.save(aircraft("CS-Y71", company("TAP Air Portugal", "Portugal", "TP", "TAP")));
        fixture.collaboratorRepository.save(new CustomerCollaborator(
                "Area",
                "Collaborator",
                CollaboratorEmail.valueOf("area.collaborator@aisafe.com"),
                area("LISACC"),
                SystemUsername.valueOf("area.collaborator@aisafe.com")
        ));

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().decommissionAircraft("CS-Y71"));

        assertEquals("Authenticated collaborator is not associated with an air transport company.", result.getMessage());
    }

    @Test
    void ensureAuthorizationErrorsArePropagated() {
        final Fixture fixture = new Fixture("tap.auth@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAircraft("CS-AUT");
        final IllegalStateException expected = new IllegalStateException("not authorized");

        fixture.authz.throwOnAuthorization(expected);

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().decommissionAircraft("CS-AUT"));

        assertEquals(expected, result);
    }

    @Test
    void ensureNullDependenciesAreRejected() {
        final TestAuthorizationService authz = new TestAuthorizationService("tap.nulls@aisafe.com");

        assertThrows(IllegalArgumentException.class, () -> new DecommissionAircraftController(
                null,
                new InMemoryAircraftRepository(),
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository(),
                new TestPendingAircraftFlightsService()
        ));

        assertThrows(IllegalArgumentException.class, () -> new DecommissionAircraftController(
                authz,
                null,
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository(),
                new TestPendingAircraftFlightsService()
        ));

        assertThrows(IllegalArgumentException.class, () -> new DecommissionAircraftController(
                authz,
                new InMemoryAircraftRepository(),
                null,
                new InMemoryCustomerCollaboratorRepository(),
                new TestPendingAircraftFlightsService()
        ));

        assertThrows(IllegalArgumentException.class, () -> new DecommissionAircraftController(
                authz,
                new InMemoryAircraftRepository(),
                new InMemoryAirTransportCompanyRepository(),
                null,
                new TestPendingAircraftFlightsService()
        ));

        assertThrows(IllegalArgumentException.class, () -> new DecommissionAircraftController(
                authz,
                new InMemoryAircraftRepository(),
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository(),
                null
        ));
    }

    private static CustomerCollaborator companyCollaborator(final String username, final AirTransportCompany company) {
        return new CustomerCollaborator(
                "Test",
                "Collaborator",
                CollaboratorEmail.valueOf(username),
                company,
                SystemUsername.valueOf(username)
        );
    }

    private static AirControlArea area(final String code) {
        return new AirControlArea(
                AirControlAreaCode.valueOf(code),
                "Test Area " + code,
                GeographicBoundary.valueOf(35.0, 45.0, -15.0, -5.0),
                MinimumFuelRequirement.valueOf(1000, FuelUnit.valueOf("KG"))
        );
    }

    private static AirTransportCompany company(
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

    private static Aircraft aircraft(final String registration, final AirTransportCompany company) {
        final EngineModel engineModel = engineModel("US71-LEAP-1A", "CFM International");
        final AircraftModel aircraftModel = aircraftModel("US71-A320neo", "Airbus", 186, engineModel);

        return new Aircraft(
                AircraftRegistration.valueOf(registration),
                AircraftRegistrationCountry.valueOf("Portugal"),
                CabinConfiguration.valueOf(150, 12, 0),
                AircraftOperationalStatus.active(),
                6,
                company,
                aircraftModel,
                engineModel
        );
    }

    private static EngineModel engineModel(final String modelName, final String manufacturer) {
        return new EngineModel(
                EngineModelDesignator.valueOf(modelName, manufacturer),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                120.0,
                140.0,
                0.60
        );
    }

    private static AircraftModel aircraftModel(
            final String modelName,
            final String manufacturer,
            final int passengerCapacity,
            final EngineModel certifiedEngineModel) {

        return new AircraftModel(
                AircraftModelDesignator.valueOf(modelName, manufacturer),
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
                passengerCapacity,
                List.of(certifiedEngineModel)
        );
    }

    private static class Fixture {

        private final TestAuthorizationService authz;
        private final InMemoryAircraftRepository aircraftRepository = new InMemoryAircraftRepository();
        private final InMemoryAirTransportCompanyRepository companyRepository =
                new InMemoryAirTransportCompanyRepository();
        private final InMemoryCustomerCollaboratorRepository collaboratorRepository =
                new InMemoryCustomerCollaboratorRepository();
        private final TestPendingAircraftFlightsService pendingFlightsService = new TestPendingAircraftFlightsService();

        private AirTransportCompany company;

        Fixture(final String username) {
            this.authz = new TestAuthorizationService(username);
        }

        void bootstrapValidCompanyCollaborator() {
            company = company("TAP Air Portugal", "Portugal", "TP", "TAP");

            companyRepository.save(company);
            collaboratorRepository.save(companyCollaborator(authz.username(), company));
        }

        void bootstrapValidCompanyCollaboratorAndAircraft(final String registration) {
            bootstrapValidCompanyCollaborator();
            aircraftRepository.save(aircraft(registration, company));
        }

        DecommissionAircraftController controller() {
            return new DecommissionAircraftController(
                    authz,
                    aircraftRepository,
                    companyRepository,
                    collaboratorRepository,
                    pendingFlightsService
            );
        }
    }

    private static class TestPendingAircraftFlightsService implements PendingAircraftFlightsService {

        private boolean hasPendingFlights;
        private AircraftRegistration pendingRegistration;

        @Override
        public boolean hasPendingFlights(final Aircraft aircraft) {
            return hasPendingFlights || aircraft.registration().equals(pendingRegistration);
        }
    }

    private static class TestAuthorizationService extends AisafeAuthorizationService {

        private final String username;
        private Role[] requestedRoles;
        private RuntimeException authorizationException;

        TestAuthorizationService(final String username) {
            this.username = username;
        }

        @Override
        public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {
            requestedRoles = roles;

            if (authorizationException != null) {
                throw authorizationException;
            }
        }

        @Override
        public String authenticatedUsername() {
            return username;
        }

        void throwOnAuthorization(final RuntimeException exception) {
            authorizationException = exception;
        }

        Role[] requestedRoles() {
            return requestedRoles;
        }

        String username() {
            return username;
        }
    }
}
