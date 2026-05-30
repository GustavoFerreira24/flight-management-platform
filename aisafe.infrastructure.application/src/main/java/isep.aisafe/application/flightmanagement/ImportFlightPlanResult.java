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

    private final List<ImportedFlightPlan> importedFlightPlans;
    private final List<FlightPlanDslError> errors;

    private ImportFlightPlanResult(
            final List<ImportedFlightPlan> importedFlightPlans,
            final List<FlightPlanDslError> errors) {

        this.importedFlightPlans = List.copyOf(Objects.requireNonNull(importedFlightPlans, "importedFlightPlans cannot be null"));
        this.errors = List.copyOf(Objects.requireNonNull(errors, "errors cannot be null"));
    }

    public static ImportFlightPlanResult success(final ImportedFlightPlan importedFlightPlan) {
        return success(List.of(Objects.requireNonNull(importedFlightPlan, "importedFlightPlan cannot be null")));
    }

    public static ImportFlightPlanResult success(final List<ImportedFlightPlan> importedFlightPlans) {
        if (Objects.requireNonNull(importedFlightPlans, "importedFlightPlans cannot be null").isEmpty()) {
            throw new IllegalArgumentException("importedFlightPlans cannot be empty for an import success");
        }

        return new ImportFlightPlanResult(
                importedFlightPlans,
                List.of()
        );
    }

    public static ImportFlightPlanResult failure(final List<FlightPlanDslError> errors) {
        if (Objects.requireNonNull(errors, "errors cannot be null").isEmpty()) {
            throw new IllegalArgumentException("errors cannot be empty for an import failure");
        }

        return new ImportFlightPlanResult(List.of(), errors);
    }

    public boolean isImported() {
        return !importedFlightPlans.isEmpty();
    }

    public Optional<ImportedFlightPlan> importedFlightPlan() {
        return importedFlightPlans.isEmpty() ? Optional.empty() : Optional.of(importedFlightPlans.getFirst());
    }

    public List<ImportedFlightPlan> importedFlightPlans() {
        return importedFlightPlans;
    }

    public List<FlightPlanDslError> errors() {
        return errors;
    }
}
