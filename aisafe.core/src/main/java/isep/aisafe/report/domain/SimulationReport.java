package isep.aisafe.report.domain;

import eapli.framework.validations.Preconditions;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Simulation result report generated after a simulated flight-control run.
 */
public final class SimulationReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String simulationIdentifier;
    private final LocalDateTime generatedAt;
    private final boolean passedValidation;
    private final List<SimulationFlightResult> flightResults;
    private final List<SimulationSafetyViolation> safetyViolations;

    /**
     * Creates a simulation report.
     *
     * @param simulationIdentifier simulation identifier
     * @param generatedAt generation timestamp
     * @param passedValidation final validation result
     * @param flightResults individual flight execution statuses
     * @param safetyViolations detected safety violations
     */
    public SimulationReport(
            final String simulationIdentifier,
            final LocalDateTime generatedAt,
            final boolean passedValidation,
            final List<SimulationFlightResult> flightResults,
            final List<SimulationSafetyViolation> safetyViolations) {

        Preconditions.ensure(
                simulationIdentifier != null && !simulationIdentifier.isBlank(),
                "Simulation identifier is required."
        );
        Preconditions.nonNull(generatedAt, "Report generation timestamp is required.");
        Preconditions.nonNull(flightResults, "Simulation flight results are required.");
        Preconditions.nonNull(safetyViolations, "Simulation safety violations are required.");
        Preconditions.ensure(
                !passedValidation || safetyViolations.isEmpty(),
                "A simulation with safety violations cannot pass validation."
        );

        this.simulationIdentifier = simulationIdentifier.trim();
        this.generatedAt = generatedAt;
        this.passedValidation = passedValidation;
        this.flightResults = List.copyOf(flightResults);
        this.safetyViolations = List.copyOf(safetyViolations);
    }

    public String simulationIdentifier() {
        return simulationIdentifier;
    }

    public LocalDateTime generatedAt() {
        return generatedAt;
    }

    public boolean passedValidation() {
        return passedValidation;
    }

    public int totalFlights() {
        return flightResults.size();
    }

    public List<SimulationFlightResult> flightResults() {
        return flightResults;
    }

    public List<SimulationSafetyViolation> safetyViolations() {
        return safetyViolations;
    }
}
