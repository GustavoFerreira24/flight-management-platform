package isep.aisafe.application.customercollaborator;

import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.IATACode;
import isep.aisafe.customercollaborator.domain.CollaboratorEmail;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.CustomerReference;
import isep.aisafe.customercollaborator.domain.SystemUsername;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.usermanagement.domain.AisafeRoles;

import java.util.Set;

/**
 * Coordinates the registration of customer collaborators.
 */
public class RegisterCustomerCollaboratorController {

    private final AisafeAuthorizationService authz;
    private final CustomerCollaboratorRepository collaboratorRepository;
    private final AirTransportCompanyRepository airTransportCompanyRepository;
    private final AirControlAreaRepository airControlAreaRepository;
    private final UserManagementService userManagementService;

    /**
     * Creates the controller with production authorization and persistence services.
     */
    public RegisterCustomerCollaboratorController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().customerCollaborators(),
                PersistenceContext.repositories().airTransportCompanies(),
                PersistenceContext.repositories().airControlAreas(),
                null
        );
    }

    RegisterCustomerCollaboratorController(
            final AisafeAuthorizationService authz,
            final CustomerCollaboratorRepository collaboratorRepository,
            final AirTransportCompanyRepository airTransportCompanyRepository,
            final AirControlAreaRepository airControlAreaRepository,
            final UserManagementService userManagementService) {

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
        this.userManagementService = userManagementService;
    }

    /**
     * Registers a customer collaborator with mandatory identity data.
     *
     * @param firstName collaborator first name
     * @param lastName collaborator last name
     * @param email collaborator email
     * @param iataCode air transport company IATA code
     * @param initialPassword initial password for the collaborator system user
     * @return registered collaborator
     */
    public CustomerCollaborator registerCollaboratorForAirTransportCompany(
            final String firstName,
            final String lastName,
            final String email,
            final String iataCode,
            final String initialPassword) {

        final IATACode customerIataCode = IATACode.valueOf(iataCode);

        airTransportCompanyRepository.findByIataCode(customerIataCode)
                .orElseThrow(() -> new IllegalArgumentException("Air transport company customer not found."));

        return registerCollaborator(
                firstName,
                lastName,
                email,
                CustomerReference.airTransportCompany(customerIataCode.toString()),
                initialPassword,
                Set.of(AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR)
        );
    }

    /**
     * Registers a customer collaborator for an existing air control area.
     *
     * @param firstName collaborator first name
     * @param lastName collaborator last name
     * @param email collaborator email
     * @param areaCode air control area code
     * @param initialPassword initial password for the collaborator system user
     * @return registered collaborator
     */
    public CustomerCollaborator registerCollaboratorForAirControlArea(
            final String firstName,
            final String lastName,
            final String email,
            final String areaCode,
            final String initialPassword) {

        final AirControlAreaCode customerAreaCode = AirControlAreaCode.valueOf(areaCode);

        airControlAreaRepository.findByCode(customerAreaCode)
                .orElseThrow(() -> new IllegalArgumentException("Air control area customer not found."));

        return registerCollaborator(
                firstName,
                lastName,
                email,
                CustomerReference.airControlArea(customerAreaCode.toString()),
                initialPassword,
                Set.of(AisafeRoles.FLIGHT_CONTROL_OPERATOR)
        );
    }

    private CustomerCollaborator registerCollaborator(
            final String firstName,
            final String lastName,
            final String email,
            final CustomerReference customerReference,
            final String initialPassword,
            final Set<Role> systemUserRoles) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        ensureNotBlank(firstName, "Collaborator first name is required.");
        ensureNotBlank(lastName, "Collaborator last name is required.");

        final CollaboratorEmail collaboratorEmail = CollaboratorEmail.valueOf(email);

        if (collaboratorRepository.findByEmail(collaboratorEmail).isPresent()) {
            throw new IllegalArgumentException("Customer collaborator email already exists.");
        }

        ensureNotBlank(initialPassword, "Collaborator initial password is required.");

        final SystemUsername systemUsername = SystemUsername.valueOf(collaboratorEmail.toString());

        final CustomerCollaborator collaborator = new CustomerCollaborator(
                firstName,
                lastName,
                collaboratorEmail,
                customerReference,
                systemUsername
        );

        final CustomerCollaborator savedCollaborator = collaboratorRepository.save(collaborator);

        userService().registerNewUser(
                systemUsername.toString(),
                initialPassword,
                firstName,
                lastName,
                collaboratorEmail.toString(),
                systemUserRoles
        );

        return savedCollaborator;
    }

    private void ensureNotBlank(final String value, final String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private UserManagementService userService() {
        if (userManagementService != null) {
            return userManagementService;
        }
        return AuthzRegistry.userService();
    }
}
