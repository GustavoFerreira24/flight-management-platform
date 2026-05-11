package isep.aisafe.aircraft.domain;

import eapli.framework.domain.model.ValueObject;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents the operational status of an aircraft.
 */
@Embeddable
public class AircraftOperationalStatus implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    private static final String ACTIVE = "ACTIVE";
    private static final String IN_MAINTENANCE = "IN_MAINTENANCE";
    private static final String DECOMMISSIONED = "DECOMMISSIONED";

    private String value;

    /**
     * Required by ORM.
     */
    protected AircraftOperationalStatus() {
    }

    private AircraftOperationalStatus(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Aircraft operational status cannot be empty.");
        }

        final String normalizedValue = value.trim().toUpperCase();

        if (!normalizedValue.equals(ACTIVE)
                && !normalizedValue.equals(IN_MAINTENANCE)
                && !normalizedValue.equals(DECOMMISSIONED)) {
            throw new IllegalArgumentException("Invalid aircraft operational status.");
        }

        this.value = normalizedValue;
    }

    /**
     * Creates an aircraft operational status from text.
     *
     * @param value the status text
     * @return a new aircraft operational status
     */
    public static AircraftOperationalStatus valueOf(final String value) {
        return new AircraftOperationalStatus(value);
    }

    /**
     * Creates the default operational status for a newly registered aircraft.
     *
     * @return active operational status
     */
    public static AircraftOperationalStatus active() {
        return new AircraftOperationalStatus(ACTIVE);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AircraftOperationalStatus)) {
            return false;
        }

        final AircraftOperationalStatus that = (AircraftOperationalStatus) other;
        return Objects.equals(this.value, that.value);
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