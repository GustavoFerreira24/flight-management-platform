package isep.aisafe.pilot.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PilotIDTest {

    @Test
    void ensurePilotIDIsCreatedSuccessfully() {
        // Arrange
        final String validId = "ABC";

        // Act
        final PilotID pilotID = PilotID.valueOf(validId);

        // Assert
        assertNotNull(pilotID);
    }

    @Test
    void ensurePilotIDRejectsBlankValue() {
        // Arrange
        final String blankId = "";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> PilotID.valueOf(blankId));
    }

    @Test
    void ensurePilotIDRejectsNullValue() {
        // Arrange
        final String nullId = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> PilotID.valueOf(nullId));
    }

    @Test
    void ensurePilotIDRejectsInvalidFormat() {
        // Arrange
        final String invalidId = "ABC123";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> PilotID.valueOf(invalidId));
    }

    @Test
    void ensurePilotIDRejectsNumbers() {
        // Arrange
        final String invalidId = "12";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> PilotID.valueOf(invalidId));
    }

    @Test
    void ensurePilotIDIsStoredUpperCase() {
        // Arrange
        final String lowercaseId = "abc";

        // Act
        final PilotID pilotID = PilotID.valueOf(lowercaseId);

        // Assert
        assertEquals(PilotID.valueOf("ABC"), pilotID);
    }

    @Test
    void ensurePilotIDToStringReturnsStoredValue() {
        // Arrange
        final PilotID pilotID = PilotID.valueOf("abc");

        // Act
        final String result = pilotID.toString();

        // Assert
        assertEquals("ABC", result);
    }

    @Test
    void ensureTwoPilotIDsWithSameValueAreEqual() {
        // Arrange
        final PilotID first = PilotID.valueOf("ABC");
        final PilotID second = PilotID.valueOf("ABC");

        // Act
        final boolean result = first.equals(second);

        // Assert
        assertTrue(result);
    }

    @Test
    void ensureTwoPilotIDsWithDifferentValuesAreNotEqual() {
        // Arrange
        final PilotID first = PilotID.valueOf("ABC");
        final PilotID second = PilotID.valueOf("CDE");

        // Act
        final boolean result = first.equals(second);

        // Assert
        assertFalse(result);
    }

    @Test
    void ensurePilotIDCompareToReturnsZeroForEqualIDs() {
        // Arrange
        final PilotID first = PilotID.valueOf("ABC");
        final PilotID second = PilotID.valueOf("ABC");

        // Act
        final int result = first.compareTo(second);

        // Assert
        assertEquals(0, result);
    }

    @Test
    void ensurePilotIDCompareToReturnsNonZeroForDifferentIDs() {
        // Arrange
        final PilotID first = PilotID.valueOf("ABC");
        final PilotID second = PilotID.valueOf("DEF");

        // Act
        final int result = first.compareTo(second);

        // Assert
        assertNotEquals(0, result);
    }

    @Test
    void ensurePilotIDNotEqualsNonPilotIDObject() {
        // Arrange
        final PilotID pilotID = PilotID.valueOf("ABC");
        final Object other = "ABC";

        // Act
        final boolean result = pilotID.equals(other);

        // Assert
        assertFalse(result);
    }

    @Test
    void ensurePilotIDEqualsItself() {
        // Arrange
        final PilotID pilotID = PilotID.valueOf("ABC");

        // Act
        final boolean result = pilotID.equals(pilotID);

        // Assert
        assertTrue(result);
    }
}
