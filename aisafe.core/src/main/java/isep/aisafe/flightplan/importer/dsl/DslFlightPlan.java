package isep.aisafe.flightplan.importer.dsl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DslFlightPlan {

    private final String identifier;
    private final String type;
    private final String aircraftRegistration;
    private final String pilotIdentifier;
    private final DslQuantity load;
    private final List<DslFlightLeg> legs;
    private final DslSourceLocation location;

    public DslFlightPlan(
            final String identifier,
            final String type,
            final String aircraftRegistration,
            final String pilotIdentifier,
            final DslQuantity load,
            final List<DslFlightLeg> legs,
            final DslSourceLocation location) {

        this.identifier = Objects.requireNonNull(identifier, "identifier cannot be null").trim();
        this.type = Objects.requireNonNull(type, "type cannot be null").trim().toUpperCase();
        this.aircraftRegistration = normalizeOptional(aircraftRegistration);
        this.pilotIdentifier = normalizeOptional(pilotIdentifier);
        this.load = load;
        this.legs = List.copyOf(Objects.requireNonNull(legs, "legs cannot be null"));
        this.location = Objects.requireNonNull(location, "location cannot be null");
    }

    public String identifier() {
        return identifier;
    }

    public String type() {
        return type;
    }

    public Optional<String> aircraftRegistration() {
        return Optional.ofNullable(aircraftRegistration);
    }

    public Optional<String> pilotIdentifier() {
        return Optional.ofNullable(pilotIdentifier);
    }

    public Optional<DslQuantity> load() {
        return Optional.ofNullable(load);
    }

    public List<DslFlightLeg> legs() {
        return legs;
    }

    public DslSourceLocation location() {
        return location;
    }

    private String normalizeOptional(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}
