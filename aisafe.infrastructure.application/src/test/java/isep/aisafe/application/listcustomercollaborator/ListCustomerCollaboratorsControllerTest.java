package isep.aisafe.application.listcustomercollaborator;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.aircontrolarea.domain.*;
import isep.aisafe.airtransportcompany.domain.*;
import isep.aisafe.application.listcustomercollaborators.ListCustomerCollaboratorsController;
import isep.aisafe.customercollaborator.domain.*;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListCustomerCollaboratorsControllerTest {

    @BeforeEach
    void clearRepositories() {
        new InMemoryCustomerCollaboratorRepository().clear();
    }

    @Test
    void ensureListForAirTransportCompanyReturnsOnlyActiveCollaborators() {

        final Fixture fixture = new Fixture();

        fixture.bootstrapAirTransportCompany("TP");

        final CustomerCollaborator active =
                fixture.registerCompanyCollaborator("john@test.com", "TP");

        final CustomerCollaborator inactive =
                fixture.registerCompanyCollaborator("inactive@test.com", "TP");

        inactive.disable();

        final List<CustomerCollaborator> result =
                fixture.controller().listForAirTransportCompany("TP");

        assertArrayEquals(
                AisafePermissions.USER_MANAGEMENT,
                fixture.authz.requestedRoles()
        );

        assertEquals(1, result.size());
        assertTrue(result.contains(active));
        assertFalse(result.contains(inactive));
    }

    @Test
    void ensureUnknownAirTransportCompanyIsRejected() {

        final Fixture fixture = new Fixture();

        assertThrows(IllegalArgumentException.class, () ->
                fixture.controller().listForAirTransportCompany("SS")
        );
    }

    @Test
    void ensureListForAirControlAreaReturnsOnlyActiveCollaborators() {

        final Fixture fixture = new Fixture();

        fixture.bootstrapAirControlArea("LISACC");

        final CustomerCollaborator active =
                fixture.registerAreaCollaborator("controller@test.com", "LISACC");

        final CustomerCollaborator inactive =
                fixture.registerAreaCollaborator("active.controller@test.com", "LISACC");

        inactive.disable();

        final List<CustomerCollaborator> result =
                fixture.controller().listForAirControlArea("LISACC");

        assertArrayEquals(
                AisafePermissions.BACKOFFICE_CONFIGURATION,
                fixture.authz.requestedRoles()
        );

        assertEquals(1, result.size());
        assertTrue(result.contains(active));
        assertFalse(result.contains(inactive));
    }

    @Test
    void ensureUnknownAirControlAreaIsRejected() {

        final Fixture fixture = new Fixture();

        assertThrows(IllegalArgumentException.class, () ->
                fixture.controller().listForAirControlArea("UNKNOWN")
        );
    }

    private static class Fixture {

        private final TestAuthorizationService authz =
                new TestAuthorizationService();

        private final InMemoryCustomerCollaboratorRepository collaboratorRepository =
                new InMemoryCustomerCollaboratorRepository();

        private final InMemoryAirTransportCompanyRepository companyRepository =
                new InMemoryAirTransportCompanyRepository();

        private final InMemoryAirControlAreaRepository areaRepository =
                new InMemoryAirControlAreaRepository();

        ListCustomerCollaboratorsController controller() {
            return new ListCustomerCollaboratorsController(
                    authz,
                    collaboratorRepository,
                    companyRepository,
                    areaRepository
            );
        }

        void bootstrapAirTransportCompany(final String iata) {

            final AirTransportCompany company =
                    new AirTransportCompany(
                            "Test Company",
                            Country.valueOf("Portugal"),
                            IATACode.valueOf(iata),
                            ICAOCode.valueOf("TST")
                    );

            companyRepository.save(company);
        }

        void bootstrapAirControlArea(final String areaCode) {

            final AirControlArea area =
                    new AirControlArea(
                            AirControlAreaCode.valueOf(areaCode),
                            "Lisbon ACC",
                            GeographicBoundary.valueOf(30, 45, -15, 5),
                            MinimumFuelRequirement.valueOf(
                                    5000,
                                    FuelUnit.valueOf("KG")
                            )
                    );

            areaRepository.save(area);
        }

        CustomerCollaborator registerCompanyCollaborator(
                final String email,
                final String iata) {

            final CustomerCollaborator collaborator =
                    new CustomerCollaborator(
                            "John",
                            "Doe",
                            CollaboratorEmail.valueOf(email),
                            companyRepository.ofIdentity(IATACode.valueOf(iata)).orElseThrow(),
                            SystemUsername.valueOf(email)
                    );

            collaboratorRepository.save(collaborator);

            return collaborator;
        }

        CustomerCollaborator registerAreaCollaborator(
                final String email,
                final String areaCode) {

            final CustomerCollaborator collaborator =
                    new CustomerCollaborator(
                            "John",
                            "Doe",
                            CollaboratorEmail.valueOf(email),
                            areaRepository.ofIdentity(AirControlAreaCode.valueOf(areaCode)).orElseThrow(),
                            SystemUsername.valueOf(email)
                    );

            collaboratorRepository.save(collaborator);

            return collaborator;
        }
    }

    private static class TestAuthorizationService
            extends AisafeAuthorizationService {

        private Role[] requestedRoles;

        @Override
        public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {
            this.requestedRoles = roles;
        }

        Role[] requestedRoles() {
            return requestedRoles;
        }
    }
}
