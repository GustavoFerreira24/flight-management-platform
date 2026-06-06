package isep.aisafe.application.remoteaccess;

import isep.aisafe.application.flightmanagement.InsertWeatherDataInFlightPlanController;
import isep.aisafe.application.weather.ConsultWeatherDataController;
import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.weatherdata.domain.WeatherData;

import java.util.Optional;

/**
 * Production adapter that delegates Pilot remote commands to existing EAPLI controllers.
 */
public class ControllerPilotRemoteOperations implements PilotRemoteOperations {

    private final ConsultWeatherDataController consultWeatherDataController;
    private final InsertWeatherDataInFlightPlanController insertWeatherDataInFlightPlanController;

    public ControllerPilotRemoteOperations() {
        this(
                new ConsultWeatherDataController(),
                new InsertWeatherDataInFlightPlanController()
        );
    }

    ControllerPilotRemoteOperations(
            final ConsultWeatherDataController consultWeatherDataController,
            final InsertWeatherDataInFlightPlanController insertWeatherDataInFlightPlanController) {

        if (consultWeatherDataController == null) {
            throw new IllegalArgumentException("Consult weather data controller cannot be null.");
        }
        if (insertWeatherDataInFlightPlanController == null) {
            throw new IllegalArgumentException("Insert weather data controller cannot be null.");
        }

        this.consultWeatherDataController = consultWeatherDataController;
        this.insertWeatherDataInFlightPlanController = insertWeatherDataInFlightPlanController;
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

    private String formatWeatherData(final WeatherData weatherData) {
        return "Weather data: " + weatherData.identity()
                + "; day=" + weatherData.day()
                + "; area=" + weatherData.airControlAreaCode()
                + "; measurements=" + weatherData.measurements().size();
    }
}
