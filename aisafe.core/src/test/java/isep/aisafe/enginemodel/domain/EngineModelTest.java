package isep.aisafe.enginemodel.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngineModelTest {

    private static final EngineModelDesignator DESIGNATOR =
            EngineModelDesignator.valueOf("LEAP-1A", "CFM International");
    private static final EngineType ENGINE_TYPE = EngineType.valueOf("TURBOFAN");
    private static final FuelType FUEL_TYPE = FuelType.valueOf("JET A-1");

    private EngineModel buildEngineModel() {
        return new EngineModel(
                DESIGNATOR,
                ENGINE_TYPE,
                FUEL_TYPE,
                120.0,
                140.0,
                0.60
        );
    }

    @Test
    void ensureValidEngineModelIsAccepted() {
        // Arrange / Act
        final EngineModel engineModel = buildEngineModel();

        // Assert
        assertNotNull(engineModel);
    }

    @Test
    void ensureEngineModelHasExpectedIdentity() {
        // Arrange
        final EngineModel engineModel = buildEngineModel();

        // Act / Assert
        assertEquals(DESIGNATOR, engineModel.identity());
        assertEquals(DESIGNATOR, engineModel.designator());
    }

    @Test
    void ensureDesignatorIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(null, ENGINE_TYPE, FUEL_TYPE, 120.0, 140.0, 0.60));
    }

    @Test
    void ensureEngineTypeIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(DESIGNATOR, null, FUEL_TYPE, 120.0, 140.0, 0.60));
    }

    @Test
    void ensureFuelTypeIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(DESIGNATOR, ENGINE_TYPE, null, 120.0, 140.0, 0.60));
    }

    @Test
    void ensurePowerMustBePositive() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(DESIGNATOR, ENGINE_TYPE, FUEL_TYPE, 0, 140.0, 0.60));
    }

    @Test
    void ensureThrustMustBePositive() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(DESIGNATOR, ENGINE_TYPE, FUEL_TYPE, 120.0, 0, 0.60));
    }

    @Test
    void ensureTsfcMustBePositive() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(DESIGNATOR, ENGINE_TYPE, FUEL_TYPE, 120.0, 140.0, 0));
    }

    @Test
    void ensureSameAsUsesIdentity() {
        // Arrange
        final EngineModel first = buildEngineModel();
        final EngineModel second = buildEngineModel();

        // Act / Assert
        assertTrue(first.sameAs(second));
    }
}
