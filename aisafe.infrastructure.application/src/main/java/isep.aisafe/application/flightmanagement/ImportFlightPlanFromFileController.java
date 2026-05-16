package isep.aisafe.application.flightmanagement;

import isep.aisafe.flightplan.importer.ImportedFlightPlan;
import isep.aisafe.flightplan.importer.ImportedFlightPlanRepository;
import isep.aisafe.flightplan.importer.InMemoryImportedFlightPlanRepository;
import isep.aisafe.flightplan.importer.dsl.FlightPlanDslSyntaxValidator;
import isep.aisafe.flightplan.importer.dsl.FlightPlanParseResult;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Application use case for US081: import a flight plan from a Core Flight DSL file.
 */
public class ImportFlightPlanFromFileController {

    private static final ImportedFlightPlanRepository DEFAULT_REPOSITORY = new InMemoryImportedFlightPlanRepository();

    private final AisafeAuthorizationService authz;
    private final FlightPlanDslSyntaxValidator validator;
    private final ImportedFlightPlanRepository repository;
    private final Clock clock;

    /**
     * Creates the controller with production services.
     */
    public ImportFlightPlanFromFileController() {
        this(new AisafeAuthorizationService(), new FlightPlanDslSyntaxValidator(), DEFAULT_REPOSITORY, Clock.systemDefaultZone());
    }

    ImportFlightPlanFromFileController(
            final AisafeAuthorizationService authz,
            final FlightPlanDslSyntaxValidator validator,
            final ImportedFlightPlanRepository repository,
            final Clock clock) {

        this.authz = Objects.requireNonNull(authz, "authz cannot be null");
        this.validator = Objects.requireNonNull(validator, "validator cannot be null");
        this.repository = Objects.requireNonNull(repository, "repository cannot be null");
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    /**
     * Validates and imports a flight plan file.
     *
     * @param file the Core Flight DSL file to import
     * @return success with the imported flight plan, or failure with validation errors
     * @throws IOException when the file cannot be read
     */
    public ImportFlightPlanResult importFromFile(final Path file) throws IOException {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT);

        ensureReadableFile(file);

        final FlightPlanParseResult parseResult = validator.validate(file);
        if (!parseResult.isValid()) {
            return ImportFlightPlanResult.failure(parseResult.errors());
        }

        final ImportedFlightPlan importedFlightPlan = ImportedFlightPlan.from(
                parseResult.flightPlan(),
                file.toAbsolutePath().normalize(),
                LocalDateTime.now(clock)
        );

        return ImportFlightPlanResult.success(repository.save(importedFlightPlan));
    }

    public Iterable<ImportedFlightPlan> importedFlightPlans() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT);
        return repository.findAll();
    }

    private void ensureReadableFile(final Path file) {
        if (file == null) {
            throw new IllegalArgumentException("Flight plan file is required.");
        }

        if (!Files.isRegularFile(file) || !Files.isReadable(file)) {
            throw new IllegalArgumentException("Flight plan file must exist and be readable.");
        }
    }
}
