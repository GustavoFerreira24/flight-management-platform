package isep.aisafe.application.remoteaccess;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.authz.CredentialHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirCompanyRemoteAccessControllerTest {

    /**
     * Verifica que HELP pode ser usado sem login para descobrir comandos remotos.
     */
    @Test
    void ensureHelpDoesNotRequireLogin() {
        final AirCompanyRemoteAccessController controller = controller(true);

        final RemoteAccessResponse response = controller.handle("HELP");

        assertEquals("DATA", response.status());
        assertTrue(response.payload().contains("LOGIN"));
        assertTrue(response.payload().contains("LIST_FLEET"));
    }

    /**
     * Verifica que credenciais validas abrem uma sessao remota autorizada.
     */
    @Test
    void ensureValidLoginOpensRemoteSession() {
        final FakeCredentialHandler credentials = new FakeCredentialHandler(true);
        final FakeAuthorizationService authz = new FakeAuthorizationService();
        final AirCompanyRemoteAccessController controller = controller(credentials, authz, new FakeOperations());

        final RemoteAccessResponse response = controller.handle("LOGIN|tap.collaborator@aisafe.com|Password1");

        assertTrue(response.isOk());
        assertTrue(controller.isAuthenticated());
        assertEquals("tap.collaborator@aisafe.com", controller.username());
        assertArrayEquals(AisafePermissions.AIR_COMPANY_CONFIGURATION, authz.requestedRoles);
        assertEquals("tap.collaborator@aisafe.com", credentials.identity);
        assertEquals("Password1", credentials.password);
    }

    /**
     * Verifica que credenciais invalidas nao abrem sessao remota.
     */
    @Test
    void ensureInvalidLoginIsRejected() {
        final AirCompanyRemoteAccessController controller = controller(false);

        final RemoteAccessResponse response = controller.handle("LOGIN|tap.collaborator@aisafe.com|wrong");

        assertTrue(response.isError());
        assertFalse(controller.isAuthenticated());
        assertEquals("Invalid credentials.", response.payload());
    }

    /**
     * Verifica que comandos protegidos exigem login remoto previo.
     */
    @Test
    void ensureProtectedCommandRequiresLogin() {
        final FakeOperations operations = new FakeOperations();
        final AirCompanyRemoteAccessController controller = controller(new FakeCredentialHandler(true),
                new FakeAuthorizationService(), operations);

        final RemoteAccessResponse response = controller.handle("LIST_FLEET");

        assertTrue(response.isError());
        assertEquals("Remote login is required.", response.payload());
        assertFalse(operations.listFleetCalled);
    }

    /**
     * Verifica que comandos protegidos tambem validam autorizacao antes de delegar.
     */
    @Test
    void ensureAuthorizationIsCheckedBeforeProtectedCommand() {
        final FakeAuthorizationService authz = new FakeAuthorizationService();
        final FakeOperations operations = new FakeOperations();
        final AirCompanyRemoteAccessController controller = controller(new FakeCredentialHandler(true), authz, operations);

        controller.handle("LOGIN|tap.collaborator@aisafe.com|Password1");
        authz.exceptionToThrow = new IllegalStateException("not authorized");

        final RemoteAccessResponse response = controller.handle("LIST_FLEET");

        assertTrue(response.isError());
        assertEquals("not authorized", response.payload());
        assertFalse(operations.listFleetCalled);
    }

    /**
     * Verifica que LIST_FLEET e delegado nas operacoes remotas depois de login valido.
     */
    @Test
    void ensureListFleetDelegatesToRemoteOperations() {
        final FakeOperations operations = new FakeOperations();
        final AirCompanyRemoteAccessController controller = controller(new FakeCredentialHandler(true),
                new FakeAuthorizationService(), operations);

        controller.handle("LOGIN|tap.collaborator@aisafe.com|Password1");
        final RemoteAccessResponse response = controller.handle("LIST_FLEET");

        assertEquals("DATA", response.status());
        assertEquals("CS-TAP; model=A320neo", response.payload());
        assertTrue(operations.listFleetCalled);
    }

    /**
     * Verifica que REGISTER_AIRCRAFT converte numeros e delega a operacao.
     */
    @Test
    void ensureRegisterAircraftDelegatesToRemoteOperations() {
        final FakeOperations operations = new FakeOperations();
        final AirCompanyRemoteAccessController controller = controller(new FakeCredentialHandler(true),
                new FakeAuthorizationService(), operations);

        controller.handle("LOGIN|tap.collaborator@aisafe.com|Password1");
        final RemoteAccessResponse response = controller.handle(
                "REGISTER_AIRCRAFT|CS-TAP|Portugal|ACTIVE|6|150|20|10|A320neo|Airbus|LEAP-1A|CFM International"
        );

        assertTrue(response.isOk());
        assertEquals("registered CS-TAP", response.payload());
        assertEquals("CS-TAP", operations.registration);
        assertEquals(6, operations.crewSize);
        assertEquals(150, operations.economySeats);
        assertEquals(20, operations.businessSeats);
        assertEquals(10, operations.firstClassSeats);
    }

    /**
     * Verifica que valores numericos invalidos sao rejeitados antes da delegacao.
     */
    @Test
    void ensureInvalidIntegerArgumentIsRejected() {
        final FakeOperations operations = new FakeOperations();
        final AirCompanyRemoteAccessController controller = controller(new FakeCredentialHandler(true),
                new FakeAuthorizationService(), operations);

        controller.handle("LOGIN|tap.collaborator@aisafe.com|Password1");
        final RemoteAccessResponse response = controller.handle(
                "REGISTER_AIRCRAFT|CS-TAP|Portugal|ACTIVE|crew|150|20|10|A320neo|Airbus|LEAP-1A|CFM International"
        );

        assertTrue(response.isError());
        assertEquals("crewSize must be an integer.", response.payload());
        assertNull(operations.registration);
    }

    /**
     * Verifica que LOGOUT fecha a sessao remota local.
     */
    @Test
    void ensureLogoutClosesRemoteSession() {
        final AirCompanyRemoteAccessController controller = controller(true);

        controller.handle("LOGIN|tap.collaborator@aisafe.com|Password1");
        final RemoteAccessResponse response = controller.handle("LOGOUT");

        assertTrue(response.isOk());
        assertFalse(controller.isAuthenticated());
        assertNull(controller.username());
    }

    private AirCompanyRemoteAccessController controller(final boolean credentialsAccepted) {
        return controller(new FakeCredentialHandler(credentialsAccepted), new FakeAuthorizationService(), new FakeOperations());
    }

    private AirCompanyRemoteAccessController controller(
            final CredentialHandler credentials,
            final AisafeAuthorizationService authz,
            final AirCompanyRemoteOperations operations) {

        return new AirCompanyRemoteAccessController(credentials, authz, operations);
    }

    private static final class FakeCredentialHandler implements CredentialHandler {

        private final boolean accepted;
        private String identity;
        private String password;

        private FakeCredentialHandler(final boolean accepted) {
            this.accepted = accepted;
        }

        @Override
        public boolean authWithCredentialData(final String identity, final String password) {
            this.identity = identity;
            this.password = password;
            return accepted;
        }
    }

    private static final class FakeAuthorizationService extends AisafeAuthorizationService {

        private Role[] requestedRoles;
        private RuntimeException exceptionToThrow;

        @Override
        public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {
            requestedRoles = roles;
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
        }
    }

    private static final class FakeOperations implements AirCompanyRemoteOperations {

        private boolean listFleetCalled;
        private String registration;
        private int crewSize;
        private int economySeats;
        private int businessSeats;
        private int firstClassSeats;

        @Override
        public String listAircraftFleet() {
            listFleetCalled = true;
            return "CS-TAP; model=A320neo";
        }

        @Override
        public String registerAircraft(
                final String registration,
                final String registrationCountry,
                final String operationalStatus,
                final int crewSize,
                final int economySeats,
                final int businessSeats,
                final int firstClassSeats,
                final String aircraftModelName,
                final String aircraftModelManufacturer,
                final String engineModelName,
                final String engineModelManufacturer) {

            this.registration = registration;
            this.crewSize = crewSize;
            this.economySeats = economySeats;
            this.businessSeats = businessSeats;
            this.firstClassSeats = firstClassSeats;
            return "registered " + registration;
        }

        @Override
        public String decommissionAircraft(final String registration) {
            return "decommissioned " + registration;
        }

        @Override
        public String createFlightRoute(
                final String routeDesignator,
                final String departureAirportIcao,
                final String destinationAirportIcao) {

            return "route " + routeDesignator;
        }
    }
}
