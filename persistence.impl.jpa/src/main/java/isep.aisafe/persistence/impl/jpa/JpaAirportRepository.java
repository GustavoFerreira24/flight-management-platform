package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.airport.domain.Airport;
import isep.aisafe.airport.domain.AirportIATACode;
import isep.aisafe.airport.domain.AirportICAOCode;
import isep.aisafe.airport.domain.AirportRepository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * JPA implementation of the airport repository.
 */
class JpaAirportRepository
        extends JpaAutoTxRepository<Airport, AirportICAOCode, AirportICAOCode>
        implements AirportRepository {

    JpaAirportRepository(final TransactionalContext autoTx) {
        super(autoTx, "icao");
    }

    JpaAirportRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "icao");
    }

    @Override
    public Optional<Airport> findByIataCode(final AirportIATACode iataCode) {
        final Map<String, Object> params = new HashMap<>();
        params.put("iata", iataCode);

        final Iterator<Airport> matches = match("e.iata=:iata", params).iterator();

        if (matches.hasNext()) {
            return Optional.of(matches.next());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Airport> findByIcaoCode(final AirportICAOCode icaoCode) {
        return ofIdentity(icaoCode);
    }
}
