package isep.aisafe.flightplan.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;
import isep.aisafe.weatherdata.domain.WeatherData;
import isep.aisafe.weatherdata.domain.WeatherDataId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    @AttributeOverride(name = "value", column = @Column(name = "PILOT_IDENTIFIER", nullable = false))
    private PilotIdentifier pilotIdentifier;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "FLIGHT_PLAN_WEATHER_DATA", joinColumns = @JoinColumn(name = "FLIGHT_PLAN_PK"))
    private List<WeatherDataId> weatherDataIds = new ArrayList<>();

    protected FlightPlan() {
    }

    public FlightPlan(final FlightPlanIdentifier identifier, final PilotIdentifier pilotIdentifier) {
        Preconditions.nonNull(identifier, "Flight plan identifier is required.");
        Preconditions.nonNull(pilotIdentifier, "Pilot identifier is required.");

        this.identifier = identifier;
        this.pilotIdentifier = pilotIdentifier;
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
        }
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

    public PilotIdentifier pilotIdentifier() {
        return pilotIdentifier;
    }

    public List<WeatherDataId> weatherDataIds() {
        return Collections.unmodifiableList(weatherDataIds);
    }
}
