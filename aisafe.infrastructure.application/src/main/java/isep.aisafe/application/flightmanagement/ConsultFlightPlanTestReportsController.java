package isep.aisafe.application.flightmanagement;

import isep.aisafe.flightplan.domain.FlightPlanTestReport;
import isep.aisafe.flightplan.domain.FlightPlanTestReportRepository;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Application use case for consulting persisted flight plan validation reports.
 */
public class ConsultFlightPlanTestReportsController {

    private final AisafeAuthorizationService authz;
    private final FlightPlanTestReportRepository reportRepository;

    /**
     * Creates the controller with production services.
     */
    public ConsultFlightPlanTestReportsController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().flightPlanTestReports()
        );
    }

    ConsultFlightPlanTestReportsController(
            final AisafeAuthorizationService authz,
            final FlightPlanTestReportRepository reportRepository) {

        this.authz = Objects.requireNonNull(authz, "authz cannot be null");
        this.reportRepository = Objects.requireNonNull(reportRepository, "reportRepository cannot be null");
    }

    /**
     * Lists persisted reports for flight plans owned by the authenticated pilot.
     *
     * @return reports ordered from most recent to oldest
     */
    public List<FlightPlanTestReport> authenticatedPilotReports() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT);

        final String authenticatedEmail = authz.authenticatedEmail();
        final List<FlightPlanTestReport> reports = new ArrayList<>();

        for (final FlightPlanTestReport report : reportRepository.findAll()) {
            if (report.flightPlan().pilot().email().toString().equalsIgnoreCase(authenticatedEmail)) {
                reports.add(report);
            }
        }

        reports.sort(Comparator
                .comparing(FlightPlanTestReport::testedAt).reversed()
                .thenComparing(report -> report.identity().toString()));

        return List.copyOf(reports);
    }
}
