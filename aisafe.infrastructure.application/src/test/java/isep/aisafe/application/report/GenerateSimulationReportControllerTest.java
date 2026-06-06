package isep.aisafe.application.report;

import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.report.domain.SimulationFlightResult;
import isep.aisafe.report.domain.SimulationReport;
import isep.aisafe.report.domain.SimulationSafetyViolation;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenerateSimulationReportControllerTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void ensureExecuteRequiresFlightControlPermission() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final GenerateSimulationReportController controller = controller(authz);

        // Act
        controller.execute();

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_CONTROL, authz.requestedRoles());
    }

    @Test
    void ensureSimulationReportIsGeneratedAndRendered() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final GenerateSimulationReportController controller = controller(authz);
        final List<SimulationFlightResult> flightResults = List.of(
                new SimulationFlightResult("TP101", "COMPLETED"),
                new SimulationFlightResult("TP102", "FAILED")
        );
        final List<SimulationSafetyViolation> safetyViolations = List.of(new SimulationSafetyViolation(
                LocalDateTime.of(2027, 1, 10, 10, 45),
                "40.0,-8.0,FL300",
                "Minimum separation breach"
        ));

        // Act
        final SimulationReport report = controller.generateSimulationReport(
                "SIM-001",
                false,
                flightResults,
                safetyViolations
        );
        final String renderedReport = controller.generateSimulationReportText(
                "SIM-001",
                false,
                flightResults,
                safetyViolations
        );

        // Assert
        assertArrayEquals(AisafePermissions.FLIGHT_CONTROL, authz.requestedRoles());
        assertEquals(LocalDateTime.of(2027, 1, 10, 12, 0), report.generatedAt());
        assertEquals(2, report.totalFlights());
        assertFalse(report.passedValidation());
        assertTrue(renderedReport.contains("AISafe Simulation Report"), renderedReport);
        assertTrue(renderedReport.contains("Simulation: SIM-001"), renderedReport);
        assertTrue(renderedReport.contains("Validation result: FAIL"), renderedReport);
        assertTrue(renderedReport.contains("Total flights: 2"), renderedReport);
        assertTrue(renderedReport.contains("TP101 - COMPLETED"), renderedReport);
        assertTrue(renderedReport.contains("2027-01-10T10:45 | 40.0,-8.0,FL300"), renderedReport);
    }

    @Test
    void ensureSimulationReportIsStoredInFile() throws Exception {
        // Arrange
        final GenerateSimulationReportController controller = controller(new CapturingAuthorizationService());
        final List<SimulationFlightResult> flightResults = List.of(new SimulationFlightResult("TP101", "COMPLETED"));

        // Act
        final Path reportFile = controller.generateSimulationReportFile(
                "SIM/001",
                true,
                flightResults,
                List.of(),
                temporaryDirectory
        );

        // Assert
        assertTrue(Files.exists(reportFile));
        assertEquals("simulation-report-SIM_001-20270110120000.txt", reportFile.getFileName().toString());
        final String content = Files.readString(reportFile);
        assertTrue(content.contains("Validation result: PASS"), content);
        assertTrue(content.contains("No safety violations detected."), content);
    }

    @Test
    void ensureReportWithSafetyViolationsCannotPassValidation() {
        // Arrange
        final GenerateSimulationReportController controller = controller(new CapturingAuthorizationService());
        final List<SimulationSafetyViolation> safetyViolations = List.of(new SimulationSafetyViolation(
                LocalDateTime.of(2027, 1, 10, 10, 45),
                "40.0,-8.0,FL300",
                "Minimum separation breach"
        ));

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> controller.generateSimulationReport(
                "SIM-002",
                true,
                List.of(new SimulationFlightResult("TP101", "FAILED")),
                safetyViolations
        ));
    }

    @Test
    void ensureAuthorizationFailureStopsReportGeneration() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final RuntimeException failure = new RuntimeException("not authorized");
        authz.throwOnAuthorization(failure);
        final GenerateSimulationReportController controller = controller(authz);

        // Act / Assert
        assertSame(failure, assertThrows(
                RuntimeException.class,
                () -> controller.generateSimulationReport("SIM-003", true, List.of(), List.of())
        ));
    }

    private GenerateSimulationReportController controller(final CapturingAuthorizationService authz) {
        return new GenerateSimulationReportController(
                authz,
                new SimulationReportGenerator(),
                new SimulationReportRenderer(),
                new SimulationReportFileWriter(),
                Clock.fixed(Instant.parse("2027-01-10T12:00:00Z"), ZoneOffset.UTC)
        );
    }
}
