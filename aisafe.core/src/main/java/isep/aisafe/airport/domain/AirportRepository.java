package isep.aisafe.airport.domain;

import eapli.framework.domain.repositories.DomainRepository;

import java.util.Optional;

/**
 * Repository for airport aggregates.
 */
public interface AirportRepository extends DomainRepository<AirportICAOCode, Airport> {


    Optional<Airport> findByIataCode(AirportIATACode iataCode);

    Optional<Airport> findByIcaoCode(AirportICAOCode icaoCode);

}