package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import eapli.framework.validations.Preconditions;
import isep.aisafe.Application;
import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanIdentifier;
import isep.aisafe.flightplan.domain.FlightPlanRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JPA implementation of the flight plan repository.
 */
class JpaFlightPlanRepository
        extends JpaAutoTxRepository<FlightPlan, FlightPlanIdentifier, FlightPlanIdentifier>
        implements FlightPlanRepository {

    JpaFlightPlanRepository(final TransactionalContext autoTx) {
        super(autoTx, "identifier");
    }

    JpaFlightPlanRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "identifier");
    }

    @Override
    public Optional<FlightPlan> findByIdentifier(final FlightPlanIdentifier identifier) {
        final Map<String, Object> params = new HashMap<>();
        params.put("identifier", identifier);

        return matchOne("e.identifier=:identifier", params);
    }

    @Override
    public Iterable<FlightPlan> findByDepartureBetween(
            final LocalDateTime startInclusive,
            final LocalDateTime endExclusive) {

        Preconditions.nonNull(startInclusive, "Start date/time cannot be null.");
        Preconditions.nonNull(endExclusive, "End date/time cannot be null.");
        Preconditions.ensure(startInclusive.isBefore(endExclusive), "Start date/time must be before end date/time.");

        final Map<String, Object> params = new HashMap<>();
        params.put("startDay", startInclusive.toLocalDate());
        params.put("startTime", startInclusive.toLocalTime());
        params.put("endDay", endExclusive.toLocalDate());
        params.put("endTime", endExclusive.toLocalTime());

        return match(
                "(e.flight.departure.day > :startDay "
                        + "OR (e.flight.departure.day = :startDay AND e.flight.departure.time >= :startTime)) "
                        + "AND (e.flight.departure.day < :endDay "
                        + "OR (e.flight.departure.day = :endDay AND e.flight.departure.time < :endTime))",
                params
        );
    }
}
