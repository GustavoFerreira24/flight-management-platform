package isep.aisafe.application.flightmanagement;

import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanIdentifier;
import isep.aisafe.flightplan.domain.FlightPlanRepository;
import isep.aisafe.flightplan.domain.FlightPlanStatus;
import isep.aisafe.flightplan.domain.FlightPlanTestReport;
import isep.aisafe.flightplan.domain.FlightPlanTestReportIdentifier;
import isep.aisafe.flightplan.domain.FlightPlanTestReportRepository;
import isep.aisafe.flightplan.domain.InMemoryFlightPlanTestReportRepository;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Application use case for US085: test and validate a pilot's flight plan.
 */
public class TestFlightPlanController {

    private final AisafeAuthorizationService authz;
    private final FlightPlanRepository flightPlanRepository;
    private final FlightPlanTestReportRepository flightPlanTestReportRepository;
    private static final DateTimeFormatter REPORT_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    /**
     * Creates the controller with production services.
     */
    public TestFlightPlanController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().flightPlans(),
                PersistenceContext.repositories().flightPlanTestReports()
        );
    }

    TestFlightPlanController(
            final AisafeAuthorizationService authz,
            final FlightPlanRepository flightPlanRepository) {

        this(authz, flightPlanRepository, new InMemoryFlightPlanTestReportRepository());
    }

    TestFlightPlanController(
            final AisafeAuthorizationService authz,
            final FlightPlanRepository flightPlanRepository,
            final FlightPlanTestReportRepository flightPlanTestReportRepository) {

        this.authz = Objects.requireNonNull(authz, "authz cannot be null");
        this.flightPlanRepository = Objects.requireNonNull(
                flightPlanRepository,
                "flightPlanRepository cannot be null"
        );
        this.flightPlanTestReportRepository = Objects.requireNonNull(
                flightPlanTestReportRepository,
                "flightPlanTestReportRepository cannot be null"
        );
    }

    /**
     * Authorizes access to the flight plan test use case.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT);
    }

    /**
     * Lists the flight plans owned by the currently authenticated pilot and available for testing.
     *
     * @return authenticated pilot flight plans available for testing, ordered by departure date/time
     */
    public List<FlightPlan> authenticatedPilotFlightPlans() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT);

        final String authenticatedEmail = authz.authenticatedEmail();
        final List<FlightPlan> flightPlans = new ArrayList<>();

        for (final FlightPlan flightPlan : flightPlanRepository.findAll()) {
            if (belongsToAuthenticatedPilot(flightPlan, authenticatedEmail)
                    && flightPlan.canBeTested()) {
                flightPlans.add(flightPlan);
            }
        }

        flightPlans.sort(Comparator
                .comparing(FlightPlan::flightDepartureDateTime)
                .thenComparing(flightPlan -> flightPlan.identity().toString()));

        return List.copyOf(flightPlans);
    }

    /**
     * Tests an existing flight plan using the persisted domain data needed by the simulation.
     *
     * @param flightPlanIdentifierText selected flight plan identifier
     * @return test result with success or validation messages
     */
    public TestFlightPlanResult testFlightPlan(final String flightPlanIdentifierText) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT);
        ensureNotBlank(flightPlanIdentifierText, "Flight plan identifier is required.");

        final FlightPlanIdentifier identifier = FlightPlanIdentifier.valueOf(flightPlanIdentifierText);
        FlightPlan flightPlan = flightPlanRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new IllegalArgumentException("Flight plan not found."));

        ensureFlightPlanBelongsToAuthenticatedPilot(flightPlan);

        final List<String> stateMessages = validateTestState(flightPlan);
        if (!stateMessages.isEmpty()) {
            return TestFlightPlanResult.failure(flightPlan.status(), stateMessages);
        }

        final FlightPlanStatus initialStatus = flightPlan.status();
        final String testedBy = authz.authenticatedEmail();

        if (flightPlan.status() == FlightPlanStatus.DRAFT) {
            flightPlan.markUnderValidation();
            flightPlan = flightPlanRepository.save(flightPlan);
        }

        final List<String> validationMessages = validateOperationalRules(flightPlan);
        if (!validationMessages.isEmpty()) {
            flightPlan.reject();
            final FlightPlan savedFlightPlan = flightPlanRepository.save(flightPlan);
            final FlightPlanTestReport report = saveReport(
                    savedFlightPlan,
                    testedBy,
                    false,
                    initialStatus,
                    validationMessages
            );

            return TestFlightPlanResult.failure(
                    savedFlightPlan.status(),
                    validationMessages,
                    report.identity().toString()
            );
        }

        flightPlan.validate();
        final FlightPlan savedFlightPlan = flightPlanRepository.save(flightPlan);
        final List<String> successMessages = successMessages(savedFlightPlan);
        final FlightPlanTestReport report = saveReport(
                savedFlightPlan,
                testedBy,
                true,
                initialStatus,
                successMessages
        );

        return TestFlightPlanResult.success(
                savedFlightPlan.status(),
                successMessages,
                report.identity().toString()
        );
    }

    private FlightPlanTestReport saveReport(
            final FlightPlan flightPlan,
            final String testedBy,
            final boolean successful,
            final FlightPlanStatus initialStatus,
            final List<String> messages) {

        final LocalDateTime testedAt = LocalDateTime.now();
        final FlightPlanTestReport report = new FlightPlanTestReport(
                FlightPlanTestReportIdentifier.valueOf(reportIdentifier(flightPlan, testedAt)),
                flightPlan,
                testedAt,
                testedBy,
                successful,
                initialStatus,
                FlightPlanStatus.UNDER_VALIDATION,
                flightPlan.status(),
                messages
        );

        return flightPlanTestReportRepository.save(report);
    }

    private String reportIdentifier(final FlightPlan flightPlan, final LocalDateTime testedAt) {
        return "FPT-" + flightPlan.identity() + "-" + REPORT_TIMESTAMP_FORMAT.format(testedAt);
    }

    private List<String> validateTestState(final FlightPlan flightPlan) {
        if (flightPlan.status() == FlightPlanStatus.VALIDATED) {
            return List.of("Flight plan is already validated.");
        }

        if (flightPlan.status() == FlightPlanStatus.REJECTED) {
            return List.of("Flight plan is rejected and cannot be tested.");
        }

        if (!flightPlan.canBeTested()) {
            return List.of("Flight plan status does not allow testing.");
        }

        return List.of();
    }

    private List<String> validateOperationalRules(final FlightPlan flightPlan) {
        final List<String> messages = new ArrayList<>();

        if (!flightPlan.flight().route().isActive()) {
            messages.add("Flight route is not active.");
        }

        if (!flightPlan.aircraft().canBeUsedForFlights()) {
            messages.add("Aircraft cannot be used for flights.");
        }

        if (!flightPlan.aircraft().company().sameAs(flightPlan.flight().route().company())) {
            messages.add("Aircraft does not belong to the route's air transport company.");
        }

        if (!flightPlan.pilot().pilotStatus().isValid(flightPlan.pilot().certification())) {
            messages.add("Pilot is not active.");
        }

        if (!flightPlan.pilot().companyIATACode().equals(flightPlan.flight().route().company().iataCode())) {
            messages.add("Pilot does not belong to the route's air transport company.");
        }

        if (!pilotCertifiedForAircraftModel(flightPlan)) {
            messages.add("Pilot is not certified for the aircraft model.");
        }

        if (!flightPlan.aircraft().aircraftModel().hasCertifiedEngineModel(flightPlan.aircraft().engineModel())) {
            messages.add("Aircraft engine model is not certified for the aircraft model.");
        }

        if (flightPlan.fuelQuantity().amount() <= 0) {
            messages.add("Flight plan fuel quantity must be greater than zero.");
        }

        return messages;
    }

    private boolean pilotCertifiedForAircraftModel(final FlightPlan flightPlan) {
        final String aircraftModelDesignator = flightPlan.aircraft().aircraftModel().designator().toString();
        final String aircraftModelName = aircraftModelDesignator.split("\\|", 2)[0].trim();

        for (AircraftModelDesignator certifiedModel : flightPlan.pilot().certifiedAircraftModelsDesignators()) {
            final String certifiedDesignator = certifiedModel.toString();
            final String certifiedModelName = certifiedDesignator.split("\\|", 2)[0].trim();

            if (certifiedDesignator.equalsIgnoreCase(aircraftModelDesignator)
                    || certifiedModelName.equalsIgnoreCase(aircraftModelName)) {
                return true;
            }
        }

        return false;
    }

    private List<String> successMessages(final FlightPlan flightPlan) {
        final List<String> messages = new ArrayList<>();

        messages.add("Flight plan validated successfully.");
        messages.add("Flight: " + flightPlan.flightDesignator());
        messages.add("Aircraft: " + flightPlan.aircraftRegistration());
        messages.add("Pilot: " + flightPlan.pilotIdentifier());
        messages.add("Fuel: " + flightPlan.fuelQuantity());
        messages.add("Weather data records associated: " + flightPlan.weatherDataCount());

        if (!flightPlan.hasWeatherData()) {
            messages.add("No persisted weather data is associated; standard no-weather assumptions were used.");
        }

        return messages;
    }

    private void ensureFlightPlanBelongsToAuthenticatedPilot(final FlightPlan flightPlan) {
        final String authenticatedEmail = authz.authenticatedEmail();

        if (!belongsToAuthenticatedPilot(flightPlan, authenticatedEmail)) {
            throw new IllegalArgumentException("Flight plan does not belong to authenticated pilot.");
        }
    }

    private boolean belongsToAuthenticatedPilot(final FlightPlan flightPlan, final String authenticatedEmail) {
        final String flightPlanPilotEmail = flightPlan.pilot().email().toString();
        return flightPlanPilotEmail.equalsIgnoreCase(authenticatedEmail);
    }

    private void ensureNotBlank(final String value, final String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
