package isep.aisafe.weatherdata.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Type of atmospheric measurement being registered.
 */
@Embeddable
public class WeatherParameter implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Map<String, Set<String>> COMPATIBLE_UNITS = Map.of(
            "TEMPERATURE", Set.of("C"),
            "WIND_SPEED", Set.of("M/S", "KT"),
            "WIND_DIRECTION", Set.of("DEG"),
            "PRESSURE", Set.of("HPA"),
            "HUMIDITY", Set.of("PERCENT")
    );

    @Column(nullable = false)
    private String name;

    protected WeatherParameter() {
    }

    private WeatherParameter(final String name) {
        Preconditions.ensure(name != null && !name.isBlank(), "Weather parameter is required.");

        final String normalized = name.trim().replace('-', '_').replace(' ', '_').toUpperCase();
        if (!COMPATIBLE_UNITS.containsKey(normalized)) {
            throw new IllegalArgumentException("Unsupported weather parameter: " + name);
        }

        this.name = normalized;
    }

    public static WeatherParameter valueOf(final String name) {
        return new WeatherParameter(name);
    }

    public boolean isCompatibleWith(final WeatherUnit unit) {
        return unit != null && COMPATIBLE_UNITS.get(name).contains(unit.toString());
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WeatherParameter)) {
            return false;
        }
        final WeatherParameter that = (WeatherParameter) other;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
