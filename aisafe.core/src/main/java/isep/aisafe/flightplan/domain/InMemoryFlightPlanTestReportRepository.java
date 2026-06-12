package isep.aisafe.flightplan.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

import java.util.Optional;

/**
 * In-memory implementation of the flight plan test report repository.
 */
public class InMemoryFlightPlanTestReportRepository
        extends InMemoryDomainRepository<FlightPlanTestReport, FlightPlanTestReportIdentifier>
        implements FlightPlanTestReportRepository {

    @Override
    public Optional<FlightPlanTestReport> findByIdentifier(final FlightPlanTestReportIdentifier identifier) {
        return matchOne(report -> report.identity().equals(identifier));
    }

    @Override
    public Iterable<FlightPlanTestReport> findByFlightPlan(final FlightPlanIdentifier flightPlanIdentifier) {
        return match(report -> report.flightPlan().identity().equals(flightPlanIdentifier));
    }

    /**
     * Removes all in-memory flight plan test reports.
     */
    public void clear() {
        data().clear();
    }
}
