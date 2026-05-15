package isep.aisafe.aircontrolarea.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that identifies an air control area.
 */
@Embeddable
public class AirControlAreaCode implements ValueObject, Comparable<AirControlAreaCode>, Serializable {

    private static final long serialVersionUID = 1L;

    private String value;

    protected AirControlAreaCode() {
    }

    private AirControlAreaCode(final String value) {
        Preconditions.ensure(value != null && !value.isBlank(), "Air control area code is required.");

        final String normalized = value.trim().toUpperCase();

        Preconditions.ensure(
                normalized.matches("[A-Z0-9]{2,10}"),
                "Air control area code must have 2 to 10 letters or digits."
        );

        this.value = normalized;
    }

    public static AirControlAreaCode valueOf(final String value) {
        return new AirControlAreaCode(value);
    }

    @Override
    public int compareTo(final AirControlAreaCode other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AirControlAreaCode)) {
            return false;
        }
        final AirControlAreaCode that = (AirControlAreaCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
