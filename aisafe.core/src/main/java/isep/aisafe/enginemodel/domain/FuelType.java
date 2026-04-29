package isep.aisafe.enginemodel.domain;

import eapli.framework.domain.model.ValueObject;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FuelType implements ValueObject, Serializable {

    private String value;

    protected FuelType() {
    }

    private FuelType(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Fuel type cannot be empty.");
        }

        this.value = value.trim().replaceAll("\\s+", " ").toUpperCase();
    }

    public static FuelType valueOf(final String value) {
        return new FuelType(value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof FuelType)) {
            return false;
        }

        final FuelType that = (FuelType) other;
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
