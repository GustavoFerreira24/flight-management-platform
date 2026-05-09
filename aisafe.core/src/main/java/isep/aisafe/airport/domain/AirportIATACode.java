package isep.aisafe.airport.domain;

import eapli.framework.domain.model.ValueObject;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents an airport IATA code.
 *
 * An airport IATA code must have exactly three letters.
 */
@Embeddable
public class AirportIATACode implements ValueObject, Comparable<AirportIATACode>, Serializable {

    private String value;

    /**
     * Required by ORM.
     */
    protected AirportIATACode() {
    }

    private AirportIATACode(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Airport IATA code cannot be empty.");
        }

        final String normalizedValue = value.trim().toUpperCase();

        if (!normalizedValue.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Airport IATA code must have exactly 3 letters.");
        }

        this.value = normalizedValue;
    }

    /**
     * Creates an airport IATA code from a text value.
     *
     * @param value the IATA code text
     * @return a new airport IATA code value object
     */
    public static AirportIATACode valueOf(final String value) {
        return new AirportIATACode(value);
    }

    /**
     * Compares this airport IATA code with another one.
     *
     * @param other the other airport IATA code
     * @return comparison result
     */
    @Override
    public int compareTo(final AirportIATACode other) {
        return this.value.compareTo(other.value);
    }

    /**
     * Checks equality by value.
     *
     * @param other the object to compare with
     * @return true if both airport IATA codes have the same value
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AirportIATACode)) {
            return false;
        }

        final AirportIATACode that = (AirportIATACode) other;
        return Objects.equals(this.value, that.value);
    }

    /**
     * Returns the hash code based on the airport IATA code value.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns the airport IATA code as text.
     *
     * @return the airport IATA code value
     */
    @Override
    public String toString() {
        return value;
    }
}