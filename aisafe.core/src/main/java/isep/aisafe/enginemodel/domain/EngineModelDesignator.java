package isep.aisafe.enginemodel.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EngineModelDesignator implements ValueObject, Serializable, Comparable<EngineModelDesignator> {

    private String value;

    protected EngineModelDesignator() {
    }

    private EngineModelDesignator(final String value) {
        Preconditions.ensure(value != null && !value.trim().isEmpty(), "Engine model designator cannot be empty.");

        this.value = normalize(value);
    }

    public static EngineModelDesignator valueOf(final String value) {
        return new EngineModelDesignator(value);
    }

    public static EngineModelDesignator valueOf(final String modelName, final String manufacturer) {
        Preconditions.ensure(modelName != null && !modelName.trim().isEmpty(), "Engine model name cannot be empty.");
        Preconditions.ensure(
                manufacturer != null && !manufacturer.trim().isEmpty(),
                "Engine model manufacturer cannot be empty."
        );

        return new EngineModelDesignator(modelName.trim() + " | " + manufacturer.trim());
    }

    private static String normalize(final String raw) {
        return raw.trim().replaceAll("\\s+", " ").toUpperCase();
    }

    @Override
    public int compareTo(final EngineModelDesignator other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof EngineModelDesignator)) {
            return false;
        }

        final EngineModelDesignator that = (EngineModelDesignator) other;
        return Objects.equals(this.value, that.value);
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
