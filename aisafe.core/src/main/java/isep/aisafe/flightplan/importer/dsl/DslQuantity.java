package isep.aisafe.flightplan.importer.dsl;

import java.util.Objects;

public final class DslQuantity {

    private final double value;
    private final String unit;
    private final DslSourceLocation location;

    public DslQuantity(final double value, final String unit, final DslSourceLocation location) {
        this.value = value;
        this.unit = Objects.requireNonNull(unit, "unit cannot be null").trim();
        this.location = Objects.requireNonNull(location, "location cannot be null");
    }

    public double value() {
        return value;
    }

    public String unit() {
        return unit;
    }

    public DslSourceLocation location() {
        return location;
    }
}
