package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * JPA implementation of the air transport company repository.
 */
class JpaAirTransportCompanyRepository
        extends JpaAutoTxRepository<AirTransportCompany, IATACode, IATACode>
        implements AirTransportCompanyRepository {

    /**
     * Creates a repository using an external transactional context.
     *
     * @param autoTx the transactional context
     */
    JpaAirTransportCompanyRepository(final TransactionalContext autoTx) {
        super(autoTx, "iataCode");
    }

    /**
     * Creates a repository using the given persistence unit.
     *
     * @param persistenceUnitName the persistence unit name
     */
    JpaAirTransportCompanyRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "iataCode");
    }

    /**
     * Finds an air transport company by its IATA code.
     *
     * @param iataCode the IATA code to search for
     * @return the matching company, if it exists
     */
    @Override
    public Optional<AirTransportCompany> findByIataCode(final IATACode iataCode) {
        final Map<String, Object> params = new HashMap<>();
        params.put("iataCode", iataCode);

        final Iterator<AirTransportCompany> matches =
                match("e.iataCode=:iataCode", params).iterator();

        if (matches.hasNext()) {
            return Optional.of(matches.next());
        }

        return Optional.empty();
    }

    /**
     * Finds an air transport company by its ICAO code.
     *
     * @param icaoCode the ICAO code to search for
     * @return the matching company, if it exists
     */
    @Override
    public Optional<AirTransportCompany> findByIcaoCode(final ICAOCode icaoCode) {
        final Map<String, Object> params = new HashMap<>();
        params.put("icaoCode", icaoCode);

        final Iterator<AirTransportCompany> matches =
                match("e.icaoCode=:icaoCode", params).iterator();

        if (matches.hasNext()) {
            return Optional.of(matches.next());
        }

        return Optional.empty();
    }
}