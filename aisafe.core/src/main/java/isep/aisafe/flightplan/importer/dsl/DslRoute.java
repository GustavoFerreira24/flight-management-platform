package isep.aisafe.flightplan.importer.dsl;

import java.util.Objects;

public final class DslRoute {

    private final String originAirportCode;
    private final String destinationAirportCode;
    private final DslSourceLocation location;

    public DslRoute(final String originAirportCode, final String destinationAirportCode, final DslSourceLocation location) {
        this.originAirportCode = Objects.requireNonNull(originAirportCode, "originAirportCode cannot be null").trim();
        this.destinationAirportCode = Objects.requireNonNull(destinationAirportCode, "destinationAirportCode cannot be null").trim();
        this.location = Objects.requireNonNull(location, "location cannot be null");
    }

    public String originAirportCode() {
        return originAirportCode;
    }

    public String destinationAirportCode() {
        return destinationAirportCode;
    }

    public DslSourceLocation location() {
        return location;
    }
}
