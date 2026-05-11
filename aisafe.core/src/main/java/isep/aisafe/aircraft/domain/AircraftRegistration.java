package isep.aisafe.aircraft.domain;

import eapli.framework.domain.model.ValueObject;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents an aircraft registration number.
 */
@Embeddable
public class AircraftRegistration implements ValueObject, Comparable<AircraftRegistration>, Serializable {

    private static final long serialVersionUID = 1L;

    private String value;

    /**
     * Required by ORM.
     */
    protected AircraftRegistration() {
    }

    private AircraftRegistration(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Aircraft registration cannot be empty.");
        }

        final String normalizedValue = value.trim().toUpperCase();

        if (!normalizedValue.matches("[A-Z0-9][A-Z0-9-]{1,9}")) {
            throw new IllegalArgumentException(
                    "Aircraft registration must have 2 to 10 letters, digits or hyphens."
            );
        }

        this.value = normalizedValue;
    }

    /**
     * Creates an aircraft registration from text.
     *
     * @param value the registration text
     * @return a new aircraft registration
     */
    public static AircraftRegistration valueOf(final String value) {
        return new AircraftRegistration(value);
    }

    @Override
    public int compareTo(final AircraftRegistration other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AircraftRegistration)) {
            return false;
        }

        final AircraftRegistration that = (AircraftRegistration) other;
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