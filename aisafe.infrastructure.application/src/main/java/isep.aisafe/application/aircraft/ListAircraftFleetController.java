package isep.aisafe.application.aircraft;

import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.aircraft.domain.AircraftRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.CustomerReference;
import isep.aisafe.customercollaborator.domain.CustomerType;
import isep.aisafe.customercollaborator.domain.SystemUsername;
import isep.aisafe.infrastructure.authz.*;
import isep.aisafe.infrastructure.persistence.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for listing the authenticated air transport company's aircraft fleet.
 */
public class ListAircraftFleetController {

    private final AisafeAuthorizationService authz;
    private final AircraftRepository aircraftRepository;
    private final AirTransportCompanyRepository airTransportCompanyRepository;
    private final CustomerCollaboratorRepository customerCollaboratorRepository;

    /**
     * Creates the controller with default services and repositories.
     */
    public ListAircraftFleetController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().aircraft(),
                PersistenceContext.repositories().airTransportCompanies(),
                PersistenceContext.repositories().customerCollaborators()
        );
    }

    ListAircraftFleetController(
            final AisafeAuthorizationService authz,
            final AircraftRepository aircraftRepository,
            final AirTransportCompanyRepository airTransportCompanyRepository,
            final CustomerCollaboratorRepository customerCollaboratorRepository) {

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

        this.authz = authz;
        this.aircraftRepository = aircraftRepository;
        this.airTransportCompanyRepository = airTransportCompanyRepository;
        this.customerCollaboratorRepository = customerCollaboratorRepository;
    }

    /**
     * Lists the authenticated collaborator company's aircraft fleet without filters.
     *
     * @return aircraft fleet entries
     */
    public List<AircraftFleetEntry> listAircraftFleet() {
        return listAircraftFleet(AircraftFleetFilter.empty());
    }

    /**
     * Lists the authenticated collaborator company's aircraft fleet with optional filters.
     *
     * @param filter optional filters to apply
     * @return aircraft fleet entries matching the filters
     */
    public List<AircraftFleetEntry> listAircraftFleet(final AircraftFleetFilter filter) {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        final AircraftFleetFilter effectiveFilter = filter == null ? AircraftFleetFilter.empty() : filter;
        final AirTransportCompany company = authenticatedCollaboratorCompany();
        final List<AircraftFleetEntry> entries = new ArrayList<>();

        for (final Aircraft aircraft : aircraftRepository.findByCompany(company)) {
            final AircraftFleetEntry entry = toEntry(aircraft);

            if (matches(entry, effectiveFilter)) {
                entries.add(entry);
            }
        }

        entries.sort(Comparator.comparing(AircraftFleetEntry::registration));
        return entries;
    }

    private boolean matches(final AircraftFleetEntry entry, final AircraftFleetFilter filter) {
        return containsIgnoreCase(entry.model(), filter.model())
                && containsIgnoreCase(entry.maker(), filter.maker())
                && atLeast(entry.totalSeats(), filter.minimumCapacity())
                && atMost(entry.totalSeats(), filter.maximumCapacity())
                && atLeast(entry.ageInYears(), filter.minimumAge())
                && atMost(entry.ageInYears(), filter.maximumAge());
    }

    private boolean containsIgnoreCase(final String value, final String filter) {
        return filter == null || value.toLowerCase().contains(filter.toLowerCase());
    }

    private boolean atLeast(final int value, final Integer minimum) {
        return minimum == null || value >= minimum;
    }

    private boolean atMost(final int value, final Integer maximum) {
        return maximum == null || value <= maximum;
    }

    private AircraftFleetEntry toEntry(final Aircraft aircraft) {
        final String[] modelParts = splitDesignator(aircraft.aircraftModel().designator().toString());

        return new AircraftFleetEntry(
                aircraft.registration().toString(),
                modelParts[0],
                aircraft.aircraftModel().manufacturer().designator().toString(),
                aircraft.cabinConfiguration().totalSeats(),
                aircraft.aircraftModel().passengerCapacity(),
                aircraft.manufactureYear(),
                aircraft.ageInYears(),
                aircraft.operationalStatus().toString(),
                aircraft.engineModel().designator().toString()
        );
    }

    private String[] splitDesignator(final String designator) {
        final String[] parts = designator.split("\\s*\\|\\s*", 2);

        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalStateException("Invalid aircraft model designator: " + designator);
        }

        return parts;
    }

    private AirTransportCompany authenticatedCollaboratorCompany() {
        final String username = authz.authenticatedUsername();
        final SystemUsername systemUsername = SystemUsername.valueOf(username);

        final CustomerCollaborator collaborator = customerCollaboratorRepository
                .findBySystemUsername(systemUsername)
                .orElseThrow(() -> new IllegalStateException("Authenticated user is not a customer collaborator."));

        final CustomerReference customerReference = collaborator.customerReference();

        if (customerReference.customerType() != CustomerType.AIR_TRANSPORT_COMPANY) {
            throw new IllegalStateException(
                    "Authenticated collaborator is not associated with an air transport company."
            );
        }

        final IATACode companyIataCode = IATACode.valueOf(customerReference.customerIdentity());

        return airTransportCompanyRepository
                .findByIataCode(companyIataCode)
                .orElseThrow(() -> new IllegalStateException("Associated air transport company was not found."));
    }
}
