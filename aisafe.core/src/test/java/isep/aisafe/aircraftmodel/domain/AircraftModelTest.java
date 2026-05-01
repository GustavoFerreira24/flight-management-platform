package isep.aisafe.aircraftmodel.domain;

import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AircraftModelTest {

    private static final AircraftModelDesignator DESIGNATOR =
            AircraftModelDesignator.valueOf("A320neo", "Airbus");
    private static final AircraftModelType TYPE = AircraftModelType.valueOf("PASSENGER");
    private static final AircraftModelMotorization MOTORIZATION =
            AircraftModelMotorization.valueOf("TURBOFAN");

    private EngineModel engineModel(final String modelName, final String engineType) {
        return new EngineModel(
                EngineModelDesignator.valueOf(modelName, "CFM International"),
                EngineType.valueOf(engineType),
                FuelType.valueOf("JET A-1"),
                120.0,
                140.0,
                0.60
        );
    }

    private AircraftModel buildAircraftModel() {
        return new AircraftModel(
                DESIGNATOR,
                TYPE,
                MOTORIZATION,
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
                186,
                List.of(engineModel("LEAP-1A", "TURBOFAN"))
        );
    }

    @Test
    void ensureValidAircraftModelIsAccepted() {
        // Arrange / Act
        final AircraftModel aircraftModel = buildAircraftModel();

        // Assert
        assertEquals(DESIGNATOR, aircraftModel.identity());
        assertEquals(186, aircraftModel.passengerCapacity());
    }

    @Test
    void ensureDesignatorIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AircraftModel(
                null, TYPE, MOTORIZATION, 6300, 44500, 79000, 64500, 27200,
                12000, 833, 122.6, 0.024, 0.62, 186,
                List.of(engineModel("LEAP-1A", "TURBOFAN"))));
    }

    @Test
    void ensureTypeIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AircraftModel(
                DESIGNATOR, null, MOTORIZATION, 6300, 44500, 79000, 64500, 27200,
                12000, 833, 122.6, 0.024, 0.62, 186,
                List.of(engineModel("LEAP-1A", "TURBOFAN"))));
    }

    @Test
    void ensureMotorizationIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AircraftModel(
                DESIGNATOR, TYPE, null, 6300, 44500, 79000, 64500, 27200,
                12000, 833, 122.6, 0.024, 0.62, 186,
                List.of(engineModel("LEAP-1A", "TURBOFAN"))));
    }

    @Test
    void ensureTechnicalValuesMustBePositive() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AircraftModel(
                DESIGNATOR, TYPE, MOTORIZATION, 0, 44500, 79000, 64500, 27200,
                12000, 833, 122.6, 0.024, 0.62, 186,
                List.of(engineModel("LEAP-1A", "TURBOFAN"))));
    }

    @Test
    void ensurePassengerCapacityMustBePositive() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AircraftModel(
                DESIGNATOR, TYPE, MOTORIZATION, 6300, 44500, 79000, 64500, 27200,
                12000, 833, 122.6, 0.024, 0.62, 0,
                List.of(engineModel("LEAP-1A", "TURBOFAN"))));
    }

    @Test
    void ensureAtLeastOneCertifiedEngineModelIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AircraftModel(
                DESIGNATOR, TYPE, MOTORIZATION, 6300, 44500, 79000, 64500, 27200,
                12000, 833, 122.6, 0.024, 0.62, 186, List.of()));
    }

    @Test
    void ensureNullCertifiedEngineModelListIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AircraftModel(
                DESIGNATOR, TYPE, MOTORIZATION, 6300, 44500, 79000, 64500, 27200,
                12000, 833, 122.6, 0.024, 0.62, 186, null));
    }

    @Test
    void ensureIncompatibleEngineModelIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new AircraftModel(
                DESIGNATOR, TYPE, MOTORIZATION, 6300, 44500, 79000, 64500, 27200,
                12000, 833, 122.6, 0.024, 0.62, 186,
                List.of(engineModel("TJ-1", "TURBOJET"))));
    }

    @Test
    void ensureDuplicateEngineModelIsRejected() {
        // Arrange
        final AircraftModel aircraftModel = buildAircraftModel();
        final EngineModel duplicateEngineModel = engineModel("LEAP-1A", "TURBOFAN");

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> aircraftModel.addCertifiedVariant(duplicateEngineModel));
    }

    @Test
    void ensureCompatibleDifferentEngineModelCanBeAdded() {
        // Arrange
        final AircraftModel aircraftModel = buildAircraftModel();
        final EngineModel anotherEngineModel = engineModel("LEAP-1B", "TURBOFAN");

        // Act / Assert
        assertDoesNotThrow(() -> aircraftModel.addCertifiedVariant(anotherEngineModel));
    }

    @Test
    void ensureSameAsUsesIdentity() {
        // Arrange
        final AircraftModel first = buildAircraftModel();
        final AircraftModel second = buildAircraftModel();

        // Act / Assert
        assertTrue(first.sameAs(second));
    }
}
