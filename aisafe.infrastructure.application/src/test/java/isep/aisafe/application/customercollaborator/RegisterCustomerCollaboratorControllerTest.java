package isep.aisafe.application.customercollaborator;

import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import eapli.framework.infrastructure.authz.domain.model.SystemUser;
import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.InMemoryAirControlAreaRepository;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
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
import isep.aisafe.usermanagement.domain.AisafeRoles;
import org.junit.jupiter.api.Test;

import java.util.Set;

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
                customerRepositories.airControlAreas,
                new FakeUserManagementService()
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
        final FakeUserManagementService userService = new FakeUserManagementService();
        final RegisterCustomerCollaboratorController controller = controller(
                authz,
                repository,
                customerRepositories,
                userService
        );

        // Act
        final CustomerCollaborator collaborator = controller.registerCollaboratorForAirTransportCompany(
                "Customer",
                "Collaborator",
                "registered.company.collaborator@aisafe.com",
                "TP",
                "Password1"
        );

        // Assert
        assertArrayEquals(AisafePermissions.BACKOFFICE_CONFIGURATION, authz.requestedRoles());
        assertEquals("Customer", collaborator.firstName());
        assertEquals("Collaborator", collaborator.lastName());
        assertEquals("registered.company.collaborator@aisafe.com", collaborator.email().toString());
        assertEquals("registered.company.collaborator@aisafe.com", collaborator.systemUsername().toString());
        assertEquals(CustomerType.AIR_TRANSPORT_COMPANY, collaborator.customerReference().customerType());
        assertEquals("TP", collaborator.customerReference().customerIdentity());
        assertEquals("registered.company.collaborator@aisafe.com", userService.username);
        assertEquals("Password1", userService.password);
        assertEquals(Set.of(AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR), userService.roles);
        assertEquals(1, userService.calls);
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
        final FakeUserManagementService userService = new FakeUserManagementService();
        final RegisterCustomerCollaboratorController controller = controller(
                new CapturingAuthorizationService(),
                repository,
                customerRepositories,
                userService
        );

        // Act
        final CustomerCollaborator collaborator = controller.registerCollaboratorForAirControlArea(
                "Customer",
                "Collaborator",
                "registered.area.collaborator@aisafe.com",
                "LISACC",
                "Password1"
        );

        // Assert
        assertEquals(CustomerReference.airControlArea("LISACC"), collaborator.customerReference());
        assertEquals(Set.of(AisafeRoles.FLIGHT_CONTROL_OPERATOR), userService.roles);
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
        final FakeUserManagementService userService = new FakeUserManagementService();
        final RegisterCustomerCollaboratorController controller = controller(
                new CapturingAuthorizationService(),
                repository,
                customerRepositories,
                userService
        );

        controller.registerCollaboratorForAirTransportCompany(
                "Customer",
                "Collaborator",
                "duplicate.collaborator@aisafe.com",
                "TP",
                "Password1"
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerCollaboratorForAirControlArea(
                        "Other",
                        "Collaborator",
                        "duplicate.collaborator@aisafe.com",
                        "LISACC",
                        "Password1"
                )
        );
        assertEquals(1, userService.calls);
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
        final FakeUserManagementService userService = new FakeUserManagementService();
        final RegisterCustomerCollaboratorController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryCustomerCollaboratorRepository(),
                customerRepositories,
                userService
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerCollaboratorForAirTransportCompany(
                        "Customer",
                        "Collaborator",
                        "unknown.company.collaborator@aisafe.com",
                        "XX",
                        "Password1"
                )
        );
        assertEquals(0, userService.calls);
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
        final FakeUserManagementService userService = new FakeUserManagementService();
        final RegisterCustomerCollaboratorController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryCustomerCollaboratorRepository(),
                customerRepositories,
                userService
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerCollaboratorForAirControlArea(
                        "Customer",
                        "Collaborator",
                        "unknown.area.collaborator@aisafe.com",
                        "UNKNOWN",
                        "Password1"
                )
        );
        assertEquals(0, userService.calls);
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
        final FakeUserManagementService userService = new FakeUserManagementService();
        final RegisterCustomerCollaboratorController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryCustomerCollaboratorRepository(),
                customerRepositories,
                userService
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerCollaboratorForAirTransportCompany(
                        "",
                        "Collaborator",
                        "invalid.data.collaborator@aisafe.com",
                        "TP",
                        "Password1"
                )
        );
        assertEquals(0, userService.calls);
    }

    /**
     * Purpose: verify that the initial password is mandatory for system-user creation.
     * Scenario: register a collaborator with a blank initial password.
     * Expected output: an IllegalArgumentException is thrown and no system user is created.
     */
    @Test
    void ensureBlankInitialPasswordIsRejected() {
        // Arrange
        final CustomerRepositories customerRepositories = customerRepositories();
        final FakeUserManagementService userService = new FakeUserManagementService();
        final RegisterCustomerCollaboratorController controller = controller(
                new CapturingAuthorizationService(),
                new InMemoryCustomerCollaboratorRepository(),
                customerRepositories,
                userService
        );

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerCollaboratorForAirTransportCompany(
                        "Customer",
                        "Collaborator",
                        "blank.password.collaborator@aisafe.com",
                        "TP",
                        " "
                )
        );
        assertEquals(0, userService.calls);
    }

    private RegisterCustomerCollaboratorController controller(
            final CapturingAuthorizationService authz,
            final InMemoryCustomerCollaboratorRepository collaboratorRepository,
            final CustomerRepositories customerRepositories,
            final FakeUserManagementService userService) {
        return new RegisterCustomerCollaboratorController(
                authz,
                collaboratorRepository,
                customerRepositories.airTransportCompanies,
                customerRepositories.airControlAreas,
                userService
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
                "Lisbon Area Control Centre",
                GeographicBoundary.valueOf(-90, 90, -180, 180),
                MinimumFuelRequirement.valueOf(1, FuelUnit.valueOf("KG"))
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

    private static final class FakeUserManagementService extends UserManagementService {

        private String username;
        private String password;
        private Set<Role> roles;
        private int calls;

        private FakeUserManagementService() {
            super(null, null, null);
        }

        @Override
        public SystemUser registerNewUser(
                final String username,
                final String rawPassword,
                final String firstName,
                final String lastName,
                final String email,
                final Set<Role> roles) {
            calls++;
            this.username = username;
            password = rawPassword;
            this.roles = roles;
            return null;
        }
    }
}
