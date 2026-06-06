package isep.aisafe.application.report;

import isep.aisafe.report.domain.SimulationFlightResult;
import isep.aisafe.report.domain.SimulationReport;
import isep.aisafe.report.domain.SimulationSafetyViolation;

import java.util.Objects;

/**
 * Renders simulation reports as plain text.
 */
public class SimulationReportRenderer {

    /**
     * Renders a simulation report.
     *
     * @param report report to render
     * @return plain text report
     */
    public String render(final SimulationReport report) {
        Objects.requireNonNull(report, "report cannot be null");

        final StringBuilder builder = new StringBuilder();
        builder.append("AISafe Simulation Report").append(System.lineSeparator());
        builder.append("Simulation: ").append(report.simulationIdentifier()).append(System.lineSeparator());
        builder.append("Generated at: ").append(report.generatedAt()).append(System.lineSeparator());
        builder.append("Validation result: ").append(report.passedValidation() ? "PASS" : "FAIL")
                .append(System.lineSeparator());

        builder.append(System.lineSeparator());
        builder.append("Summary").append(System.lineSeparator());
        builder.append("-------").append(System.lineSeparator());
        builder.append("Total flights: ").append(report.totalFlights()).append(System.lineSeparator());
        builder.append("Safety violations: ").append(report.safetyViolations().size()).append(System.lineSeparator());

        builder.append(System.lineSeparator());
        builder.append("Flight Execution Status").append(System.lineSeparator());
        builder.append("-----------------------").append(System.lineSeparator());
        if (report.flightResults().isEmpty()) {
            builder.append("No flight execution results were provided.").append(System.lineSeparator());
        } else {
            for (final SimulationFlightResult flightResult : report.flightResults()) {
                builder.append(flightResult.flightIdentifier())
                        .append(" - ")
                        .append(flightResult.executionStatus())
                        .append(System.lineSeparator());
            }
        }

        builder.append(System.lineSeparator());
        builder.append("Safety Violations").append(System.lineSeparator());
        builder.append("-----------------").append(System.lineSeparator());
        if (report.safetyViolations().isEmpty()) {
            builder.append("No safety violations detected.").append(System.lineSeparator());
        } else {
            for (final SimulationSafetyViolation violation : report.safetyViolations()) {
                builder.append(violation.timestamp())
                        .append(" | ")
                        .append(violation.position())
                        .append(" | ")
                        .append(violation.description())
                        .append(System.lineSeparator());
            }
        }

        return builder.toString();
    }
}
