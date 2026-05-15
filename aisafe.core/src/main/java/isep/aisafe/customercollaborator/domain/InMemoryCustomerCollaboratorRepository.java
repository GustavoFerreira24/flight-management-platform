package isep.aisafe.customercollaborator.domain;

import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public Optional<CustomerCollaborator> findBySystemUsername(final SystemUsername systemUsername) {
        return matchOne(collaborator -> collaborator.systemUsername().equals(systemUsername));
    }

    @Override
    public List<CustomerCollaborator> findByCustomerReference(final CustomerReference reference) {

        List<CustomerCollaborator> result = new ArrayList<>();

        for (CustomerCollaborator collaborator : data().values()) {

            if (collaborator.customerReference().equals(reference)) {
                result.add(collaborator);
            }
        }

        return result;
    }
}
