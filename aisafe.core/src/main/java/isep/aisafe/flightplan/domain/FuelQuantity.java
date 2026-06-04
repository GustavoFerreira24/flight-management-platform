package isep.aisafe.flightplan.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents the fuel quantity assigned to a flight plan.
 */
@Embeddable
public class FuelQuantity implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "FUEL_AMOUNT", nullable = false)
    private double amount;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "FUEL_UNIT", nullable = false))
    private FuelUnit unit;

    /**
     * Required by ORM.
     */
    protected FuelQuantity() {
    }

    private FuelQuantity(final double amount, final FuelUnit unit) {
        Preconditions.ensure(amount > 0, "Fuel quantity must be greater than zero.");
        Preconditions.nonNull(unit, "Fuel unit cannot be null.");

        this.amount = amount;
        this.unit = unit;
    }

    public static FuelQuantity valueOf(final double amount, final FuelUnit unit) {
        return new FuelQuantity(amount, unit);
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
        if (!(other instanceof FuelQuantity)) {
            return false;
        }
        final FuelQuantity that = (FuelQuantity) other;
        return Double.compare(that.amount, amount) == 0 && Objects.equals(unit, that.unit);
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
