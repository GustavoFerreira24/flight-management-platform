package isep.aisafe.application.listcustomercollaborators;

import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.CustomerReference;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

import java.util.List;


/**
 * Application service responsible for listing only active {@link CustomerCollaborator}
 * entities associated with either an Air Transport Company or an Air Control Area.
 *
 * This controller enforces authorization rules before accessing collaborator data
 * and ensures that only active collaborators are returned.
 *
 * Responsibilities:
 *    Retrieve collaborators from the repository
 *    Filter only active collaborators
 */
public class ListCustomerCollaboratorsController {

    private final AisafeAuthorizationService authz;
    private final CustomerCollaboratorRepository collaboratorRepository;
    private final AirTransportCompanyRepository airTransportCompanyRepository;
    private final AirControlAreaRepository airControlAreaRepository;

    public ListCustomerCollaboratorsController() {

        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().customerCollaborators(),
                PersistenceContext.repositories().airTransportCompanies(),
                PersistenceContext.repositories().airControlAreas()
        );

    }

    /**
     * Full constructor allowing dependency injection (recommended for testing).
     *
     * @param authz authorization service used to validate user permissions
     * @param collaboratorRepository repository of customer collaborators
     * @param airTransportCompanyRepository repository of air transport companies
     * @param airControlAreaRepository repository of air control areas
     * @throws IllegalArgumentException if any dependency is null
     */

    public ListCustomerCollaboratorsController(
            final AisafeAuthorizationService authz,
            final CustomerCollaboratorRepository collaboratorRepository,
            final AirTransportCompanyRepository airTransportCompanyRepository,
            final AirControlAreaRepository airControlAreaRepository) {

        if (authz == null || collaboratorRepository == null || airTransportCompanyRepository == null || airControlAreaRepository == null) {
            throw new IllegalArgumentException("As dependências não podem ser nulas.");
        }

        this.authz = authz;
        this.collaboratorRepository = collaboratorRepository;
        this.airTransportCompanyRepository = airTransportCompanyRepository;
        this.airControlAreaRepository = airControlAreaRepository;
    }

    /**
     * Retrieves all active collaborators associated with a given Air Transport Company.
     *
     * Process:
     *     Validates that the user has {@link AisafePermissions#USER_MANAGEMENT} permission
     *     Validates that the Air Transport Company exists
     *     Fetches collaborators linked to the company
     *     Filters only active collaborators
     * @param iataCode IATA code of the Air Transport Company
     * @return list of active collaborators associated with the company
     * @throws IllegalArgumentException if the company does not exist
     */

    public List<CustomerCollaborator> listForAirTransportCompany(String iataCode) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.USER_MANAGEMENT);

        IATACode customerIataCode = IATACode.valueOf(iataCode);
        airTransportCompanyRepository.findByIataCode(customerIataCode).orElseThrow(() -> new IllegalArgumentException("Air transport company not found."));

        CustomerReference reference = CustomerReference.airTransportCompany(customerIataCode.toString());

        return collaboratorRepository.findByCustomerReference(reference).stream().filter(CustomerCollaborator::isActive).toList();
    }

        /**
        * Retrieves all active collaborators associated with a given Air Control Area.
        *
        * Process:
        *     Validates that the user has {@link AisafePermissions#BACKOFFICE_CONFIGURATION} permission
        *     Validates that the Air Control Area exists
        *     Fetches collaborators linked to the area
        *     Filters only active collaborators
        * @param areaCode code of the Air Control Area
        * @return list of active collaborators associated with the area
        * @throws IllegalArgumentException if the area does not exist
        */

    public List<CustomerCollaborator> listForAirControlArea(String areaCode) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        AirControlAreaCode customerAreaCode = AirControlAreaCode.valueOf(areaCode);
        airControlAreaRepository.findByCode(customerAreaCode).orElseThrow(() -> new IllegalArgumentException("Air control area not found."));

        CustomerReference reference = CustomerReference.airControlArea(areaCode);
        return collaboratorRepository.findByCustomerReference(reference).stream().filter(CustomerCollaborator::isActive).toList();

    }

}
