package isep.aisafe.application.aircompany;

import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.InMemoryAirTransportCompanyRepository;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirCompanyConfigurationControllerTest {

    /**
     * Verifica que o controlador pode ser criado apenas com o repositorio.
     */
    @Test
    void ensureControllerCanBeCreatedWithRepositoryOnly() {
        final AirCompanyConfigurationController controller =
                new AirCompanyConfigurationController(new InMemoryAirTransportCompanyRepository());

        assertNotNull(controller);
    }

    /**
     * Verifica que a criacao do controlador rejeita um repositorio nulo.
     */
    @Test
    void ensureNullRepositoryIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new AirCompanyConfigurationController(new CapturingAuthorizationService(), null));
    }

    /**
     * Verifica que a criacao do controlador rejeita um servico de autorizacao nulo.
     */
    @Test
    void ensureNullAuthorizationServiceIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new AirCompanyConfigurationController(null, new InMemoryAirTransportCompanyRepository()));
    }

    /**
     * Verifica que o registo de uma companhia aerea valida e aceite e persistido.
     */
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

        assertArrayEquals(AisafePermissions.BACKOFFICE_CONFIGURATION, authz.requestedRoles());
        assertEquals(IATACode.valueOf("ZD"), result.identity());
        assertEquals("Valid Test Airline", result.name());
        assertEquals("Portugal", result.country().toString());
        assertEquals("ZD", result.iataCode().toString());
        assertEquals("ZDD", result.icaoCode().toString());
        assertTrue(repository.findByIataCode(result.identity()).isPresent());
    }

    /**
     * Verifica que o registo rejeita um codigo IATA ja existente.
     */
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

    /**
     * Verifica que o registo rejeita um codigo ICAO ja existente.
     */
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

    /**
     * Verifica que o registo rejeita dados invalidos da companhia aerea.
     */
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

    /**
     * Verifica que erros de autorizacao sao propagados pelo controlador.
     */
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

    /**
     * Verifica que a execucao do controlador valida a autorizacao necessaria.
     */
    @Test
    void ensureExecuteChecksAuthorization() {
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final AirCompanyConfigurationController controller = new AirCompanyConfigurationController(
                authz, new InMemoryAirTransportCompanyRepository());

        controller.execute();

        assertArrayEquals(AisafePermissions.BACKOFFICE_CONFIGURATION, authz.requestedRoles());
    }
}
