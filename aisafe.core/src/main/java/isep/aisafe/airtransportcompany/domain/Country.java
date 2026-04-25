package isep.aisafe.airtransportcompany.domain;

import eapli.framework.domain.model.ValueObject;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Country implements ValueObject, Serializable {

    private String name;

    protected Country() {
    }

    private Country(final String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Country name cannot be empty.");
        }

        this.name = name.trim();
    }

    public static Country valueOf(final String name) {
        return new Country(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (!(other instanceof Country)) return false;

        final Country that = (Country) other;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}