package isep.aisafe.application.customercollaborator;

import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.customercollaborator.domain.CollaboratorEmail;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.CustomerReference;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

/**
 * Coordinates the registration of customer collaborators.
 */
public class RegisterCustomerCollaboratorController {

    private final AisafeAuthorizationService authz;
    private final CustomerCollaboratorRepository collaboratorRepository;
    private final AirTransportCompanyRepository airTransportCompanyRepository;
    private final AirControlAreaRepository airControlAreaRepository;

    /**
     * Creates the controller with production authorization and persistence services.
     */
    public RegisterCustomerCollaboratorController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().customerCollaborators(),
                PersistenceContext.repositories().airTransportCompanies(),
                PersistenceContext.repositories().airControlAreas()
        );
    }

    /**
     * Creates the controller with a custom collaborator repository.
     *
     * @param collaboratorRepository repository used to persist collaborators
     */
    public RegisterCustomerCollaboratorController(
            final CustomerCollaboratorRepository collaboratorRepository) {
        this(
                new AisafeAuthorizationService(),
                collaboratorRepository,
                PersistenceContext.repositories().airTransportCompanies(),
                PersistenceContext.repositories().airControlAreas()
        );
    }

    RegisterCustomerCollaboratorController(
            final AisafeAuthorizationService authz,
            final CustomerCollaboratorRepository collaboratorRepository,
            final AirTransportCompanyRepository airTransportCompanyRepository,
            final AirControlAreaRepository airControlAreaRepository) {

        if (authz == null) {
            throw new IllegalArgumentException("Authorization service cannot be null.");
        }

        if (collaboratorRepository == null) {
            throw new IllegalArgumentException("Customer collaborator repository cannot be null.");
        }

        if (airTransportCompanyRepository == null) {
            throw new IllegalArgumentException("Air transport company repository cannot be null.");
        }

        if (airControlAreaRepository == null) {
            throw new IllegalArgumentException("Air control area repository cannot be null.");
        }

        this.authz = authz;
        this.collaboratorRepository = collaboratorRepository;
        this.airTransportCompanyRepository = airTransportCompanyRepository;
        this.airControlAreaRepository = airControlAreaRepository;
    }

    /**
     * Registers a customer collaborator with mandatory identity data.
     *
     * @param firstName collaborator first name
     * @param lastName collaborator last name
     * @param email collaborator email
     * @return registered collaborator
     */
    public CustomerCollaborator registerCollaboratorForAirTransportCompany(
            final String firstName,
            final String lastName,
            final String email,
            final String iataCode) {

        final IATACode customerIataCode = IATACode.valueOf(iataCode);

        airTransportCompanyRepository.findByIataCode(customerIataCode)
                .orElseThrow(() -> new IllegalArgumentException("Air transport company customer not found."));

        return registerCollaborator(
                firstName,
                lastName,
                email,
                CustomerReference.airTransportCompany(customerIataCode.toString())
        );
    }

    /**
     * Registers a customer collaborator for an existing air control area.
     *
     * @param firstName collaborator first name
     * @param lastName collaborator last name
     * @param email collaborator email
     * @param areaCode air control area code
     * @return registered collaborator
     */
    public CustomerCollaborator registerCollaboratorForAirControlArea(
            final String firstName,
            final String lastName,
            final String email,
            final String areaCode) {

        final AirControlAreaCode customerAreaCode = AirControlAreaCode.valueOf(areaCode);

        airControlAreaRepository.findByCode(customerAreaCode)
                .orElseThrow(() -> new IllegalArgumentException("Air control area customer not found."));

        return registerCollaborator(
                firstName,
                lastName,
                email,
                CustomerReference.airControlArea(customerAreaCode.toString())
        );
    }

    private CustomerCollaborator registerCollaborator(
            final String firstName,
            final String lastName,
            final String email,
            final CustomerReference customerReference) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        final CollaboratorEmail collaboratorEmail = CollaboratorEmail.valueOf(email);

        if (collaboratorRepository.findByEmail(collaboratorEmail).isPresent()) {
            throw new IllegalArgumentException("Customer collaborator email already exists.");
        }

        final CustomerCollaborator collaborator = new CustomerCollaborator(
                firstName,
                lastName,
                collaboratorEmail,
                customerReference
        );

        return collaboratorRepository.save(collaborator);
    }
}
