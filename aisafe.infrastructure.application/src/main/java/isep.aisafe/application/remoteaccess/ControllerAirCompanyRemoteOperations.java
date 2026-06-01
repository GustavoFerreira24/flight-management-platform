package isep.aisafe.application.remoteaccess;

import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.application.aircraft.AircraftFleetEntry;
import isep.aisafe.application.aircraft.DecommissionAircraftController;
import isep.aisafe.application.aircraft.ListAircraftFleetController;
import isep.aisafe.application.aircraft.RegisterAircraftController;
import isep.aisafe.application.flightroute.RegisterFlightRouteController;
import isep.aisafe.flightroute.domain.FlightRoute;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Production adapter that delegates remote commands to existing EAPLI controllers.
 */
public class ControllerAirCompanyRemoteOperations implements AirCompanyRemoteOperations {

    private final ListAircraftFleetController listAircraftFleetController;
    private final RegisterAircraftController registerAircraftController;
    private final DecommissionAircraftController decommissionAircraftController;
    private final RegisterFlightRouteController registerFlightRouteController;

    public ControllerAirCompanyRemoteOperations() {
        this(
                new ListAircraftFleetController(),
                new RegisterAircraftController(),
                new DecommissionAircraftController(),
                new RegisterFlightRouteController()
        );
    }

    ControllerAirCompanyRemoteOperations(
            final ListAircraftFleetController listAircraftFleetController,
            final RegisterAircraftController registerAircraftController,
            final DecommissionAircraftController decommissionAircraftController,
            final RegisterFlightRouteController registerFlightRouteController) {

        if (listAircraftFleetController == null) {
            throw new IllegalArgumentException("List aircraft fleet controller cannot be null.");
        }
        if (registerAircraftController == null) {
            throw new IllegalArgumentException("Register aircraft controller cannot be null.");
        }
        if (decommissionAircraftController == null) {
            throw new IllegalArgumentException("Decommission aircraft controller cannot be null.");
        }
        if (registerFlightRouteController == null) {
            throw new IllegalArgumentException("Register flight route controller cannot be null.");
        }

        this.listAircraftFleetController = listAircraftFleetController;
        this.registerAircraftController = registerAircraftController;
        this.decommissionAircraftController = decommissionAircraftController;
        this.registerFlightRouteController = registerFlightRouteController;
    }

    @Override
    public String listAircraftFleet() {
        final List<AircraftFleetEntry> entries = listAircraftFleetController.listAircraftFleet();

        if (entries.isEmpty()) {
            return "No aircraft registered in company fleet.";
        }

        return entries.stream()
                .map(this::formatFleetEntry)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String registerAircraft(
            final String registration,
            final String registrationCountry,
            final String operationalStatus,
            final int crewSize,
            final int economySeats,
            final int businessSeats,
            final int firstClassSeats,
            final String aircraftModelName,
            final String aircraftModelManufacturer,
            final String engineModelName,
            final String engineModelManufacturer) {

        final Aircraft aircraft = registerAircraftController.registerAircraft(
                registration,
                registrationCountry,
                operationalStatus,
                crewSize,
                economySeats,
                businessSeats,
                firstClassSeats,
                aircraftModelName,
                aircraftModelManufacturer,
                engineModelName,
                engineModelManufacturer
        );

        return "Aircraft registered: " + aircraft.registration();
    }

    @Override
    public String decommissionAircraft(final String registration) {
        final Aircraft aircraft = decommissionAircraftController.decommissionAircraft(registration);
        return "Aircraft decommissioned: " + aircraft.registration();
    }

    @Override
    public String createFlightRoute(
            final String routeDesignator,
            final String departureAirportIcao,
            final String destinationAirportIcao) {

        final FlightRoute route = registerFlightRouteController.registerFlightRoute(
                routeDesignator,
                departureAirportIcao,
                destinationAirportIcao
        );

        return "Flight route created: " + route.identity();
    }

    private String formatFleetEntry(final AircraftFleetEntry entry) {
        return entry.registration()
                + "; model=" + entry.model()
                + "; maker=" + entry.maker()
                + "; seats=" + entry.totalSeats()
                + "/" + entry.modelCapacity()
                + "; status=" + entry.operationalStatus()
                + "; engine=" + entry.engineModel();
    }
}
