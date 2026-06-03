package isep.aisafe.flight.domain;

import eapli.framework.domain.repositories.DomainRepository;
import isep.aisafe.flightroute.domain.FlightRoute;

import java.util.Optional;

/**
 * Repository for flight aggregates.
 */
public interface FlightRepository extends DomainRepository<FlightDesignator, Flight> {

    Optional<Flight> findByDesignator(FlightDesignator designator);

    Iterable<Flight> findByRoute(FlightRoute route);
}
