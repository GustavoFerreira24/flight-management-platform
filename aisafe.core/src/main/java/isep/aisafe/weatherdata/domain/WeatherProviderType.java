package isep.aisafe.weatherdata.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Type of source that provided a weather measurement.
 */
@Embeddable
public class WeatherProviderType implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String value;

    protected WeatherProviderType() {
    }

    private WeatherProviderType(final String value) {
        Preconditions.ensure(value != null && !value.isBlank(), "Weather provider type is required.");

        this.value = value.trim().replaceAll("\\s+", " ").toUpperCase();
    }

    public static WeatherProviderType valueOf(final String value) {
        return new WeatherProviderType(value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WeatherProviderType)) {
            return false;
        }
        final WeatherProviderType that = (WeatherProviderType) other;
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
