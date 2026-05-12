package isep.aisafe.airport.domain;

import eapli.framework.domain.repositories.DomainRepository;
import isep.aisafe.airport.domain.AirportIATACode;
import isep.aisafe.airport.domain.AirportICAOCode;

import java.util.Optional;

/**
 * Repository for airport aggregates.
 */
public interface AirportRepository extends DomainRepository<AirportICAOCode, Airport> {


    Optional<Airport> findByIataCode(AirportIATACode iataCode);

    Optional<Airport> findByIcaoCode(AirportICAOCode icaoCode);

}