package isep.aisafe.application.flightmanagement;

import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.aircraft.domain.AircraftRegistration;
import isep.aisafe.aircraft.domain.AircraftRepository;
import isep.aisafe.flight.domain.Flight;
import isep.aisafe.flight.domain.FlightDesignator;
import isep.aisafe.flight.domain.FlightRepository;
import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanIdentifier;
import isep.aisafe.flightplan.domain.FlightPlanRepository;
import isep.aisafe.flightplan.domain.FuelQuantity;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.pilot.domain.Pilot;
import isep.aisafe.pilot.domain.PilotID;
import isep.aisafe.pilot.domain.PilotRepository;

import java.util.Optional;

/**
 * Application use case for creating flight plans.
 */
public class CreateFlightPlanController {

    private final AisafeAuthorizationService authz;
    private final FlightPlanRepository flightPlanRepository;
    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;
    private final PilotRepository pilotRepository;

    /**
     * Creates the controller with default services and repositories.
     */
    public CreateFlightPlanController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().flightPlans(),
                PersistenceContext.repositories().flights(),
                PersistenceContext.repositories().aircraft(),
                PersistenceContext.repositories().pilots()
        );
    }

    CreateFlightPlanController(
            final AisafeAuthorizationService authz,
            final FlightPlanRepository flightPlanRepository,
            final FlightRepository flightRepository,
            final AircraftRepository aircraftRepository,
            final PilotRepository pilotRepository) {

        if (authz == null) {
            throw new IllegalArgumentException("Authorization service cannot be null.");
        }
        if (flightPlanRepository == null) {
            throw new IllegalArgumentException("Flight plan repository cannot be null.");
        }
        if (flightRepository == null) {
            throw new IllegalArgumentException("Flight repository cannot be null.");
        }
        if (aircraftRepository == null) {
            throw new IllegalArgumentException("Aircraft repository cannot be null.");
        }
        if (pilotRepository == null) {
            throw new IllegalArgumentException("Pilot repository cannot be null.");
        }

        this.authz = authz;
        this.flightPlanRepository = flightPlanRepository;
        this.flightRepository = flightRepository;
        this.aircraftRepository = aircraftRepository;
        this.pilotRepository = pilotRepository;
    }

    /**
     * Authorizes access to flight plan creation.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT);
    }

    /**
     * Creates a draft flight plan for an existing flight and aircraft, assigning it to the
     * authenticated pilot.
     *
     * @param flightPlanIdentifierText flight plan identifier
     * @param flightDesignatorText flight designator
     * @param aircraftRegistrationText aircraft registration
     * @param fuelAmount assigned fuel amount
     * @param fuelUnitText assigned fuel unit
     * @return persisted flight plan
     */
    public FlightPlan createFlightPlan(
            final String flightPlanIdentifierText,
            final String flightDesignatorText,
            final String aircraftRegistrationText,
            final double fuelAmount,
            final String fuelUnitText) {

        return createFlightPlan(
                flightPlanIdentifierText,
                flightDesignatorText,
                aircraftRegistrationText,
                null,
                fuelAmount,
                fuelUnitText
        );
    }

    /**
     * Creates a draft flight plan for an existing flight and aircraft. When a pilot id is supplied by
     * an external client, it must match the authenticated pilot.
     *
     * @param flightPlanIdentifierText flight plan identifier
     * @param flightDesignatorText flight designator
     * @param aircraftRegistrationText aircraft registration
     * @param pilotIdText pilot identifier supplied by the client
     * @param fuelAmount assigned fuel amount
     * @param fuelUnitText assigned fuel unit
     * @return persisted flight plan
     */
    public FlightPlan createFlightPlan(
            final String flightPlanIdentifierText,
            final String flightDesignatorText,
            final String aircraftRegistrationText,
            final String pilotIdText,
            final double fuelAmount,
            final String fuelUnitText) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT);

        final FlightPlanIdentifier flightPlanIdentifier = FlightPlanIdentifier.valueOf(flightPlanIdentifierText);
        if (flightPlanRepository.findByIdentifier(flightPlanIdentifier).isPresent()) {
            throw new IllegalArgumentException("Flight plan identifier already exists.");
        }

        final Flight flight = flightRepository.findByDesignator(FlightDesignator.valueOf(flightDesignatorText))
                .orElseThrow(() -> new IllegalArgumentException("Flight was not found."));
        final Aircraft aircraft = aircraftRepository.findByRegistration(AircraftRegistration.valueOf(aircraftRegistrationText))
                .orElseThrow(() -> new IllegalArgumentException("Aircraft was not found."));
        final Pilot pilot = authenticatedPilot(pilotIdText);
        final FuelQuantity fuelQuantity = FuelQuantity.valueOf(fuelAmount, FuelUnit.valueOf(fuelUnitText));

        final FlightPlan flightPlan = new FlightPlan(
                flightPlanIdentifier,
                flight,
                aircraft,
                pilot,
                fuelQuantity
        );

        return flightPlanRepository.save(flightPlan);
    }

    private Pilot authenticatedPilot(final String pilotIdText) {
        final Pilot pilot = findAuthenticatedPilot()
                .orElseThrow(() -> new IllegalStateException("Authenticated pilot was not found."));

        if (pilotIdText != null && !pilotIdText.isBlank()) {
            final PilotID requestedPilotId = PilotID.valueOf(pilotIdText);
            if (!pilot.identity().equals(requestedPilotId)) {
                throw new IllegalArgumentException("Pilot must match authenticated user.");
            }
        }

        return pilot;
    }

    private Optional<Pilot> findAuthenticatedPilot() {
        final String authenticatedEmail = authz.authenticatedEmail();
        final Optional<Pilot> byEmail = pilotRepository.findByEmail(authenticatedEmail);
        if (byEmail.isPresent()) {
            return byEmail;
        }

        final String authenticatedUsername = authz.authenticatedUsername();
        final Optional<Pilot> byUsernameEmail = pilotRepository.findByEmail(authenticatedUsername);
        if (byUsernameEmail.isPresent()) {
            return byUsernameEmail;
        }

        try {
            return pilotRepository.findByCode(PilotID.valueOf(authenticatedUsername));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}
