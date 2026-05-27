package isep.aisafe.manufacturer.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ManufacturerDesignator implements ValueObject, Serializable, Comparable<ManufacturerDesignator> {

    private String value;

    protected ManufacturerDesignator() {
    }

    private ManufacturerDesignator(final String value) {
        Preconditions.ensure(value != null && !value.trim().isEmpty(), "Manufacturer designator cannot be empty.");
        this.value = normalize(value);
    }

    public static ManufacturerDesignator valueOf(final String value) {
        return new ManufacturerDesignator(value);
    }

    private static String normalize(final String raw) {
        return raw.trim().replaceAll("\\s+", " ").toUpperCase();
    }

    @Override
    public int compareTo(final ManufacturerDesignator other) {
        return value.compareTo(other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ManufacturerDesignator)) {
            return false;
        }

        final ManufacturerDesignator that = (ManufacturerDesignator) other;
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
