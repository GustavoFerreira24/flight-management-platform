package isep.aisafe.flightroute.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;

import java.util.Optional;

/**
 * In-memory implementation of the flight route repository.
 */
public class InMemoryFlightRouteRepository
        extends InMemoryDomainRepository<FlightRoute, RouteDesignator>
        implements FlightRouteRepository {

    @Override
    public Optional<FlightRoute> findByRouteDesignator(final RouteDesignator routeDesignator) {
        return matchOne(route -> route.routeDesignator().equals(routeDesignator));
    }

    @Override
    public Iterable<FlightRoute> findByCompany(final AirTransportCompany company) {
        return match(route -> route.company().sameAs(company));
    }
}
