package isep.aisafe.weatherdata.domain;

import eapli.framework.domain.model.ValueObject;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

/**
 * A single atmospheric measurement inside a weather data aggregate.
 */
@Embeddable
public class WeatherMeasurement implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "PROVIDER_NAME", nullable = false)),
            @AttributeOverride(name = "type.value", column = @Column(name = "PROVIDER_TYPE", nullable = false))
    })
    private WeatherProvider provider;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "PARAMETER_NAME", nullable = false))
    private WeatherParameter parameter;

    @Column(nullable = false)
    private double value;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "UNIT_VALUE", nullable = false))
    private WeatherUnit unit;

    @Column(nullable = false)
    private double altitude;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "LATITUDE", nullable = false)),
            @AttributeOverride(name = "longitude", column = @Column(name = "LONGITUDE", nullable = false))
    })
    private WeatherCoordinates coordinates;

    protected WeatherMeasurement() {
    }

    WeatherMeasurement(
            final WeatherProvider provider,
            final WeatherParameter parameter,
            final double value,
            final WeatherUnit unit,
            final double altitude,
            final WeatherCoordinates coordinates) {

        if (provider == null) {
            throw new IllegalArgumentException("Weather provider is required.");
        }

        if (parameter == null) {
            throw new IllegalArgumentException("Weather parameter is required.");
        }

        if (unit == null) {
            throw new IllegalArgumentException("Weather unit is required.");
        }

        if (!parameter.isCompatibleWith(unit)) {
            throw new IllegalArgumentException("Weather unit is not compatible with parameter.");
        }

        if (coordinates == null) {
            throw new IllegalArgumentException("Weather coordinates are required.");
        }

        if (altitude < 0) {
            throw new IllegalArgumentException("Altitude cannot be negative.");
        }

        validateValue(parameter, value);

        this.provider = provider;
        this.parameter = parameter;
        this.value = value;
        this.unit = unit;
        this.altitude = altitude;
        this.coordinates = coordinates;
    }

    private void validateValue(final WeatherParameter parameter, final double value) {
        if ("TEMPERATURE".equals(parameter.name()) && (value < -100 || value > 70)) {
            throw new IllegalArgumentException("Temperature must be between -100 and 70 C.");
        }

        if ("WIND_SPEED".equals(parameter.name()) && value < 0) {
            throw new IllegalArgumentException("Wind speed cannot be negative.");
        }

        if ("WIND_DIRECTION".equals(parameter.name()) && (value < 0 || value > 360)) {
            throw new IllegalArgumentException("Wind direction must be between 0 and 360 degrees.");
        }

        if ("PRESSURE".equals(parameter.name()) && (value < 800 || value > 1100)) {
            throw new IllegalArgumentException("Pressure must be between 800 and 1100 hPa.");
        }

        if ("HUMIDITY".equals(parameter.name()) && (value < 0 || value > 100)) {
            throw new IllegalArgumentException("Humidity must be between 0 and 100 percent.");
        }
    }

    public WeatherProvider provider() {
        return provider;
    }

    public WeatherParameter parameter() {
        return parameter;
    }

    public double value() {
        return value;
    }

    public WeatherUnit unit() {
        return unit;
    }

    public double altitude() {
        return altitude;
    }

    public WeatherCoordinates coordinates() {
        return coordinates;
    }
}
