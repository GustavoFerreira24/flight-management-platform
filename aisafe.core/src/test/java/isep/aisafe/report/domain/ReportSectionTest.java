package isep.aisafe.report.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportSectionTest {

    @Test
    void ensureReportSectionCanBeCreated() {
        // Arrange
        final List<String> lines = List.of("Total flight plans: 2");

        // Act
        final ReportSection section = new ReportSection(" Summary ", lines);

        // Assert
        assertEquals("Summary", section.title());
        assertEquals(lines, section.lines());
    }

    @Test
    void ensureReportSectionKeepsDefensiveCopyOfLines() {
        // Arrange
        final List<String> lines = new ArrayList<>();
        lines.add("Initial line");

        // Act
        final ReportSection section = new ReportSection("Details", lines);
        lines.add("Mutated line");

        // Assert
        assertEquals(1, section.lines().size());
        assertThrows(UnsupportedOperationException.class, () -> section.lines().add("Another line"));
    }

    @Test
    void ensureRequiredReportSectionDataIsValidated() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> new ReportSection(null, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new ReportSection(" ", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new ReportSection("Summary", null));
    }
}
