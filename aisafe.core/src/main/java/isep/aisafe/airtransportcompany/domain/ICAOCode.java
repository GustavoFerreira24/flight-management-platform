package isep.aisafe.airtransportcompany.domain;

import eapli.framework.domain.model.ValueObject;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class ICAOCode implements ValueObject, Serializable, Comparable<ICAOCode> {

    private String value;

    protected ICAOCode() {
    }

    private ICAOCode(final String value) {
        if (value == null || !value.trim().toUpperCase().matches("[A-Z]{2,3}")) {
            throw new IllegalArgumentException("ICAO code must have 2 or 3 letters.");
        }

        this.value = value.trim().toUpperCase();
    }

    public static ICAOCode valueOf(final String value) {
        return new ICAOCode(value);
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
        if (!(other instanceof ICAOCode)) {
            return false;
        }

        final ICAOCode that = (ICAOCode) other;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(final ICAOCode other) {
        return this.value.compareTo(other.value);
    }
}
