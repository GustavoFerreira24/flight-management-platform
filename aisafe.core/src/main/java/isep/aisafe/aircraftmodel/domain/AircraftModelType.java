package isep.aisafe.aircraftmodel.domain;

import eapli.framework.domain.model.ValueObject;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AircraftModelType implements ValueObject, Serializable {

    private String value;

    protected AircraftModelType() {
    }

    private AircraftModelType(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Aircraft model type cannot be empty.");
        }

        this.value = value.trim().replaceAll("\\s+", " ").toUpperCase();
    }

    public static AircraftModelType valueOf(final String value) {
        return new AircraftModelType(value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AircraftModelType)) {
            return false;
        }

        final AircraftModelType that = (AircraftModelType) other;
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
