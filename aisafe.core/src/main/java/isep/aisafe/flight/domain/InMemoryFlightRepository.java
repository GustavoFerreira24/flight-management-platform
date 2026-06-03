package isep.aisafe.flight.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;
import isep.aisafe.flightroute.domain.FlightRoute;

import java.util.Optional;

/**
 * In-memory implementation of the flight repository.
 */
public class InMemoryFlightRepository
        extends InMemoryDomainRepository<Flight, FlightDesignator>
        implements FlightRepository {

    @Override
    public Optional<Flight> findByDesignator(final FlightDesignator designator) {
        return matchOne(flight -> flight.designator().equals(designator));
    }

    @Override
    public Iterable<Flight> findByRoute(final FlightRoute route) {
        return match(flight -> flight.route().sameAs(route));
    }
}
