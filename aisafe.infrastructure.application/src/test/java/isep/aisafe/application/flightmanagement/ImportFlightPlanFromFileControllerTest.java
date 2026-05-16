package isep.aisafe.application.flightmanagement;

import isep.aisafe.flightplan.importer.ImportedFlightPlan;
import isep.aisafe.flightplan.importer.InMemoryImportedFlightPlanRepository;
import isep.aisafe.flightplan.importer.dsl.FlightPlanDslErrorType;
import isep.aisafe.flightplan.importer.dsl.FlightPlanDslSyntaxValidator;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportFlightPlanFromFileControllerTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-05-16T10:15:30Z"), ZoneOffset.UTC);

    @TempDir
    private Path temporaryDirectory;

    @Test
    void ensureDefaultConstructorIsAvailable() {
        assertNotNull(new ImportFlightPlanFromFileController());
    }

    @Test
    void ensureValidFlightPlanFileIsImportedAndStored() throws IOException {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryImportedFlightPlanRepository repository = new InMemoryImportedFlightPlanRepository();
        final ImportFlightPlanFromFileController controller = controller(authz, repository);
        final Path file = writeFlightPlan("valid-flight-plan.fpl", """
                flight TP123 regular {
                  aircraft CS-TXA;
                  pilot pilot01;
                  leg {
                    departure OPO at 2026-05-10T10:00;
                    arrival LIS at 2026-05-10T11:00;
                    route OPO -> LIS;
                    segment (41.0,-8.0) -> (38.0,-9.0) altitude [1000,2000] m width 10 km wind 90 deg 12 m/s;
                    fuel 5000 kg;
                  }
                }
                """);

        // Act
        final ImportFlightPlanResult result = controller.importFromFile(file);

        // Assert
        assertTrue(result.isImported());
        assertTrue(result.errors().isEmpty());
        assertEquals(1, repository.count());
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_MANAGEMENT, authz.requestedRoles());

        final ImportedFlightPlan importedFlightPlan = result.importedFlightPlan().orElseThrow();
        assertEquals("TP123", importedFlightPlan.identifier());
        assertEquals("REGULAR", importedFlightPlan.type());
        assertEquals("CS-TXA", importedFlightPlan.aircraftRegistration().orElseThrow());
        assertEquals("pilot01", importedFlightPlan.pilotIdentifier().orElseThrow());
        assertEquals(1, importedFlightPlan.legCount());
        assertEquals(file.toAbsolutePath().normalize(), importedFlightPlan.sourceFile());
        assertEquals(LocalDateTime.of(2026, 5, 16, 10, 15, 30), importedFlightPlan.importedAt());
        assertTrue(repository.findByIdentifier("TP123").isPresent());
    }

    @Test
    void ensureInvalidFlightPlanFileIsRejectedAndNotStored() throws IOException {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryImportedFlightPlanRepository repository = new InMemoryImportedFlightPlanRepository();
        final ImportFlightPlanFromFileController controller = controller(authz, repository);
        final Path file = writeFlightPlan("invalid-flight-plan.fpl", """
                flight TP123 regular {
                  leg {
                    departure OPO at 2026-05-10T10:00;
                    arrival LIS at 2026-05-10T09:00;
                    route LIS -> OPO;
                    segment (41.0,-8.0) -> (41.0,-8.0);
                    fuel -1 kg;
                  }
                }
                """);

        // Act
        final ImportFlightPlanResult result = controller.importFromFile(file);

        // Assert
        assertFalse(result.isImported());
        assertFalse(result.errors().isEmpty());
        assertEquals(List.of(FlightPlanDslErrorType.SEMANTIC), result.errors().stream().map(error -> error.type()).distinct().toList());
        assertEquals(0, repository.count());
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_MANAGEMENT, authz.requestedRoles());
    }

    @Test
    void ensureMissingFileIsRejectedBeforeImport() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryImportedFlightPlanRepository repository = new InMemoryImportedFlightPlanRepository();
        final ImportFlightPlanFromFileController controller = controller(authz, repository);
        final Path missingFile = temporaryDirectory.resolve("missing.fpl");

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> controller.importFromFile(missingFile));
        assertEquals(0, repository.count());
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_MANAGEMENT, authz.requestedRoles());
    }

    @Test
    void ensureImportedFlightPlansRequiresAuthorization() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryImportedFlightPlanRepository repository = new InMemoryImportedFlightPlanRepository();
        final ImportFlightPlanFromFileController controller = controller(authz, repository);

        // Act
        controller.importedFlightPlans();

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_PLAN_MANAGEMENT, authz.requestedRoles());
    }

    private ImportFlightPlanFromFileController controller(
            final CapturingAuthorizationService authz,
            final InMemoryImportedFlightPlanRepository repository) {

        return new ImportFlightPlanFromFileController(
                authz,
                new FlightPlanDslSyntaxValidator(),
                repository,
                FIXED_CLOCK
        );
    }

    private Path writeFlightPlan(final String fileName, final String source) throws IOException {
        final Path file = temporaryDirectory.resolve(fileName);
        Files.writeString(file, source);
        return file;
    }
}
