package isep.aisafe.customercollaborator.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollaboratorEmailTest {

    /**
     * Purpose: verify that valid collaborator emails are accepted and normalized.
     * Scenario: create an email with spaces and uppercase letters.
     * Expected output: the stored value is lowercase and trimmed.
     */
    @Test
    void ensureValidEmailIsAcceptedAndNormalized() {
        // Arrange / Act
        final CollaboratorEmail email = CollaboratorEmail.valueOf(" Collaborator@AISAFE.com ");

        // Assert
        assertEquals("collaborator@aisafe.com", email.toString());
    }

    /**
     * Purpose: verify that an email is mandatory.
     * Scenario: create a collaborator email with a null value.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureNullEmailIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> CollaboratorEmail.valueOf(null));
    }

    /**
     * Purpose: verify that blank emails are rejected.
     * Scenario: create a collaborator email using only whitespace.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureBlankEmailIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> CollaboratorEmail.valueOf(" "));
    }

    /**
     * Purpose: verify that the email format is validated.
     * Scenario: create a collaborator email without a valid email structure.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureInvalidEmailFormatIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> CollaboratorEmail.valueOf("invalid-email"));
    }

    /**
     * Purpose: verify value-object equality semantics.
     * Scenario: compare two emails with the same address but different casing.
     * Expected output: both objects are equal and have the same hash code.
     */
    @Test
    void ensureEmailsWithSameValueAreEqual() {
        // Arrange
        final CollaboratorEmail first = CollaboratorEmail.valueOf("collaborator@aisafe.com");
        final CollaboratorEmail second = CollaboratorEmail.valueOf("COLLABORATOR@AISAFE.COM");

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
