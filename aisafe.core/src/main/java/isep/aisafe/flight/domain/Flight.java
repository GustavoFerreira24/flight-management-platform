package isep.aisafe.flight.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;
import isep.aisafe.flightroute.domain.FlightRoute;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;

import java.io.Serializable;

/**
 * Represents a scheduled flight instance for a flight route.
 */
@Entity
public class Flight implements AggregateRoot<FlightDesignator>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "FLIGHT_DESIGNATOR", nullable = false, unique = true))
    private FlightDesignator designator;

    @Embedded
    private Departure departure;

    @ManyToOne(optional = false)
    private FlightRoute route;

    /**
     * Required by ORM.
     */
    protected Flight() {
    }

    /**
     * Creates a scheduled flight for an active route.
     *
     * @param designator the flight designator
     * @param route the route where the flight takes place
     * @param departure the flight departure definition
     */
    public Flight(
            final FlightDesignator designator,
            final FlightRoute route,
            final Departure departure) {

        Preconditions.nonNull(designator, "Flight designator cannot be null.");
        Preconditions.nonNull(route, "Flight route cannot be null.");
        Preconditions.nonNull(departure, "Departure cannot be null.");
        Preconditions.ensure(route.isActive(), "Flight route must be active.");
        Preconditions.ensure(designator.belongsTo(route.company()), "Flight designator must start with the route company's IATA code.");

        this.designator = designator;
        this.route = route;
        this.departure = departure;
    }

    @Override
    public FlightDesignator identity() {
        return designator;
    }

    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof Flight)) {
            return false;
        }
        final Flight that = (Flight) other;
        return identity().equals(that.identity());
    }

    public FlightDesignator designator() {
        return designator;
    }

    public FlightRoute route() {
        return route;
    }

    public Departure departure() {
        return departure;
    }
}
