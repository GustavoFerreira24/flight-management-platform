package isep.aisafe.airport.domain;

import eapli.framework.domain.model.ValueObject;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents an airport ICAO code.
 *
 * An airport ICAO code must have exactly four letters.
 */
@Embeddable
public class AirportICAOCode implements ValueObject, Comparable<AirportICAOCode>, Serializable {

    private String value;

    /**
     * Required by ORM.
     */
    protected AirportICAOCode() {
    }

    private AirportICAOCode(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Airport ICAO code cannot be empty.");
        }

        final String normalizedValue = value.trim().toUpperCase();

        if (!normalizedValue.matches("[A-Z]{4}")) {
            throw new IllegalArgumentException("Airport ICAO code must have exactly 4 letters.");
        }

        this.value = normalizedValue;
    }

    /**
     * Creates an airport ICAO code from a text value.
     *
     * @param value the ICAO code text
     * @return a new airport ICAO code value object
     */
    public static AirportICAOCode valueOf(final String value) {
        return new AirportICAOCode(value);
    }

    /**
     * Compares this airport ICAO code with another one.
     *
     * @param other the other airport ICAO code
     * @return comparison result
     */
    @Override
    public int compareTo(final AirportICAOCode other) {
        return this.value.compareTo(other.value);
    }

    /**
     * Checks equality by value.
     *
     * @param other the object to compare with
     * @return true if both airport ICAO codes have the same value
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AirportICAOCode)) {
            return false;
        }

        final AirportICAOCode that = (AirportICAOCode) other;
        return Objects.equals(this.value, that.value);
    }

    /**
     * Returns the hash code based on the airport ICAO code value.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns the airport ICAO code as text.
     *
     * @return the airport ICAO code value
     */
    @Override
    public String toString() {
        return value;
    }
}