package isep.aisafe.application.flightmanagement;

import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.SystemUsername;
import isep.aisafe.flight.domain.Departure;
import isep.aisafe.flight.domain.Flight;
import isep.aisafe.flight.domain.FlightDesignator;
import isep.aisafe.flight.domain.FlightRepository;
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.flightroute.domain.FlightRouteRepository;
import isep.aisafe.flightroute.domain.RouteDesignator;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Application use case for creating scheduled flights.
 */
public class CreateFlightController {

    private final AisafeAuthorizationService authz;
    private final FlightRouteRepository flightRouteRepository;
    private final FlightRepository flightRepository;
    private final CustomerCollaboratorRepository customerCollaboratorRepository;

    /**
     * Creates the controller with default services and repositories.
     */
    public CreateFlightController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().flightRoutes(),
                PersistenceContext.repositories().flights(),
                PersistenceContext.repositories().customerCollaborators()
        );
    }

    CreateFlightController(
            final AisafeAuthorizationService authz,
            final FlightRouteRepository flightRouteRepository,
            final FlightRepository flightRepository,
            final CustomerCollaboratorRepository customerCollaboratorRepository) {

        if (authz == null) {
            throw new IllegalArgumentException("Authorization service cannot be null.");
        }
        if (flightRouteRepository == null) {
            throw new IllegalArgumentException("Flight route repository cannot be null.");
        }
        if (flightRepository == null) {
            throw new IllegalArgumentException("Flight repository cannot be null.");
        }
        if (customerCollaboratorRepository == null) {
            throw new IllegalArgumentException("Customer collaborator repository cannot be null.");
        }

        this.authz = authz;
        this.flightRouteRepository = flightRouteRepository;
        this.flightRepository = flightRepository;
        this.customerCollaboratorRepository = customerCollaboratorRepository;
    }

    /**
     * Authorizes access to flight creation.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);
    }

    /**
     * Creates a scheduled flight for an existing active flight route.
     *
     * @param routeDesignatorText route designator
     * @param flightDesignatorText flight designator
     * @param departureDayText departure day in yyyy-MM-dd format
     * @param departureTimeText departure time in HH:mm format
     * @return persisted flight
     */
    public Flight createFlight(
            final String routeDesignatorText,
            final String flightDesignatorText,
            final String departureDayText,
            final String departureTimeText) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        final RouteDesignator routeDesignator = RouteDesignator.valueOf(routeDesignatorText);

        final FlightRoute route = flightRouteRepository.findByRouteDesignator(routeDesignator)
                .orElseThrow(() -> new IllegalArgumentException("Flight route was not found."));

        ensureRouteBelongsToAuthenticatedCompany(route);

        final FlightDesignator flightDesignator = FlightDesignator.valueOf(flightDesignatorText);

        if (flightRepository.findByDesignator(flightDesignator).isPresent()) {
            throw new IllegalArgumentException("Flight designator already exists.");
        }

        final Departure departure = Departure.valueOf(
                LocalDate.parse(departureDayText),
                LocalTime.parse(departureTimeText)
        );

        final Flight flight = new Flight(
                flightDesignator,
                route,
                departure
        );

        return flightRepository.save(flight);
    }

    /**
     * Checks whether a flight designator already exists.
     *
     * @param flightDesignatorText flight designator text
     * @return true if the designator is already in use
     */
    public boolean flightDesignatorExists(final String flightDesignatorText) {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        return flightRepository.findByDesignator(FlightDesignator.valueOf(flightDesignatorText)).isPresent();
    }

    private void ensureRouteBelongsToAuthenticatedCompany(final FlightRoute route) {
        final AirTransportCompany company = authenticatedCollaboratorCompany();

        if (!route.company().sameAs(company)) {
            throw new IllegalArgumentException(
                    "Flight route does not belong to the authenticated collaborator's company."
            );
        }
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