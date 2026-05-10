package isep.aisafe.flightplan.importer.dsl;

import java.util.Objects;

public final class DslWind {

    private final double directionDegrees;
    private final double speed;
    private final String unit;
    private final DslSourceLocation location;

    public DslWind(final double directionDegrees, final double speed, final String unit, final DslSourceLocation location) {
        this.directionDegrees = directionDegrees;
        this.speed = speed;
        this.unit = unit == null ? null : unit.trim();
        this.location = Objects.requireNonNull(location, "location cannot be null");
    }

    public double directionDegrees() {
        return directionDegrees;
    }

    public double speed() {
        return speed;
    }

    public String unit() {
        return unit;
    }

    public DslSourceLocation location() {
        return location;
    }
}
