package isep.aisafe.application.aircraft;

import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.aircraft.domain.AircraftRegistration;
import isep.aisafe.aircraft.domain.AircraftRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.SystemUsername;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for retiring an aircraft from an air transport company's fleet.
 */
public class DecommissionAircraftController {

    private final AisafeAuthorizationService authz;
    private final AircraftRepository aircraftRepository;
    private final AirTransportCompanyRepository airTransportCompanyRepository;
    private final CustomerCollaboratorRepository customerCollaboratorRepository;
    private final PendingAircraftFlightsService pendingFlightsService;

    /**
     * Creates the controller with default services and repositories.
     */
    public DecommissionAircraftController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().aircraft(),
                PersistenceContext.repositories().airTransportCompanies(),
                PersistenceContext.repositories().customerCollaborators(),
                new NoPendingAircraftFlightsService()
        );
    }

    DecommissionAircraftController(
            final AisafeAuthorizationService authz,
            final AircraftRepository aircraftRepository,
            final AirTransportCompanyRepository airTransportCompanyRepository,
            final CustomerCollaboratorRepository customerCollaboratorRepository,
            final PendingAircraftFlightsService pendingFlightsService) {

        if (authz == null) {
            throw new IllegalArgumentException("Authorization service cannot be null.");
        }

        if (aircraftRepository == null) {
            throw new IllegalArgumentException("Aircraft repository cannot be null.");
        }

        if (airTransportCompanyRepository == null) {
            throw new IllegalArgumentException("Air transport company repository cannot be null.");
        }

        if (customerCollaboratorRepository == null) {
            throw new IllegalArgumentException("Customer collaborator repository cannot be null.");
        }

        if (pendingFlightsService == null) {
            throw new IllegalArgumentException("Pending aircraft flights service cannot be null.");
        }

        this.authz = authz;
        this.aircraftRepository = aircraftRepository;
        this.airTransportCompanyRepository = airTransportCompanyRepository;
        this.customerCollaboratorRepository = customerCollaboratorRepository;
        this.pendingFlightsService = pendingFlightsService;
    }

    /**
     * Decommissions an aircraft owned by the authenticated collaborator's company.
     *
     * @param registration aircraft registration number
     * @return the decommissioned aircraft
     */
    public Aircraft decommissionAircraft(final String registration) {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        final AircraftRegistration aircraftRegistration = AircraftRegistration.valueOf(registration);
        final Aircraft aircraft = aircraftRepository
                .findByRegistration(aircraftRegistration)
                .orElseThrow(() -> new IllegalArgumentException("Aircraft was not found."));

        final AirTransportCompany company = authenticatedCollaboratorCompany();

        if (!aircraft.company().sameAs(company)) {
            throw new IllegalStateException("Aircraft does not belong to authenticated collaborator's company.");
        }

        if (pendingFlightsService.hasPendingFlights(aircraft)) {
            throw new IllegalStateException("Aircraft has pending flights registered in the system.");
        }

        aircraft.decommission();

        return aircraftRepository.save(aircraft);
    }

    /**
     * Lists aircraft from the authenticated collaborator's company that can be decommissioned.
     *
     * @return aircraft candidates for decommissioning
     */
    public List<Aircraft> decommissionableAircraft() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        final AirTransportCompany company = authenticatedCollaboratorCompany();
        final List<Aircraft> candidates = new ArrayList<>();

        for (Aircraft aircraft : aircraftRepository.findByCompany(company)) {
            if (!aircraft.isDecommissioned() && !pendingFlightsService.hasPendingFlights(aircraft)) {
                candidates.add(aircraft);
            }
        }

        return candidates;
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
