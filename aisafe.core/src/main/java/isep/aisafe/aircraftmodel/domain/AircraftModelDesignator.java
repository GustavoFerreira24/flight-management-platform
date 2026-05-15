package isep.aisafe.aircraftmodel.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AircraftModelDesignator implements ValueObject, Serializable, Comparable<AircraftModelDesignator> {

    private String value;

    protected AircraftModelDesignator() {
    }

    private AircraftModelDesignator(final String value) {
        Preconditions.ensure(value != null && !value.trim().isEmpty(), "Aircraft model designator cannot be empty.");

        this.value = normalize(value);
    }

    public static AircraftModelDesignator valueOf(final String value) {
        return new AircraftModelDesignator(value);
    }

    public static AircraftModelDesignator valueOf(final String modelName, final String manufacturer) {
        Preconditions.ensure(modelName != null && !modelName.trim().isEmpty(), "Aircraft model name cannot be empty.");
        Preconditions.ensure(
                manufacturer != null && !manufacturer.trim().isEmpty(),
                "Aircraft model manufacturer cannot be empty."
        );

        return new AircraftModelDesignator(modelName.trim() + " | " + manufacturer.trim());
    }

    private static String normalize(final String raw) {
        return raw.trim().replaceAll("\\s+", " ").toUpperCase();
    }

    @Override
    public int compareTo(final AircraftModelDesignator other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AircraftModelDesignator)) {
            return false;
        }

        final AircraftModelDesignator that = (AircraftModelDesignator) other;
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
