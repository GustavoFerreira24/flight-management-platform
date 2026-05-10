package isep.aisafe.flightplan.importer.dsl;

import java.util.Objects;

public final class DslAltitudeSlot {

    private final double minimum;
    private final double maximum;
    private final String unit;
    private final DslSourceLocation location;

    public DslAltitudeSlot(final double minimum, final double maximum, final String unit, final DslSourceLocation location) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.unit = unit == null ? null : unit.trim();
        this.location = Objects.requireNonNull(location, "location cannot be null");
    }

    public double minimum() {
        return minimum;
    }

    public double maximum() {
        return maximum;
    }

    public String unit() {
        return unit;
    }

    public DslSourceLocation location() {
        return location;
    }
}
