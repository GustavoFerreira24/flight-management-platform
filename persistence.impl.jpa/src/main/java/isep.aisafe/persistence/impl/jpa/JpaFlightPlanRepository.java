package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanIdentifier;
import isep.aisafe.flightplan.domain.FlightPlanRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JPA implementation of the flight plan repository.
 */
class JpaFlightPlanRepository
        extends JpaAutoTxRepository<FlightPlan, FlightPlanIdentifier, FlightPlanIdentifier>
        implements FlightPlanRepository {

    JpaFlightPlanRepository(final TransactionalContext autoTx) {
        super(autoTx, "identifier");
    }

    JpaFlightPlanRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "identifier");
    }

    @Override
    public Optional<FlightPlan> findByIdentifier(final FlightPlanIdentifier identifier) {
        final Map<String, Object> params = new HashMap<>();
        params.put("identifier", identifier);

        return matchOne("e.identifier=:identifier", params);
    }
}
