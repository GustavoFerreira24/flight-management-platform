package isep.aisafe.pilot.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PilotStatusTest {

    @Test
    void ensurePilotStatusIsValidWhenTodayIsWithinCertificationPeriod() {
        // Arrange
        final Certification certification = new Certification("01-01-2020", "01-01-2099");

        // Act
        final PilotStatus status = PilotStatus.valueOf(certification);

        // Assert
        assertTrue(status.isValid(certification));
    }

    @Test
    void ensurePilotStatusIsInvalidWhenCertificationHasExpired() {
        // Arrange
        final Certification certification = new Certification("01-01-2020", "01-01-2021");

        // Act
        final PilotStatus status = PilotStatus.valueOf(certification);

        // Assert
        assertFalse(status.isValid(certification));
    }

    @Test
    void ensurePilotStatusIsInvalidWhenCertificationHasNotStartedYet() {
        // Arrange
        final Certification certification = new Certification("01-01-2090", "01-01-2099");

        // Act
        final PilotStatus status = PilotStatus.valueOf(certification);

        // Assert
        assertFalse(status.isValid(certification));
    }

    @Test
    void ensurePilotStatusActivateReturnsValidStatus() {
        // Arrange
        final Certification certification = new Certification("01-01-2020", "01-01-2027");
        final PilotStatus status = PilotStatus.valueOf(certification);

        // Act
        final PilotStatus activated = status.activate(certification);

        // Assert
            assertTrue(activated.isValid(certification));
    }

    @Test
    void ensurePilotStatusDeactivateReturnsInvalidStatus() {
        // Arrange
        final Certification certification = new Certification("01-01-2020", "01-01-2099");
        final PilotStatus status = PilotStatus.valueOf(certification);

        // Act
        final PilotStatus deactivated = status.deactivate();

        // Assert
        assertFalse(deactivated.isValid(certification));
    }
}