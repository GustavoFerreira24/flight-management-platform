package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineModelRepository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

class JpaEngineModelRepository
        extends JpaAutoTxRepository<EngineModel, EngineModelDesignator, EngineModelDesignator>
        implements EngineModelRepository {

    JpaEngineModelRepository(final TransactionalContext autoTx) {
        super(autoTx, "designator");
    }

    JpaEngineModelRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "designator");
    }

    @Override
    public Optional<EngineModel> findByDesignator(final EngineModelDesignator designator) {
        final Map<String, Object> params = new HashMap<>();
        params.put("designator", designator);
        final Iterator<EngineModel> matches = match("e.designator=:designator", params).iterator();
        if (matches.hasNext()) {
            return Optional.of(matches.next());
        }
        return Optional.empty();
    }
}
