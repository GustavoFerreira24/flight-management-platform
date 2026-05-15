package isep.aisafe.aircraft.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents the seat configuration of an aircraft cabin.
 */
@Embeddable
public class CabinConfiguration implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    private int economySeats;
    private int businessSeats;
    private int firstClassSeats;

    /**
     * Required by ORM.
     */
    protected CabinConfiguration() {
    }

    private CabinConfiguration(
            final int economySeats,
            final int businessSeats,
            final int firstClassSeats) {

        Preconditions.nonNegative(economySeats, "Economy seats cannot be negative.");
        Preconditions.nonNegative(businessSeats, "Business seats cannot be negative.");
        Preconditions.nonNegative(firstClassSeats, "First class seats cannot be negative.");
        Preconditions.ensure(
                economySeats + businessSeats + firstClassSeats > 0,
                "Cabin configuration must have at least one seat."
        );

        this.economySeats = economySeats;
        this.businessSeats = businessSeats;
        this.firstClassSeats = firstClassSeats;
    }

    /**
     * Creates a cabin configuration.
     *
     * @param economySeats the number of economy seats
     * @param businessSeats the number of business seats
     * @param firstClassSeats the number of first class seats
     * @return a new cabin configuration
     */
    public static CabinConfiguration valueOf(
            final int economySeats,
            final int businessSeats,
            final int firstClassSeats) {

        return new CabinConfiguration(economySeats, businessSeats, firstClassSeats);
    }

    public int economySeats() {
        return economySeats;
    }

    public int businessSeats() {
        return businessSeats;
    }

    public int firstClassSeats() {
        return firstClassSeats;
    }

    public int totalSeats() {
        return economySeats + businessSeats + firstClassSeats;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CabinConfiguration)) {
            return false;
        }

        final CabinConfiguration that = (CabinConfiguration) other;
        return economySeats == that.economySeats
                && businessSeats == that.businessSeats
                && firstClassSeats == that.firstClassSeats;
    }

    @Override
    public int hashCode() {
        return Objects.hash(economySeats, businessSeats, firstClassSeats);
    }

    @Override
    public String toString() {
        return "Economy: " + economySeats
                + ", Business: " + businessSeats
                + ", First Class: " + firstClassSeats;
    }
}
