package isep.aisafe.flightroute.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents a flight route type.
 */
@Embeddable
public class FlightRouteType implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    private static final String REGULAR = "REGULAR";

    private String value;

    /**
     * Required by ORM.
     */
    protected FlightRouteType() {
    }

    private FlightRouteType(final String value) {
        Preconditions.ensure(value != null && !value.trim().isEmpty(), "Flight route type cannot be empty.");

        final String normalizedValue = value.trim().toUpperCase().replaceAll("\\s+", "_");

        Preconditions.ensure(
                normalizedValue.matches("[A-Z][A-Z_]{0,49}"),
                "Flight route type must contain only letters, spaces or underscores."
        );

        this.value = normalizedValue;
    }

    /**
     * Creates a flight route type from text.
     *
     * @param value the type text
     * @return a new flight route type
     */
    public static FlightRouteType valueOf(final String value) {
        return new FlightRouteType(value);
    }

    /**
     * Creates the default type for a newly registered flight route.
     *
     * @return regular flight route type
     */
    public static FlightRouteType regular() {
        return new FlightRouteType(REGULAR);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof FlightRouteType)) {
            return false;
        }

        final FlightRouteType that = (FlightRouteType) other;
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
