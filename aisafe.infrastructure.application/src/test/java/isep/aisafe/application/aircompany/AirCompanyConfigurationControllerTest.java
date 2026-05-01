package isep.aisafe.application.aircompany;

import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.InMemoryAirTransportCompanyRepository;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirCompanyConfigurationControllerTest {

    @Test
    void ensureControllerCanBeCreatedWithRepositoryOnly() {
        final AirCompanyConfigurationController controller =
                new AirCompanyConfigurationController(new InMemoryAirTransportCompanyRepository());

        assertNotNull(controller);
    }

    @Test
    void ensureNullRepositoryIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new AirCompanyConfigurationController(new CapturingAuthorizationService(), null));
    }

    @Test
    void ensureNullAuthorizationServiceIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new AirCompanyConfigurationController(null, new InMemoryAirTransportCompanyRepository()));
    }

    @Test
    void ensureValidAirTransportCompanyRegistrationIsAccepted() {
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryAirTransportCompanyRepository repository = new InMemoryAirTransportCompanyRepository();
        final AirCompanyConfigurationController controller = new AirCompanyConfigurationController(authz, repository);

        final AirTransportCompany result = controller.registerAirTransportCompany(
                "Valid Test Airline",
                "Portugal",
                "ZD",
                "ZDD"
        );

        assertArrayEquals(AisafePermissions.AIR_COMPANY_CONFIGURATION, authz.requestedRoles());
        assertEquals(IATACode.valueOf("ZD"), result.identity());
        assertEquals("Valid Test Airline", result.name());
        assertEquals("Portugal", result.country().toString());
        assertEquals("ZD", result.iataCode().toString());
        assertEquals("ZDD", result.icaoCode().toString());
        assertTrue(repository.findByIataCode(result.identity()).isPresent());
    }

    @Test
    void ensureDuplicateIataCodeRegistrationIsRejected() {
        final InMemoryAirTransportCompanyRepository repository = new InMemoryAirTransportCompanyRepository();
        final AirCompanyConfigurationController controller = new AirCompanyConfigurationController(
                new CapturingAuthorizationService(), repository);

        controller.registerAirTransportCompany(
                "First Test Airline",
                "Portugal",
                "ZA",
                "ZAA"
        );

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                controller.registerAirTransportCompany(
                        "Second Test Airline",
                        "Portugal",
                        "ZA",
                        "ZAB"
                ));

        assertEquals("Air transport company IATA code already exists.", result.getMessage());
    }

    @Test
    void ensureDuplicateIcaoCodeRegistrationIsRejected() {
        final InMemoryAirTransportCompanyRepository repository = new InMemoryAirTransportCompanyRepository();
        final AirCompanyConfigurationController controller = new AirCompanyConfigurationController(
                new CapturingAuthorizationService(), repository);

        controller.registerAirTransportCompany(
                "Third Test Airline",
                "Portugal",
                "ZB",
                "ZBB"
        );

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                controller.registerAirTransportCompany(
                        "Fourth Test Airline",
                        "Portugal",
                        "ZC",
                        "ZBB"
                ));

        assertEquals("Air transport company ICAO code already exists.", result.getMessage());
    }

    @Test
    void ensureInvalidAirTransportCompanyDataIsRejected() {
        final AirCompanyConfigurationController controller = new AirCompanyConfigurationController(
                new CapturingAuthorizationService(), new InMemoryAirTransportCompanyRepository());

        assertThrows(IllegalArgumentException.class, () -> controller.registerAirTransportCompany(
                "Invalid Company",
                "Portugal",
                "T",
                "TAP"
        ));
    }

    @Test
    void ensureAuthorizationErrorsArePropagated() {
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final IllegalStateException expected = new IllegalStateException("not authorized");
        authz.throwOnAuthorization(expected);

        final AirCompanyConfigurationController controller = new AirCompanyConfigurationController(
                authz, new InMemoryAirTransportCompanyRepository());

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                controller.registerAirTransportCompany(
                        "Authorization Test Airline",
                        "Portugal",
                        "ZE",
                        "ZEE"
                ));

        assertEquals(expected, result);
    }

    @Test
    void ensureExecuteChecksAuthorization() {
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final AirCompanyConfigurationController controller = new AirCompanyConfigurationController(
                authz, new InMemoryAirTransportCompanyRepository());

        controller.execute();

        assertArrayEquals(AisafePermissions.AIR_COMPANY_CONFIGURATION, authz.requestedRoles());
    }
}