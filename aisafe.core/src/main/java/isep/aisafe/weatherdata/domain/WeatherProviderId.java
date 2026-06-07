package isep.aisafe.weatherdata.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Identifies a registered weather provider.
 */
@Embeddable
public class WeatherProviderId implements ValueObject, Comparable<WeatherProviderId>, Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true)
    private String value;

    protected WeatherProviderId() {
    }

    private WeatherProviderId(final String value) {
        Preconditions.ensure(value != null && !value.isBlank(), "Weather provider name is required.");
        this.value = value.trim().replaceAll("\\s+", " ").toUpperCase();
    }

    /**
     * Creates a provider identifier from provider name text.
     *
     * @param value provider name text
     * @return normalized provider identifier
     */
    public static WeatherProviderId valueOf(final String value) {
        return new WeatherProviderId(value);
    }

    @Override
    public int compareTo(final WeatherProviderId other) {
        return value.compareTo(other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WeatherProviderId)) {
            return false;
        }
        final WeatherProviderId that = (WeatherProviderId) other;
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
