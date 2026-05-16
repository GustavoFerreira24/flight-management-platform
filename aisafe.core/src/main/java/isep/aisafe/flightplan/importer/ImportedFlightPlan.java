package isep.aisafe.flightplan.importer;

import isep.aisafe.flightplan.importer.dsl.DslFlightPlan;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Flight plan accepted from an external Core Flight DSL file.
 */
public final class ImportedFlightPlan {

    private final String identifier;
    private final String type;
    private final String aircraftRegistration;
    private final String pilotIdentifier;
    private final int legCount;
    private final Path sourceFile;
    private final LocalDateTime importedAt;
    private final DslFlightPlan dslFlightPlan;

    private ImportedFlightPlan(
            final DslFlightPlan dslFlightPlan,
            final Path sourceFile,
            final LocalDateTime importedAt) {

        this.dslFlightPlan = Objects.requireNonNull(dslFlightPlan, "dslFlightPlan cannot be null");
        this.identifier = dslFlightPlan.identifier();
        this.type = dslFlightPlan.type();
        this.aircraftRegistration = dslFlightPlan.aircraftRegistration().orElse(null);
        this.pilotIdentifier = dslFlightPlan.pilotIdentifier().orElse(null);
        this.legCount = dslFlightPlan.legs().size();
        this.sourceFile = Objects.requireNonNull(sourceFile, "sourceFile cannot be null");
        this.importedAt = Objects.requireNonNull(importedAt, "importedAt cannot be null");
    }

    public static ImportedFlightPlan from(
            final DslFlightPlan dslFlightPlan,
            final Path sourceFile,
            final LocalDateTime importedAt) {

        return new ImportedFlightPlan(dslFlightPlan, sourceFile, importedAt);
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

    public int legCount() {
        return legCount;
    }

    public Path sourceFile() {
        return sourceFile;
    }

    public LocalDateTime importedAt() {
        return importedAt;
    }

    public DslFlightPlan dslFlightPlan() {
        return dslFlightPlan;
    }
}
