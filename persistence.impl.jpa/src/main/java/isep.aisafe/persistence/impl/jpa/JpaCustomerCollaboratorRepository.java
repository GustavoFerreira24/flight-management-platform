package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.customercollaborator.domain.*;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JPA implementation of the customer collaborator repository.
 */
class JpaCustomerCollaboratorRepository
        extends JpaAutoTxRepository<CustomerCollaborator, CollaboratorEmail, CollaboratorEmail>
        implements CustomerCollaboratorRepository {

    JpaCustomerCollaboratorRepository(final TransactionalContext autoTx) {
        super(autoTx, "email");
    }

    JpaCustomerCollaboratorRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "email");
    }

    @Override
    public Optional<CustomerCollaborator> findByEmail(final CollaboratorEmail email) {
        final Map<String, Object> params = new HashMap<>();
        params.put("email", email);

        return matchOne("e.email=:email", params);
    }

    @Override
    public Optional<CustomerCollaborator> findBySystemUsername(final SystemUsername systemUsername) {
        final Map<String, Object> params = new HashMap<>();
        params.put("systemUsername", systemUsername);

        return matchOne("e.systemUsername=:systemUsername", params);
    }

    @Override
    public List<CustomerCollaborator> findByCustomerReference(
            final CustomerReference reference) {

        TypedQuery<CustomerCollaborator> query =
                entityManager().createQuery(
                        "SELECT c FROM CustomerCollaborator c " +
                                "WHERE c.customerReference = :reference",
                        CustomerCollaborator.class
                );

        query.setParameter("reference", reference);

        return query.getResultList();
    }
}
