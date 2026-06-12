package isep.aisafe.flightplan.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;
import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.flight.domain.Flight;
import isep.aisafe.pilot.domain.Pilot;
import isep.aisafe.weatherdata.domain.WeatherData;
import isep.aisafe.weatherdata.domain.WeatherDataId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Flight plan aggregate managed by the flight plan domain.
 */
@Entity
public class FlightPlan implements AggregateRoot<FlightPlanIdentifier>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "FLIGHT_PLAN_ID", nullable = false, unique = true))
    private FlightPlanIdentifier identifier;

    @Embedded
    private FuelQuantity fuelQuantity;

    @ManyToOne(optional = false)
    private Flight flight;

    @ManyToOne(optional = false)
    private Aircraft aircraft;

    @ManyToOne(optional = false)
    private Pilot pilot;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private FlightPlanStatus status = FlightPlanStatus.DRAFT;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "FLIGHT_PLAN_WEATHER_DATA", joinColumns = @JoinColumn(name = "FLIGHT_PLAN_PK"))
    @AttributeOverride(name = "value", column = @Column(name = "WEATHER_DATA_ID", nullable = false))
    private List<WeatherDataId> weatherDataIds = new ArrayList<>();

    protected FlightPlan() {
    }

    public FlightPlan(
            final FlightPlanIdentifier identifier,
            final Flight flight,
            final Aircraft aircraft,
            final Pilot pilot,
            final FuelQuantity fuelQuantity) {

        Preconditions.nonNull(identifier, "Flight plan identifier is required.");
        Preconditions.nonNull(flight, "Flight is required.");
        Preconditions.nonNull(aircraft, "Aircraft is required.");
        Preconditions.nonNull(pilot, "Pilot is required.");
        Preconditions.nonNull(fuelQuantity, "Fuel quantity is required.");
        Preconditions.ensure(aircraft.canBeUsedForFlights(), "Aircraft cannot be used for flights.");
        Preconditions.ensure(
                aircraft.company().sameAs(flight.route().company()),
                "Aircraft must belong to the route's air transport company."
        );
        Preconditions.ensure(
                pilot.companyIATACode().equals(flight.route().company().iataCode()),
                "Pilot must belong to the route's air transport company."
        );
        Preconditions.ensure(pilot.pilotStatus().isValid(pilot.certification()), "Pilot must be active.");

        this.identifier = identifier;
        this.flight = flight;
        this.aircraft = aircraft;
        this.pilot = pilot;
        this.fuelQuantity = fuelQuantity;
        this.status = FlightPlanStatus.DRAFT;
    }

    /**
     * Associates existing weather data with this flight plan.
     *
     * @param weatherData weather data to associate
     */
    public void addWeatherData(final WeatherData weatherData) {
        Preconditions.nonNull(weatherData, "Weather data is required.");

        final WeatherDataId weatherDataId = weatherData.identity();
        if (!weatherDataIds.contains(weatherDataId)) {
            weatherDataIds.add(weatherDataId);
            invalidateValidationIfNeeded();
        }
    }

    /**
     * Marks this flight plan as being validated.
     */
    public void markUnderValidation() {
        Preconditions.ensure(
                status == FlightPlanStatus.DRAFT,
                "Only draft flight plans can start validation."
        );

        status = FlightPlanStatus.UNDER_VALIDATION;
    }

    /**
     * Marks this flight plan as validated.
     */
    public void validate() {
        Preconditions.ensure(
                status == FlightPlanStatus.DRAFT || status == FlightPlanStatus.UNDER_VALIDATION,
                "Only draft or under validation flight plans can be validated."
        );

        status = FlightPlanStatus.VALIDATED;
    }

    /**
     * Marks this flight plan as rejected.
     */
    public void reject() {
        Preconditions.ensure(
                status == FlightPlanStatus.DRAFT || status == FlightPlanStatus.UNDER_VALIDATION,
                "Only draft or under validation flight plans can be rejected."
        );

        status = FlightPlanStatus.REJECTED;
    }

    /**
     * Checks whether this flight plan can be tested/validated.
     *
     * @return true if this flight plan can be tested
     */
    public boolean canBeTested() {
        return status == FlightPlanStatus.DRAFT || status == FlightPlanStatus.UNDER_VALIDATION;
    }

    /**
     * Returns this aggregate identity.
     *
     * @return flight plan identifier
     */
    @Override
    public FlightPlanIdentifier identity() {
        return identifier;
    }

    /**
     * Checks whether another object represents the same flight plan.
     *
     * @param other object to compare
     * @return true when both objects have the same identity
     */
    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof FlightPlan)) {
            return false;
        }

        final FlightPlan that = (FlightPlan) other;
        return identity().equals(that.identity());
    }

    /**
     * Returns this flight plan identifier.
     *
     * @return flight plan identifier
     */
    public FlightPlanIdentifier identifier() {
        return identifier;
    }

    /**
     * Returns the flight covered by this flight plan.
     *
     * @return planned flight
     */
    public Flight flight() {
        return flight;
    }

    /**
     * Returns this flight plan's scheduled departure timestamp.
     *
     * @return departure date/time
     */
    public LocalDateTime flightDepartureDateTime() {
        return flight.departure().dateTime();
    }

    /**
     * Returns the planned flight designator as display text.
     *
     * @return flight designator
     */
    public String flightDesignator() {
        return flight.designator().toString();
    }

    /**
     * Returns the aircraft assigned to this flight plan.
     *
     * @return assigned aircraft
     */
    public Aircraft aircraft() {
        return aircraft;
    }

    /**
     * Returns the assigned aircraft registration as display text.
     *
     * @return aircraft registration
     */
    public String aircraftRegistration() {
        return aircraft.registration().toString();
    }

    /**
     * Returns the pilot assigned to this flight plan.
     *
     * @return assigned pilot
     */
    public Pilot pilot() {
        return pilot;
    }

    /**
     * Returns the assigned pilot identifier as display text.
     *
     * @return pilot identifier
     */
    public String pilotIdentifier() {
        return pilot.identity().toString();
    }

    /**
     * Returns the fuel quantity assigned to this flight plan.
     *
     * @return assigned fuel quantity
     */
    public FuelQuantity fuelQuantity() {
        return fuelQuantity;
    }

    /**
     * Returns the lifecycle status of this flight plan.
     *
     * @return flight plan status
     */
    public FlightPlanStatus status() {
        return status;
    }

    /**
     * Returns the weather data identities associated with this flight plan.
     *
     * @return unmodifiable weather data identity list
     */
    public List<WeatherDataId> weatherDataIds() {
        return Collections.unmodifiableList(weatherDataIds);
    }

    /**
     * Returns how many weather data records are associated with this flight plan.
     *
     * @return weather data count
     */
    public int weatherDataCount() {
        return weatherDataIds.size();
    }

    /**
     * Checks whether this flight plan has associated weather data.
     *
     * @return true when at least one weather data record is associated
     */
    public boolean hasWeatherData() {
        return !weatherDataIds.isEmpty();
    }

    private void invalidateValidationIfNeeded() {
        if (status == FlightPlanStatus.VALIDATED) {
            status = FlightPlanStatus.DRAFT;
        }
    }
}