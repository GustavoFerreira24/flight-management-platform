package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.flightroute.domain.FlightRouteRepository;
import isep.aisafe.flightroute.domain.RouteDesignator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * JPA implementation of the flight route repository.
 */
class JpaFlightRouteRepository
        extends JpaAutoTxRepository<FlightRoute, RouteDesignator, RouteDesignator>
        implements FlightRouteRepository {

    JpaFlightRouteRepository(final TransactionalContext autoTx) {
        super(autoTx, "routeDesignator");
    }

    JpaFlightRouteRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "routeDesignator");
    }

    @Override
    public Optional<FlightRoute> findByRouteDesignator(final RouteDesignator routeDesignator) {
        final Map<String, Object> params = new HashMap<>();
        params.put("routeDesignator", routeDesignator);

        final Iterator<FlightRoute> matches = match("e.routeDesignator=:routeDesignator", params).iterator();

        if (matches.hasNext()) {
            return Optional.of(matches.next());
        }

        return Optional.empty();
    }

    @Override
    public Iterable<FlightRoute> findByCompany(final AirTransportCompany company) {
        final Map<String, Object> params = new HashMap<>();
        params.put("company", company);

        return match("e.company=:company", params);
    }
}
