package isep.aisafe.enginemodel.domain;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryEngineModelRepositoryTest {

    private EngineModel engineModel() {
        return new EngineModel(
                EngineModelDesignator.valueOf("LEAP-1A", "CFM International"),
                EngineType.valueOf("TURBOFAN"),
                FuelType.valueOf("JET A-1"),
                120.0,
                140.0,
                0.60
        );
    }

    @Test
    void ensureSavedEngineModelCanBeFoundByDesignator() {
        // Arrange
        final InMemoryEngineModelRepository repository = new InMemoryEngineModelRepository();
        final EngineModel engineModel = engineModel();

        // Act
        repository.save(engineModel);
        final Optional<EngineModel> result = repository.findByDesignator(engineModel.designator());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(engineModel, result.get());
    }

    @Test
    void ensureUnknownEngineModelDesignatorReturnsEmpty() {
        // Arrange
        final InMemoryEngineModelRepository repository = new InMemoryEngineModelRepository();

        // Act
        final Optional<EngineModel> result = repository.findByDesignator(
                EngineModelDesignator.valueOf("PW1100G", "Pratt Whitney"));

        // Assert
        assertTrue(result.isEmpty());
    }
}
