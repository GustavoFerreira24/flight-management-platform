package isep.aisafe.report.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonthlyReportTest {

    @Test
    void ensureMonthlyReportCanBeCreated() {
        // Arrange
        final YearMonth period = YearMonth.of(2027, 1);
        final LocalDateTime generatedAt = LocalDateTime.of(2027, 2, 1, 9, 30);
        final ReportSection section = new ReportSection("Summary", List.of("Total flight plans: 1"));

        // Act
        final MonthlyReport report = new MonthlyReport(" AISafe Monthly Report ", period, generatedAt, List.of(section));

        // Assert
        assertEquals("AISafe Monthly Report", report.title());
        assertEquals(period, report.period());
        assertEquals(generatedAt, report.generatedAt());
        assertEquals(List.of(section), report.sections());
    }

    @Test
    void ensureMonthlyReportKeepsDefensiveCopyOfSections() {
        // Arrange
        final List<ReportSection> sections = new ArrayList<>();
        sections.add(new ReportSection("Summary", List.of("Total flight plans: 1")));

        // Act
        final MonthlyReport report = new MonthlyReport(
                "AISafe Monthly Report",
                YearMonth.of(2027, 1),
                LocalDateTime.of(2027, 2, 1, 9, 30),
                sections
        );
        sections.add(new ReportSection("Details", List.of("FP001")));

        // Assert
        assertEquals(1, report.sections().size());
        assertThrows(UnsupportedOperationException.class, () -> report.sections().add(
                new ReportSection("Other", List.of())
        ));
    }

    @Test
    void ensureRequiredMonthlyReportDataIsValidated() {
        // Arrange
        final YearMonth period = YearMonth.of(2027, 1);
        final LocalDateTime generatedAt = LocalDateTime.of(2027, 2, 1, 9, 30);
        final List<ReportSection> sections = List.of(new ReportSection("Summary", List.of()));

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new MonthlyReport(null, period, generatedAt, sections));
        assertThrows(IllegalArgumentException.class, () -> new MonthlyReport(" ", period, generatedAt, sections));
        assertThrows(IllegalArgumentException.class, () -> new MonthlyReport("Report", null, generatedAt, sections));
        assertThrows(IllegalArgumentException.class, () -> new MonthlyReport("Report", period, null, sections));
        assertThrows(IllegalArgumentException.class, () -> new MonthlyReport("Report", period, generatedAt, null));
    }
}
