package isep.aisafe.pilot.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PilotTest {

    private PilotID validID() {
        return PilotID.valueOf("ABC");
    }

    private Certification validCertification() {
        return new Certification("01-01-2020", "01-01-2099");
    }

    private PilotStatus validStatus(Certification certification) {
        return PilotStatus.valueOf(certification);
    }

    @Test
    void ensurePilotIsCreatedSuccessfully() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);

        // Act
        final Pilot pilot = new Pilot(id, certification, status);

        // Assert
        assertNotNull(pilot);
    }

    @Test
    void ensurePilotIdentityIsCorrect() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);
        final Pilot pilot = new Pilot(id, certification, status);

        // Act
        final PilotID result = pilot.identity();

        // Assert
        assertEquals(id, result);
    }

    @Test
    void ensurePilotRejectsNullID() {
        // Arrange
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(null, certification, status));
    }

    @Test
    void ensurePilotRejectsNullCertification() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(id, null, status));
    }

    @Test
    void ensurePilotRejectsNullStatus() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(id, certification, null));
    }

    @Test
    void ensurePilotDeactivateChangesStatusToInvalid() {
        // Arrange
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()));

        // Act
        pilot.deactivate();

        // Assert
        assertFalse(pilot.pilotStatus().isValid());
    }

    @Test
    void ensurePilotActivateChangesStatusToValid() {
        // Arrange
        final Certification expiredCertification = new Certification("01-01-2020", "01-01-2021");
        final Pilot pilot = new Pilot(validID(), expiredCertification, validStatus(expiredCertification));

        // Act
        pilot.activate();

        // Assert
        assertTrue(pilot.pilotStatus().isValid());
    }

    @Test
    void ensurePilotSameAsReturnsTrueForSamePilot() {
        // Arrange
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()));

        // Act
        final boolean result = pilot.sameAs(pilot);

        // Assert
        assertTrue(result);
    }

    @Test
    void ensurePilotSameAsReturnsFalseForDifferentPilot() {
        // Arrange
        final Pilot pilot1 = new Pilot(PilotID.valueOf("ABC"), validCertification(), validStatus(validCertification()));
        final Pilot pilot2 = new Pilot(PilotID.valueOf("DEF"), validCertification(), validStatus(validCertification()));

        // Act
        final boolean result = pilot1.sameAs(pilot2);

        // Assert
        assertFalse(result);
    }

    @Test
    void ensurePilotIDMethodReturnsCorrectID() {
        // Arrange
        final PilotID id = validID();
        final Pilot pilot = new Pilot(id, validCertification(), validStatus(validCertification()));

        // Act
        final PilotID result = pilot.pilotID();

        // Assert
        assertEquals(id, result);
    }

    @Test
    void ensurePilotHashCodeIsConsistent() {
        // Arrange
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()));

        // Act
        final int first = pilot.hashCode();
        final int second = pilot.hashCode();

        // Assert
        assertEquals(first, second);
    }

    @Test
    void ensurePilotCertificationReturnsCorrectCertification() {
        // Arrange
        final Certification certification = validCertification();
        final Pilot pilot = new Pilot(validID(), certification, validStatus(certification));

        // Act
        final Certification result = pilot.certification();

        // Assert
        assertEquals(certification, result);
    }
}