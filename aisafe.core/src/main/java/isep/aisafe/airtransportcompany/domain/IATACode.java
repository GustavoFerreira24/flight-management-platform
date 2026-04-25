package isep.aisafe.airtransportcompany.domain;

import eapli.framework.domain.model.ValueObject;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IATACode implements ValueObject, Serializable {

    private String value;

    protected IATACode() {
    }

    private IATACode(final String value) {
        if (value == null || !value.trim().toUpperCase().matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("IATA code must have exactly 2 letters.");
        }

        this.value = value.trim().toUpperCase();
    }

    public static IATACode valueOf(final String value) {
        return new IATACode(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof IATACode)) {
            return false;
        }

        final IATACode that = (IATACode) other;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}