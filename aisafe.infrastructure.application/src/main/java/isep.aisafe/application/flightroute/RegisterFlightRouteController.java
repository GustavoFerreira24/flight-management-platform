package isep.aisafe.application.flightroute;

import isep.aisafe.airport.domain.Airport;
import isep.aisafe.airport.domain.AirportICAOCode;
import isep.aisafe.airport.domain.AirportRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.SystemUsername;
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.flightroute.domain.FlightRouteRepository;
import isep.aisafe.flightroute.domain.FlightRouteType;
import isep.aisafe.flightroute.domain.RouteDesignator;
import isep.aisafe.infrastructure.authz.*;
import isep.aisafe.infrastructure.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for registering flight routes for an air transport company.
 */
public class RegisterFlightRouteController {

    private final AisafeAuthorizationService authz;
    private final FlightRouteRepository flightRouteRepository;
    private final AirportRepository airportRepository;
    private final CustomerCollaboratorRepository customerCollaboratorRepository;

    /**
     * Creates the controller with default services and repositories.
     */
    public RegisterFlightRouteController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().flightRoutes(),
                PersistenceContext.repositories().airports(),
                PersistenceContext.repositories().customerCollaborators()
        );
    }

    RegisterFlightRouteController(
            final AisafeAuthorizationService authz,
            final FlightRouteRepository flightRouteRepository,
            final AirportRepository airportRepository,
            final CustomerCollaboratorRepository customerCollaboratorRepository) {

        if (authz == null) {
            throw new IllegalArgumentException("Authorization service cannot be null.");
        }

        if (flightRouteRepository == null) {
            throw new IllegalArgumentException("Flight route repository cannot be null.");
        }

        if (airportRepository == null) {
            throw new IllegalArgumentException("Airport repository cannot be null.");
        }

        if (customerCollaboratorRepository == null) {
            throw new IllegalArgumentException("Customer collaborator repository cannot be null.");
        }

        this.authz = authz;
        this.flightRouteRepository = flightRouteRepository;
        this.airportRepository = airportRepository;
        this.customerCollaboratorRepository = customerCollaboratorRepository;
    }

    /**
     * Registers a regular active flight route for the authenticated collaborator's company.
     *
     * @param routeDesignatorText the route designator
     * @param departureAirportIcaoText departure airport ICAO code
     * @param destinationAirportIcaoText destination airport ICAO code
     * @return the registered flight route
     */
    public FlightRoute registerFlightRoute(
            final String routeDesignatorText,
            final String departureAirportIcaoText,
            final String destinationAirportIcaoText) {

        return registerFlightRoute(
                routeDesignatorText,
                FlightRouteType.regular().toString(),
                departureAirportIcaoText,
                destinationAirportIcaoText
        );
    }

    /**
     * Registers an active flight route for the authenticated collaborator's company.
     *
     * @param routeDesignatorText the route designator
     * @param routeTypeText the route type
     * @param departureAirportIcaoText departure airport ICAO code
     * @param destinationAirportIcaoText destination airport ICAO code
     * @return the registered flight route
     */
    public FlightRoute registerFlightRoute(
            final String routeDesignatorText,
            final String routeTypeText,
            final String departureAirportIcaoText,
            final String destinationAirportIcaoText) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        final RouteDesignator routeDesignator = RouteDesignator.valueOf(routeDesignatorText);

        if (flightRouteRepository.findByRouteDesignator(routeDesignator).isPresent()) {
            throw new IllegalArgumentException("Flight route designator already exists.");
        }

        final AirTransportCompany company = authenticatedCollaboratorCompany();

        final Airport departureAirport = airportByIcao(departureAirportIcaoText, "Departure airport was not found.");
        final Airport destinationAirport = airportByIcao(destinationAirportIcaoText, "Destination airport was not found.");

        final FlightRoute route = new FlightRoute(
                routeDesignator,
                FlightRouteType.valueOf(routeTypeText),
                company,
                departureAirport,
                destinationAirport
        );

        return flightRouteRepository.save(route);
    }

    /**
     * Checks whether a flight route designator is already in use.
     *
     * @param routeDesignatorText route designator text
     * @return true if the designator already exists
     */
    public boolean routeDesignatorExists(final String routeDesignatorText) {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        return flightRouteRepository.findByRouteDesignator(RouteDesignator.valueOf(routeDesignatorText)).isPresent();
    }

    /**
     * Lists airports available for route creation.
     *
     * @return sorted airport selection labels
     */
    public List<String> airportOptions() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        final List<String> airports = new ArrayList<>();

        for (final Airport airport : airportRepository.findAll()) {
            airports.add(airport.icaoCode() + " | " + airport.iataCode() + " | " + airport.name());
        }

        airports.sort(String::compareTo);
        return airports;
    }

    private Airport airportByIcao(final String airportIcaoText, final String notFoundMessage) {
        final AirportICAOCode airportIcao = AirportICAOCode.valueOf(airportIcaoText);

        return airportRepository
                .findByIcaoCode(airportIcao)
                .orElseThrow(() -> new IllegalArgumentException(notFoundMessage));
    }

    private AirTransportCompany authenticatedCollaboratorCompany() {
        final String username = authz.authenticatedUsername();
        final SystemUsername systemUsername = SystemUsername.valueOf(username);

        final CustomerCollaborator collaborator = customerCollaboratorRepository
                .findBySystemUsername(systemUsername)
                .orElseThrow(() -> new IllegalStateException("Authenticated user is not a customer collaborator."));

        if (!collaborator.isAssociatedWithAirTransportCompany()) {
            throw new IllegalStateException(
                    "Authenticated collaborator is not associated with an air transport company."
            );
        }

        return collaborator.airTransportCompany();
    }
}
