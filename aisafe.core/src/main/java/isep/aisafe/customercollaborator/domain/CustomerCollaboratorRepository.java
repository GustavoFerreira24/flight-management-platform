package isep.aisafe.customercollaborator.domain;

import eapli.framework.domain.repositories.DomainRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for customer collaborator aggregates.
 */
public interface CustomerCollaboratorRepository
        extends DomainRepository<CollaboratorEmail, CustomerCollaborator> {

    /**
     * Finds a collaborator by email.
     *
     * @param email collaborator email
     * @return matching collaborator, if one exists
     */
    Optional<CustomerCollaborator> findByEmail(CollaboratorEmail email);

    /**
     * Finds a collaborator by the username of the associated system user.
     *
     * @param systemUsername system username associated with the collaborator
     * @return matching collaborator, if one exists
     */
    Optional<CustomerCollaborator> findBySystemUsername(SystemUsername systemUsername);

        /**
         * Finds a collaborator by the reference of the associated customer.
         *
         * @param customerReference reference of the customer associated with the collaborator
         * @return matching collaborator, if one exists
         */
    List<CustomerCollaborator> findByCustomerReference(CustomerReference customerReference);
}
