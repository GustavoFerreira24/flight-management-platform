package isep.aisafe.application.flightroute;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.airport.domain.Airport;
import isep.aisafe.airport.domain.AirportIATACode;
import isep.aisafe.airport.domain.AirportICAOCode;
import isep.aisafe.airport.domain.Elevation;
import isep.aisafe.airport.domain.GpsCoordinates;
import isep.aisafe.airport.domain.InMemoryAirportRepository;
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
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.flightroute.domain.FlightRouteStatus;
import isep.aisafe.flightroute.domain.FlightRouteType;
import isep.aisafe.flightroute.domain.InMemoryFlightRouteRepository;
import isep.aisafe.flightroute.domain.RouteDesignator;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegisterFlightRouteControllerTest {

    @Test
    void ensureValidFlightRouteRegistrationIsAccepted() {
        final Fixture fixture = new Fixture("tap.route@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAirports();

        final FlightRoute result = fixture.controller().registerFlightRoute("TP730", "LRAA", "LRAB");

        assertArrayEquals(AisafePermissions.AIR_COMPANY_CONFIGURATION, fixture.authz.requestedRoles());
        assertEquals(RouteDesignator.valueOf("TP730"), result.identity());
        assertEquals(FlightRouteType.regular(), result.routeType());
        assertEquals(FlightRouteStatus.active(), result.routeStatus());
        assertEquals(fixture.company, result.company());
        assertEquals(fixture.departureAirport, result.departureAirport());
        assertEquals(fixture.destinationAirport, result.destinationAirport());
        assertTrue(fixture.flightRouteRepository.findByRouteDesignator(RouteDesignator.valueOf("TP730")).isPresent());
    }

    @Test
    void ensureFlightRouteCanBeRegisteredWithExplicitType() {
        final Fixture fixture = new Fixture("tap.type@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAirports();

        final FlightRoute result = fixture.controller().registerFlightRoute("TP731", "seasonal", "LRAA", "LRAB");

        assertEquals(FlightRouteType.valueOf("SEASONAL"), result.routeType());
    }

    @Test
    void ensureDuplicateRouteDesignatorIsRejected() {
        final Fixture fixture = new Fixture("tap.duplicate.route@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAirports();
        fixture.controller().registerFlightRoute("TP732", "LRAA", "LRAB");

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                fixture.controller().registerFlightRoute("TP732", "LRAA", "LRAB"));

        assertEquals("Flight route designator already exists.", result.getMessage());
    }

    @Test
    void ensureRouteDesignatorMustBelongToAuthenticatedCompany() {
        final Fixture fixture = new Fixture("tap.foreign.route@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAirports();

        assertThrows(IllegalArgumentException.class, () ->
                fixture.controller().registerFlightRoute("FR733", "LRAA", "LRAB"));
    }

    @Test
    void ensureDepartureAirportMustExist() {
        final Fixture fixture = new Fixture("tap.departure.route@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAirports();

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                fixture.controller().registerFlightRoute("TP734", "ZZZZ", "LRAB"));

        assertEquals("Departure airport was not found.", result.getMessage());
    }

    @Test
    void ensureDestinationAirportMustExist() {
        final Fixture fixture = new Fixture("tap.destination.route@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAirports();

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                fixture.controller().registerFlightRoute("TP735", "LRAA", "ZZZZ"));

        assertEquals("Destination airport was not found.", result.getMessage());
    }

    @Test
    void ensureDepartureAndDestinationMustBeDifferent() {
        final Fixture fixture = new Fixture("tap.same.route@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAirports();

        assertThrows(IllegalArgumentException.class, () ->
                fixture.controller().registerFlightRoute("TP736", "LRAA", "LRAA"));
    }

    @Test
    void ensureExistingRouteDesignatorCanBeCheckedBeforeRegistration() {
        final Fixture fixture = new Fixture("tap.check.route@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAirports();
        fixture.controller().registerFlightRoute("TP737", "LRAA", "LRAB");

        assertTrue(fixture.controller().routeDesignatorExists("TP737"));
        assertFalse(fixture.controller().routeDesignatorExists("TP9998"));
    }

    @Test
    void ensureAirportsCanBeListedForSelection() {
        final Fixture fixture = new Fixture("tap.airports.route@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAirports();

        final List<String> result = fixture.controller().airportOptions();

        assertTrue(result.contains("LRAA | RAA | Route A Airport"));
        assertTrue(result.contains("LRAB | RAB | Route B Airport"));
    }

    @Test
    void ensureAuthenticatedUserMustBeCustomerCollaborator() {
        final Fixture fixture = new Fixture("unknown.route@aisafe.com");
        fixture.bootstrapAirportsOnly();

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().registerFlightRoute("TP738", "LRAA", "LRAB"));

        assertEquals("Authenticated user is not a customer collaborator.", result.getMessage());
    }

    @Test
    void ensureCollaboratorMustBeAssociatedWithAirTransportCompany() {
        final Fixture fixture = new Fixture("area.route@aisafe.com");
        fixture.bootstrapAirportsOnly();
        fixture.collaboratorRepository.save(new CustomerCollaborator(
                "Area",
                "Collaborator",
                CollaboratorEmail.valueOf("area.route@aisafe.com"),
                CustomerReference.airControlArea("LISACC"),
                SystemUsername.valueOf("area.route@aisafe.com")
        ));

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().registerFlightRoute("TP739", "LRAA", "LRAB"));

        assertEquals("Authenticated collaborator is not associated with an air transport company.", result.getMessage());
    }

    @Test
    void ensureAssociatedAirTransportCompanyMustExist() {
        final Fixture fixture = new Fixture("missing.route.company@aisafe.com");
        fixture.bootstrapAirportsOnly();
        fixture.collaboratorRepository.save(companyCollaborator("missing.route.company@aisafe.com", "ZZ"));

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().registerFlightRoute("ZZ740", "LRAA", "LRAB"));

        assertEquals("Associated air transport company was not found.", result.getMessage());
    }

    @Test
    void ensureAuthorizationErrorsArePropagated() {
        final Fixture fixture = new Fixture("tap.auth.route@aisafe.com");
        fixture.bootstrapValidCompanyCollaboratorAndAirports();
        final IllegalStateException expected = new IllegalStateException("not authorized");

        fixture.authz.throwOnAuthorization(expected);

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                fixture.controller().registerFlightRoute("TP741", "LRAA", "LRAB"));

        assertEquals(expected, result);
    }

    @Test
    void ensureNullDependenciesAreRejected() {
        final TestAuthorizationService authz = new TestAuthorizationService("tap.null.route@aisafe.com");

        assertThrows(IllegalArgumentException.class, () -> new RegisterFlightRouteController(
                null,
                new InMemoryFlightRouteRepository(),
                new InMemoryAirportRepository(),
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository()
        ));

        assertThrows(IllegalArgumentException.class, () -> new RegisterFlightRouteController(
                authz,
                null,
                new InMemoryAirportRepository(),
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository()
        ));

        assertThrows(IllegalArgumentException.class, () -> new RegisterFlightRouteController(
                authz,
                new InMemoryFlightRouteRepository(),
                null,
                new InMemoryAirTransportCompanyRepository(),
                new InMemoryCustomerCollaboratorRepository()
        ));

        assertThrows(IllegalArgumentException.class, () -> new RegisterFlightRouteController(
                authz,
                new InMemoryFlightRouteRepository(),
                new InMemoryAirportRepository(),
                null,
                new InMemoryCustomerCollaboratorRepository()
        ));

        assertThrows(IllegalArgumentException.class, () -> new RegisterFlightRouteController(
                authz,
                new InMemoryFlightRouteRepository(),
                new InMemoryAirportRepository(),
                new InMemoryAirTransportCompanyRepository(),
                null
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

    private static Airport airport(
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
                new AirControlArea(
                        AirControlAreaCode.valueOf(icao + "FIR"),
                        name + " Control",
                        GeographicBoundary.valueOf(-90, 90, -180, 180),
                        MinimumFuelRequirement.valueOf(1, FuelUnit.valueOf("KG"))
                )
        );
    }

    private static class Fixture {

        private final TestAuthorizationService authz;
        private final InMemoryFlightRouteRepository flightRouteRepository = new InMemoryFlightRouteRepository();
        private final InMemoryAirportRepository airportRepository = new InMemoryAirportRepository();
        private final InMemoryAirTransportCompanyRepository companyRepository =
                new InMemoryAirTransportCompanyRepository();
        private final InMemoryCustomerCollaboratorRepository collaboratorRepository =
                new InMemoryCustomerCollaboratorRepository();

        private AirTransportCompany company;
        private Airport departureAirport;
        private Airport destinationAirport;

        Fixture(final String username) {
            this.authz = new TestAuthorizationService(username);
        }

        void bootstrapValidCompanyCollaboratorAndAirports() {
            company = company("TAP Air Portugal", "Portugal", "TP", "TAP");
            companyRepository.save(company);
            collaboratorRepository.save(companyCollaborator(authz.username(), "TP"));
            bootstrapAirportsOnly();
        }

        void bootstrapAirportsOnly() {
            departureAirport = airport("LRAA", "RAA", "Route A Airport", "Route A");
            destinationAirport = airport("LRAB", "RAB", "Route B Airport", "Route B");

            airportRepository.save(departureAirport);
            airportRepository.save(destinationAirport);
        }

        RegisterFlightRouteController controller() {
            return new RegisterFlightRouteController(
                    authz,
                    flightRouteRepository,
                    airportRepository,
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
