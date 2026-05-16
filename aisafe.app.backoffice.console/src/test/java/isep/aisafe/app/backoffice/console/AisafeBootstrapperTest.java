package isep.aisafe.app.backoffice.console;

import eapli.framework.infrastructure.authz.application.UserManagementService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import eapli.framework.infrastructure.authz.domain.model.SystemUser;
import isep.aisafe.aircontrolarea.domain.*;
import isep.aisafe.airport.domain.Airport;
import isep.aisafe.airport.domain.InMemoryAirportRepository;
import isep.aisafe.customercollaborator.domain.CollaboratorEmail;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerReference;
import isep.aisafe.customercollaborator.domain.InMemoryCustomerCollaboratorRepository;
import isep.aisafe.usermanagement.domain.AisafeRoles;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static isep.aisafe.app.backoffice.console.AisafeBootstrapper.registerAirportIfMissing;
import static org.junit.jupiter.api.Assertions.*;

class AisafeBootstrapperTest {

    /**
     * Purpose: verify customer collaborator bootstrap creation.
     * Scenario: bootstrap a collaborator that does not exist yet.
     * Expected output: a collaborator is persisted and a matching system user is requested.
     */
    @Test
    void ensureCustomerCollaboratorIsBootstrappedWhenMissing() {
        // Arrange
        final FakeUserManagementService userService = new FakeUserManagementService();
        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();
        final CustomerReference customerReference = CustomerReference.airTransportCompany("TP");

        // Act
        final CustomerCollaborator collaborator = AisafeBootstrapper.registerCustomerCollaboratorIfMissing(
                userService,
                repository,
                "TAP",
                "Collaborator",
                "tap.collaborator@aisafe.com",
                "Password1",
                customerReference,
                Set.of(AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR)
        );

        // Assert
        assertEquals("TAP", collaborator.firstName());
        assertEquals("Collaborator", collaborator.lastName());
        assertEquals(customerReference, collaborator.customerReference());
        assertEquals("tap.collaborator@aisafe.com", collaborator.systemUsername().toString());
        assertTrue(repository.findByEmail(
                CollaboratorEmail.valueOf("tap.collaborator@aisafe.com")
        ).isPresent());
        assertEquals("tap.collaborator@aisafe.com", userService.username);
        assertEquals("Password1", userService.password);
        assertEquals(Set.of(AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR), userService.roles);
        assertEquals(1, userService.calls);
    }

    /**
     * Purpose: verify customer collaborator bootstrap idempotency.
     * Scenario: bootstrap the same collaborator twice.
     * Expected output: the second bootstrap reuses the existing collaborator and does not create another user.
     */
    @Test
    void ensureCustomerCollaboratorBootstrapIsIdempotent() {
        // Arrange
        final FakeUserManagementService userService = new FakeUserManagementService();
        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();
        final CustomerReference customerReference = CustomerReference.airControlArea("LISACC");

        AisafeBootstrapper.registerCustomerCollaboratorIfMissing(
                userService,
                repository,
                "Lisbon",
                "Controller",
                "lisacc.collaborator@aisafe.com",
                "Password1",
                customerReference,
                Set.of(AisafeRoles.FLIGHT_CONTROL_OPERATOR)
        );

        // Act
        final CustomerCollaborator existingCollaborator = AisafeBootstrapper.registerCustomerCollaboratorIfMissing(
                userService,
                repository,
                "Lisbon",
                "Controller",
                "lisacc.collaborator@aisafe.com",
                "Password1",
                customerReference,
                Set.of(AisafeRoles.FLIGHT_CONTROL_OPERATOR)
        );

        // Assert
        assertEquals(CustomerReference.airControlArea("LISACC"), existingCollaborator.customerReference());
        assertEquals(1, userService.calls);
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

    @Test
    void ensureAirportIsBootstrappedWhenMissing() {
        // Arrange
        final FakeUserManagementService userService = new FakeUserManagementService();
        final InMemoryAirControlAreaRepository areaRepo = new InMemoryAirControlAreaRepository();
        final InMemoryAirportRepository airportRepo = new InMemoryAirportRepository();

        final AirControlArea lisbonArea = new AirControlArea(
                AirControlAreaCode.valueOf("LISACC"),
                "Lisbon Area Control Centre",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );

        areaRepo.save(lisbonArea);


        final Airport airport = AisafeBootstrapper.registerAirportIfMissing(
                airportRepo,
                "LPPR",
                "OPO",
                "Francisco Sá Carneiro",
                "Portugal",
                "Porto",
                41.2355,
                -8.6703,
                69.0,
                lisbonArea
        );

        assertEquals("LPPR", airport.icaoCode().toString());
        assertEquals("OPO", airport.iataCode().toString());
        assertEquals("Francisco Sá Carneiro", airport.name());
        assertEquals("Portugal", airport.country().toString());
        assertEquals("Porto", airport.town());
        assertEquals(41.2355, airport.coordinates().latitude());
        assertEquals(-8.6703, airport.coordinates().longitude());
        assertEquals(69.0, airport.elevation().meters());
    }
}
