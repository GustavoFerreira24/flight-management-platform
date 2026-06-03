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

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListAircraftFleetControllerTest {

    @Test
    void ensureAuthenticatedCompanyFleetCanBeListed() {
        final Fixture fixture = new Fixture("tap.fleet@aisafe.com", "QA");
        fixture.bootstrap();
        fixture.saveAircraft("CS-L01", fixture.tap, fixture.airbus, fixture.leap, 160, 2018);
        fixture.saveAircraft("CS-L02", fixture.tap, fixture.boeing, fixture.leapB, 180, 2015);
        fixture.saveAircraft("EI-OUT", fixture.ryanair, fixture.boeing, fixture.leapB, 180, 2019);

        final List<AircraftFleetEntry> result = fixture.controller().listAircraftFleet();

        assertArrayEquals(AisafePermissions.AIR_COMPANY_CONFIGURATION, fixture.authz.requestedRoles());
        assertEquals(2, result.size());
        assertEquals("CS-L01", result.get(0).registration());
        assertEquals("US72-A320", result.get(0).model());
        assertEquals("AIRBUS", result.get(0).maker());
        assertEquals(160, result.get(0).totalSeats());
        assertEquals(186, result.get(0).modelCapacity());
        assertEquals(2018, result.get(0).manufactureYear());
        assertEquals(Year.now().getValue() - 2018, result.get(0).ageInYears());
        assertEquals("ACTIVE", result.get(0).operationalStatus());
        assertEquals("US72-LEAP-1A | CFM INTERNATIONAL", result.get(0).engineModel());
        assertEquals("CS-L02", result.get(1).registration());
    }

    @Test
    void ensureFleetCanBeFilteredByModelMakerCapacityAndAge() {
        final Fixture fixture = new Fixture("tap.filters@aisafe.com", "QB");
        fixture.bootstrap();
        fixture.saveAircraft("CS-F01", fixture.tap, fixture.airbus, fixture.leap, 160, 2018);
        fixture.saveAircraft("CS-F02", fixture.tap, fixture.boeing, fixture.leapB, 180, 2015);
        fixture.saveAircraft("CS-F03", fixture.tap, fixture.airbus, fixture.leap, 120, 2024);

        final int targetAge = Year.now().getValue() - 2018;
        final AircraftFleetFilter filter = new AircraftFleetFilter(
                "a320",
                "airbus",
                150,
                170,
                targetAge,
                targetAge
        );

        final List<AircraftFleetEntry> result = fixture.controller().listAircraftFleet(filter);

        assertEquals(1, result.size());
        assertEquals("CS-F01", result.get(0).registration());
    }

    @Test
    void ensureNullFilterListsFullFleet() {
        final Fixture fixture = new Fixture("tap.nullfilter@aisafe.com", "QC");
        fixture.bootstrap();
        fixture.saveAircraft("CS-N01", fixture.tap, fixture.airbus, fixture.leap, 160, 2018);

        final List<AircraftFleetEntry> result = fixture.controller().listAircraftFleet(null);

        assertEquals(1, result.size());
    }

    @Test
    void ensureInvalidFilterRangesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftFleetFilter(null, null, -1, null, null, null));
        assertThrows(IllegalArgumentException.class, () -> new AircraftFleetFilter(null, null, 200, 100, null, null));
        assertThrows(IllegalArgumentException.class, () -> new AircraftFleetFilter(null, null, null, null, 10, 5));
    }

    @Test
    void ensureAuthenticatedUserMustBeCustomerCollaborator() {
        final Fixture fixture = new Fixture("unknown.fleet@aisafe.com", "QD");

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().listAircraftFleet());

        assertEquals("Authenticated user is not a customer collaborator.", result.getMessage());
    }

    @Test
    void ensureAuthorizationErrorsArePropagated() {
        final Fixture fixture = new Fixture("tap.authfleet@aisafe.com", "QE");
        final IllegalStateException expected = new IllegalStateException("not authorized");

        fixture.authz.throwOnAuthorization(expected);

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().listAircraftFleet());

        assertEquals(expected, result);
    }

    @Test
    void ensureNullDependenciesAreRejected() {
        final TestAuthorizationService authz = new TestAuthorizationService("tap.nullfleet@aisafe.com");

        assertThrows(IllegalArgumentException.class, () -> new ListAircraftFleetController(
                null,
                new InMemoryAircraftRepository(),
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository()
        ));

        assertThrows(IllegalArgumentException.class, () -> new ListAircraftFleetController(
                authz,
                null,
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository()
        ));

        assertThrows(IllegalArgumentException.class, () -> new ListAircraftFleetController(
                authz,
                new InMemoryAircraftRepository(),
                null,
                new InMemoryCustomerCollaboratorRepository()
        ));

        assertThrows(IllegalArgumentException.class, () -> new ListAircraftFleetController(
                authz,
                new InMemoryAircraftRepository(),
                new InMemoryAirTransportCompanyRepository(),
                null
        ));
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

    private static CustomerCollaborator companyCollaborator(final String username, final String companyIataCode) {
        return new CustomerCollaborator(
                "Test",
                "Collaborator",
                CollaboratorEmail.valueOf(username),
                company("Test Company " + companyIataCode, "Portugal", companyIataCode, "TST"),
                SystemUsername.valueOf(username)
        );
    }

    private static class Fixture {

        private final TestAuthorizationService authz;
        private final InMemoryAircraftRepository aircraftRepository = new InMemoryAircraftRepository();
        private final InMemoryAirTransportCompanyRepository companyRepository =
                new InMemoryAirTransportCompanyRepository();
        private final InMemoryCustomerCollaboratorRepository collaboratorRepository =
                new InMemoryCustomerCollaboratorRepository();
        private final String companyIataCode;

        private AirTransportCompany tap;
        private AirTransportCompany ryanair;
        private EngineModel leap;
        private EngineModel leapB;
        private AircraftModel airbus;
        private AircraftModel boeing;

        Fixture(final String username, final String companyIataCode) {
            authz = new TestAuthorizationService(username);
            this.companyIataCode = companyIataCode;
        }

        void bootstrap() {
            tap = company("TAP Air Portugal " + companyIataCode, "Portugal", companyIataCode, "TAP");
            ryanair = company("Ryanair", "Ireland", "FR", "RYR");
            leap = engineModel("US72-LEAP-1A", "CFM International");
            leapB = engineModel("US72-LEAP-1B", "CFM International");
            airbus = aircraftModel("US72-A320", "Airbus", 186, leap);
            boeing = aircraftModel("US72-B737", "Boeing", 189, leapB);

            companyRepository.save(tap);
            companyRepository.save(ryanair);
            collaboratorRepository.save(companyCollaborator(authz.username(), companyIataCode));
        }

        void saveAircraft(
                final String registration,
                final AirTransportCompany company,
                final AircraftModel aircraftModel,
                final EngineModel engineModel,
                final int totalSeats,
                final int manufactureYear) {

            aircraftRepository.save(new Aircraft(
                    AircraftRegistration.valueOf(registration),
                    AircraftRegistrationCountry.valueOf("Portugal"),
                    CabinConfiguration.valueOf(totalSeats, 0, 0),
                    AircraftOperationalStatus.active(),
                    6,
                    company,
                    aircraftModel,
                    engineModel,
                    manufactureYear
            ));
        }

        ListAircraftFleetController controller() {
            return new ListAircraftFleetController(
                    authz,
                    aircraftRepository,
                    companyRepository,
                    collaboratorRepository
            );
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
