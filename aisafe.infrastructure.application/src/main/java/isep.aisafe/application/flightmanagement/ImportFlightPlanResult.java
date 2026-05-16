package isep.aisafe.application.flightmanagement;

import isep.aisafe.flightplan.importer.ImportedFlightPlan;
import isep.aisafe.flightplan.importer.dsl.FlightPlanDslError;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Result of importing a flight plan file through the Core Flight DSL pipeline.
 */
public final class ImportFlightPlanResult {

    private final ImportedFlightPlan importedFlightPlan;
    private final List<FlightPlanDslError> errors;

    private ImportFlightPlanResult(
            final ImportedFlightPlan importedFlightPlan,
            final List<FlightPlanDslError> errors) {

        this.importedFlightPlan = importedFlightPlan;
        this.errors = List.copyOf(Objects.requireNonNull(errors, "errors cannot be null"));
    }

    public static ImportFlightPlanResult success(final ImportedFlightPlan importedFlightPlan) {
        return new ImportFlightPlanResult(
                Objects.requireNonNull(importedFlightPlan, "importedFlightPlan cannot be null"),
                List.of()
        );
    }

    public static ImportFlightPlanResult failure(final List<FlightPlanDslError> errors) {
        if (Objects.requireNonNull(errors, "errors cannot be null").isEmpty()) {
            throw new IllegalArgumentException("errors cannot be empty for an import failure");
        }

        return new ImportFlightPlanResult(null, errors);
    }

    public boolean isImported() {
        return importedFlightPlan != null;
    }

    public Optional<ImportedFlightPlan> importedFlightPlan() {
        return Optional.ofNullable(importedFlightPlan);
    }

    public List<FlightPlanDslError> errors() {
        return errors;
    }
}
