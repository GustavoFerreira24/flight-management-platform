package isep.aisafe.application.remoteaccess;

import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.application.aircraft.AircraftFleetEntry;
import isep.aisafe.application.aircraft.DecommissionAircraftController;
import isep.aisafe.application.aircraft.ListAircraftFleetController;
import isep.aisafe.application.aircraft.RegisterAircraftController;
import isep.aisafe.application.flightmanagement.CreateFlightController;
import isep.aisafe.application.flightroute.RegisterFlightRouteController;
import isep.aisafe.application.pilot.AddPilotController;
import isep.aisafe.application.pilot.DeactivatePilotController;
import isep.aisafe.application.pilot.ListPilotRosterController;
import isep.aisafe.flight.domain.Flight;
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.pilot.domain.Pilot;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Production adapter that delegates remote commands to existing EAPLI controllers.
 */
public class ControllerAirCompanyRemoteOperations implements AirCompanyRemoteOperations {

    private final ListAircraftFleetController listAircraftFleetController;
    private final RegisterAircraftController registerAircraftController;
    private final DecommissionAircraftController decommissionAircraftController;
    private final RegisterFlightRouteController registerFlightRouteController;
    private final CreateFlightController createFlightController;
    private final AddPilotController addPilotController;
    private final ListPilotRosterController listPilotRosterController;
    private final DeactivatePilotController deactivatePilotController;

    public ControllerAirCompanyRemoteOperations() {
        this(
                new ListAircraftFleetController(),
                new RegisterAircraftController(),
                new DecommissionAircraftController(),
                new RegisterFlightRouteController(),
                new CreateFlightController(),
                new AddPilotController(),
                new ListPilotRosterController(),
                new DeactivatePilotController()
        );
    }

    ControllerAirCompanyRemoteOperations(
            final ListAircraftFleetController listAircraftFleetController,
            final RegisterAircraftController registerAircraftController,
            final DecommissionAircraftController decommissionAircraftController,
            final RegisterFlightRouteController registerFlightRouteController,
            final CreateFlightController createFlightController,
            final AddPilotController addPilotController,
            final ListPilotRosterController listPilotRosterController,
            final DeactivatePilotController deactivatePilotController) {

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
        if (createFlightController == null) {
            throw new IllegalArgumentException("Create flight controller cannot be null.");
        }
        if (addPilotController == null) {
            throw new IllegalArgumentException("Add pilot controller cannot be null.");
        }
        if (listPilotRosterController == null) {
            throw new IllegalArgumentException("List pilot roster controller cannot be null.");
        }
        if (deactivatePilotController == null) {
            throw new IllegalArgumentException("Deactivate pilot controller cannot be null.");
        }

        this.listAircraftFleetController = listAircraftFleetController;
        this.registerAircraftController = registerAircraftController;
        this.decommissionAircraftController = decommissionAircraftController;
        this.registerFlightRouteController = registerFlightRouteController;
        this.createFlightController = createFlightController;
        this.addPilotController = addPilotController;
        this.listPilotRosterController = listPilotRosterController;
        this.deactivatePilotController = deactivatePilotController;
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

    @Override
    public String createFlight(
            final String routeDesignator,
            final String flightDesignator,
            final String departureDay,
            final String departureTime) {

        final Flight flight = createFlightController.createFlight(
                routeDesignator,
                flightDesignator,
                departureDay,
                departureTime
        );

        return "Flight created: " + flight.identity();
    }

    @Override
    public String addPilot(final String pilotId) {
        addPilotController.addPilot(pilotId);
        return "Pilot added to roster: " + pilotId.trim().toUpperCase();
    }

    @Override
    public String listPilotRoster() {
        final Set<Pilot> roster = listPilotRosterController.listPilotRoster();
        if (roster.isEmpty()) {
            return "No pilots registered in company roster.";
        }

        return roster.stream()
                .sorted((left, right) -> left.identity().toString().compareTo(right.identity().toString()))
                .map(this::formatPilot)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String deactivatePilot(final String pilotId) {
        deactivatePilotController.deactivatePilot(pilotId);
        return "Pilot deactivated: " + pilotId.trim().toUpperCase();
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

    private String formatPilot(final Pilot pilot) {
        return pilot.identity()
                + "; name=" + pilot.firstName() + " " + pilot.lastName()
                + "; status=" + pilot.pilotStatus()
                + "; company=" + pilot.companyIATACode();
    }
}
