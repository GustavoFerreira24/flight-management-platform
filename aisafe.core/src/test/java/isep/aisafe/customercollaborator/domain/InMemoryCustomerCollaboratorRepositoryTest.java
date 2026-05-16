package isep.aisafe.customercollaborator.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCustomerCollaboratorRepositoryTest {
    /**
     * Purpose: verify the in-memory repository lookup needed by application tests.
     * Scenario: save a collaborator and search by the same email.
     * Expected output: the collaborator is found.
     */
    @Test
    void ensureCollaboratorCanBeFoundByEmail() {
        // Arrange
        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();
        final CollaboratorEmail email = CollaboratorEmail.valueOf("collaborator@aisafe.com");
        final CustomerCollaborator collaborator = new CustomerCollaborator(
                "Customer",
                "Collaborator",
                email,
                CustomerReference.airTransportCompany("TP"),
                SystemUsername.valueOf("collaborator@aisafe.com")
        );

        // Act
        repository.save(collaborator);

        // Assert
        assertTrue(repository.findByEmail(email).isPresent());
    }

    /**
     * Purpose: verify lookup by associated system username.
     * Scenario: save a collaborator and search by the same system username.
     * Expected output: the collaborator is found.
     */
    @Test
    void ensureCollaboratorCanBeFoundBySystemUsername() {
        // Arrange
        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();
        final SystemUsername systemUsername = SystemUsername.valueOf("collaborator@aisafe.com");
        final CustomerCollaborator collaborator = new CustomerCollaborator(
                "Customer",
                "Collaborator",
                CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                CustomerReference.airTransportCompany("TP"),
                systemUsername
        );

        // Act
        repository.save(collaborator);

        // Assert
        assertTrue(repository.findBySystemUsername(systemUsername).isPresent());
    }

    @Test
    void ensureFindByCustomerReferenceOnlyReturnsMatchingReference() {

        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();

        final CustomerReference tpRef =
                CustomerReference.airTransportCompany("TP");

        final CustomerReference frRef =
                CustomerReference.airTransportCompany("FR");

        final CustomerCollaborator tpCollaborator =
                new CustomerCollaborator(
                        "John",
                        "Doe",
                        CollaboratorEmail.valueOf("john@tp.com"),
                        tpRef,
                        SystemUsername.valueOf("john@tp.com")
                );

        final CustomerCollaborator frCollaborator =
                new CustomerCollaborator(
                        "Jane",
                        "Doe",
                        CollaboratorEmail.valueOf("jane@fr.com"),
                        frRef,
                        SystemUsername.valueOf("jane@fr.com")
                );

        repository.save(tpCollaborator);
        repository.save(frCollaborator);

        final List<CustomerCollaborator> result =
                repository.findByCustomerReference(tpRef);

        assertEquals(1, result.size());
        assertTrue(result.contains(tpCollaborator));
    }

    @Test
    void ensureFindByCustomerReferenceReturnsEmptyWhenRepositoryIsEmpty() {

        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();

        final List<CustomerCollaborator> result =
                repository.findByCustomerReference(
                        CustomerReference.airTransportCompany("TP")
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void ensureCustomerReferenceTypesAreNotMixed() {

        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();

        final CustomerCollaborator transportCollaborator =
                new CustomerCollaborator(
                        "John",
                        "Doe",
                        CollaboratorEmail.valueOf("john@tp.com"),
                        CustomerReference.airTransportCompany("TP"),
                        SystemUsername.valueOf("john@tp.com")
                );

        final CustomerCollaborator controlAreaCollaborator =
                new CustomerCollaborator(
                        "Jane",
                        "Doe",
                        CollaboratorEmail.valueOf("jane@tp.com"),
                        CustomerReference.airControlArea("LISACC"),
                        SystemUsername.valueOf("jane@tp.com")
                );

        repository.save(transportCollaborator);
        repository.save(controlAreaCollaborator);

        final List<CustomerCollaborator> result =
                repository.findByCustomerReference(
                        CustomerReference.airTransportCompany("TP")
                );

        assertEquals(1, result.size());
        assertTrue(result.contains(transportCollaborator));
        assertFalse(result.contains(controlAreaCollaborator));
    }
}
