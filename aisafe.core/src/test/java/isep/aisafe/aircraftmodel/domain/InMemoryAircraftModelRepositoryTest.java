package isep.aisafe.aircraftmodel.domain;

import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAircraftModelRepositoryTest {

    private AircraftModel aircraftModel() {
        final EngineModel engineModel = new EngineModel(
                EngineModelDesignator.valueOf("LEAP-1A", "CFM International"),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                120.0,
                140.0,
                0.60
        );

        return new AircraftModel(
                AircraftModelDesignator.valueOf("A320neo", "Airbus"),
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
                186,
                List.of(engineModel)
        );
    }

    @Test
    void ensureSavedAircraftModelCanBeFoundByDesignator() {
        // Arrange
        final InMemoryAircraftModelRepository repository = new InMemoryAircraftModelRepository();
        final AircraftModel aircraftModel = aircraftModel();

        // Act
        repository.save(aircraftModel);
        final Optional<AircraftModel> result = repository.findByDesignator(aircraftModel.designator());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(aircraftModel, result.get());
    }

    @Test
    void ensureUnknownAircraftModelDesignatorReturnsEmpty() {
        // Arrange
        final InMemoryAircraftModelRepository repository = new InMemoryAircraftModelRepository();

        // Act
        final Optional<AircraftModel> result = repository.findByDesignator(
                AircraftModelDesignator.valueOf("B737", "Boeing"));

        // Assert
        assertTrue(result.isEmpty());
    }
}
