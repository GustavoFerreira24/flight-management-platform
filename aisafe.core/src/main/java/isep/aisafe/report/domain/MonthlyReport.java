package isep.aisafe.report.domain;

import eapli.framework.validations.Preconditions;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

/**
 * Monthly operational report generated from AISafe data.
 */
public final class MonthlyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String title;
    private final YearMonth period;
    private final LocalDateTime generatedAt;
    private final List<ReportSection> sections;

    /**
     * Creates a monthly report.
     *
     * @param title report title
     * @param period report month
     * @param generatedAt generation timestamp
     * @param sections report sections
     */
    public MonthlyReport(
            final String title,
            final YearMonth period,
            final LocalDateTime generatedAt,
            final List<ReportSection> sections) {

        Preconditions.ensure(title != null && !title.isBlank(), "Report title is required.");
        Preconditions.nonNull(period, "Report period is required.");
        Preconditions.nonNull(generatedAt, "Report generation timestamp is required.");
        Preconditions.nonNull(sections, "Report sections are required.");

        this.title = title.trim();
        this.period = period;
        this.generatedAt = generatedAt;
        this.sections = List.copyOf(sections);
    }

    public String title() {
        return title;
    }

    public YearMonth period() {
        return period;
    }

    public LocalDateTime generatedAt() {
        return generatedAt;
    }

    public List<ReportSection> sections() {
        return sections;
    }
}
