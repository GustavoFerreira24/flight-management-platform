package isep.aisafe.customercollaborator.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

import java.util.Optional;

/**
 * In-memory repository for customer collaborator tests.
 */
public class InMemoryCustomerCollaboratorRepository
        extends InMemoryDomainRepository<CustomerCollaborator, CollaboratorEmail>
        implements CustomerCollaboratorRepository {

    @Override
    public Optional<CustomerCollaborator> findByEmail(final CollaboratorEmail email) {
        return matchOne(collaborator -> collaborator.email().equals(email));
    }
}
