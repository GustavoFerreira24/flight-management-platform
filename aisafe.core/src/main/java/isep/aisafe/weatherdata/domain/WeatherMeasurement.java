package isep.aisafe.weatherdata.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

/**
 * A single atmospheric measurement inside a weather data aggregate.
 */
@Embeddable
public class WeatherMeasurement implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(optional = false)
    @JoinColumn(name = "WEATHER_PROVIDER_PK", nullable = false)
    private WeatherProvider provider;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "PROVIDER_TYPE", nullable = false))
    private WeatherProviderType providerType;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "PARAMETER_NAME", nullable = false))
    private WeatherParameter parameter;

    @Column(name = "MEASUREMENT_VALUE", nullable = false)
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
            final WeatherProviderType providerType,
            final WeatherParameter parameter,
            final double value,
            final WeatherUnit unit,
            final double altitude,
            final WeatherCoordinates coordinates) {

        Preconditions.nonNull(provider, "Weather provider is required.");
        Preconditions.nonNull(providerType, "Weather provider type is required.");
        Preconditions.ensure(provider.supports(providerType), "Weather provider does not support the submitted provider type.");
        Preconditions.nonNull(parameter, "Weather parameter is required.");
        Preconditions.nonNull(unit, "Weather unit is required.");
        Preconditions.ensure(parameter.isCompatibleWith(unit), "Weather unit is not compatible with parameter.");
        Preconditions.nonNull(coordinates, "Weather coordinates are required.");
        Preconditions.ensure(altitude >= 0, "Altitude cannot be negative.");

        validateValue(parameter, value);

        this.provider = provider;
        this.providerType = providerType;
        this.parameter = parameter;
        this.value = value;
        this.unit = unit;
        this.altitude = altitude;
        this.coordinates = coordinates;
    }

    private void validateValue(final WeatherParameter parameter, final double value) {
        Preconditions.ensure(
                !"TEMPERATURE".equals(parameter.name()) || (value >= -100 && value <= 70),
                "Temperature must be between -100 and 70 C."
        );
        Preconditions.ensure(
                !"WIND_SPEED".equals(parameter.name()) || value >= 0,
                "Wind speed cannot be negative."
        );
        Preconditions.ensure(
                !"WIND_DIRECTION".equals(parameter.name()) || (value >= 0 && value <= 360),
                "Wind direction must be between 0 and 360 degrees."
        );
        Preconditions.ensure(
                !"PRESSURE".equals(parameter.name()) || (value >= 800 && value <= 1100),
                "Pressure must be between 800 and 1100 hPa."
        );
        Preconditions.ensure(
                !"HUMIDITY".equals(parameter.name()) || (value >= 0 && value <= 100),
                "Humidity must be between 0 and 100 percent."
        );
    }

    public WeatherProvider provider() {
        return provider;
    }

    public WeatherProviderType providerType() {
        return providerType;
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
