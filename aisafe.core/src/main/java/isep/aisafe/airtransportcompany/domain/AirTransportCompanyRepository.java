package isep.aisafe.airtransportcompany.domain;

import eapli.framework.domain.repositories.DomainRepository;
import isep.aisafe.IATACode;
import isep.aisafe.ICAOCode;

import java.util.Optional;

/**
 * Repository for air transport company aggregates.
 */
public interface AirTransportCompanyRepository
        extends DomainRepository<IATACode, AirTransportCompany> {

    /**
     * Finds an air transport company by its IATA code.
     *
     * @param iataCode the IATA code to search for
     * @return the matching company, if it exists
     */
    Optional<AirTransportCompany> findByIataCode(IATACode iataCode);

    /**
     * Finds an air transport company by its ICAO code.
     *
     * @param icaoCode the ICAO code to search for
     * @return the matching company, if it exists
     */
    Optional<AirTransportCompany> findByIcaoCode(ICAOCode icaoCode);
}