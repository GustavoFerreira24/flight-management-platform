package isep.aisafe.pilot.domain;

import isep.aisafe.airtransportcompany.domain.IATACode;
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

    private IATACode validCompany() {
        return IATACode.valueOf("TP");
    }

    @Test
    void ensurePilotIsCreatedSuccessfully() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);
        final IATACode company = validCompany();

        // Act
        final Pilot pilot = new Pilot(id, certification, status, company);

        // Assert
        assertNotNull(pilot);
    }

    @Test
    void ensurePilotIdentityIsCorrect() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);
        final IATACode company = validCompany();
        final Pilot pilot = new Pilot(id, certification, status, company);

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
        final IATACode company = validCompany();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(null, certification, status, company));
    }

    @Test
    void ensurePilotRejectsNullCertification() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);
        final IATACode company = validCompany();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(id, null, status, company));
    }

    @Test
    void ensurePilotRejectsNullStatus() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final IATACode company = validCompany();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(id, certification, null, company));
    }

    @Test
    void ensurePilotRejectsNullCompany() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(id, certification, null, null));
    }

    @Test
    void ensurePilotDeactivateChangesStatusToInvalid() {
        // Arrange
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()), validCompany());

        // Act
        pilot.deactivate();

        // Assert
        assertFalse(pilot.pilotStatus().isValid());
    }

    @Test
    void ensurePilotActivateChangesStatusToValid() {
        // Arrange
        final Certification expiredCertification = new Certification("01-01-2020", "01-01-2021");
        final Pilot pilot = new Pilot(validID(), expiredCertification, validStatus(expiredCertification), validCompany());

        // Act
        pilot.activate();

        // Assert
        assertTrue(pilot.pilotStatus().isValid());
    }

    @Test
    void ensurePilotSameAsReturnsTrueForSamePilot() {
        // Arrange
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()), validCompany());

        // Act
        final boolean result = pilot.sameAs(pilot);

        // Assert
        assertTrue(result);
    }

    @Test
    void ensurePilotSameAsReturnsFalseForDifferentPilot() {
        // Arrange
        final Pilot pilot1 = new Pilot(PilotID.valueOf("ABC"), validCertification(), validStatus(validCertification()), validCompany());
        final Pilot pilot2 = new Pilot(PilotID.valueOf("DEF"), validCertification(), validStatus(validCertification()), validCompany());

        // Act
        final boolean result = pilot1.sameAs(pilot2);

        // Assert
        assertFalse(result);
    }

    @Test
    void ensurePilotIDMethodReturnsCorrectID() {
        // Arrange
        final PilotID id = validID();
        final Pilot pilot = new Pilot(id, validCertification(), validStatus(validCertification()), validCompany());

        // Act
        final PilotID result = pilot.pilotID();

        // Assert
        assertEquals(id, result);
    }

    @Test
    void ensurePilotHashCodeIsConsistent() {
        // Arrange
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()), validCompany());

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
        final Pilot pilot = new Pilot(validID(), certification, validStatus(certification), validCompany());

        // Act
        final Certification result = pilot.certification();

        // Assert
        assertEquals(certification, result);
    }

    @Test
    void ensurePilotCompanyIATACodeReturnsCorrectIATACode() {
        // Arrange
        final IATACode company = validCompany();
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()), company);

        // Act
        final IATACode result = pilot.companyIATACode();

        // Assert
        assertEquals(company, result);
    }
}