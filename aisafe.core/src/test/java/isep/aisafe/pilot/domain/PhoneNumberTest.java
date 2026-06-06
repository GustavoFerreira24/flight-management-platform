package isep.aisafe.pilot.domain;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class PhoneNumberTest {

    @Test
    void ensurePhoneNumberIsCreatedSuccessfullyForPortugal() {
        // Arrange
        final String phoneNumber = "+351912345678";

        // Act
        final PhoneNumber result = PhoneNumber.valueOf(phoneNumber);

        // Assert
        assertNotNull(result);
    }

    @Test
    void ensurePhoneNumberIsCreatedSuccessfullyForSpain() {
        // Arrange
        final String phoneNumber = "+34612345678";

        // Act
        final PhoneNumber result = PhoneNumber.valueOf(phoneNumber);

        // Assert
        assertNotNull(result);
    }

    @Test
    void ensurePhoneNumberIsCreatedSuccessfullyForFrance() {
        // Arrange
        final String phoneNumber = "+33123456789";

        // Act
        final PhoneNumber result = PhoneNumber.valueOf(phoneNumber);

        // Assert
        assertNotNull(result);
    }

    @Test
    void ensurePhoneNumberIsCreatedSuccessfullyForUK() {
        // Arrange
        final String phoneNumber = "+447911123456";

        // Act
        final PhoneNumber result = PhoneNumber.valueOf(phoneNumber);

        // Assert
        assertNotNull(result);
    }

    @Test
    void ensurePhoneNumberIsCreatedSuccessfullyForUS() {
        // Arrange
        final String phoneNumber = "+12025550123";

        // Act
        final PhoneNumber result = PhoneNumber.valueOf(phoneNumber);

        // Assert
        assertNotNull(result);
    }

    @Test
    void ensurePhoneNumberIsCreatedSuccessfullyForBrazil() {
        // Arrange
        final String phoneNumber = "+5511912345678";

        // Act
        final PhoneNumber result = PhoneNumber.valueOf(phoneNumber);

        // Assert
        assertNotNull(result);
    }

    @Test
    void ensurePhoneNumberIsCreatedSuccessfullyWithNormalization() {
        // Arrange
        final String phoneNumber = "+351 912 345 678";

        // Act
        final PhoneNumber result = PhoneNumber.valueOf(phoneNumber);

        // Assert
        assertNotNull(result);
    }

    @Test
    void ensurePhoneNumberIsRejectedIfEmpty() {
        // Arrange
        final String phoneNumber = "";

        // Act & Assert
        try {
            PhoneNumber.valueOf(phoneNumber);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Phone number cannot be empty.", e.getMessage());
        }
    }

    @Test
    void ensurePhoneNumberIsRejectedIfNull() {
        // Arrange
        final String phoneNumber = null;

        // Act & Assert
        try {
            PhoneNumber.valueOf(phoneNumber);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Phone number cannot be empty.", e.getMessage());
        }
    }

    @Test
    void ensurePhoneNumberIsRejectedIfDoesNotStartWithPlus() {
        // Arrange
        final String phoneNumber = "351912345678";

        // Act & Assert
        try {
            PhoneNumber.valueOf(phoneNumber);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Invalid phone number"));
        }
    }

    @Test
    void ensurePhoneNumberIsRejectedIfInvalidPortugueseNumber() {
        // Arrange
        final String phoneNumber = "+351123456789"; // starts with invalid digit

        // Act & Assert
        try {
            PhoneNumber.valueOf(phoneNumber);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
            // Expected exception
        }
    }

    @Test
    void ensurePhoneNumberIsRejectedIfInvalidSpanishNumber() {
        // Arrange
        final String phoneNumber = "+34512345678";

        // Act & Assert
        try {
            PhoneNumber.valueOf(phoneNumber);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
            // Expected exception
        }
    }

    @Test
    void ensurePhoneNumberIsRejectedIfWrongLengthForPortugal() {
        // Arrange
        final String phoneNumber = "+35191234567"; // too short

        // Act & Assert
        try {
            PhoneNumber.valueOf(phoneNumber);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
            // Expected exception
        }
    }

    @Test
    void ensurePhoneNumberIsRejectedIfTooShortForGenericCountry() {
        // Arrange
        final String phoneNumber = "+39123";

        // Act & Assert
        try {
            PhoneNumber.valueOf(phoneNumber);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
            // Expected exception
        }
    }

    @Test
    void ensurePhoneNumberIsRejectedIfInvalidUSNumber() {
        // Arrange
        final String phoneNumber = "+11234567890";

        // Act & Assert
        try {
            PhoneNumber.valueOf(phoneNumber);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
            // Expected exception
        }
    }
}