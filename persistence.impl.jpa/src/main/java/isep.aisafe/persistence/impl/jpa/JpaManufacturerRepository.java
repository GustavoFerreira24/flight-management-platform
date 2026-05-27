package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.manufacturer.domain.Manufacturer;
import isep.aisafe.manufacturer.domain.ManufacturerDesignator;
import isep.aisafe.manufacturer.domain.ManufacturerRepository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

class JpaManufacturerRepository
        extends JpaAutoTxRepository<Manufacturer, ManufacturerDesignator, ManufacturerDesignator>
        implements ManufacturerRepository {

    JpaManufacturerRepository(final TransactionalContext autoTx) {
        super(autoTx, "designator");
    }

    JpaManufacturerRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "designator");
    }

    @Override
    public Optional<Manufacturer> findByDesignator(final ManufacturerDesignator designator) {
        final Map<String, Object> params = new HashMap<>();
        params.put("designator", designator);

        final Iterator<Manufacturer> matches = match("e.designator=:designator", params).iterator();

        if (matches.hasNext()) {
            return Optional.of(matches.next());
        }

        return Optional.empty();
    }
}
