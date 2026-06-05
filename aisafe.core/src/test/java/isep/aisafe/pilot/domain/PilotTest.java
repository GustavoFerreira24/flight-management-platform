package isep.aisafe.pilot.domain;

import eapli.framework.general.domain.model.EmailAddress;
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

    private EmailAddress validEmail() {
        return EmailAddress.valueOf("pilot@example.com");
    }

    private PhoneNumber validPhoneNumber() {
        return PhoneNumber.valueOf("+351912345678");
    }

    private String validFirstName() {
        return "Ben";
    }

    private String validLastName() {
        return "Dover";
    }




    @Test
    void ensurePilotIsCreatedSuccessfully() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);
        final IATACode company = validCompany();
        final EmailAddress email = validEmail();
        final PhoneNumber phoneNumber = validPhoneNumber();
        final String firstName = validFirstName();
        final String lastName = validLastName();

        // Act
        final Pilot pilot = new Pilot(id, certification, status, company, email, phoneNumber, firstName, lastName);

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
        final EmailAddress email = validEmail();
        final PhoneNumber phoneNumber = validPhoneNumber();
        final String firstName = validFirstName();
        final String lastName = validLastName();

        // Act
        final Pilot pilot = new Pilot(id, certification, status, company, email, phoneNumber, firstName, lastName);
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
        final EmailAddress email = validEmail();
        final PhoneNumber phoneNumber = validPhoneNumber();
        final String firstName = validFirstName();
        final String lastName = validLastName();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(null, certification, status, company, email, phoneNumber, firstName, lastName));
    }

    @Test
    void ensurePilotRejectsNullCertification() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);
        final IATACode company = validCompany();
        final EmailAddress email = validEmail();
        final PhoneNumber phoneNumber = validPhoneNumber();
        final String firstName = validFirstName();
        final String lastName = validLastName();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(id, null, status, company, email, phoneNumber, firstName, lastName));
    }

    @Test
    void ensurePilotRejectsNullStatus() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final IATACode company = validCompany();
        final EmailAddress email = validEmail();
        final PhoneNumber phoneNumber = validPhoneNumber();
        final String firstName = validFirstName();
        final String lastName = validLastName();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(id, certification, null, company, email, phoneNumber, firstName, lastName));
    }

    @Test
    void ensurePilotRejectsNullCompany() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);
        final EmailAddress email = validEmail();
        final PhoneNumber phoneNumber = validPhoneNumber();
        final String firstName = validFirstName();
        final String lastName = validLastName();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(id, certification, status, null, email, phoneNumber, firstName, lastName));
    }

    @Test
    void ensurePilotRejectsNullEmail() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);
        final IATACode company = validCompany();
        final PhoneNumber phoneNumber = validPhoneNumber();
        final String firstName = validFirstName();
        final String lastName = validLastName();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(id, certification, status, company, null, phoneNumber, firstName, lastName));
    }

    @Test
    void ensurePilotRejectsNullPhoneNumber() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);
        final IATACode company = validCompany();
        final EmailAddress email = validEmail();
        final String firstName = validFirstName();
        final String lastName = validLastName();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(id, certification, status, company, email, null, firstName, lastName));
    }

    @Test
    void ensurePilotRejectsNullFirstName() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);
        final IATACode company = validCompany();
        final EmailAddress email = validEmail();
        final PhoneNumber phoneNumber = validPhoneNumber();
        final String lastName = validLastName();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(id, certification, status, company, email, phoneNumber, null, lastName));
    }

    @Test
    void ensurePilotRejectsNullLastName() {
        // Arrange
        final PilotID id = validID();
        final Certification certification = validCertification();
        final PilotStatus status = validStatus(certification);
        final IATACode company = validCompany();
        final EmailAddress email = validEmail();
        final PhoneNumber phoneNumber = validPhoneNumber();
        final String firstName = validFirstName();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new Pilot(id, certification, status, company, email, phoneNumber, firstName, null));
    }

    @Test
    void ensurePilotDeactivateChangesStatusToInvalid() {
        // Arrange
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()), validCompany(), validEmail(), validPhoneNumber(), validFirstName(), validLastName());

        // Act
        pilot.deactivate();

        // Assert
        assertFalse(pilot.pilotStatus().isValid());
    }

    @Test
    void ensurePilotActivateChangesStatusToValid() {
        // Arrange
        final Certification expiredCertification = new Certification("01-01-2020", "01-01-2021");
        final Pilot pilot = new Pilot(validID(), expiredCertification, validStatus(expiredCertification), validCompany(), validEmail(), validPhoneNumber(), validFirstName(), validLastName());

        // Act
        pilot.activate();

        // Assert
        assertTrue(pilot.pilotStatus().isValid());
    }

    @Test
    void ensurePilotSameAsReturnsTrueForSamePilot() {
        // Arrange
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()), validCompany(), validEmail(), validPhoneNumber(), validFirstName(), validLastName());

        // Act
        final boolean result = pilot.sameAs(pilot);

        // Assert
        assertTrue(result);
    }

    @Test
    void ensurePilotSameAsReturnsFalseForDifferentPilot() {
        // Arrange
        final Pilot pilot1 = new Pilot(PilotID.valueOf("ABC"), validCertification(), validStatus(validCertification()), validCompany(), validEmail(), validPhoneNumber(), validFirstName(), validLastName());
        final Pilot pilot2 = new Pilot(PilotID.valueOf("DEF"), validCertification(), validStatus(validCertification()), validCompany(), validEmail(), validPhoneNumber(), validFirstName(), validLastName());

        // Act
        final boolean result = pilot1.sameAs(pilot2);

        // Assert
        assertFalse(result);
    }

    @Test
    void ensurePilotIDMethodReturnsCorrectID() {
        // Arrange
        final PilotID id = validID();
        final Pilot pilot = new Pilot(id, validCertification(), validStatus(validCertification()), validCompany(), validEmail(), validPhoneNumber(), validFirstName(), validLastName());

        // Act
        final PilotID result = pilot.pilotID();

        // Assert
        assertEquals(id, result);
    }

    @Test
    void ensurePilotHashCodeIsConsistent() {
        // Arrange
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()), validCompany(), validEmail(), validPhoneNumber(), validFirstName(), validLastName());

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
        final Pilot pilot = new Pilot(validID(), certification, validStatus(certification), validCompany(), validEmail(), validPhoneNumber(), validFirstName(), validLastName());

        // Act
        final Certification result = pilot.certification();

        // Assert
        assertEquals(certification, result);
    }

    @Test
    void ensurePilotCompanyIATACodeReturnsCorrectIATACode() {
        // Arrange
        final IATACode company = validCompany();
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()), company, validEmail(), validPhoneNumber(), validFirstName(), validLastName());

        // Act
        final IATACode result = pilot.companyIATACode();

        // Assert
        assertEquals(company, result);
    }

    @Test
    void ensurePilotEmailReturnsCorrectEmail() {
        // Arrange
        final EmailAddress email = validEmail();
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()), validCompany(), email, validPhoneNumber(), validFirstName(), validLastName());

        // Act
        final EmailAddress result = pilot.email();

        // Assert
        assertEquals(email, result);
    }

    @Test
    void ensurePilotPhoneNumberReturnsCorrectPhoneNumber() {
        // Arrange
        final PhoneNumber phoneNumber = validPhoneNumber();
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()), validCompany(), validEmail(), phoneNumber, validFirstName(), validLastName());

        // Act
        final PhoneNumber result = pilot.phoneNumber();

        // Assert
        assertEquals(phoneNumber, result);
    }

    @Test
    void ensurePilotFirstNameReturnsCorrectFirstName() {
        // Arrange
        final String firstName = validFirstName();
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()), validCompany(), validEmail(), validPhoneNumber(), firstName, validLastName());

        // Act
        final String result = pilot.firstName();

        // Assert
        assertEquals(firstName, result);
    }

    @Test
    void ensurePilotLastNameReturnsCorrectLastName() {
        // Arrange
        final String lastName = validLastName();
        final Pilot pilot = new Pilot(validID(), validCertification(), validStatus(validCertification()), validCompany(), validEmail(), validPhoneNumber(), validFirstName(), lastName);

        // Act
        final String result = pilot.lastName();

        // Assert
        assertEquals(lastName, result);
    }


}