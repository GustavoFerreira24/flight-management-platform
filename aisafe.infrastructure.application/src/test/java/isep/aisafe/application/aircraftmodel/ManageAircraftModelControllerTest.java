package isep.aisafe.application.aircraftmodel;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.InMemoryAircraftModelRepository;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import isep.aisafe.enginemodel.domain.InMemoryEngineModelRepository;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManageAircraftModelControllerTest {

    private static class AllowAllAuthorizationService extends AisafeAuthorizationService {
        @Override
        public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {
        }
    }

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

    private ManageAircraftModelController controller(
            final InMemoryAircraftModelRepository aircraftModelRepository,
            final InMemoryEngineModelRepository engineModelRepository) {
        return new ManageAircraftModelController(
                new AllowAllAuthorizationService(),
                aircraftModelRepository,
                engineModelRepository
        );
    }

    @Test
    void ensureValidAircraftModelRegistrationIsAccepted() {
        // Arrange
        final InMemoryAircraftModelRepository aircraftModelRepository = new InMemoryAircraftModelRepository();
        final InMemoryEngineModelRepository engineModelRepository = new InMemoryEngineModelRepository();
        final EngineModel engineModel = engineModel("LEAP-1A", "TURBOFAN");
        engineModelRepository.save(engineModel);
        final ManageAircraftModelController controller = controller(aircraftModelRepository, engineModelRepository);

        // Act
        final AircraftModel result = controller.registerAircraftModel(
                "A320neo Valid",
                "Airbus",
                "PASSENGER",
                "TURBOFAN",
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
                List.of(engineModel.designator().toString())
        );

        // Assert
        assertEquals(AircraftModelDesignator.valueOf("A320neo Valid", "Airbus"), result.identity());
        assertTrue(aircraftModelRepository.findByDesignator(result.identity()).isPresent());
    }

    @Test
    void ensureDuplicateAircraftModelRegistrationIsRejected() {
        // Arrange
        final InMemoryAircraftModelRepository aircraftModelRepository = new InMemoryAircraftModelRepository();
        final InMemoryEngineModelRepository engineModelRepository = new InMemoryEngineModelRepository();
        final EngineModel engineModel = engineModel("LEAP-1A", "TURBOFAN");
        engineModelRepository.save(engineModel);
        final ManageAircraftModelController controller = controller(aircraftModelRepository, engineModelRepository);

        controller.registerAircraftModel(
                "A320neo Duplicate", "Airbus", "PASSENGER", "TURBOFAN", 6300, 44500,
                79000, 64500, 27200, 12000, 833, 122.6, 0.024, 0.62, 186,
                List.of(engineModel.designator().toString())
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> controller.registerAircraftModel(
                "A320neo Duplicate", "Airbus", "PASSENGER", "TURBOFAN", 6300, 44500,
                79000, 64500, 27200, 12000, 833, 122.6, 0.024, 0.62, 186,
                List.of(engineModel.designator().toString())
        ));
    }

    @Test
    void ensureMissingCertifiedEngineModelIsRejected() {
        // Arrange
        final ManageAircraftModelController controller = controller(
                new InMemoryAircraftModelRepository(),
                new InMemoryEngineModelRepository()
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> controller.registerAircraftModel(
                "A320neo Missing Engine", "Airbus", "PASSENGER", "TURBOFAN", 6300, 44500,
                79000, 64500, 27200, 12000, 833, 122.6, 0.024, 0.62, 186,
                List.of("UNKNOWN-ENGINE | UNKNOWN MAKER")
        ));
    }

    @Test
    void ensureEmptyCertifiedEngineModelListIsRejected() {
        // Arrange
        final ManageAircraftModelController controller = controller(
                new InMemoryAircraftModelRepository(),
                new InMemoryEngineModelRepository()
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> controller.registerAircraftModel(
                "A320neo Empty Engine List", "Airbus", "PASSENGER", "TURBOFAN", 6300, 44500,
                79000, 64500, 27200, 12000, 833, 122.6, 0.024, 0.62, 186,
                List.of()
        ));
    }

    @Test
    void ensureIncompatibleEngineModelIsRejected() {
        // Arrange
        final InMemoryEngineModelRepository engineModelRepository = new InMemoryEngineModelRepository();
        final EngineModel engineModel = engineModel("TJ-1", "TURBOJET");
        engineModelRepository.save(engineModel);
        final ManageAircraftModelController controller = controller(
                new InMemoryAircraftModelRepository(),
                engineModelRepository
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> controller.registerAircraftModel(
                "A320neo Incompatible", "Airbus", "PASSENGER", "TURBOFAN", 6300, 44500,
                79000, 64500, 27200, 12000, 833, 122.6, 0.024, 0.62, 186,
                List.of(engineModel.designator().toString())
        ));
    }

    @Test
    void ensureCompatibleEngineModelsCanBeListedByMotorization() {
        // Arrange
        final InMemoryEngineModelRepository engineModelRepository = new InMemoryEngineModelRepository();
        final EngineModel compatibleEngineModel = engineModel("LEAP-1A", "TURBOFAN");
        final EngineModel incompatibleEngineModel = engineModel("TJ-1", "TURBOJET");
        engineModelRepository.save(compatibleEngineModel);
        engineModelRepository.save(incompatibleEngineModel);
        final ManageAircraftModelController controller = controller(
                new InMemoryAircraftModelRepository(),
                engineModelRepository
        );

        // Act
        final List<String> result = controller.compatibleEngineModelDesignators("TURBOFAN");

        // Assert
        assertTrue(result.contains(compatibleEngineModel.designator().toString()));
        assertFalse(result.contains(incompatibleEngineModel.designator().toString()));
    }
}
