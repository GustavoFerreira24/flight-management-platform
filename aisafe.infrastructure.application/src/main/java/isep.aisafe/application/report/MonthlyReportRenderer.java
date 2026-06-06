package isep.aisafe.application.report;

import isep.aisafe.report.domain.MonthlyReport;
import isep.aisafe.report.domain.ReportSection;

import java.util.Objects;

/**
 * Renders monthly reports as plain text.
 */
public class MonthlyReportRenderer {

    /**
     * Renders a monthly report.
     *
     * @param report report to render
     * @return plain text report
     */
    public String render(final MonthlyReport report) {
        Objects.requireNonNull(report, "report cannot be null");

        final StringBuilder builder = new StringBuilder();
        builder.append(report.title()).append(System.lineSeparator());
        builder.append("Period: ").append(report.period()).append(System.lineSeparator());
        builder.append("Generated at: ").append(report.generatedAt()).append(System.lineSeparator());

        for (final ReportSection section : report.sections()) {
            builder.append(System.lineSeparator());
            builder.append(section.title()).append(System.lineSeparator());
            builder.append("-".repeat(section.title().length())).append(System.lineSeparator());
            for (final String line : section.lines()) {
                builder.append(line).append(System.lineSeparator());
            }
        }

        return builder.toString();
    }
}
