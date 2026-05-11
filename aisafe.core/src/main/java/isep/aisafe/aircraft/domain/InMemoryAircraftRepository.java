package isep.aisafe.aircraft.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;

import java.util.Optional;

/**
 * In-memory implementation of the aircraft repository.
 */
public class InMemoryAircraftRepository
        extends InMemoryDomainRepository<Aircraft, AircraftRegistration>
        implements AircraftRepository {

    @Override
    public Optional<Aircraft> findByRegistration(final AircraftRegistration registration) {
        return matchOne(aircraft -> aircraft.registration().equals(registration));
    }

    @Override
    public Iterable<Aircraft> findByCompany(final AirTransportCompany company) {
        return match(aircraft -> aircraft.company().sameAs(company));
    }
}