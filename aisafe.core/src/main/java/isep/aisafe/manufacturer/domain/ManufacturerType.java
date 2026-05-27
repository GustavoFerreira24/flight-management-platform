package isep.aisafe.manufacturer.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Embeddable
public class ManufacturerType implements ValueObject, Serializable {

    private static final Set<String> SUPPORTED_TYPES = Set.of("AIRCRAFT", "ENGINE", "AIRCRAFT_AND_ENGINE");

    private String value;

    protected ManufacturerType() {
    }

    private ManufacturerType(final String value) {
        Preconditions.ensure(value != null && !value.trim().isEmpty(), "Manufacturer type cannot be empty.");

        final String normalized = value.trim().replaceAll("[\\s-]+", "_").toUpperCase();
        Preconditions.ensure(SUPPORTED_TYPES.contains(normalized), "Unsupported manufacturer type: " + value);

        this.value = normalized;
    }

    public static ManufacturerType valueOf(final String value) {
        return new ManufacturerType(value);
    }

    public boolean supportsAircraftModels() {
        return "AIRCRAFT".equals(value) || "AIRCRAFT_AND_ENGINE".equals(value);
    }

    public boolean supportsEngineModels() {
        return "ENGINE".equals(value) || "AIRCRAFT_AND_ENGINE".equals(value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ManufacturerType)) {
            return false;
        }

        final ManufacturerType that = (ManufacturerType) other;
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
