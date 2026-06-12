package isep.aisafe.flightplan.domain;

import eapli.framework.domain.repositories.DomainRepository;

import java.util.Optional;

/**
 * Repository for persisted flight plan test reports.
 */
public interface FlightPlanTestReportRepository
        extends DomainRepository<FlightPlanTestReportIdentifier, FlightPlanTestReport> {

    Optional<FlightPlanTestReport> findByIdentifier(FlightPlanTestReportIdentifier identifier);

    Iterable<FlightPlanTestReport> findByFlightPlan(FlightPlanIdentifier flightPlanIdentifier);
}
