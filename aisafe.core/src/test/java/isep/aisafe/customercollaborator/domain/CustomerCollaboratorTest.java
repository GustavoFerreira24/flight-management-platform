package isep.aisafe.customercollaborator.domain;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerCollaboratorTest {

    /**
     * Purpose: verify successful collaborator creation.
     * Scenario: provide valid first name, last name, email and customer association.
     * Expected output: the collaborator stores all values and uses email as identity.
     */
    @Test
    void ensureValidCollaboratorIsCreated() {
        // Arrange
        final CollaboratorEmail email = CollaboratorEmail.valueOf("collaborator@aisafe.com");
        final AirTransportCompany airTransportCompany = airTransportCompany("TP", "TAP");

        // Act
        final CustomerCollaborator collaborator = new CustomerCollaborator(
                "Customer",
                "Collaborator",
                email,
                airTransportCompany,
                SystemUsername.valueOf("registered.company.collaborator@aisafe.com")
        );

        // Assert
        assertEquals("Customer", collaborator.firstName());
        assertEquals("Collaborator", collaborator.lastName());
        assertEquals(email, collaborator.email());
        assertEquals(airTransportCompany, collaborator.airTransportCompany());
        assertNull(collaborator.airControlArea());
        assertEquals(email, collaborator.identity());
    }

    /**
     * Purpose: verify that first name is mandatory.
     * Scenario: create a collaborator with a blank first name.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureBlankFirstNameIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new CustomerCollaborator(
                        " ",
                        "Collaborator",
                        CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                        airTransportCompany("TP", "TAP"),
                        SystemUsername.valueOf("collaborator@aisafe.com")
                )
        );
    }

    /**
     * Purpose: verify that last name is mandatory.
     * Scenario: create a collaborator with a blank last name.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureBlankLastNameIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new CustomerCollaborator(
                        "Customer",
                        " ",
                        CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                        airTransportCompany("TP", "TAP"),
                        SystemUsername.valueOf("collaborator@aisafe.com")
                )
        );
    }

    /**
     * Purpose: verify that collaborator email is mandatory.
     * Scenario: create a collaborator without an email value object.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureNullEmailIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new CustomerCollaborator(
                        "Customer",
                        "Collaborator",
                        null,
                        airTransportCompany("TP", "TAP"),
                        SystemUsername.valueOf("collaborator@aisafe.com")
                )
        );
    }

    /**
     * Purpose: verify that customer association is mandatory.
     * Scenario: create a collaborator without a customer association.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureNullCustomerAssociationIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new CustomerCollaborator(
                        "Customer",
                        "Collaborator",
                        CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                        null,
                        null,
                        SystemUsername.valueOf("collaborator@aisafe.com")
                )
        );
    }

    @Test
    void ensureMultipleCustomerAssociationsAreRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new CustomerCollaborator(
                        "Customer",
                        "Collaborator",
                        CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                        airTransportCompany("TP", "TAP"),
                        airControlArea("LISACC"),
                        SystemUsername.valueOf("collaborator@aisafe.com")
                )
        );
    }

    /**
     * Purpose: verify that the associated system username value object is mandatory.
     * Scenario: create a collaborator without a system username value object.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureNullSystemUsernameIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new CustomerCollaborator(
                        "Customer",
                        "Collaborator",
                        CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                        airTransportCompany("TP", "TAP"),
                        null
                )
        );
    }

    /**
     * Purpose: verify aggregate identity comparison.
     * Scenario: compare collaborators by aggregate identity.
     * Expected output: sameAs returns true only for another collaborator with the same identity.
     */
    @Test
    void ensureSameAsUsesIdentity() {
        // Arrange
        final CustomerCollaborator first = new CustomerCollaborator(
                "Customer",
                "Collaborator",
                CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                airTransportCompany("TP", "TAP"),
                SystemUsername.valueOf("collaborator@aisafe.com")
        );
        final CustomerCollaborator second = new CustomerCollaborator(
                "Other",
                "Name",
                CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                airTransportCompany("TP", "TAP"),
                SystemUsername.valueOf("collaborator@aisafe.com")
        );
        final CustomerCollaborator third = new CustomerCollaborator(
                "Other",
                "Name",
                CollaboratorEmail.valueOf("other.collaborator@aisafe.com"),
                airControlArea("LISACC"),
                SystemUsername.valueOf("other.collaborator@aisafe.com")
        );

        // Act / Assert
        assertTrue(first.sameAs(second));
        assertFalse(first.sameAs(third));
        assertFalse(first.sameAs("not a collaborator"));
    }

    @Test
    void ensureCollaboratorIsActiveWhenCreated() {

        final CustomerCollaborator collaborator = buildCollaborator();

        assertTrue(collaborator.isActive());
    }

    @Test
    void ensureDisableSetsCollaboratorInactive() {

        final CustomerCollaborator collaborator =
                buildCollaborator();

        collaborator.disable();

        assertFalse(collaborator.isActive());
    }

    @Test
    void ensureEnableSetsCollaboratorActive() {

        final CustomerCollaborator collaborator =
                buildCollaborator();

        collaborator.enable();

        assertTrue(collaborator.isActive());
    }

    private CustomerCollaborator buildCollaborator() {

        return new CustomerCollaborator(
                "John",
                "Doe",
                CollaboratorEmail.valueOf("john@test.com"),
                airTransportCompany("TP", "TAP"),
                SystemUsername.valueOf("john@test.com")
        );
    }

    private AirTransportCompany airTransportCompany(final String iataCode, final String icaoCode) {

        return new AirTransportCompany(
                "Test Company " + iataCode,
                Country.valueOf("Portugal"),
                IATACode.valueOf(iataCode),
                ICAOCode.valueOf(icaoCode)
        );
    }

    private AirControlArea airControlArea(final String code) {

        return new AirControlArea(
                AirControlAreaCode.valueOf(code),
                "Test Area " + code,
                GeographicBoundary.valueOf(35.0, 45.0, -15.0, -5.0),
                MinimumFuelRequirement.valueOf(1000, FuelUnit.valueOf("KG"))
        );
    }
}
