package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.flightplan.domain.FlightPlanIdentifier;
import isep.aisafe.flightplan.domain.FlightPlanTestReport;
import isep.aisafe.flightplan.domain.FlightPlanTestReportIdentifier;
import isep.aisafe.flightplan.domain.FlightPlanTestReportRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JPA implementation of the flight plan test report repository.
 */
class JpaFlightPlanTestReportRepository
        extends JpaAutoTxRepository<FlightPlanTestReport, FlightPlanTestReportIdentifier, FlightPlanTestReportIdentifier>
        implements FlightPlanTestReportRepository {

    JpaFlightPlanTestReportRepository(final TransactionalContext autoTx) {
        super(autoTx, "identifier");
    }

    JpaFlightPlanTestReportRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "identifier");
    }

    @Override
    public Optional<FlightPlanTestReport> findByIdentifier(final FlightPlanTestReportIdentifier identifier) {
        final Map<String, Object> params = new HashMap<>();
        params.put("identifier", identifier);

        return matchOne("e.identifier=:identifier", params);
    }

    @Override
    public Iterable<FlightPlanTestReport> findByFlightPlan(final FlightPlanIdentifier flightPlanIdentifier) {
        final Map<String, Object> params = new HashMap<>();
        params.put("flightPlanIdentifier", flightPlanIdentifier);

        return match("e.flightPlan.identifier=:flightPlanIdentifier", params);
    }
}
