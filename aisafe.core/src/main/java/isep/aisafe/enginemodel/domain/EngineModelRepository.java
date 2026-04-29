package isep.aisafe.enginemodel.domain;

import eapli.framework.domain.repositories.DomainRepository;

import java.util.Optional;

public interface EngineModelRepository extends DomainRepository<EngineModelDesignator, EngineModel> {

    Optional<EngineModel> findByDesignator(EngineModelDesignator designator);
}
