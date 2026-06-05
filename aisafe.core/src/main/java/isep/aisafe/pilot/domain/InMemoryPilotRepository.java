package isep.aisafe.pilot.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

import java.util.Optional;

/**
 * InMemory implementation of the pilot repository for tests.
 */
public class InMemoryPilotRepository extends InMemoryDomainRepository<Pilot, PilotID> implements PilotRepository {

    @Override
    public Optional<Pilot> findByCode(PilotID id) {
        return matchOne(pilot -> pilot.identity().equals(id));
    }

    @Override
    public Optional<Pilot> findByEmail(String email) {
        return matchOne(pilot -> pilot.email().equals(email));
    }

    public void clear() {
        data().clear();
    }
}
