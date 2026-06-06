package isep.aisafe.weatherdata.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Weather data recorded for one day and one air control area.
 */
@Entity
public class WeatherData implements AggregateRoot<WeatherDataId>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "WEATHER_DATA_ID", nullable = false, unique = true))
    private WeatherDataId id;

    @Column(name = "WEATHER_DAY", nullable = false)
    private LocalDate day;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "AREA_CODE", nullable = false))
    private AirControlAreaCode airControlAreaCode;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "WEATHER_MEASUREMENT", joinColumns = @JoinColumn(name = "WEATHER_DATA_PK"))
    private List<WeatherMeasurement> measurements = new ArrayList<>();

    protected WeatherData() {
    }

    public WeatherData(final LocalDate day, final AirControlAreaCode airControlAreaCode) {
        Preconditions.nonNull(day, "Weather data day is required.");
        Preconditions.nonNull(airControlAreaCode, "Air control area code is required.");

        this.day = day;
        this.airControlAreaCode = airControlAreaCode;
        id = WeatherDataId.valueOf(day, airControlAreaCode);
    }

    /**
     * Adds a measurement to this weather data aggregate.
     *
     * @param provider measurement source
     * @param providerType acquisition type used by the provider
     * @param parameter measured weather parameter
     * @param value measured value
     * @param unit measurement unit
     * @param altitude altitude where the value applies
     * @param coordinates coordinates where the value applies
     */
    public void addMeasurement(
            final WeatherProvider provider,
            final WeatherProviderType providerType,
            final WeatherParameter parameter,
            final double value,
            final WeatherUnit unit,
            final double altitude,
            final WeatherCoordinates coordinates) {

        measurements.add(new WeatherMeasurement(provider, providerType, parameter, value, unit, altitude, coordinates));
    }

    @Override
    public WeatherDataId identity() {
        return id;
    }

    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof WeatherData)) {
            return false;
        }
        final WeatherData that = (WeatherData) other;
        return identity().equals(that.identity());
    }

    public LocalDate day() {
        return day;
    }

    public AirControlAreaCode airControlAreaCode() {
        return airControlAreaCode;
    }

    public List<WeatherMeasurement> measurements() {
        return Collections.unmodifiableList(measurements);
    }
}
