package isep.aisafe.flightplan.importer.dsl;

import java.util.List;
import java.util.Objects;

public final class DslFlightPlan {

    private final String identifier;
    private final String type;
    private final List<DslFlightLeg> legs;
    private final DslSourceLocation location;

    public DslFlightPlan(
            final String identifier,
            final String type,
            final List<DslFlightLeg> legs,
            final DslSourceLocation location) {

        this.identifier = Objects.requireNonNull(identifier, "identifier cannot be null").trim();
        this.type = Objects.requireNonNull(type, "type cannot be null").trim().toUpperCase();
        this.legs = List.copyOf(Objects.requireNonNull(legs, "legs cannot be null"));
        this.location = Objects.requireNonNull(location, "location cannot be null");
    }

    public String identifier() {
        return identifier;
    }

    public String type() {
        return type;
    }

    public List<DslFlightLeg> legs() {
        return legs;
    }

    public DslSourceLocation location() {
        return location;
    }
}
