package isep.aisafe.flightplan.importer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * In-memory repository used until full flight plan persistence is introduced.
 */
public class InMemoryImportedFlightPlanRepository implements ImportedFlightPlanRepository {

    private final Map<String, ImportedFlightPlan> flightPlans = new LinkedHashMap<>();

    @Override
    public ImportedFlightPlan save(final ImportedFlightPlan flightPlan) {
        Objects.requireNonNull(flightPlan, "flightPlan cannot be null");

        flightPlans.put(flightPlan.identifier(), flightPlan);
        return flightPlan;
    }

    @Override
    public Optional<ImportedFlightPlan> findByIdentifier(final String identifier) {
        if (identifier == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(flightPlans.get(identifier));
    }

    @Override
    public Iterable<ImportedFlightPlan> findAll() {
        final List<ImportedFlightPlan> snapshot = new ArrayList<>(flightPlans.values());
        return List.copyOf(snapshot);
    }

    @Override
    public long count() {
        return flightPlans.size();
    }
}
