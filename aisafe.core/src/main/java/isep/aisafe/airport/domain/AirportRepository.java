package isep.aisafe.airport.domain;

import eapli.framework.domain.repositories.DomainRepository;

/**
 * Repository for airport aggregates.
 */
public interface AirportRepository extends DomainRepository<AirportICAOCode, Airport> {
}