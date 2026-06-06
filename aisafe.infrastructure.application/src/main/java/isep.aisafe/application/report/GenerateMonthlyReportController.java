package isep.aisafe.application.report;

import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanRepository;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.report.domain.MonthlyReport;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Objects;

/**
 * Application controller for US112: monthly report generation.
 */
public class GenerateMonthlyReportController {

    private final AisafeAuthorizationService authz;
    private final FlightPlanRepository flightPlanRepository;
    private final MonthlyReportGenerator generator;
    private final MonthlyReportRenderer renderer;
    private final Clock clock;

    /**
     * Creates the controller with production services.
     */
    public GenerateMonthlyReportController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().flightPlans(),
                new MonthlyReportGenerator(),
                new MonthlyReportRenderer(),
                Clock.systemDefaultZone()
        );
    }

    GenerateMonthlyReportController(
            final AisafeAuthorizationService authz,
            final FlightPlanRepository flightPlanRepository,
            final MonthlyReportGenerator generator,
            final MonthlyReportRenderer renderer,
            final Clock clock) {

        this.authz = Objects.requireNonNull(authz, "authz cannot be null");
        this.flightPlanRepository = Objects.requireNonNull(flightPlanRepository, "flightPlanRepository cannot be null");
        this.generator = Objects.requireNonNull(generator, "generator cannot be null");
        this.renderer = Objects.requireNonNull(renderer, "renderer cannot be null");
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    /**
     * Authorizes access to monthly report generation.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_CONTROL);
    }

    /**
     * Generates a monthly report.
     *
     * @param year report year
     * @param month report month from 1 to 12
     * @return monthly report
     */
    public MonthlyReport generateMonthlyReport(final int year, final int month) {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_CONTROL);

        final YearMonth period = reportPeriod(year, month);
        final LocalDateTime start = period.atDay(1).atStartOfDay();
        final LocalDateTime end = start.plusMonths(1);
        final Iterable<FlightPlan> flightPlans = flightPlanRepository.findByDepartureBetween(start, end);

        return generator.generate(period, flightPlans, LocalDateTime.now(clock));
    }

    /**
     * Generates and renders a monthly report.
     *
     * @param year report year
     * @param month report month from 1 to 12
     * @return plain text monthly report
     */
    public String generateMonthlyReportText(final int year, final int month) {
        return renderer.render(generateMonthlyReport(year, month));
    }

    private YearMonth reportPeriod(final int year, final int month) {
        try {
            return YearMonth.of(year, month);
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException("Report period must contain a valid year and month.", ex);
        }
    }
}
