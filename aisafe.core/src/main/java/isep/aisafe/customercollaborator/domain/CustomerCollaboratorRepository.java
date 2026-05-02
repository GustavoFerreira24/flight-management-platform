package isep.aisafe.customercollaborator.domain;

import eapli.framework.domain.repositories.DomainRepository;

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
}
