package isep.aisafe.application.report;

import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanStatus;
import isep.aisafe.flightplan.domain.FlightPlanTestStatus;
import isep.aisafe.report.domain.MonthlyReport;
import isep.aisafe.report.domain.ReportSection;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Builds monthly reports from flight plans.
 */
public class MonthlyReportGenerator {

    /**
     * Generates a monthly report for the provided flight plans.
     *
     * @param period report period
     * @param flightPlans flight plans departing during the period
     * @param generatedAt generation timestamp
     * @return generated monthly report
     */
    public MonthlyReport generate(
            final YearMonth period,
            final Iterable<FlightPlan> flightPlans,
            final LocalDateTime generatedAt) {

        Objects.requireNonNull(period, "period cannot be null");
        Objects.requireNonNull(flightPlans, "flightPlans cannot be null");
        Objects.requireNonNull(generatedAt, "generatedAt cannot be null");

        final List<FlightPlan> flightPlanList = toList(flightPlans);
        final List<ReportSection> sections = new ArrayList<>();
        sections.add(summarySection(flightPlanList));
        sections.add(statusSection(flightPlanList));
        sections.add(testStatusSection(flightPlanList));
        sections.add(detailsSection(flightPlanList));

        return new MonthlyReport("AISafe Monthly Report", period, generatedAt, sections);
    }

    private List<FlightPlan> toList(final Iterable<FlightPlan> flightPlans) {
        final List<FlightPlan> result = new ArrayList<>();
        for (final FlightPlan flightPlan : flightPlans) {
            result.add(flightPlan);
        }
        return result;
    }

    private ReportSection summarySection(final List<FlightPlan> flightPlans) {
        final Set<String> flights = new LinkedHashSet<>();
        final Set<String> aircraft = new LinkedHashSet<>();
        final Set<String> pilots = new LinkedHashSet<>();
        int withWeatherData = 0;

        for (final FlightPlan flightPlan : flightPlans) {
            flights.add(flightPlan.flightDesignator());
            aircraft.add(flightPlan.aircraftRegistration());
            pilots.add(flightPlan.pilotIdentifier());
            if (flightPlan.hasWeatherData()) {
                withWeatherData++;
            }
        }

        return new ReportSection("Summary", List.of(
                "Total flight plans: " + flightPlans.size(),
                "Unique flights: " + flights.size(),
                "Unique aircraft: " + aircraft.size(),
                "Unique pilots: " + pilots.size(),
                "Flight plans with weather data: " + withWeatherData,
                "Flight plans without weather data: " + (flightPlans.size() - withWeatherData)
        ));
    }

    private ReportSection statusSection(final List<FlightPlan> flightPlans) {
        final Map<FlightPlanStatus, Integer> counters = new EnumMap<>(FlightPlanStatus.class);
        for (final FlightPlanStatus status : FlightPlanStatus.values()) {
            counters.put(status, 0);
        }
        for (final FlightPlan flightPlan : flightPlans) {
            counters.compute(flightPlan.status(), (status, count) -> count + 1);
        }

        final List<String> lines = new ArrayList<>();
        for (final FlightPlanStatus status : FlightPlanStatus.values()) {
            lines.add(status + ": " + counters.get(status));
        }
        return new ReportSection("Flight Plan Status", lines);
    }

    private ReportSection testStatusSection(final List<FlightPlan> flightPlans) {
        final Map<FlightPlanTestStatus, Integer> counters = new EnumMap<>(FlightPlanTestStatus.class);
        for (final FlightPlanTestStatus status : FlightPlanTestStatus.values()) {
            counters.put(status, 0);
        }
        for (final FlightPlan flightPlan : flightPlans) {
            counters.compute(flightPlan.testStatus(), (status, count) -> count + 1);
        }

        final List<String> lines = new ArrayList<>();
        for (final FlightPlanTestStatus status : FlightPlanTestStatus.values()) {
            lines.add(status + ": " + counters.get(status));
        }
        return new ReportSection("Flight Plan Test Status", lines);
    }

    private ReportSection detailsSection(final List<FlightPlan> flightPlans) {
        if (flightPlans.isEmpty()) {
            return new ReportSection("Flight Plans", List.of("No flight plans found for this period."));
        }

        final List<String> lines = new ArrayList<>();
        lines.add("Identifier | Departure | Flight | Aircraft | Pilot | Status | Test Status | Weather Data");
        for (final FlightPlan flightPlan : flightPlans) {
            lines.add(String.format(
                    "%s | %s | %s | %s | %s | %s | %s | %d",
                    flightPlan.identifier(),
                    flightPlan.flightDepartureDateTime(),
                    flightPlan.flightDesignator(),
                    flightPlan.aircraftRegistration(),
                    flightPlan.pilotIdentifier(),
                    flightPlan.status(),
                    flightPlan.testStatus(),
                    flightPlan.weatherDataCount()
            ));
        }
        return new ReportSection("Flight Plans", lines);
    }
}
