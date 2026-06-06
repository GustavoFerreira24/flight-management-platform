package isep.aisafe.flightplan.domain;

import eapli.framework.domain.repositories.DomainRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for flight plan aggregates.
 */
public interface FlightPlanRepository extends DomainRepository<FlightPlanIdentifier, FlightPlan> {

    /**
     * Finds a flight plan by its business identifier.
     *
     * @param identifier flight plan identifier
     * @return flight plan, if it exists
     */
    Optional<FlightPlan> findByIdentifier(FlightPlanIdentifier identifier);

    /**
     * Finds flight plans whose flight departure is inside the given date/time interval.
     *
     * @param startInclusive interval start, inclusive
     * @param endExclusive interval end, exclusive
     * @return flight plans departing inside the interval
     */
    Iterable<FlightPlan> findByDepartureBetween(LocalDateTime startInclusive, LocalDateTime endExclusive);
}
