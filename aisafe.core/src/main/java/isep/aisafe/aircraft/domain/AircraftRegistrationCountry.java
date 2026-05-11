package isep.aisafe.aircraft.domain;

import eapli.framework.domain.model.ValueObject;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents the country where an aircraft is registered.
 */
@Embeddable
public class AircraftRegistrationCountry implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    private String value;

    /**
     * Required by ORM.
     */
    protected AircraftRegistrationCountry() {
    }

    private AircraftRegistrationCountry(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Aircraft registration country cannot be empty.");
        }

        this.value = value.trim().replaceAll("\\s+", " ");
    }

    /**
     * Creates an aircraft registration country from text.
     *
     * @param value the country name
     * @return a new aircraft registration country
     */
    public static AircraftRegistrationCountry valueOf(final String value) {
        return new AircraftRegistrationCountry(value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AircraftRegistrationCountry)) {
            return false;
        }

        final AircraftRegistrationCountry that = (AircraftRegistrationCountry) other;
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