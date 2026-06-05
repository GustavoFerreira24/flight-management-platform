package isep.aisafe.flightplan.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;
import eapli.framework.validations.Preconditions;

import java.time.LocalDateTime;
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

    @Override
    public Iterable<FlightPlan> findByDepartureBetween(
            final LocalDateTime startInclusive,
            final LocalDateTime endExclusive) {

        Preconditions.nonNull(startInclusive, "Start date/time cannot be null.");
        Preconditions.nonNull(endExclusive, "End date/time cannot be null.");
        Preconditions.ensure(startInclusive.isBefore(endExclusive), "Start date/time must be before end date/time.");

        return match(flightPlan -> {
            final LocalDateTime departure = flightPlan.flight().departure().dateTime();
            return !departure.isBefore(startInclusive) && departure.isBefore(endExclusive);
        });
    }
}
