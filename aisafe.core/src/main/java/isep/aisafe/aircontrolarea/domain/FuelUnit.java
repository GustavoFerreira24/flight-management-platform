package isep.aisafe.aircontrolarea.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents a fuel unit.
 */
@Embeddable
public class FuelUnit implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    private String value;

    protected FuelUnit() {
    }

    private FuelUnit(final String value) {
        Preconditions.ensure(value != null && !value.isBlank(), "Fuel unit is required.");

        final String normalized = value.trim().toUpperCase();

        Preconditions.ensure(normalized.matches("KG|L"), "Fuel unit must be KG or L.");

        this.value = normalized;
    }

    /**
     * Creates a fuel unit.
     *
     * @param value the fuel unit text
     * @return a new fuel unit
     */
    public static FuelUnit valueOf(final String value) {
        return new FuelUnit(value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof FuelUnit)) {
            return false;
        }

        final FuelUnit that = (FuelUnit) other;
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
