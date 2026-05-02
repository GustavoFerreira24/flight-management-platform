package isep.aisafe.customercollaborator.domain;

import org.junit.jupiter.api.Test;

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
                CustomerReference.airTransportCompany("TP")
        );

        // Act
        repository.save(collaborator);

        // Assert
        assertTrue(repository.findByEmail(email).isPresent());
    }
}
