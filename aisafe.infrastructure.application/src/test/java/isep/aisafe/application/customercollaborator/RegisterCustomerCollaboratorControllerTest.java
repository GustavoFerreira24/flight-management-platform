package isep.aisafe.application.customercollaborator;

import isep.aisafe.customercollaborator.domain.CollaboratorEmail;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.InMemoryCustomerCollaboratorRepository;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterCustomerCollaboratorControllerTest {

    /**
     * Purpose: verify that the testable constructor can be used with a repository.
     * Scenario: create the controller with an in-memory collaborator repository.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new RegisterCustomerCollaboratorController(
                new InMemoryCustomerCollaboratorRepository()
        ));
    }

    /**
     * Purpose: verify the successful US061.1 collaborator registration flow.
     * Scenario: an authorized backoffice operation submits valid collaborator data.
     * Expected output: authorization is checked and the collaborator is persisted.
     */
    @Test
    void ensureCollaboratorIsRegisteredAndPersisted() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();
        final RegisterCustomerCollaboratorController controller =
                new RegisterCustomerCollaboratorController(authz, repository);

        // Act
        final CustomerCollaborator collaborator = controller.registerCollaborator(
                "Customer",
                "Collaborator",
                "registered.collaborator@aisafe.com"
        );

        // Assert
        assertArrayEquals(AisafePermissions.BACKOFFICE_CONFIGURATION, authz.requestedRoles());
        assertEquals("Customer", collaborator.firstName());
        assertEquals("Collaborator", collaborator.lastName());
        assertEquals("registered.collaborator@aisafe.com", collaborator.email().toString());
        assertTrue(repository.findByEmail(
                CollaboratorEmail.valueOf("registered.collaborator@aisafe.com")
        ).isPresent());
    }

    /**
     * Purpose: verify that collaborator email uniqueness is enforced.
     * Scenario: register two collaborators with the same email.
     * Expected output: the second registration throws an IllegalArgumentException.
     */
    @Test
    void ensureDuplicateCollaboratorEmailIsRejected() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();
        final RegisterCustomerCollaboratorController controller =
                new RegisterCustomerCollaboratorController(authz, repository);

        controller.registerCollaborator("Customer", "Collaborator", "duplicate.collaborator@aisafe.com");

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerCollaborator("Other", "Collaborator", "duplicate.collaborator@aisafe.com")
        );
    }

    /**
     * Purpose: verify that invalid collaborator data is not persisted.
     * Scenario: register a collaborator with a blank first name.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureInvalidCollaboratorDataIsRejected() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final RegisterCustomerCollaboratorController controller =
                new RegisterCustomerCollaboratorController(
                        authz,
                        new InMemoryCustomerCollaboratorRepository()
                );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerCollaborator("", "Collaborator", "invalid.data.collaborator@aisafe.com")
        );
    }
}
