package isep.aisafe.application.aircontrolarea;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.InMemoryAirControlAreaRepository;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterAirControlAreaControllerTest {

    /**
     * Verifica que o controlador pode ser criado apenas com o repositorio.
     */
    @Test
    void ensureControllerCanBeCreatedWithRepositoryOnly() {
        final RegisterAirControlAreaController controller =
                new RegisterAirControlAreaController(new InMemoryAirControlAreaRepository());

        assertNotNull(controller);
    }

    /**
     * Verifica que o controlador rejeita um repositorio nulo.
     */
    @Test
    void ensureNullRepositoryIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new RegisterAirControlAreaController(
                        new CapturingAuthorizationService(),
                        null
                ));
    }

    /**
     * Verifica que o controlador rejeita um servico de autorizacao nulo.
     */
    @Test
    void ensureNullAuthorizationServiceIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new RegisterAirControlAreaController(
                        null,
                        new InMemoryAirControlAreaRepository()
                ));
    }

    /**
     * Verifica que o registo de uma area de controlo aereo valida e aceite e persistido.
     */
    @Test
    void ensureValidAirControlAreaRegistrationIsAccepted() {
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryAirControlAreaRepository repository = new InMemoryAirControlAreaRepository();

        final RegisterAirControlAreaController controller =
                new RegisterAirControlAreaController(authz, repository);

        final AirControlArea result = controller.registerAirControlArea(
                "VALIDFIR",
                "Valid FIR",
                30.0,
                45.0,
                -15.0,
                5.0,
                5000.0,
                "KG"
        );

        assertArrayEquals(AisafePermissions.BACKOFFICE_CONFIGURATION, authz.requestedRoles());
        assertEquals(AirControlAreaCode.valueOf("VALIDFIR"), result.identity());
        assertEquals("Valid FIR", result.name());
        assertEquals(GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0), result.geographicBoundary());
        assertEquals(
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG")),
                result.minimumFuelRequirement()
        );
        assertTrue(repository.findByCode(result.identity()).isPresent());
    }

    /**
     * Verifica que o registo de um codigo de area duplicado e rejeitado.
     */
    @Test
    void ensureDuplicateAirControlAreaCodeRegistrationIsRejected() {
        final InMemoryAirControlAreaRepository repository = new InMemoryAirControlAreaRepository();

        final RegisterAirControlAreaController controller =
                new RegisterAirControlAreaController(
                        new CapturingAuthorizationService(),
                        repository
                );

        controller.registerAirControlArea(
                "DUPFIR50",
                "First Duplicate FIR",
                30.0,
                45.0,
                -15.0,
                5.0,
                5000.0,
                "KG"
        );

        final IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                controller.registerAirControlArea(
                        "DUPFIR50",
                        "Second Duplicate FIR",
                        10.0,
                        20.0,
                        30.0,
                        40.0,
                        6000.0,
                        "L"
                ));

        assertEquals("Air control area code already exists.", result.getMessage());
    }

    /**
     * Verifica que o registo com codigo de area invalido e rejeitado.
     */
    @Test
    void ensureInvalidAirControlAreaCodeIsRejected() {
        final RegisterAirControlAreaController controller =
                new RegisterAirControlAreaController(
                        new CapturingAuthorizationService(),
                        new InMemoryAirControlAreaRepository()
                );

        assertThrows(IllegalArgumentException.class, () ->
                controller.registerAirControlArea(
                        "A",
                        "Invalid FIR",
                        30.0,
                        45.0,
                        -15.0,
                        5.0,
                        5000.0,
                        "KG"
                ));
    }

    /**
     * Verifica que o registo com limite geografico invalido e rejeitado.
     */
    @Test
    void ensureInvalidGeographicBoundaryIsRejected() {
        final RegisterAirControlAreaController controller =
                new RegisterAirControlAreaController(
                        new CapturingAuthorizationService(),
                        new InMemoryAirControlAreaRepository()
                );

        assertThrows(IllegalArgumentException.class, () ->
                controller.registerAirControlArea(
                        "BOUNDFIR",
                        "Boundary FIR",
                        45.0,
                        30.0,
                        -15.0,
                        5.0,
                        5000.0,
                        "KG"
                ));
    }

    /**
     * Verifica que o registo com quantidade minima de combustivel invalida e rejeitado.
     */
    @Test
    void ensureInvalidMinimumFuelAmountIsRejected() {
        final RegisterAirControlAreaController controller =
                new RegisterAirControlAreaController(
                        new CapturingAuthorizationService(),
                        new InMemoryAirControlAreaRepository()
                );

        assertThrows(IllegalArgumentException.class, () ->
                controller.registerAirControlArea(
                        "FUELFIR",
                        "Fuel FIR",
                        30.0,
                        45.0,
                        -15.0,
                        5.0,
                        0.0,
                        "KG"
                ));
    }

    /**
     * Verifica que o registo com unidade de combustivel invalida e rejeitado.
     */
    @Test
    void ensureInvalidFuelUnitIsRejected() {
        final RegisterAirControlAreaController controller =
                new RegisterAirControlAreaController(
                        new CapturingAuthorizationService(),
                        new InMemoryAirControlAreaRepository()
                );

        assertThrows(IllegalArgumentException.class, () ->
                controller.registerAirControlArea(
                        "UNITFIR",
                        "Unit FIR",
                        30.0,
                        45.0,
                        -15.0,
                        5.0,
                        5000.0,
                        "GAL"
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

        final RegisterAirControlAreaController controller =
                new RegisterAirControlAreaController(
                        authz,
                        new InMemoryAirControlAreaRepository()
                );

        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                controller.registerAirControlArea(
                        "AUTHFIR",
                        "Authorization FIR",
                        30.0,
                        45.0,
                        -15.0,
                        5.0,
                        5000.0,
                        "KG"
                ));

        assertEquals(expected, result);
    }
}