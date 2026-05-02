package isep.aisafe.customercollaborator.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemUsernameTest {

    /**
     * Purpose: verify successful system username creation.
     * Scenario: create a username with surrounding spaces and uppercase letters.
     * Expected output: the username is trimmed and normalized to lowercase.
     */
    @Test
    void ensureValidSystemUsernameIsCreatedAndTrimmed() {
        // Arrange / Act
        final SystemUsername username = SystemUsername.valueOf(" Collaborator@Aisafe.com ");

        // Assert
        assertEquals("collaborator@aisafe.com", username.toString());
    }

    /**
     * Purpose: verify that system username is mandatory.
     * Scenario: create a system username with a blank value.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureBlankSystemUsernameIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> SystemUsername.valueOf(" "));
    }

    /**
     * Purpose: verify value equality.
     * Scenario: compare equivalent system usernames.
     * Expected output: both usernames are equal and have the same hash code.
     */
    @Test
    void ensureEqualityUsesUsernameValue() {
        // Arrange
        final SystemUsername first = SystemUsername.valueOf("collaborator@aisafe.com");
        final SystemUsername second = SystemUsername.valueOf("collaborator@aisafe.com");

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
