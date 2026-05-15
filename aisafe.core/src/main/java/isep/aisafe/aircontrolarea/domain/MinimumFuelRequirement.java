package isep.aisafe.aircontrolarea.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents the minimum fuel required in an air control area.
 */
@Embeddable
public class MinimumFuelRequirement implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "MINIMUM_FUEL_AMOUNT")
    private double amount;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "FUEL_UNIT", nullable = false))
    private FuelUnit unit;

    protected MinimumFuelRequirement() {
    }

    private MinimumFuelRequirement(final double amount, final FuelUnit unit) {
        Preconditions.ensure(amount > 0, "Minimum fuel amount must be greater than zero.");
        Preconditions.nonNull(unit, "Fuel unit is required.");

        this.amount = amount;
        this.unit = unit;
    }

    /**
     * Creates a minimum fuel requirement.
     *
     * @param amount the fuel amount
     * @param unit the fuel unit
     * @return a new minimum fuel requirement
     */
    public static MinimumFuelRequirement valueOf(final double amount, final FuelUnit unit) {
        return new MinimumFuelRequirement(amount, unit);
    }

    public double amount() {
        return amount;
    }

    public FuelUnit unit() {
        return unit;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof MinimumFuelRequirement)) {
            return false;
        }

        final MinimumFuelRequirement that = (MinimumFuelRequirement) other;
        return Double.compare(that.amount, amount) == 0
                && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, unit);
    }

    @Override
    public String toString() {
        return amount + " " + unit;
    }
}
