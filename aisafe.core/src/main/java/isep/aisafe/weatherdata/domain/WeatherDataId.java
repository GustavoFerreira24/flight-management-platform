package isep.aisafe.weatherdata.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Identifies weather data for one day and one air control area.
 */
@Embeddable
public class WeatherDataId implements ValueObject, Comparable<WeatherDataId>, Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true)
    private String value;

    protected WeatherDataId() {
    }

    private WeatherDataId(final LocalDate day, final AirControlAreaCode airControlAreaCode) {
        Preconditions.nonNull(day, "Weather data day is required.");
        Preconditions.nonNull(airControlAreaCode, "Air control area code is required.");

        value = day + "|" + airControlAreaCode;
    }

    public static WeatherDataId valueOf(final LocalDate day, final AirControlAreaCode airControlAreaCode) {
        return new WeatherDataId(day, airControlAreaCode);
    }

    @Override
    public int compareTo(final WeatherDataId other) {
        return value.compareTo(other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WeatherDataId)) {
            return false;
        }
        final WeatherDataId that = (WeatherDataId) other;
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
