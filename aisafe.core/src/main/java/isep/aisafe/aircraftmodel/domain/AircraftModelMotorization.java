package isep.aisafe.aircraftmodel.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AircraftModelMotorization implements ValueObject, Serializable {

    private String value;

    protected AircraftModelMotorization() {
    }

    private AircraftModelMotorization(final String value) {
        Preconditions.ensure(value != null && !value.trim().isEmpty(), "Aircraft model motorization cannot be empty.");

        this.value = normalize(value);
    }

    public static AircraftModelMotorization valueOf(final String value) {
        return new AircraftModelMotorization(value);
    }

    private static String normalize(final String raw) {
        return raw.trim().replaceAll("\\s+", " ").toUpperCase();
    }

    public boolean isCompatibleWith(final String engineTypeValue) {
        return this.value.equals(normalize(engineTypeValue));
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AircraftModelMotorization)) {
            return false;
        }

        final AircraftModelMotorization that = (AircraftModelMotorization) other;
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
