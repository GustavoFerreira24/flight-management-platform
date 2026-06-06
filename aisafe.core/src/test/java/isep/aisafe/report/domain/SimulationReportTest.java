package isep.aisafe.report.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationReportTest {

    @Test
    void ensureSimulationReportCanBeCreated() {
        // Arrange
        final LocalDateTime generatedAt = LocalDateTime.of(2027, 1, 10, 10, 30);
        final List<SimulationFlightResult> flightResults = List.of(
                new SimulationFlightResult("TP101", "COMPLETED"),
                new SimulationFlightResult("TP102", "COMPLETED")
        );

        // Act
        final SimulationReport report = new SimulationReport(
                " SIM-001 ",
                generatedAt,
                true,
                flightResults,
                List.of()
        );

        // Assert
        assertEquals("SIM-001", report.simulationIdentifier());
        assertEquals(generatedAt, report.generatedAt());
        assertTrue(report.passedValidation());
        assertEquals(2, report.totalFlights());
        assertEquals(flightResults, report.flightResults());
        assertTrue(report.safetyViolations().isEmpty());
    }

    @Test
    void ensureSimulationReportKeepsDefensiveCopies() {
        // Arrange
        final List<SimulationFlightResult> flightResults = new ArrayList<>();
        final List<SimulationSafetyViolation> safetyViolations = new ArrayList<>();
        flightResults.add(new SimulationFlightResult("TP101", "FAILED"));
        safetyViolations.add(new SimulationSafetyViolation(
                LocalDateTime.of(2027, 1, 10, 10, 30),
                "40.0,-8.0,FL300",
                "Minimum separation breach"
        ));

        // Act
        final SimulationReport report = new SimulationReport(
                "SIM-002",
                LocalDateTime.of(2027, 1, 10, 11, 0),
                false,
                flightResults,
                safetyViolations
        );
        flightResults.add(new SimulationFlightResult("TP102", "COMPLETED"));
        safetyViolations.clear();

        // Assert
        assertEquals(1, report.flightResults().size());
        assertEquals(1, report.safetyViolations().size());
        assertThrows(UnsupportedOperationException.class, () -> report.flightResults().clear());
        assertThrows(UnsupportedOperationException.class, () -> report.safetyViolations().clear());
    }

    @Test
    void ensureRequiredSimulationReportDataIsValidated() {
        // Arrange
        final LocalDateTime generatedAt = LocalDateTime.of(2027, 1, 10, 10, 30);

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new SimulationReport(null, generatedAt, true, List.of(), List.of()));
        assertThrows(IllegalArgumentException.class, () -> new SimulationReport(" ", generatedAt, true, List.of(), List.of()));
        assertThrows(IllegalArgumentException.class, () -> new SimulationReport("SIM", null, true, List.of(), List.of()));
        assertThrows(IllegalArgumentException.class, () -> new SimulationReport("SIM", generatedAt, true, null, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new SimulationReport("SIM", generatedAt, true, List.of(), null));
    }

    @Test
    void ensureReportWithSafetyViolationsCannotPassValidation() {
        // Arrange
        final SimulationSafetyViolation violation = new SimulationSafetyViolation(
                LocalDateTime.of(2027, 1, 10, 10, 30),
                "40.0,-8.0,FL300",
                "Minimum separation breach"
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new SimulationReport(
                "SIM-003",
                LocalDateTime.of(2027, 1, 10, 11, 0),
                true,
                List.of(new SimulationFlightResult("TP101", "FAILED")),
                List.of(violation)
        ));
    }

    @Test
    void ensureFlightResultRequiredDataIsValidated() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new SimulationFlightResult(null, "COMPLETED"));
        assertThrows(IllegalArgumentException.class, () -> new SimulationFlightResult(" ", "COMPLETED"));
        assertThrows(IllegalArgumentException.class, () -> new SimulationFlightResult("TP101", null));
        assertThrows(IllegalArgumentException.class, () -> new SimulationFlightResult("TP101", " "));
    }

    @Test
    void ensureSafetyViolationRequiredDataIsValidated() {
        // Arrange
        final LocalDateTime timestamp = LocalDateTime.of(2027, 1, 10, 10, 30);

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new SimulationSafetyViolation(null, "40.0,-8.0", "Violation"));
        assertThrows(IllegalArgumentException.class, () -> new SimulationSafetyViolation(timestamp, null, "Violation"));
        assertThrows(IllegalArgumentException.class, () -> new SimulationSafetyViolation(timestamp, " ", "Violation"));
        assertThrows(IllegalArgumentException.class, () -> new SimulationSafetyViolation(timestamp, "40.0,-8.0", null));
        assertThrows(IllegalArgumentException.class, () -> new SimulationSafetyViolation(timestamp, "40.0,-8.0", " "));
    }
}
