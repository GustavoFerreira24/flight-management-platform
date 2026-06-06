package isep.aisafe.report.domain;

import eapli.framework.validations.Preconditions;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Safety violation detected during a simulation.
 */
public final class SimulationSafetyViolation implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LocalDateTime timestamp;
    private final String position;
    private final String description;

    /**
     * Creates a safety violation entry.
     *
     * @param timestamp violation timestamp
     * @param position aircraft position when the violation occurred
     * @param description violation description
     */
    public SimulationSafetyViolation(
            final LocalDateTime timestamp,
            final String position,
            final String description) {

        Preconditions.nonNull(timestamp, "Safety violation timestamp is required.");
        Preconditions.ensure(position != null && !position.isBlank(), "Safety violation position is required.");
        Preconditions.ensure(description != null && !description.isBlank(), "Safety violation description is required.");

        this.timestamp = timestamp;
        this.position = position.trim();
        this.description = description.trim();
    }

    public LocalDateTime timestamp() {
        return timestamp;
    }

    public String position() {
        return position;
    }

    public String description() {
        return description;
    }
}
