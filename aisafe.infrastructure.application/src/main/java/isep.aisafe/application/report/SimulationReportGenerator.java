package isep.aisafe.application.report;

import isep.aisafe.report.domain.SimulationFlightResult;
import isep.aisafe.report.domain.SimulationReport;
import isep.aisafe.report.domain.SimulationSafetyViolation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builds simulation reports from simulation execution results.
 */
public class SimulationReportGenerator {

    /**
     * Generates a simulation report.
     *
     * @param simulationIdentifier simulation identifier
     * @param passedValidation final validation result
     * @param flightResults individual flight execution results
     * @param safetyViolations detected safety violations
     * @param generatedAt generation timestamp
     * @return generated simulation report
     */
    public SimulationReport generate(
            final String simulationIdentifier,
            final boolean passedValidation,
            final Iterable<SimulationFlightResult> flightResults,
            final Iterable<SimulationSafetyViolation> safetyViolations,
            final LocalDateTime generatedAt) {

        Objects.requireNonNull(flightResults, "flightResults cannot be null");
        Objects.requireNonNull(safetyViolations, "safetyViolations cannot be null");

        final List<SimulationFlightResult> collectedFlightResults = new ArrayList<>();
        flightResults.forEach(collectedFlightResults::add);

        final List<SimulationSafetyViolation> collectedSafetyViolations = new ArrayList<>();
        safetyViolations.forEach(collectedSafetyViolations::add);

        return new SimulationReport(
                simulationIdentifier,
                generatedAt,
                passedValidation,
                collectedFlightResults,
                collectedSafetyViolations
        );
    }
}
