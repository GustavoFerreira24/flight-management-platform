package isep.aisafe.flightroute.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents the lifecycle status of a flight route.
 */
@Embeddable
public class FlightRouteStatus implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    private static final String ACTIVE = "ACTIVE";
    private static final String INACTIVE = "INACTIVE";

    private String value;

    /**
     * Required by ORM.
     */
    protected FlightRouteStatus() {
    }

    private FlightRouteStatus(final String value) {
        Preconditions.ensure(value != null && !value.trim().isEmpty(), "Flight route status cannot be empty.");

        final String normalizedValue = value.trim().toUpperCase();

        Preconditions.ensure(
                normalizedValue.equals(ACTIVE) || normalizedValue.equals(INACTIVE),
                "Invalid flight route status."
        );

        this.value = normalizedValue;
    }

    /**
     * Creates a flight route status from text.
     *
     * @param value the status text
     * @return a new flight route status
     */
    public static FlightRouteStatus valueOf(final String value) {
        return new FlightRouteStatus(value);
    }

    /**
     * Creates an active route status.
     *
     * @return active status
     */
    public static FlightRouteStatus active() {
        return new FlightRouteStatus(ACTIVE);
    }

    /**
     * Creates an inactive route status.
     *
     * @return inactive status
     */
    public static FlightRouteStatus inactive() {
        return new FlightRouteStatus(INACTIVE);
    }

    /**
     * Checks whether this status is active.
     *
     * @return true if active
     */
    public boolean isActive() {
        return ACTIVE.equals(value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof FlightRouteStatus)) {
            return false;
        }

        final FlightRouteStatus that = (FlightRouteStatus) other;
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
