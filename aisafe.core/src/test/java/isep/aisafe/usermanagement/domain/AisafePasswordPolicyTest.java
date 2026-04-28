package isep.aisafe.usermanagement.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AisafePasswordPolicyTest {

    @Test
    void ensureValidPasswordIsAccepted() {
        // Arrange
        final AisafePasswordPolicy policy = new AisafePasswordPolicy();

        // Act
        final boolean result = policy.isSatisfiedBy("Password1");

        // Assert
        assertTrue(result);
    }

    @Test
    void ensureMinimumValidPasswordIsAccepted() {
        // Arrange
        final AisafePasswordPolicy policy = new AisafePasswordPolicy();

        // Act
        final boolean result = policy.isSatisfiedBy("Passw1");

        // Assert
        assertTrue(result);
    }

    @Test
    void ensureNullPasswordIsRejected() {
        // Arrange
        final AisafePasswordPolicy policy = new AisafePasswordPolicy();

        // Act
        final boolean result = policy.isSatisfiedBy(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void ensureEmptyPasswordIsRejected() {
        // Arrange
        final AisafePasswordPolicy policy = new AisafePasswordPolicy();

        // Act
        final boolean result = policy.isSatisfiedBy("");

        // Assert
        assertFalse(result);
    }

    @Test
    void ensureShortPasswordIsRejected() {
        // Arrange
        final AisafePasswordPolicy policy = new AisafePasswordPolicy();

        // Act
        final boolean result = policy.isSatisfiedBy("Pa1");

        // Assert
        assertFalse(result);
    }

    @Test
    void ensurePasswordWithoutDigitIsRejected() {
        // Arrange
        final AisafePasswordPolicy policy = new AisafePasswordPolicy();

        // Act
        final boolean result = policy.isSatisfiedBy("Password");

        // Assert
        assertFalse(result);
    }

    @Test
    void ensurePasswordWithoutCapitalLetterIsRejected() {
        // Arrange
        final AisafePasswordPolicy policy = new AisafePasswordPolicy();

        // Act
        final boolean result = policy.isSatisfiedBy("password1");

        // Assert
        assertFalse(result);
    }
}