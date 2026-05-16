package isep.aisafe.flightplan.importer;

import java.util.Optional;

/**
 * Repository for flight plans accepted from external DSL files.
 */
public interface ImportedFlightPlanRepository {

    ImportedFlightPlan save(ImportedFlightPlan flightPlan);

    Optional<ImportedFlightPlan> findByIdentifier(String identifier);

    Iterable<ImportedFlightPlan> findAll();

    long count();
}
