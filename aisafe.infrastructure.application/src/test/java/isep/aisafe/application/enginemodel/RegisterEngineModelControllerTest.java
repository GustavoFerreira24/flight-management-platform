package isep.aisafe.application.enginemodel;

import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.InMemoryEngineModelRepository;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterEngineModelControllerTest {

    @Test
    void ensureValidEngineModelRegistrationIsAccepted() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryEngineModelRepository repository = new InMemoryEngineModelRepository();
        final RegisterEngineModelController controller = new RegisterEngineModelController(authz, repository);

        // Act
        final EngineModel result = controller.registerEngineModel(
                "PW1100G-JM",
                "Pratt Whitney",
                "TURBOFAN",
                "JET A-1",
                110.0,
                130.0,
                0.58
        );

        // Assert
        assertArrayEquals(AisafePermissions.BACKOFFICE_CONFIGURATION, authz.requestedRoles());
        assertEquals(EngineModelDesignator.valueOf("PW1100G-JM", "Pratt Whitney"), result.identity());
        assertEquals("TURBOFAN", result.engineType().toString());
        assertEquals("JET A-1", result.fuelType().toString());
        assertEquals(110.0, result.power());
        assertEquals(130.0, result.thrust());
        assertEquals(0.58, result.tsfc());
        assertTrue(repository.findByDesignator(result.identity()).isPresent());
    }

    @Test
    void ensureDuplicateEngineModelRegistrationIsRejected() {
        // Arrange
        final InMemoryEngineModelRepository repository = new InMemoryEngineModelRepository();
        final RegisterEngineModelController controller = new RegisterEngineModelController(
                new CapturingAuthorizationService(), repository);

        controller.registerEngineModel(
                "GE9X",
                "GE Aerospace",
                "TURBOFAN",
                "JET A-1",
                115.0,
                490.0,
                0.55
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> controller.registerEngineModel(
                "GE9X",
                "GE Aerospace",
                "TURBOFAN",
                "JET A-1",
                115.0,
                490.0,
                0.55
        ));
    }

    @Test
    void ensureInvalidEngineModelDataIsRejected() {
        // Arrange
        final RegisterEngineModelController controller = new RegisterEngineModelController(
                new CapturingAuthorizationService(), new InMemoryEngineModelRepository());

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> controller.registerEngineModel(
                "INVALID",
                "Maker",
                "TURBOFAN",
                "JET A-1",
                0,
                130.0,
                0.58
        ));
    }

    @Test
    void ensureAuthorizationErrorsArePropagated() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final IllegalStateException expected = new IllegalStateException("not authorized");
        authz.throwOnAuthorization(expected);
        final RegisterEngineModelController controller = new RegisterEngineModelController(
                authz, new InMemoryEngineModelRepository());

        // Act / Assert
        final IllegalStateException result = assertThrows(IllegalStateException.class, () ->
                controller.registerEngineModel(
                        "TRENT XWB",
                        "Rolls-Royce",
                        "TURBOFAN",
                        "JET A-1",
                        118.0,
                        430.0,
                        0.56
                ));
        assertEquals(expected, result);
    }
}
