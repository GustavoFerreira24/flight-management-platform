package isep.aisafe;

import eapli.framework.domain.model.ValueObject;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents an air transport company IATA code.
 *
 * A company IATA code must have exactly two letters.
 */
@Embeddable
public class IATACode implements ValueObject, Comparable<IATACode>, Serializable {

    private String value;

    protected IATACode() {}

    /**
     * Public constructor. Supports 2 letters for companies and 3 letters for airports.
     */
    public IATACode(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("IATA code cannot be empty.");
        }

        final String normalizedValue = value.trim().toUpperCase();

        // Updated to support 2 or 3 letters
        if (!normalizedValue.matches("[A-Z]{2,3}")) {
            throw new IllegalArgumentException("IATA code must have 2 or 3 letters.");
        }

        this.value = normalizedValue;
    }

    /**
     * Creates an IATA code from a text value.
     *
     * @param value the IATA code text
     * @return a new IATA code value object
     */
    public static IATACode valueOf(final String value) {
        return new IATACode(value);
    }

    /**
     * Compares this IATA code with another one.
     *
     * @param other the other IATA code
     * @return comparison result
     */
    @Override
    public int compareTo(final IATACode other) {
        return this.value.compareTo(other.value);
    }

    /**
     * Checks equality by value.
     *
     * @param other the object to compare with
     * @return true if both IATA codes have the same value
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof IATACode)) {
            return false;
        }

        final IATACode that = (IATACode) other;
        return Objects.equals(this.value, that.value);
    }

    /**
     * Returns the hash code based on the IATA code value.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns the IATA code as text.
     *
     * @return the IATA code value
     */
    @Override
    public String toString() {
        return value;
    }
}