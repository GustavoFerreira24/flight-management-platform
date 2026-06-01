package isep.aisafe.flightroute.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;
import isep.aisafe.airport.domain.Airport;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a flight route owned by an air transport company.
 */
@Entity
public class FlightRoute implements AggregateRoot<RouteDesignator>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ROUTE_DESIGNATOR", nullable = false, unique = true))
    private RouteDesignator routeDesignator;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ROUTE_TYPE", nullable = false))
    private FlightRouteType routeType;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ROUTE_STATUS", nullable = false))
    private FlightRouteStatus routeStatus;

    @Column(name = "DEACTIVATED_FROM")
    private LocalDate deactivatedFrom;

    @ManyToOne(optional = false)
    @JoinColumn(name = "DEPARTURE_AIRPORT_ICAO_CODE", referencedColumnName = "ICAO_CODE", nullable = false)
    private Airport departureAirport;

    @ManyToOne(optional = false)
    @JoinColumn(name = "DESTINATION_AIRPORT_ICAO_CODE", referencedColumnName = "ICAO_CODE", nullable = false)
    private Airport destinationAirport;

    @ManyToOne(optional = false)
    private AirTransportCompany company;

    /**
     * Required by ORM.
     */
    protected FlightRoute() {
    }

    /**
     * Creates a new regular active flight route.
     *
     * @param routeDesignator the route designator
     * @param company the owning air transport company
     * @param departureAirport the departure airport
     * @param destinationAirport the destination airport
     */
    public FlightRoute(
            final RouteDesignator routeDesignator,
            final AirTransportCompany company,
            final Airport departureAirport,
            final Airport destinationAirport) {

        this(
                routeDesignator,
                FlightRouteType.regular(),
                company,
                departureAirport,
                destinationAirport
        );
    }

    /**
     * Creates a new active flight route.
     *
     * @param routeDesignator the route designator
     * @param routeType the route type
     * @param company the owning air transport company
     * @param departureAirport the departure airport
     * @param destinationAirport the destination airport
     */
    public FlightRoute(
            final RouteDesignator routeDesignator,
            final FlightRouteType routeType,
            final AirTransportCompany company,
            final Airport departureAirport,
            final Airport destinationAirport) {

        Preconditions.nonNull(routeDesignator, "Route designator cannot be null.");
        Preconditions.nonNull(routeType, "Flight route type cannot be null.");
        Preconditions.nonNull(company, "Air transport company cannot be null.");
        Preconditions.nonNull(departureAirport, "Departure airport cannot be null.");
        Preconditions.nonNull(destinationAirport, "Destination airport cannot be null.");
        Preconditions.ensure(
                !departureAirport.identity().equals(destinationAirport.identity()),
                "Departure and destination airports must be different."
        );
        Preconditions.ensure(routeDesignator.belongsTo(company), "Route designator must start with the company IATA code.");

        this.routeDesignator = routeDesignator;
        this.routeType = routeType;
        this.routeStatus = FlightRouteStatus.active();
        this.company = company;
        this.departureAirport = departureAirport;
        this.destinationAirport = destinationAirport;
    }

    @Override
    public RouteDesignator identity() {
        return routeDesignator;
    }

    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof FlightRoute)) {
            return false;
        }

        final FlightRoute that = (FlightRoute) other;
        return this.identity().equals(that.identity());
    }

    public RouteDesignator routeDesignator() {
        return routeDesignator;
    }

    public FlightRouteType routeType() {
        return routeType;
    }

    public FlightRouteStatus routeStatus() {
        return routeStatus;
    }

    public LocalDate deactivatedFrom() {
        return deactivatedFrom;
    }

    public Airport departureAirport() {
        return departureAirport;
    }

    public Airport destinationAirport() {
        return destinationAirport;
    }

    public AirTransportCompany company() {
        return company;
    }

    /**
     * Deactivates this flight route from a future date.
     *
     * @param deactivatedFrom the first date when the route is inactive
     */
    public void deactivateFrom(final LocalDate deactivatedFrom) {
        Preconditions.nonNull(deactivatedFrom, "Deactivation date cannot be null.");
        Preconditions.ensure(deactivatedFrom.isAfter(LocalDate.now()), "Deactivation date must be in the future.");

        if (!routeStatus.isActive()) {
            throw new IllegalStateException("Flight route is already inactive.");
        }

        this.routeStatus = FlightRouteStatus.inactive();
        this.deactivatedFrom = deactivatedFrom;
    }

    /**
     * Checks whether this route is active.
     *
     * @return true if active
     */
    public boolean isActive() {
        return routeStatus.isActive();
    }
}
