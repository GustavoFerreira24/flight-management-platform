package isep.aisafe.report.domain;

import eapli.framework.validations.Preconditions;

import java.io.Serializable;
import java.util.List;

/**
 * A named section of a generated report.
 */
public final class ReportSection implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String title;
    private final List<String> lines;

    /**
     * Creates a report section.
     *
     * @param title section title
     * @param lines section content lines
     */
    public ReportSection(final String title, final List<String> lines) {
        Preconditions.ensure(title != null && !title.isBlank(), "Report section title is required.");
        Preconditions.nonNull(lines, "Report section lines are required.");

        this.title = title.trim();
        this.lines = List.copyOf(lines);
    }

    public String title() {
        return title;
    }

    public List<String> lines() {
        return lines;
    }
}
