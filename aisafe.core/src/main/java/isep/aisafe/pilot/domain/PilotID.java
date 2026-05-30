package isep.aisafe.pilot.domain;

import eapli.framework.domain.model.DomainEntities;
import eapli.framework.domain.model.ValueObject;
import isep.aisafe.airport.domain.AirportICAOCode;

import java.io.Serializable;
import java.util.Objects;

public class PilotID implements ValueObject, Comparable<PilotID>, Serializable {

    private String value;

    /**
     * Required by ORM.
     */
    protected PilotID() {
    }

    private PilotID(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Pilot's ID code cannot be empty.");
        }

        final String normalizedValue = value.trim().toUpperCase();

        if (!normalizedValue.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Pilot's ID code must have exactly 3 letters.");
        }

        this.value = normalizedValue;
    }

    /**
     * Creates a Pilot ID from a text value.
     *
     * @param value the Pilot ID text
     * @return a new Pilot ID value object
     */
    public static PilotID valueOf(final String value) {
        return new PilotID(value);
    }


    /**
     * Compares this Pilot ID with another one.
     *
     * @param other the other Pilot ID
     * @return comparison result
     */
    @Override
    public int compareTo(final PilotID other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    /**
     * Checks equality by value.
     *
     * @param other the object to compare with
     * @return true if both Pilot ID have the same value
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PilotID)) {
            return false;
        }

        final PilotID pilotID = (PilotID) other;
        return Objects.equals(this.value, pilotID.value);
    }
}
