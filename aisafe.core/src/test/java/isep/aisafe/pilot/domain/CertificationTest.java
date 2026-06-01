package isep.aisafe.pilot.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CertificationTest {

    @Test
    void ensureCertificationIsCreatedSuccessfully() {
        // Arrange
        final String issuedAt = "01-01-2024";
        final String validUntil = "01-01-2027";

        // Act
        final Certification certification = new Certification(issuedAt, validUntil);

        // Assert
        assertNotNull(certification);
    }

    @Test
    void ensureCertificationRejectsBlankIssuedAt() {
        // Arrange
        final String issuedAt = "";
        final String validUntil = "01-01-2027";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Certification(issuedAt, validUntil));
    }

    @Test
    void ensureCertificationRejectsBlankValidUntil() {
        // Arrange
        final String issuedAt = "01-01-2024";
        final String validUntil = "";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Certification(issuedAt, validUntil));
    }

    @Test
    void ensureCertificationRejectsInvalidIssuedAtFormat() {
        // Arrange
        final String issuedAt = "2024-01-01";
        final String validUntil = "01-01-2027";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Certification(issuedAt, validUntil));
    }

    @Test
    void ensureCertificationRejectsInvalidValidUntilFormat() {
        // Arrange
        final String issuedAt = "01-01-2024";
        final String validUntil = "2027-01-01";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Certification(issuedAt, validUntil));
    }

    @Test
    void ensureCertificationRejectsValidUntilBeforeIssuedAt() {
        // Arrange
        final String issuedAt = "01-01-2027";
        final String validUntil = "01-01-2024";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Certification(issuedAt, validUntil));
    }

    @Test
    void ensureCertificationRejectsValidUntilEqualToIssuedAt() {
        // Arrange
        final String issuedAt = "01-01-2024";
        final String validUntil = "01-01-2024";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Certification(issuedAt, validUntil));
    }

    @Test
    void ensureCertificationReturnsCorrectIssuedAt() {
        // Arrange
        final String issuedAt = "01-01-2024";
        final String validUntil = "01-01-2027";
        final Certification certification = new Certification(issuedAt, validUntil);

        //Act
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
        final String result = certification.issuedAt().format(formatter);

        // Assert
        assertEquals(issuedAt, result);
    }

    @Test
    void ensureCertificationReturnsCorrectValidUntil() {
        // Arrange
        final String issuedAt = "01-01-2024";
        final String validUntil = "01-01-2027";
        final Certification certification = new Certification(issuedAt, validUntil);

        // Act
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
        final String result = certification.validUntil().format(formatter);

        // Assert
        assertEquals(validUntil, result);
    }

    @Test
    void ensureCertificationToStringIsCorrect() {
        // Arrange
        final Certification certification = new Certification("01-01-2024", "01-01-2027");

        // Act
        final String result = certification.toString();

        // Assert
        assertEquals("Certification Issued At: 01-01-2024\nCertification Valid Until: 01-01-2027", result);
    }
}