package isep.aisafe.flightplan.importer.dsl;

import java.util.Objects;
import java.util.Optional;

public final class DslSegment {

    private final DslCoordinate start;
    private final DslCoordinate end;
    private final Optional<DslQuantity> speed;
    private final Optional<DslAltitudeSlot> altitudeSlot;
    private final Optional<DslQuantity> width;
    private final Optional<DslWind> wind;
    private final DslSourceLocation location;

    public DslSegment(
            final DslCoordinate start,
            final DslCoordinate end,
            final Optional<DslQuantity> speed,
            final Optional<DslAltitudeSlot> altitudeSlot,
            final Optional<DslQuantity> width,
            final Optional<DslWind> wind,
            final DslSourceLocation location) {

        this.start = Objects.requireNonNull(start, "start cannot be null");
        this.end = Objects.requireNonNull(end, "end cannot be null");
        this.speed = Objects.requireNonNull(speed, "speed cannot be null");
        this.altitudeSlot = Objects.requireNonNull(altitudeSlot, "altitudeSlot cannot be null");
        this.width = Objects.requireNonNull(width, "width cannot be null");
        this.wind = Objects.requireNonNull(wind, "wind cannot be null");
        this.location = Objects.requireNonNull(location, "location cannot be null");
    }

    public DslCoordinate start() {
        return start;
    }

    public DslCoordinate end() {
        return end;
    }

    public Optional<DslQuantity> speed() {
        return speed;
    }

    public Optional<DslAltitudeSlot> altitudeSlot() {
        return altitudeSlot;
    }

    public Optional<DslQuantity> width() {
        return width;
    }

    public Optional<DslWind> wind() {
        return wind;
    }

    public DslSourceLocation location() {
        return location;
    }
}
