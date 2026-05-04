package isep.aisafe;

import eapli.framework.domain.model.ValueObject;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents an air transport company ICAO code.
 *
 * A company ICAO code must have two or three letters.
 */
@Embeddable
public class ICAOCode implements ValueObject, Serializable, Comparable<ICAOCode> {

    private String value;

    protected ICAOCode() {    }

    /**
     * Public constructor to allow usage in Airport and Company contexts.
     * Supports 2-3 letters for companies and 4 letters for airports.
     */
    public ICAOCode(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ICAO code cannot be empty.");
        }

        final String normalizedValue = value.trim().toUpperCase();

        if (!normalizedValue.matches("[A-Z]{2,4}")) {
            throw new IllegalArgumentException("ICAO code must have between 2 and 4 letters.");
        }

        this.value = normalizedValue;
    }

     /**
     * Compares this ICAO code with another one.
     *
     * @param other the other ICAO code
     * @return comparison result
     */
    @Override
    public int compareTo(final ICAOCode other) {
        return this.value.compareTo(other.value);
    }

    /**
     * Creates an air transport company ICAO code from a text value.
     *
     * @param value the ICAO code text
     * @return a new ICAO code value object
     */
    public static ICAOCode valueOf(final String value) {
        final ICAOCode code = new ICAOCode(value);

        if (code.toString().length() > 3) {
            throw new IllegalArgumentException("ICAO code must have 2 or 3 letters.");
        }

        return code;
    }

    /**
     * Checks equality by value.
     *
     * @param other the object to compare with
     * @return true if both ICAO codes have the same value
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ICAOCode)) {
            return false;
        }

        final ICAOCode that = (ICAOCode) other;
        return Objects.equals(this.value, that.value);
    }

    /**
     * Returns the hash code based on the ICAO code value.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns the ICAO code as text.
     *
     * @return the ICAO code value
     */
    @Override
    public String toString() {
        return value;
    }
}
