package isep.aisafe.application.customercollaborator;

import isep.aisafe.customercollaborator.domain.CollaboratorEmail;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

/**
 * Coordinates the registration of customer collaborators.
 */
public class RegisterCustomerCollaboratorController {

    private final AisafeAuthorizationService authz;
    private final CustomerCollaboratorRepository collaboratorRepository;

    /**
     * Creates the controller with production authorization and persistence services.
     */
    public RegisterCustomerCollaboratorController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().customerCollaborators()
        );
    }

    /**
     * Creates the controller with a custom collaborator repository.
     *
     * @param collaboratorRepository repository used to persist collaborators
     */
    public RegisterCustomerCollaboratorController(
            final CustomerCollaboratorRepository collaboratorRepository) {
        this(new AisafeAuthorizationService(), collaboratorRepository);
    }

    RegisterCustomerCollaboratorController(
            final AisafeAuthorizationService authz,
            final CustomerCollaboratorRepository collaboratorRepository) {

        if (authz == null) {
            throw new IllegalArgumentException("Authorization service cannot be null.");
        }

        if (collaboratorRepository == null) {
            throw new IllegalArgumentException("Customer collaborator repository cannot be null.");
        }

        this.authz = authz;
        this.collaboratorRepository = collaboratorRepository;
    }

    /**
     * Registers a customer collaborator with mandatory identity data.
     *
     * @param firstName collaborator first name
     * @param lastName collaborator last name
     * @param email collaborator email
     * @return registered collaborator
     */
    public CustomerCollaborator registerCollaborator(
            final String firstName,
            final String lastName,
            final String email) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        final CollaboratorEmail collaboratorEmail = CollaboratorEmail.valueOf(email);

        if (collaboratorRepository.findByEmail(collaboratorEmail).isPresent()) {
            throw new IllegalArgumentException("Customer collaborator email already exists.");
        }

        final CustomerCollaborator collaborator = new CustomerCollaborator(
                firstName,
                lastName,
                collaboratorEmail
        );

        return collaboratorRepository.save(collaborator);
    }
}
