package isep.aisafe.airtransportcompany.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents a country.
 */
@Embeddable
public class Country implements ValueObject, Serializable {

    private String value;


    
    protected Country() {
    }

    public Country(final String value) {
        Preconditions.ensure(value != null && !value.trim().isEmpty(), "Country cannot be empty.");

        this.value = value.trim().replaceAll("\\s+", " ");
    }

    /**
     * Creates a country from a text value.
     *
     * @param value the country name
     * @return a new country value object
     */
    public static Country valueOf(final String value) {
        return new Country(value);
    }

    /**
     * Checks equality by value.
     *
     * @param other the object to compare with
     * @return true if both countries have the same value
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Country)) {
            return false;
        }

        final Country that = (Country) other;
        return Objects.equals(this.value, that.value);
    }

    /**
     * Returns the hash code based on the country value.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns the country as text.
     *
     * @return the country value
     */
    @Override
    public String toString() {
        return value;
    }
}
