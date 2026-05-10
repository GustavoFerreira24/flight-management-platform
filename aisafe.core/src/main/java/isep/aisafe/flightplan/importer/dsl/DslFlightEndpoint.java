package isep.aisafe.flightplan.importer.dsl;

import java.time.LocalDateTime;
import java.util.Objects;

public final class DslFlightEndpoint {

    private final String airportCode;
    private final LocalDateTime dateTime;
    private final DslSourceLocation location;

    public DslFlightEndpoint(final String airportCode, final LocalDateTime dateTime, final DslSourceLocation location) {
        this.airportCode = Objects.requireNonNull(airportCode, "airportCode cannot be null").trim();
        this.dateTime = Objects.requireNonNull(dateTime, "dateTime cannot be null");
        this.location = Objects.requireNonNull(location, "location cannot be null");
    }

    public String airportCode() {
        return airportCode;
    }

    public LocalDateTime dateTime() {
        return dateTime;
    }

    public DslSourceLocation location() {
        return location;
    }
}
