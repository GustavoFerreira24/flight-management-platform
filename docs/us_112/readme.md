# US112 - Monthly Report Generation

## 1. Context

US112 is an EAPLI/backoffice use case for the Flight Control area. The goal is to generate a monthly operational report from information already stored in the Java application.

This implementation is read-only. It does not create flights, flight plans or simulation data. It queries persisted flight plans by their scheduled flight departure date/time and produces a consistent textual report.

## 2. Requirements

**US112** As a Flight Control Operator, I want to generate a monthly report so that flight plan activity for a selected month can be reviewed with a consistent structure.

Acceptance criteria implemented:

- The Flight Control Operator can request a report for a given year and month.
- Only users with `FLIGHT_CONTROL` permission can generate the report.
- The report uses flight plans whose associated flight departure is inside the selected month.
- The report includes summary statistics and flight plan details.
- The use case handles months with no flight plans.
- Automated tests validate authorization, period selection, statistics and rendering.

## 3. Analysis

The report is based on `FlightPlanRepository.findByDepartureBetween(...)`, introduced in US80. For a selected `YearMonth`, the controller computes:

```text
start = first day of month at 00:00
end = start plus one month
```

Then it retrieves flight plans with departures in `[start, end)`.

The report does not depend on SCOMP simulation reports. SCOMP reports remain part of the simulation component, while this US generates an EAPLI report using Java domain data.

## 4. Design

Main classes:

- `MonthlyReport`: immutable report object with title, period, generation timestamp and sections.
- `ReportSection`: immutable section with a title and lines.
- `MonthlyReportGenerator`: builds report sections from flight plans.
- `MonthlyReportRenderer`: renders the report as plain text.
- `GenerateMonthlyReportController`: coordinates authorization, period calculation, repository query and generation.
- `GenerateMonthlyReportUI`: console UI available in the Flight Control menu.

Class diagram: [class-diagram-112.puml](class-diagram-112.puml)

Generated sections:

- Summary
- Flight Plan Status
- Flight Plan Test Status
- Flight Plans

## 5. Implementation Notes

The controller enforces:

```java
AisafePermissions.FLIGHT_CONTROL
```

The monthly lookup uses:

```java
flightPlanRepository.findByDepartureBetween(start, end)
```

To avoid external code navigating through the internals of `FlightPlan`, derived display methods were added to the aggregate:

- `flightDepartureDateTime()`
- `flightDesignator()`
- `aircraftRegistration()`
- `pilotIdentifier()`
- `weatherDataCount()`
- `hasWeatherData()`

## 6. Tests

Automated tests cover:

- creation and validation of `ReportSection`;
- creation and validation of `MonthlyReport`;
- authorization for monthly report generation;
- report generation for the selected month only;
- empty-month report generation;
- invalid period rejection;
- authorization failure propagation.

Relevant test classes:

- `ReportSectionTest`
- `MonthlyReportTest`
- `GenerateMonthlyReportControllerTest`

## 7. Execution

Run the relevant tests with:

```bash
mvn -pl aisafe.infrastructure.application -am test
mvn -pl aisafe.app.backoffice.console -am test
```
