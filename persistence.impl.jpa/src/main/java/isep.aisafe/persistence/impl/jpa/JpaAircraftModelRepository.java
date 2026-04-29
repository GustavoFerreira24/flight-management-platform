package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelRepository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

class JpaAircraftModelRepository
        extends JpaAutoTxRepository<AircraftModel, AircraftModelDesignator, AircraftModelDesignator>
        implements AircraftModelRepository {

    JpaAircraftModelRepository(final TransactionalContext autoTx) {
        super(autoTx, "designator");
    }

    JpaAircraftModelRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "designator");
    }

    @Override
    public Optional<AircraftModel> findByDesignator(final AircraftModelDesignator designator) {
        final Map<String, Object> params = new HashMap<>();
        params.put("designator", designator);
        final Iterator<AircraftModel> matches = match("e.designator=:designator", params).iterator();
        if (matches.hasNext()) {
            return Optional.of(matches.next());
        }
        return Optional.empty();
    }
}
