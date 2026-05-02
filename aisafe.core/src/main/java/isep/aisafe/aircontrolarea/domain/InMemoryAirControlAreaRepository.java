package isep.aisafe.aircontrolarea.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

import java.util.Optional;

/**
 * In-memory implementation of the air control area repository.
 */
public class InMemoryAirControlAreaRepository
        extends InMemoryDomainRepository<AirControlArea, AirControlAreaCode>
        implements AirControlAreaRepository {

    @Override
    public Optional<AirControlArea> findByCode(final AirControlAreaCode code) {
        return matchOne(area -> area.code().equals(code));
    }
}