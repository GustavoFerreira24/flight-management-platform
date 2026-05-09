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

    @Test
    void ensureControllerCanBeCreatedWithRepositoryOnly() {
        final RegisterAirControlAreaController controller =
                new RegisterAirControlAreaController(new InMemoryAirControlAreaRepository());

        assertNotNull(controller);
    }

    @Test
    void ensureNullRepositoryIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new RegisterAirControlAreaController(
                        new CapturingAuthorizationService(),
                        null
                ));
    }

    @Test
    void ensureNullAuthorizationServiceIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new RegisterAirControlAreaController(
                        null,
                        new InMemoryAirControlAreaRepository()
                ));
    }

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