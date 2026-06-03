package isep.aisafe.flight.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that identifies a scheduled flight.
 */
@Embeddable
public class FlightDesignator implements ValueObject, Comparable<FlightDesignator>, Serializable {

    private static final long serialVersionUID = 1L;

    private String value;

    /**
     * Required by ORM.
     */
    protected FlightDesignator() {
    }

    private FlightDesignator(final String value) {
        Preconditions.ensure(value != null && !value.trim().isEmpty(), "Flight designator cannot be empty.");

        final String normalizedValue = value.trim().toUpperCase();
        Preconditions.ensure(
                normalizedValue.matches("[A-Z]{2}[0-9]{1,4}[A-Z]?"),
                "Flight designator must have 2 airline letters, 1 to 4 digits and an optional suffix letter."
        );

        this.value = normalizedValue;
    }

    /**
     * Creates a flight designator from text.
     *
     * @param value the flight designator text
     * @return a new flight designator
     */
    public static FlightDesignator valueOf(final String value) {
        return new FlightDesignator(value);
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
    public int compareTo(final FlightDesignator other) {
        return value.compareTo(other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FlightDesignator)) {
            return false;
        }
        final FlightDesignator that = (FlightDesignator) other;
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
