package isep.aisafe.aircraftmodel.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

import java.util.Optional;

public class InMemoryAircraftModelRepository
        extends InMemoryDomainRepository<AircraftModel, AircraftModelDesignator>
        implements AircraftModelRepository {

    @Override
    public Optional<AircraftModel> findByDesignator(final AircraftModelDesignator designator) {
        return matchOne(aircraftModel -> aircraftModel.designator().equals(designator));
    }
}
