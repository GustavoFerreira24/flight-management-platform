package isep.aisafe.flightplan.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

import java.util.Optional;

/**
 * In-memory implementation of the flight plan repository.
 */
public class InMemoryFlightPlanRepository
        extends InMemoryDomainRepository<FlightPlan, FlightPlanIdentifier>
        implements FlightPlanRepository {

    @Override
    public Optional<FlightPlan> findByIdentifier(final FlightPlanIdentifier identifier) {
        return matchOne(flightPlan -> flightPlan.identifier().equals(identifier));
    }
}
