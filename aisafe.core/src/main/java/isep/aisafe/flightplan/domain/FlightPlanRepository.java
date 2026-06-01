package isep.aisafe.flightplan.domain;

import eapli.framework.domain.repositories.DomainRepository;

import java.util.Optional;

/**
 * Repository for flight plan aggregates.
 */
public interface FlightPlanRepository extends DomainRepository<FlightPlanIdentifier, FlightPlan> {

    Optional<FlightPlan> findByIdentifier(FlightPlanIdentifier identifier);
}
