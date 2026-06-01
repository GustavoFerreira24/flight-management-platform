package isep.aisafe.flightplan.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Identifies the pilot that owns a flight plan.
 */
@Embeddable
public class PilotIdentifier implements ValueObject, Comparable<PilotIdentifier>, Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String value;

    protected PilotIdentifier() {
    }

    private PilotIdentifier(final String value) {
        Preconditions.ensure(value != null && !value.isBlank(), "Pilot identifier is required.");
        this.value = value.trim();
    }

    public static PilotIdentifier valueOf(final String value) {
        return new PilotIdentifier(value);
    }

    @Override
    public int compareTo(final PilotIdentifier other) {
        return value.compareTo(other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PilotIdentifier)) {
            return false;
        }
        final PilotIdentifier that = (PilotIdentifier) other;
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
