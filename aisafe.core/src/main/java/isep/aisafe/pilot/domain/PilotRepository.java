package isep.aisafe.pilot.domain;

import eapli.framework.domain.repositories.DomainRepository;

import java.util.Optional;

/**
 * Repository for Pilot aggregates.
 */
public interface PilotRepository extends DomainRepository<PilotID, Pilot> {

    Optional<Pilot> findByCode(PilotID id);

    Optional<Pilot> findByEmail(String email);
}
