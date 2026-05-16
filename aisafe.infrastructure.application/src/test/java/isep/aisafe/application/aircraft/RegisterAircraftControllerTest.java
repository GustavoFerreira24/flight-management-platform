package isep.aisafe.application.aircraft;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.aircraft.domain.AircraftRegistration;
import isep.aisafe.aircraft.domain.InMemoryAircraftRepository;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelMotorization;
import isep.aisafe.aircraftmodel.domain.AircraftModelType;
import isep.aisafe.aircraftmodel.domain.AircraftVariantService;
import isep.aisafe.aircraftmodel.domain.InMemoryAircraftModelRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.airtransportcompany.domain.InMemoryAirTransportCompanyRepository;
import isep.aisafe.customercollaborator.domain.CollaboratorEmail;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerReference;
import isep.aisafe.customercollaborator.domain.InMemoryCustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.SystemUsername;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import isep.aisafe.enginemodel.domain.InMemoryEngineModelRepository;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegisterAircraftControllerTest {

    @Test
    void ensureValidAircraftRegistrationIsAccepted() {
        final Fixture fixture = new Fixture("tap.collaborator@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorModelAndEngine();

        final Aircraft result = fixture.controller().registerAircraft(
                "CS-U70",
                "Portugal",
                6,
                150,
                12,
                0,
                "US70-A320neo",
                "Airbus",
                "US70-LEAP-1A",
                "CFM International"
        );

        assertArrayEquals(AisafePermissions.AIR_COMPANY_CONFIGURATION, fixture.authz.requestedRoles());
        assertEquals(AircraftRegistration.valueOf("CS-U70"), result.identity());
        assertEquals("Portugal", result.registrationCountry().toString());
        assertEquals(162, result.cabinConfiguration().totalSeats());
        assertEquals("ACTIVE", result.operationalStatus().toString());
        assertEquals(fixture.company, result.company());
        assertEquals(fixture.aircraftModel, result.aircraftModel());
        assertEquals(fixture.engineModel, result.engineModel());
        assertTrue(fixture.aircraftRepository.findByRegistration(AircraftRegistration.valueOf("CS-U70")).isPresent());
    }

    @Test
    void ensureAircraftCanBeRegisteredWithExplicitOperationalStatus() {
        final Fixture fixture = new Fixture("tap.status@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorModelAndEngine();

        final Aircraft result = fixture.controller().registerAircraft(
                "CS-STS",
                "Portugal",
                "IN_MAINTENANCE",
                6,
                150,
                12,
                0,
                "US70-A320neo",
                "Airbus",
                "US70-LEAP-1A",
                "CFM International"
        );

        assertEquals("IN_MAINTENANCE", result.operationalStatus().toString());
    }

    @Test
    void ensureAircraftModelDesignatorsCanBeListedForSelection() {
        final Fixture fixture = new Fixture("tap.models@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorModelAndEngine();

        final List<String> result = fixture.controller().aircraftModelDesignators();

        assertArrayEquals(AisafePermissions.AIR_COMPANY_CONFIGURATION, fixture.authz.requestedRoles());
        assertEquals(List.of("US70-A320NEO | AIRBUS"), result);
    }

    @Test
    void ensureCertifiedEngineModelDesignatorsCanBeListedForSelectedAircraftModel() {
        final Fixture fixture = new Fixture("tap.engines@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorModelAndEngine();

        final List<String> result = fixture.controller().certifiedEngineModelDesignators("US70-A320neo | Airbus");

        assertArrayEquals(AisafePermissions.AIR_COMPANY_CONFIGURATION, fixture.authz.requestedRoles());
        assertEquals(List.of("US70-LEAP-1A | CFM INTERNATIONAL"), result);
    }

    @Test
    void ensureDuplicateAircraftRegistrationIsRejected() {
        final Fixture fixture = new Fixture("tap.duplicate@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorModelAndEngine();

        fixture.registerValidAircraft("CS-DUP");

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                fixture.registerValidAircraft("CS-DUP"));

        assertEquals("Aircraft registration already exists.", result.getMessage());
    }

    @Test
    void ensureExistingAircraftRegistrationCanBeCheckedBeforeRegistration() {
        final Fixture fixture = new Fixture("tap.existing@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorModelAndEngine();

        fixture.registerValidAircraft("CS-CHK");

        assertArrayEquals(AisafePermissions.AIR_COMPANY_CONFIGURATION, fixture.authz.requestedRoles());
        assertTrue(fixture.controller().aircraftRegistrationExists("CS-CHK"));
        assertFalse(fixture.controller().aircraftRegistrationExists("CS-NEW"));
    }

    @Test
    void ensureUnknownAircraftModelIsRejected() {
        final Fixture fixture = new Fixture("tap.model@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorModelAndEngine();

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                fixture.controller().registerAircraft(
                        "CS-MDL",
                        "Portugal",
                        6,
                        150,
                        12,
                        0,
                        "UNKNOWN-MODEL",
                        "Airbus",
                        "US70-LEAP-1A",
                        "CFM International"
                ));

        assertEquals("Aircraft model was not found.", result.getMessage());
    }

    @Test
    void ensureUnknownEngineModelIsRejected() {
        final Fixture fixture = new Fixture("tap.engine@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorModelAndEngine();

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                fixture.controller().registerAircraft(
                        "CS-ENG",
                        "Portugal",
                        6,
                        150,
                        12,
                        0,
                        "US70-A320neo",
                        "Airbus",
                        "UNKNOWN-ENGINE",
                        "CFM International"
                ));

        assertEquals("Engine model was not found.", result.getMessage());
    }

    @Test
    void ensureNonCertifiedEngineModelIsRejected() {
        final Fixture fixture = new Fixture("tap.variant@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorModelAndEngine();

        final EngineModel nonCertifiedEngineModel = engineModel("US70-PW1100G", "Pratt Whitney");
        fixture.engineModelRepository.save(nonCertifiedEngineModel);

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                fixture.controller().registerAircraft(
                        "CS-VAR",
                        "Portugal",
                        6,
                        150,
                        12,
                        0,
                        "US70-A320neo",
                        "Airbus",
                        "US70-PW1100G",
                        "Pratt Whitney"
                ));

        assertEquals("Engine model is not certified for this aircraft model.", result.getMessage());
        assertFalse(fixture.aircraftRepository.findByRegistration(AircraftRegistration.valueOf("CS-VAR")).isPresent());
    }

    @Test
    void ensureAuthenticatedUserMustBeCustomerCollaborator() {
        final Fixture fixture = new Fixture("unknown@aisafe.com");

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().registerAircraft(
                        "CS-UNK",
                        "Portugal",
                        6,
                        150,
                        12,
                        0,
                        "US70-A320neo",
                        "Airbus",
                        "US70-LEAP-1A",
                        "CFM International"
                ));

        assertEquals("Authenticated user is not a customer collaborator.", result.getMessage());
    }

    @Test
    void ensureCollaboratorMustBeAssociatedWithAirTransportCompany() {
        final Fixture fixture = new Fixture("area.collaborator@aisafe.com");

        fixture.collaboratorRepository.save(new CustomerCollaborator(
                "Area",
                "Collaborator",
                CollaboratorEmail.valueOf("area.collaborator@aisafe.com"),
                CustomerReference.airControlArea("LISACC"),
                SystemUsername.valueOf("area.collaborator@aisafe.com")
        ));

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().registerAircraft(
                        "CS-ACA",
                        "Portugal",
                        6,
                        150,
                        12,
                        0,
                        "US70-A320neo",
                        "Airbus",
                        "US70-LEAP-1A",
                        "CFM International"
                ));

        assertEquals("Authenticated collaborator is not associated with an air transport company.", result.getMessage());
    }

    @Test
    void ensureAssociatedAirTransportCompanyMustExist() {
        final Fixture fixture = new Fixture("missing.company@aisafe.com");

        fixture.collaboratorRepository.save(companyCollaborator(
                "missing.company@aisafe.com",
                "ZZ"
        ));

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().registerAircraft(
                        "CS-MIS",
                        "Portugal",
                        6,
                        150,
                        12,
                        0,
                        "US70-A320neo",
                        "Airbus",
                        "US70-LEAP-1A",
                        "CFM International"
                ));

        assertEquals("Associated air transport company was not found.", result.getMessage());
    }

    @Test
    void ensureAuthorizationErrorsArePropagated() {
        final Fixture fixture = new Fixture("tap.auth@aisafe.com");
        final IllegalStateException expected = new IllegalStateException("not authorized");

        fixture.authz.throwOnAuthorization(expected);

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.registerValidAircraft("CS-AUT"));

        assertEquals(expected, result);
    }

    @Test
    void ensureNullDependenciesAreRejected() {
        final TestAuthorizationService authz = new TestAuthorizationService("tap.nulls@aisafe.com");

        assertThrows(IllegalArgumentException.class, () -> new RegisterAircraftController(
                null,
                new InMemoryAircraftRepository(),
                new InMemoryAircraftModelRepository(),
                new InMemoryEngineModelRepository(),
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository()
        ));

        assertThrows(IllegalArgumentException.class, () -> new RegisterAircraftController(
                authz,
                null,
                new InMemoryAircraftModelRepository(),
                new InMemoryEngineModelRepository(),
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository()
        ));

        assertThrows(IllegalArgumentException.class, () -> new RegisterAircraftController(
                authz,
                new InMemoryAircraftRepository(),
                null,
                new InMemoryEngineModelRepository(),
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository()
        ));

        assertThrows(IllegalArgumentException.class, () -> new RegisterAircraftController(
                authz,
                new InMemoryAircraftRepository(),
                new InMemoryAircraftModelRepository(),
                null,
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository()
        ));

        assertThrows(IllegalArgumentException.class, () -> new RegisterAircraftController(
                authz,
                new InMemoryAircraftRepository(),
                new InMemoryAircraftModelRepository(),
                new InMemoryEngineModelRepository(),
                null,
                new InMemoryCustomerCollaboratorRepository()
        ));

        assertThrows(IllegalArgumentException.class, () -> new RegisterAircraftController(
                authz,
                new InMemoryAircraftRepository(),
                new InMemoryAircraftModelRepository(),
                new InMemoryEngineModelRepository(),
                new InMemoryAirTransportCompanyRepository(),
                null
        ));

        assertThrows(IllegalArgumentException.class, () -> new RegisterAircraftController(
                authz,
                new InMemoryAircraftRepository(),
                new InMemoryAircraftModelRepository(),
                new InMemoryEngineModelRepository(),
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository(),
                null
        ));

        assertDoesNotThrow(() -> new RegisterAircraftController(
                authz,
                new InMemoryAircraftRepository(),
                new InMemoryAircraftModelRepository(),
                new InMemoryEngineModelRepository(),
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository(),
                new AircraftVariantService()
        ));
    }



    private static CustomerCollaborator companyCollaborator(final String username, final String companyIataCode) {
        return new CustomerCollaborator(
                "Test",
                "Collaborator",
                CollaboratorEmail.valueOf(username),
                CustomerReference.airTransportCompany(companyIataCode),
                SystemUsername.valueOf(username)
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
        private final InMemoryAircraftModelRepository aircraftModelRepository = new InMemoryAircraftModelRepository();
        private final InMemoryEngineModelRepository engineModelRepository = new InMemoryEngineModelRepository();
        private final InMemoryAirTransportCompanyRepository companyRepository =
                new InMemoryAirTransportCompanyRepository();
        private final InMemoryCustomerCollaboratorRepository collaboratorRepository =
                new InMemoryCustomerCollaboratorRepository();

        private AirTransportCompany company;
        private AircraftModel aircraftModel;
        private EngineModel engineModel;

        Fixture(final String username) {
            this.authz = new TestAuthorizationService(username);
        }

        void bootstrapValidCompanyCollaboratorModelAndEngine() {
            company = company("TAP Air Portugal", "Portugal", "TP", "TAP");
            engineModel = engineModel("US70-LEAP-1A", "CFM International");
            aircraftModel = aircraftModel("US70-A320neo", "Airbus", 186, engineModel);

            companyRepository.save(company);
            engineModelRepository.save(engineModel);
            aircraftModelRepository.save(aircraftModel);
            collaboratorRepository.save(companyCollaborator(authz.username(), "TP"));
        }

        RegisterAircraftController controller() {
            return new RegisterAircraftController(
                    authz,
                    aircraftRepository,
                    aircraftModelRepository,
                    engineModelRepository,
                    companyRepository,
                    collaboratorRepository
            );
        }

        Aircraft registerValidAircraft(final String registration) {
            return controller().registerAircraft(
                    registration,
                    "Portugal",
                    6,
                    150,
                    12,
                    0,
                    "US70-A320neo",
                    "Airbus",
                    "US70-LEAP-1A",
                    "CFM International"
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
