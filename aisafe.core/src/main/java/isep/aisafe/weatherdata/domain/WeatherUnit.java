package isep.aisafe.weatherdata.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Unit used for a weather measurement value.
 */
@Embeddable
public class WeatherUnit implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String value;

    protected WeatherUnit() {
    }

    private WeatherUnit(final String value) {
        Preconditions.ensure(value != null && !value.isBlank(), "Weather unit is required.");

        this.value = value.trim().toUpperCase();
    }

    public static WeatherUnit valueOf(final String value) {
        return new WeatherUnit(value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WeatherUnit)) {
            return false;
        }
        final WeatherUnit that = (WeatherUnit) other;
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
