package isep.aisafe.enginemodel.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

import java.util.Optional;

public class InMemoryEngineModelRepository
        extends InMemoryDomainRepository<EngineModel, EngineModelDesignator>
        implements EngineModelRepository {

    @Override
    public Optional<EngineModel> findByDesignator(final EngineModelDesignator designator) {
        return matchOne(engineModel -> engineModel.designator().equals(designator));
    }
}
