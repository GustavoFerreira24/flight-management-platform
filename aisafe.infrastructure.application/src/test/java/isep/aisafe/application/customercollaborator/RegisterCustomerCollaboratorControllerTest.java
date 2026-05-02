package isep.aisafe.application.customercollaborator;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.InMemoryAirControlAreaRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.airtransportcompany.domain.InMemoryAirTransportCompanyRepository;
import isep.aisafe.customercollaborator.domain.CollaboratorEmail;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerReference;
import isep.aisafe.customercollaborator.domain.CustomerType;
import isep.aisafe.customercollaborator.domain.InMemoryCustomerCollaboratorRepository;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.support.CapturingAuthorizationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterCustomerCollaboratorControllerTest {

    /**
     * Purpose: verify that the testable constructor can be used with repositories.
     * Scenario: create the controller with in-memory repositories.
     * Expected output: a non-null controller instance is created.
     */
    @Test
    void ensureTestableConstructorIsAvailable() {
        // Arrange
        final CustomerRepositories customerRepositories = customerRepositories();

        // Act / Assert
        assertNotNull(new RegisterCustomerCollaboratorController(
                new CapturingAuthorizationService(),
                new InMemoryCustomerCollaboratorRepository(),
                customerRepositories.airTransportCompanies,
                customerRepositories.airControlAreas
        ));
    }

    /**
     * Purpose: verify successful collaborator registration for an air transport company customer.
     * Scenario: an authorized backoffice operation submits valid collaborator data for an existing company.
     * Expected output: authorization is checked, the collaborator is persisted and linked to that company.
     */
    @Test
    void ensureCollaboratorIsRegisteredForAirTransportCompany() {
        // Arrange
        final CapturingAuthorizationService authz = new CapturingAuthorizationService();
        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();
        final CustomerRepositories customerRepositories = customerRepositories();
        final RegisterCustomerCollaboratorController controller = controller(
                authz,
                repository,
                customerRepositories
        );

        // Act
        final CustomerCollaborator collaborator = controller.registerCollaboratorForAirTransportCompany(
                "Customer",
                "Collaborator",
                "registered.company.collaborator@aisafe.com",
                "TP"
        );

        // Assert
        assertArrayEquals(AisafePermissions.BACKOFFICE_CONFIGURATION, authz.requestedRoles());
        assertEquals("Customer", collaborator.firstName());
        assertEquals("Collaborator", collaborator.lastName());
        assertEquals("registered.company.collaborator@aisafe.com", collaborator.email().toString());
        assertEquals(CustomerType.AIR_TRANSPORT_COMPANY, collaborator.customerReference().customerType());
        assertEquals("TP", collaborator.customerReference().customerIdentity());
        assertTrue(repository.findByEmail(
                CollaboratorEmail.valueOf("registered.company.collaborator@aisafe.com")
        ).isPresent());
    }

    /**
     * Purpose: verify successful collaborator registration for an air control area customer.
     * Scenario: an authorized backoffice operation submits valid collaborator data for an existing area.
     * Expected output: the collaborator is persisted and linked to that area.
     */
    @Test
    void ensureCollaboratorIsRegisteredForAirControlArea() {
        // Arrange
        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();
        final CustomerRepositories customerRepositories = customerRepositories();
        final RegisterCustomerCollaboratorController controller = controller(
                new CapturingAuthorizationService(),
                repository,
                customerRepositories
        );

        // Act
        final CustomerCollaborator collaborator = controller.registerCollaboratorForAirControlArea(
                "Customer",
                "Collaborator",
                "registered.area.collaborator@aisafe.com",
                "LISACC"
        );

        // Assert
        assertEquals(CustomerReference.airControlArea("LISACC"), collaborator.customerReference());
        assertTrue(repository.findByEmail(
                CollaboratorEmail.valueOf("registered.area.collaborator@aisafe.com")
        ).isPresent());
    }

    /**
     * Purpose: verify that collaborator email uniqueness is enforced across customer types.
     * Scenario: register two collaborators with the same email for different customers.
     * Expected output: the second registration throws an IllegalArgumentException.
     */
    @Test
    void ensureDuplicateCollaboratorEmailIsRejected() {
        // Arrange
        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();
        final CustomerRepositories customerRepositories = customerRepositories();
        final RegisterCustomerCollaboratorController controller = controller(
                new CapturingAuthorizationService(),
                repository,
                customerRepositories
        );

        controller.registerCollaboratorForAirTransportCompany(
                "Customer",
                "Collaborator",
                "duplicate.collaborator@aisafe.com",
                "TP"
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerCollaboratorForAirControlArea(
                        "Other",
                        "Collaborator",
                        "duplicate.collaborator@aisafe.com",
                        "LISACC"
                )
        );
    }

    /**
     * Purpose: verify that the air transport company customer must exist.
     * Scenario: register a collaborator for an unknown IATA code.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureUnknownAirTransportCompanyIsRejected() {
        // Arrange
        final CustomerRepositories customerRepositories = customerRepositories();
        final RegisterCustomerCollaboratorController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryCustomerCollaboratorRepository(),
                customerRepositories
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerCollaboratorForAirTransportCompany(
                        "Customer",
                        "Collaborator",
                        "unknown.company.collaborator@aisafe.com",
                        "XX"
                )
        );
    }

    /**
     * Purpose: verify that the air control area customer must exist.
     * Scenario: register a collaborator for an unknown air control area code.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureUnknownAirControlAreaIsRejected() {
        // Arrange
        final CustomerRepositories customerRepositories = customerRepositories();
        final RegisterCustomerCollaboratorController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryCustomerCollaboratorRepository(),
                customerRepositories
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerCollaboratorForAirControlArea(
                        "Customer",
                        "Collaborator",
                        "unknown.area.collaborator@aisafe.com",
                        "UNKNOWN"
                )
        );
    }

    /**
     * Purpose: verify that invalid collaborator data is not persisted.
     * Scenario: register a collaborator with a blank first name.
     * Expected output: an IllegalArgumentException is thrown.
     */
    @Test
    void ensureInvalidCollaboratorDataIsRejected() {
        // Arrange
        final CustomerRepositories customerRepositories = customerRepositories();
        final RegisterCustomerCollaboratorController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryCustomerCollaboratorRepository(),
                customerRepositories
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerCollaboratorForAirTransportCompany(
                        "",
                        "Collaborator",
                        "invalid.data.collaborator@aisafe.com",
                        "TP"
                )
        );
    }

    private RegisterCustomerCollaboratorController controller(
            final CapturingAuthorizationService authz,
            final InMemoryCustomerCollaboratorRepository collaboratorRepository,
            final CustomerRepositories customerRepositories) {
        return new RegisterCustomerCollaboratorController(
                authz,
                collaboratorRepository,
                customerRepositories.airTransportCompanies,
                customerRepositories.airControlAreas
        );
    }

    private CustomerRepositories customerRepositories() {
        final InMemoryAirTransportCompanyRepository airTransportCompanies =
                new InMemoryAirTransportCompanyRepository();
        airTransportCompanies.save(new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        ));

        final InMemoryAirControlAreaRepository airControlAreas =
                new InMemoryAirControlAreaRepository();
        airControlAreas.save(new AirControlArea(
                AirControlAreaCode.valueOf("LISACC"),
                "Lisbon Area Control Centre"
        ));

        return new CustomerRepositories(airTransportCompanies, airControlAreas);
    }

    private static class CustomerRepositories {

        private final InMemoryAirTransportCompanyRepository airTransportCompanies;
        private final InMemoryAirControlAreaRepository airControlAreas;

        CustomerRepositories(
                final InMemoryAirTransportCompanyRepository airTransportCompanies,
                final InMemoryAirControlAreaRepository airControlAreas) {
            this.airTransportCompanies = airTransportCompanies;
            this.airControlAreas = airControlAreas;
        }
    }
}
