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
    private List<WeatherDataId> weatherDataIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "TEST_STATUS", nullable = false)
    private FlightPlanTestStatus testStatus = FlightPlanTestStatus.NOT_TESTED;

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
        Preconditions.ensure(pilot.pilotStatus().isValid(), "Pilot must be active.");

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
            invalidateTestIfNeeded();
        }
    }

    /**
     * Marks this flight plan as successfully tested.
     */
    public void markTested() {
        testStatus = FlightPlanTestStatus.TESTED;
    }

    @Override
    public FlightPlanIdentifier identity() {
        return identifier;
    }

    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof FlightPlan)) {
            return false;
        }
        final FlightPlan that = (FlightPlan) other;
        return identity().equals(that.identity());
    }

    public FlightPlanIdentifier identifier() {
        return identifier;
    }

    public Flight flight() {
        return flight;
    }

    public Aircraft aircraft() {
        return aircraft;
    }

    public Pilot pilot() {
        return pilot;
    }

    public FuelQuantity fuelQuantity() {
        return fuelQuantity;
    }

    public FlightPlanStatus status() {
        return status;
    }

    public List<WeatherDataId> weatherDataIds() {
        return Collections.unmodifiableList(weatherDataIds);
    }

    public FlightPlanTestStatus testStatus() {
        return testStatus;
    }

    private void invalidateTestIfNeeded() {
        if (testStatus == FlightPlanTestStatus.TESTED) {
            testStatus = FlightPlanTestStatus.VOID;
        }
    }
}
