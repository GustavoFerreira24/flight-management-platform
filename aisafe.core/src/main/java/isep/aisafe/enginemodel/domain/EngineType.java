package isep.aisafe.enginemodel.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EngineType implements ValueObject, Serializable {

    private String value;

    protected EngineType() {
    }

    private EngineType(final String value) {
        Preconditions.ensure(value != null && !value.trim().isEmpty(), "Engine type cannot be empty.");

        this.value = normalize(value);
    }

    public static EngineType valueOf(final String value) {
        return new EngineType(value);
    }

    private static String normalize(final String raw) {
        return raw.trim().replaceAll("\\s+", " ").toUpperCase();
    }

    public boolean sameAs(final String otherValue) {
        return this.value.equals(normalize(otherValue));
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof EngineType)) {
            return false;
        }

        final EngineType that = (EngineType) other;
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
