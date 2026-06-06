package isep.aisafe.application.report;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.report.domain.SimulationFlightResult;
import isep.aisafe.report.domain.SimulationReport;
import isep.aisafe.report.domain.SimulationSafetyViolation;

import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Application controller for US111: simulation report generation.
 */
public class GenerateSimulationReportController {

    private final AisafeAuthorizationService authz;
    private final SimulationReportGenerator generator;
    private final SimulationReportRenderer renderer;
    private final SimulationReportFileWriter fileWriter;
    private final Clock clock;

    /**
     * Creates the controller with production services.
     */
    public GenerateSimulationReportController() {
        this(
                new AisafeAuthorizationService(),
                new SimulationReportGenerator(),
                new SimulationReportRenderer(),
                new SimulationReportFileWriter(),
                Clock.systemDefaultZone()
        );
    }

    GenerateSimulationReportController(
            final AisafeAuthorizationService authz,
            final SimulationReportGenerator generator,
            final SimulationReportRenderer renderer,
            final SimulationReportFileWriter fileWriter,
            final Clock clock) {

        this.authz = Objects.requireNonNull(authz, "authz cannot be null");
        this.generator = Objects.requireNonNull(generator, "generator cannot be null");
        this.renderer = Objects.requireNonNull(renderer, "renderer cannot be null");
        this.fileWriter = Objects.requireNonNull(fileWriter, "fileWriter cannot be null");
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    /**
     * Authorizes access to simulation report generation.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_CONTROL);
    }

    /**
     * Generates a simulation report.
     *
     * @param simulationIdentifier simulation identifier
     * @param passedValidation final validation result
     * @param flightResults individual flight execution results
     * @param safetyViolations detected safety violations
     * @return simulation report
     */
    public SimulationReport generateSimulationReport(
            final String simulationIdentifier,
            final boolean passedValidation,
            final Iterable<SimulationFlightResult> flightResults,
            final Iterable<SimulationSafetyViolation> safetyViolations) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_CONTROL);
        return generator.generate(
                simulationIdentifier,
                passedValidation,
                flightResults,
                safetyViolations,
                LocalDateTime.now(clock)
        );
    }

    /**
     * Generates and renders a simulation report.
     *
     * @param simulationIdentifier simulation identifier
     * @param passedValidation final validation result
     * @param flightResults individual flight execution results
     * @param safetyViolations detected safety violations
     * @return plain text report
     */
    public String generateSimulationReportText(
            final String simulationIdentifier,
            final boolean passedValidation,
            final Iterable<SimulationFlightResult> flightResults,
            final Iterable<SimulationSafetyViolation> safetyViolations) {

        return renderer.render(generateSimulationReport(
                simulationIdentifier,
                passedValidation,
                flightResults,
                safetyViolations
        ));
    }

    /**
     * Generates, renders and stores a simulation report file.
     *
     * @param simulationIdentifier simulation identifier
     * @param passedValidation final validation result
     * @param flightResults individual flight execution results
     * @param safetyViolations detected safety violations
     * @param outputDirectory directory where the file will be created
     * @return created report file path
     */
    public Path generateSimulationReportFile(
            final String simulationIdentifier,
            final boolean passedValidation,
            final Iterable<SimulationFlightResult> flightResults,
            final Iterable<SimulationSafetyViolation> safetyViolations,
            final Path outputDirectory) {

        final SimulationReport report = generateSimulationReport(
                simulationIdentifier,
                passedValidation,
                flightResults,
                safetyViolations
        );
        return fileWriter.write(report, renderer.render(report), outputDirectory);
    }
}
