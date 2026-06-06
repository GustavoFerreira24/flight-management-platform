package isep.aisafe.application.remoteaccess;

/**
 * Pilot operations exposed through remote access.
 */
public interface PilotRemoteOperations {

    String consultWeatherData(String day, String airControlAreaCode);

    String insertWeatherData(
            String flightPlanIdentifier,
            String weatherDataDay,
            String airControlAreaCode);
}
