package isep.aisafe.customercollaborator.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerCollaboratorTest {

    /**
     * Purpose: verify successful collaborator creation.
     * Scenario: provide valid first name, last name, email and customer reference.
     * Expected output: the collaborator stores all values and uses email as identity.
     */
    @Test
    void ensureValidCollaboratorIsCreated() {
        // Arrange
        final CollaboratorEmail email = CollaboratorEmail.valueOf("collaborator@aisafe.com");
        final CustomerReference customerReference = CustomerReference.airTransportCompany("TP");

        // Act
        final CustomerCollaborator collaborator = new CustomerCollaborator(
                "Customer",
                "Collaborator",
                email,
                customerReference,
                "registered.company.collaborator@aisafe.com"
        );

        // Assert
        assertEquals("Customer", collaborator.firstName());
        assertEquals("Collaborator", collaborator.lastName());
        assertEquals(email, collaborator.email());
        assertEquals(customerReference, collaborator.customerReference());
        assertEquals(email, collaborator.identity());
    }

    /**
     * Purpose: verify that first name is mandatory.
     * Scenario: create a collaborator with a blank first name.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureBlankFirstNameIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new CustomerCollaborator(
                        " ",
                        "Collaborator",
                        CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                        CustomerReference.airTransportCompany("TP"),
                        "collaborator@aisafe.com"
                )
        );
    }

    /**
     * Purpose: verify that last name is mandatory.
     * Scenario: create a collaborator with a blank last name.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureBlankLastNameIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new CustomerCollaborator(
                        "Customer",
                        " ",
                        CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                        CustomerReference.airTransportCompany("TP"),
                        "collaborator@aisafe.com"
                )
        );
    }

    /**
     * Purpose: verify that collaborator email is mandatory.
     * Scenario: create a collaborator without an email value object.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureNullEmailIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new CustomerCollaborator(
                        "Customer",
                        "Collaborator",
                        null,
                        CustomerReference.airTransportCompany("TP"),
                        "collaborator@aisafe.com"
                )
        );
    }

    /**
     * Purpose: verify that customer association is mandatory.
     * Scenario: create a collaborator without a customer reference.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureNullCustomerReferenceIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new CustomerCollaborator(
                        "Customer",
                        "Collaborator",
                        CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                        null,
                        "collaborator@aisafe.com"
                )
        );
    }

    /**
     * Purpose: verify that the associated system username is mandatory.
     * Scenario: create a collaborator without a system username.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureBlankSystemUsernameIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new CustomerCollaborator(
                        "Customer",
                        "Collaborator",
                        CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                        CustomerReference.airTransportCompany("TP"),
                        " "
                )
        );
    }

    /**
     * Purpose: verify aggregate identity comparison.
     * Scenario: compare two collaborators with the same email and different names.
     * Expected output: sameAs returns true only for another collaborator with the same identity.
     */
    @Test
    void ensureSameAsUsesIdentity() {
        // Arrange
        final CustomerCollaborator first = new CustomerCollaborator(
                "Customer",
                "Collaborator",
                CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                CustomerReference.airTransportCompany("TP"),
                "collaborator@aisafe.com"
        );
        final CustomerCollaborator second = new CustomerCollaborator(
                "Other",
                "Name",
                CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                CustomerReference.airControlArea("LISACC"),
                "other.collaborator@aisafe.com"
        );

        // Act / Assert
        assertTrue(first.sameAs(second));
        assertFalse(first.sameAs("not a collaborator"));
    }
}
