package isep.aisafe.application.flightmanagement;

import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanIdentifier;
import isep.aisafe.flightplan.domain.FlightPlanRepository;
import isep.aisafe.flightplan.importer.dsl.DslFlightPlan;
import isep.aisafe.flightplan.importer.dsl.FlightPlanDslSyntaxValidator;
import isep.aisafe.flightplan.importer.dsl.FlightPlanParseResult;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.pilot.domain.PilotID;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Application use case for US085: test and validate a pilot's flight plan.
 */
public class TestFlightPlanController {

    private final AisafeAuthorizationService authz;
    private final FlightPlanRepository flightPlanRepository;
    private final FlightPlanDslSyntaxValidator validator;

    /**
     * Creates the controller with production services.
     */
    public TestFlightPlanController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().flightPlans(),
                new FlightPlanDslSyntaxValidator()
        );
    }

    TestFlightPlanController(
            final AisafeAuthorizationService authz,
            final FlightPlanRepository flightPlanRepository,
            final FlightPlanDslSyntaxValidator validator) {

        this.authz = Objects.requireNonNull(authz, "authz cannot be null");
        this.flightPlanRepository = Objects.requireNonNull(
                flightPlanRepository,
                "flightPlanRepository cannot be null"
        );
        this.validator = Objects.requireNonNull(validator, "validator cannot be null");
    }

    /**
     * Authorizes access to the flight plan test use case.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT);
    }

    /**
     * Tests an existing flight plan against the Flight DSL description in the given file.
     *
     * @param flightPlanIdentifierText selected flight plan identifier
     * @param dslFile Flight DSL file describing the planned flight
     * @return test result with success or validation messages
     * @throws IOException when the DSL file cannot be read
     */
    public TestFlightPlanResult testFlightPlan(
            final String flightPlanIdentifierText,
            final Path dslFile) throws IOException {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT);
        ensureNotBlank(flightPlanIdentifierText, "Flight plan identifier is required.");
        ensureReadableFile(dslFile);

        final FlightPlanIdentifier identifier = FlightPlanIdentifier.valueOf(flightPlanIdentifierText);
        final FlightPlan flightPlan = flightPlanRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new IllegalArgumentException("Flight plan not found."));

        ensureFlightPlanBelongsToAuthenticatedPilot(flightPlan);

        final FlightPlanParseResult parseResult = validator.validate(dslFile);
        if (!parseResult.isValid()) {
            return TestFlightPlanResult.failure(
                    flightPlan.testStatus(),
                    List.of("Flight DSL validation failed."),
                    parseResult.errors()
            );
        }

        final List<String> validationMessages = validateAgainstDsl(flightPlan, parseResult.flightPlans());
        if (!validationMessages.isEmpty()) {
            return TestFlightPlanResult.failure(flightPlan.testStatus(), validationMessages);
        }

        flightPlan.markTested();
        final FlightPlan savedFlightPlan = flightPlanRepository.save(flightPlan);
        return TestFlightPlanResult.success(savedFlightPlan.testStatus());
    }

    private List<String> validateAgainstDsl(
            final FlightPlan flightPlan,
            final List<DslFlightPlan> dslFlightPlans) {

        final String flightDesignator = flightPlan.flightDesignator();
        final DslFlightPlan matchingPlan = dslFlightPlans.stream()
                .filter(plan -> plan.identifier().equalsIgnoreCase(flightDesignator))
                .findFirst()
                .orElse(null);

        if (matchingPlan == null) {
            return List.of("Flight DSL file does not describe flight " + flightDesignator + ".");
        }

        final List<String> messages = new ArrayList<>();
        matchingPlan.aircraftRegistration()
                .filter(registration -> !registration.equalsIgnoreCase(flightPlan.aircraftRegistration()))
                .ifPresent(registration -> messages.add("Flight DSL aircraft does not match the selected flight plan."));
        matchingPlan.pilotIdentifier()
                .filter(pilot -> !pilot.equalsIgnoreCase(flightPlan.pilotIdentifier()))
                .ifPresent(pilot -> messages.add("Flight DSL pilot does not match the selected flight plan."));

        if (flightPlan.fuelQuantity().amount() <= 0) {
            messages.add("Flight plan fuel quantity must be greater than zero.");
        }

        return messages;
    }

    private void ensureFlightPlanBelongsToAuthenticatedPilot(final FlightPlan flightPlan) {
        final PilotID authenticatedPilot = PilotID.valueOf(authz.authenticatedUsername());
        if (!flightPlan.pilot().identity().equals(authenticatedPilot)) {
            throw new IllegalArgumentException("Flight plan does not belong to authenticated pilot.");
        }
    }

    private void ensureNotBlank(final String value, final String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void ensureReadableFile(final Path file) {
        if (file == null) {
            throw new IllegalArgumentException("Flight DSL file is required.");
        }

        if (!Files.isRegularFile(file) || !Files.isReadable(file)) {
            throw new IllegalArgumentException("Flight DSL file must exist and be readable.");
        }
    }
}
