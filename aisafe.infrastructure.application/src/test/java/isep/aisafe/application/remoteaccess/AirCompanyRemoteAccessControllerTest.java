package isep.aisafe.application.remoteaccess;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.CredentialHandler;
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
        assertTrue(response.payload().contains("CREATE_FLIGHT"));
        assertTrue(response.payload().contains("ADD_PILOT"));
        assertTrue(response.payload().contains("LIST_PILOT_ROSTER"));
        assertTrue(response.payload().contains("DEACTIVATE_PILOT"));
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

    @Test
    void ensureDecommissionAircraftDelegatesToRemoteOperations() {
        final FakeOperations operations = new FakeOperations();
        final AirCompanyRemoteAccessController controller = controller(new FakeCredentialHandler(true),
                new FakeAuthorizationService(), operations);

        controller.handle("LOGIN|tap.collaborator@aisafe.com|Password1");
        final RemoteAccessResponse response = controller.handle("DECOMMISSION_AIRCRAFT|CS-TAP");

        assertTrue(response.isOk());
        assertEquals("decommissioned CS-TAP", response.payload());
        assertEquals("CS-TAP", operations.decommissionRegistration);
    }

    @Test
    void ensureCreateRouteDelegatesToRemoteOperations() {
        final FakeOperations operations = new FakeOperations();
        final AirCompanyRemoteAccessController controller = controller(new FakeCredentialHandler(true),
                new FakeAuthorizationService(), operations);

        controller.handle("LOGIN|tap.collaborator@aisafe.com|Password1");
        final RemoteAccessResponse response = controller.handle("CREATE_ROUTE|TP123|LPPT|LPPR");

        assertTrue(response.isOk());
        assertEquals("route TP123", response.payload());
        assertEquals("TP123", operations.routeDesignator);
        assertEquals("LPPT", operations.departureAirportIcao);
        assertEquals("LPPR", operations.destinationAirportIcao);
    }

    @Test
    void ensureCreateFlightDelegatesToRemoteOperations() {
        final FakeOperations operations = new FakeOperations();
        final AirCompanyRemoteAccessController controller = controller(new FakeCredentialHandler(true),
                new FakeAuthorizationService(), operations);

        controller.handle("LOGIN|tap.collaborator@aisafe.com|Password1");
        final RemoteAccessResponse response = controller.handle("CREATE_FLIGHT|TP123|TP123A|2027-01-10|10:00");

        assertTrue(response.isOk());
        assertEquals("flight TP123A", response.payload());
        assertEquals("TP123", operations.routeDesignator);
        assertEquals("TP123A", operations.flightDesignator);
        assertEquals("2027-01-10", operations.departureDay);
        assertEquals("10:00", operations.departureTime);
    }

    @Test
    void ensureAddPilotDelegatesToRemoteOperations() {
        final FakeOperations operations = new FakeOperations();
        final AirCompanyRemoteAccessController controller = controller(new FakeCredentialHandler(true),
                new FakeAuthorizationService(), operations);

        controller.handle("LOGIN|tap.collaborator@aisafe.com|Password1");
        final RemoteAccessResponse response = controller.handle("ADD_PILOT|ABC");

        assertTrue(response.isOk());
        assertEquals("pilot ABC added", response.payload());
        assertEquals("ABC", operations.pilotId);
    }

    @Test
    void ensureListPilotRosterDelegatesToRemoteOperations() {
        final FakeOperations operations = new FakeOperations();
        final AirCompanyRemoteAccessController controller = controller(new FakeCredentialHandler(true),
                new FakeAuthorizationService(), operations);

        controller.handle("LOGIN|tap.collaborator@aisafe.com|Password1");
        final RemoteAccessResponse response = controller.handle("LIST_PILOT_ROSTER");

        assertEquals("DATA", response.status());
        assertEquals("ABC; name=Ben Dover", response.payload());
        assertTrue(operations.listPilotRosterCalled);
    }

    @Test
    void ensureDeactivatePilotDelegatesToRemoteOperations() {
        final FakeOperations operations = new FakeOperations();
        final AirCompanyRemoteAccessController controller = controller(new FakeCredentialHandler(true),
                new FakeAuthorizationService(), operations);

        controller.handle("LOGIN|tap.collaborator@aisafe.com|Password1");
        final RemoteAccessResponse response = controller.handle("DEACTIVATE_PILOT|ABC");

        assertTrue(response.isOk());
        assertEquals("pilot ABC deactivated", response.payload());
        assertEquals("ABC", operations.deactivatedPilotId);
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

    @Test
    void ensureInvalidCreateFlightArgumentCountIsRejected() {
        final FakeOperations operations = new FakeOperations();
        final AirCompanyRemoteAccessController controller = controller(new FakeCredentialHandler(true),
                new FakeAuthorizationService(), operations);

        controller.handle("LOGIN|tap.collaborator@aisafe.com|Password1");
        final RemoteAccessResponse response = controller.handle("CREATE_FLIGHT|TP123|TP123A");

        assertTrue(response.isError());
        assertEquals("CREATE_FLIGHT expects 4 argument(s).", response.payload());
        assertNull(operations.flightDesignator);
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
        private boolean listPilotRosterCalled;
        private String registration;
        private String decommissionRegistration;
        private String routeDesignator;
        private String departureAirportIcao;
        private String destinationAirportIcao;
        private String flightDesignator;
        private String departureDay;
        private String departureTime;
        private String pilotId;
        private String deactivatedPilotId;
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
            decommissionRegistration = registration;
            return "decommissioned " + registration;
        }

        @Override
        public String createFlightRoute(
                final String routeDesignator,
                final String departureAirportIcao,
                final String destinationAirportIcao) {

            this.routeDesignator = routeDesignator;
            this.departureAirportIcao = departureAirportIcao;
            this.destinationAirportIcao = destinationAirportIcao;
            return "route " + routeDesignator;
        }

        @Override
        public String createFlight(
                final String routeDesignator,
                final String flightDesignator,
                final String departureDay,
                final String departureTime) {

            this.routeDesignator = routeDesignator;
            this.flightDesignator = flightDesignator;
            this.departureDay = departureDay;
            this.departureTime = departureTime;
            return "flight " + flightDesignator;
        }

        @Override
        public String addPilot(final String pilotId) {
            this.pilotId = pilotId;
            return "pilot " + pilotId + " added";
        }

        @Override
        public String listPilotRoster() {
            listPilotRosterCalled = true;
            return "ABC; name=Ben Dover";
        }

        @Override
        public String deactivatePilot(final String pilotId) {
            deactivatedPilotId = pilotId;
            return "pilot " + pilotId + " deactivated";
        }
    }
}
