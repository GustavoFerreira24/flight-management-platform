package isep.aisafe.aircontrolarea.domain;

import eapli.framework.domain.repositories.DomainRepository;

import java.util.Optional;

/**
 * Repository for air control area aggregates.
 */
public interface AirControlAreaRepository extends DomainRepository<AirControlAreaCode, AirControlArea> {

    Optional<AirControlArea> findByCode(AirControlAreaCode code);
}