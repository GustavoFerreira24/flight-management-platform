package isep.aisafe.customercollaborator.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerReferenceTest {

    /**
     * Purpose: verify air transport company customer reference creation.
     * Scenario: create a reference with a lowercase IATA code.
     * Expected output: the reference stores the air transport company type and normalized identity.
     */
    @Test
    void ensureAirTransportCompanyReferenceIsCreated() {
        // Arrange / Act
        final CustomerReference reference = CustomerReference.airTransportCompany("tp");

        // Assert
        assertEquals(CustomerType.AIR_TRANSPORT_COMPANY, reference.customerType());
        assertEquals("TP", reference.customerIdentity());
    }

    /**
     * Purpose: verify air control area customer reference creation.
     * Scenario: create a reference with a lowercase area code.
     * Expected output: the reference stores the air control area type and normalized identity.
     */
    @Test
    void ensureAirControlAreaReferenceIsCreated() {
        // Arrange / Act
        final CustomerReference reference = CustomerReference.airControlArea("lisacc");

        // Assert
        assertEquals(CustomerType.AIR_CONTROL_AREA, reference.customerType());
        assertEquals("LISACC", reference.customerIdentity());
    }

    /**
     * Purpose: verify that a customer identity is mandatory.
     * Scenario: create a customer reference with a blank identity.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureBlankCustomerIdentityIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> CustomerReference.airTransportCompany(" "));
    }

    /**
     * Purpose: verify value equality.
     * Scenario: compare equivalent customer references created with different casing.
     * Expected output: both references are equal and have the same hash code.
     */
    @Test
    void ensureEqualityUsesTypeAndIdentity() {
        // Arrange
        final CustomerReference first = CustomerReference.airTransportCompany("tp");
        final CustomerReference second = CustomerReference.airTransportCompany("TP");

        // Act / Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, CustomerReference.airControlArea("TP"));
    }
}
