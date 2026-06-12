package isep.aisafe.application.remoteaccess;

/**
 * Pilot operations exposed through remote access.
 */
public interface PilotRemoteOperations {

    String createFlightPlan(
            String flightPlanIdentifier,
            String flightDesignator,
            String aircraftRegistration,
            String pilotId,
            String fuelAmount,
            String fuelUnit);

    String consultWeatherData(String day, String airControlAreaCode);

    String insertWeatherData(
            String flightPlanIdentifier,
            String weatherDataDay,
            String airControlAreaCode);

    String listTestableFlightPlans();

    String testFlightPlan(String flightPlanIdentifier);

    String consultFlightPlanTestReports();
}
