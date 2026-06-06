package isep.aisafe.flightplan.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Identifies a flight plan in the flight plan domain.
 */
@Embeddable
public class FlightPlanIdentifier implements ValueObject, Comparable<FlightPlanIdentifier>, Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true)
    private String value;

    protected FlightPlanIdentifier() {
    }

    private FlightPlanIdentifier(final String value) {
        Preconditions.ensure(value != null && !value.isBlank(), "Flight plan identifier is required.");
        this.value = value.trim().toUpperCase();
    }

    /**
     * Creates a flight plan identifier from display text.
     *
     * @param value flight plan identifier text
     * @return normalized flight plan identifier
     */
    public static FlightPlanIdentifier valueOf(final String value) {
        return new FlightPlanIdentifier(value);
    }

    @Override
    public int compareTo(final FlightPlanIdentifier other) {
        return value.compareTo(other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FlightPlanIdentifier)) {
            return false;
        }
        final FlightPlanIdentifier that = (FlightPlanIdentifier) other;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
