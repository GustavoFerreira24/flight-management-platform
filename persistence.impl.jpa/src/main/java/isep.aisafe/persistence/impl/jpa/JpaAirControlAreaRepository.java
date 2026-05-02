package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * JPA implementation of the air control area repository.
 */
class JpaAirControlAreaRepository
        extends JpaAutoTxRepository<AirControlArea, AirControlAreaCode, AirControlAreaCode>
        implements AirControlAreaRepository {

    JpaAirControlAreaRepository(final TransactionalContext autoTx) {
        super(autoTx, "code");
    }

    JpaAirControlAreaRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "code");
    }

    @Override
    public Optional<AirControlArea> findByCode(final AirControlAreaCode code) {
        final Map<String, Object> params = new HashMap<>();
        params.put("code", code);

        final Iterator<AirControlArea> matches = match("e.code=:code", params).iterator();

        if (matches.hasNext()) {
            return Optional.of(matches.next());
        }

        return Optional.empty();
    }
}