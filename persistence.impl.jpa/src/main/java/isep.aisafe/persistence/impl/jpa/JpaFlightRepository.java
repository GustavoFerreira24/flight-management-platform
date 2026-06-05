package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.flight.domain.Flight;
import isep.aisafe.flight.domain.FlightDesignator;
import isep.aisafe.flight.domain.FlightRepository;
import isep.aisafe.flightroute.domain.FlightRoute;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JPA implementation of the flight repository.
 */
class JpaFlightRepository
        extends JpaAutoTxRepository<Flight, FlightDesignator, FlightDesignator>
        implements FlightRepository {

    JpaFlightRepository(final TransactionalContext autoTx) {
        super(autoTx, "designator");
    }

    JpaFlightRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "designator");
    }

    @Override
    public Optional<Flight> findByDesignator(final FlightDesignator designator) {
        final Map<String, Object> params = new HashMap<>();
        params.put("designator", designator);

        return matchOne("e.designator=:designator", params);
    }

    @Override
    public Iterable<Flight> findByRoute(final FlightRoute route) {
        final Map<String, Object> params = new HashMap<>();
        params.put("route", route);

        return match("e.route=:route", params);
    }
}
