package isep.aisafe.weatherdata.domain;

import eapli.framework.domain.model.ValueObject;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Coordinates where a weather measurement applies.
 */
@Embeddable
public class WeatherCoordinates implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    private double latitude;
    private double longitude;

    protected WeatherCoordinates() {
    }

    private WeatherCoordinates(final double latitude, final double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90.");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180.");
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static WeatherCoordinates valueOf(final double latitude, final double longitude) {
        return new WeatherCoordinates(latitude, longitude);
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WeatherCoordinates)) {
            return false;
        }
        final WeatherCoordinates that = (WeatherCoordinates) other;
        return Double.compare(that.latitude, latitude) == 0
                && Double.compare(that.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
