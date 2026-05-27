package isep.aisafe.flightroute.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents a flight route designator.
 */
@Embeddable
public class RouteDesignator implements ValueObject, Comparable<RouteDesignator>, Serializable {

    private static final long serialVersionUID = 1L;

    private String value;

    /**
     * Required by ORM.
     */
    protected RouteDesignator() {
    }

    private RouteDesignator(final String value) {
        Preconditions.ensure(value != null && !value.trim().isEmpty(), "Route designator cannot be empty.");

        final String normalizedValue = value.trim().toUpperCase();

        Preconditions.ensure(
                normalizedValue.matches("[A-Z]{2}[0-9]{1,4}"),
                "Route designator must have 2 company letters followed by 1 to 4 digits."
        );

        this.value = normalizedValue;
    }

    /**
     * Creates a route designator from text.
     *
     * @param value the route designator text
     * @return a new route designator
     */
    public static RouteDesignator valueOf(final String value) {
        return new RouteDesignator(value);
    }

    /**
     * Checks whether this designator belongs to the given air transport company.
     *
     * @param company the air transport company
     * @return true if the designator starts with the company's IATA code
     */
    public boolean belongsTo(final AirTransportCompany company) {
        Preconditions.nonNull(company, "Air transport company cannot be null.");

        return value.startsWith(company.iataCode().toString());
    }

    @Override
    public int compareTo(final RouteDesignator other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof RouteDesignator)) {
            return false;
        }

        final RouteDesignator that = (RouteDesignator) other;
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
