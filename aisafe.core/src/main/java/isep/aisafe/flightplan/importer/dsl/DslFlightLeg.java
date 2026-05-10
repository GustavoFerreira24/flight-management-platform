package isep.aisafe.flightplan.importer.dsl;

import java.util.List;
import java.util.Objects;

public final class DslFlightLeg {

    private final DslFlightEndpoint departure;
    private final DslFlightEndpoint arrival;
    private final DslRoute route;
    private final List<DslSegment> segments;
    private final DslQuantity fuel;
    private final DslSourceLocation location;

    public DslFlightLeg(
            final DslFlightEndpoint departure,
            final DslFlightEndpoint arrival,
            final DslRoute route,
            final List<DslSegment> segments,
            final DslQuantity fuel,
            final DslSourceLocation location) {

        this.departure = Objects.requireNonNull(departure, "departure cannot be null");
        this.arrival = Objects.requireNonNull(arrival, "arrival cannot be null");
        this.route = Objects.requireNonNull(route, "route cannot be null");
        this.segments = List.copyOf(Objects.requireNonNull(segments, "segments cannot be null"));
        this.fuel = Objects.requireNonNull(fuel, "fuel cannot be null");
        this.location = Objects.requireNonNull(location, "location cannot be null");
    }

    public DslFlightEndpoint departure() {
        return departure;
    }

    public DslFlightEndpoint arrival() {
        return arrival;
    }

    public DslRoute route() {
        return route;
    }

    public List<DslSegment> segments() {
        return segments;
    }

    public DslQuantity fuel() {
        return fuel;
    }

    public DslSourceLocation location() {
        return location;
    }
}
