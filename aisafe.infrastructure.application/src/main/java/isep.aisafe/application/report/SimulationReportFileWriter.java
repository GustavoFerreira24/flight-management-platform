package isep.aisafe.application.report;

import isep.aisafe.report.domain.SimulationReport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Stores simulation reports in text files.
 */
public class SimulationReportFileWriter {

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * Writes a simulation report to the target directory.
     *
     * @param report report metadata
     * @param renderedReport report text
     * @param outputDirectory directory where the file will be created
     * @return created report file path
     */
    public Path write(
            final SimulationReport report,
            final String renderedReport,
            final Path outputDirectory) {

        Objects.requireNonNull(report, "report cannot be null");
        Objects.requireNonNull(renderedReport, "renderedReport cannot be null");
        Objects.requireNonNull(outputDirectory, "outputDirectory cannot be null");

        try {
            Files.createDirectories(outputDirectory);
            final Path outputFile = outputDirectory.resolve(fileName(report));
            return Files.writeString(outputFile, renderedReport, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to write simulation report file.", ex);
        }
    }

    private String fileName(final SimulationReport report) {
        return "simulation-report-"
                + safeIdentifier(report.simulationIdentifier())
                + "-"
                + FILE_TIMESTAMP.format(report.generatedAt())
                + ".txt";
    }

    private String safeIdentifier(final String identifier) {
        return identifier.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
