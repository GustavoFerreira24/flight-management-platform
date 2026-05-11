package isep.aisafe.aircraft.domain;

import eapli.framework.domain.repositories.DomainRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;

import java.util.Optional;

/**
 * Repository for aircraft aggregates.
 */
public interface AircraftRepository extends DomainRepository<AircraftRegistration, Aircraft> {

    /**
     * Finds an aircraft by registration.
     *
     * @param registration the aircraft registration
     * @return the matching aircraft, if it exists
     */
    Optional<Aircraft> findByRegistration(AircraftRegistration registration);

    /**
     * Finds aircraft belonging to an air transport company.
     *
     * @param company the air transport company
     * @return aircraft owned by the company
     */
    Iterable<Aircraft> findByCompany(AirTransportCompany company);
}