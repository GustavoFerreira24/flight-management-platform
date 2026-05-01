package isep.aisafe.aircraftmodel.domain;

import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftVariantTest {

    private EngineModel engineModel(final String modelName) {
        return new EngineModel(
                EngineModelDesignator.valueOf(modelName, "CFM International"),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                120.0,
                140.0,
                0.60
        );
    }

    @Test
    void ensureVariantCanBeCreatedFromEngineModel() {
        // Arrange
        final EngineModel engineModel = engineModel("LEAP-1A");

        // Act
        final AircraftVariant variant = AircraftVariant.createFrom(engineModel);

        // Assert
        assertEquals(engineModel, variant.engineModel());
        assertNotNull(variant.variantId());
    }

    @Test
    void ensureEngineModelIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> AircraftVariant.createFrom(null));
    }

    @Test
    void ensureVariantRecognizesItsEngineModel() {
        // Arrange
        final EngineModel engineModel = engineModel("LEAP-1A");
        final AircraftVariant variant = AircraftVariant.createFrom(engineModel);

        // Act / Assert
        assertTrue(variant.hasEngineModel(engineModel.identity()));
    }

    @Test
    void ensureVariantRejectsDifferentEngineModel() {
        // Arrange
        final EngineModel engineModel = engineModel("LEAP-1A");
        final AircraftVariant variant = AircraftVariant.createFrom(engineModel);
        final EngineModelDesignator otherDesignator =
                EngineModelDesignator.valueOf("LEAP-1B", "CFM International");

        // Act / Assert
        assertFalse(variant.hasEngineModel(otherDesignator));
    }
}
