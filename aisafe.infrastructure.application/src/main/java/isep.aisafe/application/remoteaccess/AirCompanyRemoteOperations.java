package isep.aisafe.application.remoteaccess;

/**
 * Air Transport Company Collaborator operations exposed through remote access.
 */
public interface AirCompanyRemoteOperations {

    String listAircraftFleet();

    String registerAircraft(
            String registration,
            String registrationCountry,
            String operationalStatus,
            int crewSize,
            int economySeats,
            int businessSeats,
            int firstClassSeats,
            String aircraftModelName,
            String aircraftModelManufacturer,
            String engineModelName,
            String engineModelManufacturer);

    String decommissionAircraft(String registration);

    String createFlightRoute(
            String routeDesignator,
            String departureAirportIcao,
            String destinationAirportIcao);

    String createFlight(
            String routeDesignator,
            String flightDesignator,
            String departureDay,
            String departureTime);

    String addPilot(String pilotId);

    String listPilotRoster();

    String deactivatePilot(String pilotId);
}