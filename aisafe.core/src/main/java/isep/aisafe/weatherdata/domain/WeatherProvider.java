package isep.aisafe.weatherdata.domain;

import eapli.framework.domain.model.ValueObject;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Identifies the source of a weather measurement.
 */
@Embeddable
public class WeatherProvider implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "PROVIDER_TYPE", nullable = false))
    private WeatherProviderType type;

    protected WeatherProvider() {
    }

    private WeatherProvider(final String name, final WeatherProviderType type) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Weather provider name is required.");
        }

        if (type == null) {
            throw new IllegalArgumentException("Weather provider type is required.");
        }

        this.name = name.trim().replaceAll("\\s+", " ");
        this.type = type;
    }

    public static WeatherProvider valueOf(final String name, final WeatherProviderType type) {
        return new WeatherProvider(name, type);
    }

    public String name() {
        return name;
    }

    public WeatherProviderType type() {
        return type;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WeatherProvider)) {
            return false;
        }
        final WeatherProvider that = (WeatherProvider) other;
        return Objects.equals(name, that.name) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
