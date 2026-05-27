package isep.aisafe.flightroute.domain;

import eapli.framework.domain.repositories.DomainRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;

import java.util.Optional;

/**
 * Repository for flight route aggregates.
 */
public interface FlightRouteRepository extends DomainRepository<RouteDesignator, FlightRoute> {

    /**
     * Finds a flight route by route designator.
     *
     * @param routeDesignator the route designator
     * @return the matching flight route, if it exists
     */
    Optional<FlightRoute> findByRouteDesignator(RouteDesignator routeDesignator);

    /**
     * Finds flight routes belonging to an air transport company.
     *
     * @param company the air transport company
     * @return flight routes owned by the company
     */
    Iterable<FlightRoute> findByCompany(AirTransportCompany company);
}
