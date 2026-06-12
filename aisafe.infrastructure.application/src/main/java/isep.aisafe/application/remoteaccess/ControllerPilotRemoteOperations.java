package isep.aisafe.application.remoteaccess;

import isep.aisafe.application.flightmanagement.InsertWeatherDataInFlightPlanController;
import isep.aisafe.application.flightmanagement.ConsultFlightPlanTestReportsController;
import isep.aisafe.application.flightmanagement.CreateFlightPlanController;
import isep.aisafe.application.flightmanagement.TestFlightPlanController;
import isep.aisafe.application.flightmanagement.TestFlightPlanResult;
import isep.aisafe.application.weather.ConsultWeatherDataController;
import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanTestReport;
import isep.aisafe.weatherdata.domain.WeatherData;

import java.util.List;
import java.util.Optional;

/**
 * Production adapter that delegates Pilot remote commands to existing EAPLI controllers.
 */
public class ControllerPilotRemoteOperations implements PilotRemoteOperations {

    private final ConsultWeatherDataController consultWeatherDataController;
    private final InsertWeatherDataInFlightPlanController insertWeatherDataInFlightPlanController;
    private final CreateFlightPlanController createFlightPlanController;
    private final TestFlightPlanController testFlightPlanController;
    private final ConsultFlightPlanTestReportsController consultFlightPlanTestReportsController;

    public ControllerPilotRemoteOperations() {
        this(
                new ConsultWeatherDataController(),
                new InsertWeatherDataInFlightPlanController(),
                new CreateFlightPlanController(),
                new TestFlightPlanController(),
                new ConsultFlightPlanTestReportsController()
        );
    }

    ControllerPilotRemoteOperations(
            final ConsultWeatherDataController consultWeatherDataController,
            final InsertWeatherDataInFlightPlanController insertWeatherDataInFlightPlanController,
            final CreateFlightPlanController createFlightPlanController,
            final TestFlightPlanController testFlightPlanController,
            final ConsultFlightPlanTestReportsController consultFlightPlanTestReportsController) {

        if (consultWeatherDataController == null) {
            throw new IllegalArgumentException("Consult weather data controller cannot be null.");
        }
        if (insertWeatherDataInFlightPlanController == null) {
            throw new IllegalArgumentException("Insert weather data controller cannot be null.");
        }
        if (createFlightPlanController == null) {
            throw new IllegalArgumentException("Create flight plan controller cannot be null.");
        }
        if (testFlightPlanController == null) {
            throw new IllegalArgumentException("Test flight plan controller cannot be null.");
        }
        if (consultFlightPlanTestReportsController == null) {
            throw new IllegalArgumentException("Consult flight plan test reports controller cannot be null.");
        }

        this.consultWeatherDataController = consultWeatherDataController;
        this.insertWeatherDataInFlightPlanController = insertWeatherDataInFlightPlanController;
        this.createFlightPlanController = createFlightPlanController;
        this.testFlightPlanController = testFlightPlanController;
        this.consultFlightPlanTestReportsController = consultFlightPlanTestReportsController;
    }

    @Override
    public String createFlightPlan(
            final String flightPlanIdentifier,
            final String flightDesignator,
            final String aircraftRegistration,
            final String pilotId,
            final String fuelAmount,
            final String fuelUnit) {

        final FlightPlan flightPlan = createFlightPlanController.createFlightPlan(
                flightPlanIdentifier,
                flightDesignator,
                aircraftRegistration,
                pilotId,
                Double.parseDouble(fuelAmount),
                fuelUnit
        );

        return "Flight plan created: " + formatFlightPlan(flightPlan);
    }

    @Override
    public String consultWeatherData(final String day, final String airControlAreaCode) {
        final Optional<WeatherData> weatherData = consultWeatherDataController.consultWeatherData(
                day,
                airControlAreaCode
        );

        return weatherData
                .map(this::formatWeatherData)
                .orElse("No weather data found.");
    }

    @Override
    public String insertWeatherData(
            final String flightPlanIdentifier,
            final String weatherDataDay,
            final String airControlAreaCode) {

        final FlightPlan flightPlan = insertWeatherDataInFlightPlanController.insertWeatherData(
                flightPlanIdentifier,
                weatherDataDay,
                airControlAreaCode
        );

        return "Weather data inserted in flight plan: " + flightPlan.identifier();
    }

    @Override
    public String listTestableFlightPlans() {
        final List<FlightPlan> flightPlans = testFlightPlanController.authenticatedPilotFlightPlans();
        if (flightPlans.isEmpty()) {
            return "No testable flight plans found.";
        }

        return joinFlightPlans(flightPlans);
    }

    @Override
    public String testFlightPlan(final String flightPlanIdentifier) {
        final TestFlightPlanResult result = testFlightPlanController.testFlightPlan(flightPlanIdentifier);
        return formatTestResult(result);
    }

    @Override
    public String consultFlightPlanTestReports() {
        final List<FlightPlanTestReport> reports = consultFlightPlanTestReportsController.authenticatedPilotReports();
        if (reports.isEmpty()) {
            return "No flight plan test reports found.";
        }

        final StringBuilder builder = new StringBuilder();
        for (final FlightPlanTestReport report : reports) {
            if (builder.length() > 0) {
                builder.append(" || ");
            }
            builder.append("Report: ").append(report.identity())
                    .append("; flightPlan=").append(report.flightPlan().identity())
                    .append("; testedAt=").append(report.testedAt())
                    .append("; result=").append(report.successful() ? "PASSED" : "FAILED")
                    .append("; finalStatus=").append(report.finalStatus())
                    .append("; messages=").append(String.join(", ", report.messages()));
        }
        return builder.toString();
    }

    private String formatWeatherData(final WeatherData weatherData) {
        return "Weather data: " + weatherData.identity()
                + "; day=" + weatherData.day()
                + "; area=" + weatherData.airControlAreaCode()
                + "; measurements=" + weatherData.measurements().size();
    }

    private String joinFlightPlans(final List<FlightPlan> flightPlans) {
        final StringBuilder builder = new StringBuilder();
        for (final FlightPlan flightPlan : flightPlans) {
            if (builder.length() > 0) {
                builder.append(" || ");
            }
            builder.append(formatFlightPlan(flightPlan));
        }
        return builder.toString();
    }

    private String formatFlightPlan(final FlightPlan flightPlan) {
        return flightPlan.identity()
                + "; flight=" + flightPlan.flightDesignator()
                + "; departure=" + flightPlan.flightDepartureDateTime()
                + "; aircraft=" + flightPlan.aircraftRegistration()
                + "; pilot=" + flightPlan.pilotIdentifier()
                + "; status=" + flightPlan.status()
                + "; weatherData=" + flightPlan.weatherDataCount();
    }

    private String formatTestResult(final TestFlightPlanResult result) {
        final StringBuilder builder = new StringBuilder();
        builder.append(result.isSuccessful() ? "Flight plan validation passed." : "Flight plan validation failed.")
                .append("; status=").append(result.status());

        result.reportIdentifier().ifPresent(report -> builder.append("; report=").append(report));

        builder.append("; messages=").append(String.join(", ", result.messages()));
        return builder.toString();
    }
}
