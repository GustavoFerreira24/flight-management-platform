package isep.aisafe.report.domain;

import eapli.framework.validations.Preconditions;

import java.io.Serializable;

/**
 * Execution status of one flight in a simulation report.
 */
public final class SimulationFlightResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String flightIdentifier;
    private final String executionStatus;

    /**
     * Creates a simulation flight result.
     *
     * @param flightIdentifier simulated flight identifier
     * @param executionStatus final execution status
     */
    public SimulationFlightResult(final String flightIdentifier, final String executionStatus) {
        Preconditions.ensure(
                flightIdentifier != null && !flightIdentifier.isBlank(),
                "Flight identifier is required."
        );
        Preconditions.ensure(
                executionStatus != null && !executionStatus.isBlank(),
                "Execution status is required."
        );

        this.flightIdentifier = flightIdentifier.trim();
        this.executionStatus = executionStatus.trim();
    }

    public String flightIdentifier() {
        return flightIdentifier;
    }

    public String executionStatus() {
        return executionStatus;
    }
}
