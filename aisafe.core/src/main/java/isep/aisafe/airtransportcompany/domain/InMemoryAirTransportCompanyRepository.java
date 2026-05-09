package isep.aisafe.airtransportcompany.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

import java.util.Optional;

/**
 * In-memory implementation of the air transport company repository.
 */
public class InMemoryAirTransportCompanyRepository
        extends InMemoryDomainRepository<AirTransportCompany, IATACode>
        implements AirTransportCompanyRepository {

    /**
     * Finds an air transport company by its IATA code.
     *
     * @param iataCode the IATA code to search for
     * @return the matching company, if it exists
     */
    @Override
    public Optional<AirTransportCompany> findByIataCode(final IATACode iataCode) {
        return matchOne(company -> company.iataCode().equals(iataCode));
    }

    /**
     * Finds an air transport company by its ICAO code.
     *
     * @param icaoCode the ICAO code to search for
     * @return the matching company, if it exists
     */
    @Override
    public Optional<AirTransportCompany> findByIcaoCode(final ICAOCode icaoCode) {
        return matchOne(company -> company.icaoCode().equals(icaoCode));
    }
}