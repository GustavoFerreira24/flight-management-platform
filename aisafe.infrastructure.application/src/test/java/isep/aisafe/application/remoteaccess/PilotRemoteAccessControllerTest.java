package isep.aisafe.application.remoteaccess;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.CredentialHandler;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.usermanagement.domain.AisafeRoles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PilotRemoteAccessControllerTest {

    @Test
    void ensureHelpDoesNotRequireLogin() {
        // Arrange
        final PilotRemoteAccessController controller = controller(true);

        // Act
        final RemoteAccessResponse response = controller.handle("HELP");

        // Assert
        assertEquals("DATA", response.status());
        assertTrue(response.payload().contains("LOGIN"));
        assertTrue(response.payload().contains("CREATE_FLIGHT_PLAN"));
        assertTrue(response.payload().contains("CONSULT_WEATHER"));
        assertTrue(response.payload().contains("INSERT_WEATHER_DATA"));
        assertTrue(response.payload().contains("LIST_TESTABLE_FLIGHT_PLANS"));
        assertTrue(response.payload().contains("TEST_FLIGHT_PLAN"));
        assertTrue(response.payload().contains("CONSULT_FLIGHT_PLAN_TEST_REPORTS"));
    }

    @Test
    void ensureValidLoginOpensRemoteSession() {
        // Arrange
        final FakeCredentialHandler credentials = new FakeCredentialHandler(true);
        final FakeAuthorizationService authz = new FakeAuthorizationService();
        final PilotRemoteAccessController controller = controller(credentials, authz, new FakeOperations());

        // Act
        final RemoteAccessResponse response = controller.handle("LOGIN|UAA|Password1");

        // Assert
        assertTrue(response.isOk());
        assertTrue(controller.isAuthenticated());
        assertEquals("UAA", controller.username());
        assertArrayEquals(new Role[] { AisafeRoles.PILOT }, authz.requestedRoles);
        assertEquals("UAA", credentials.identity);
        assertEquals("Password1", credentials.password);
    }

    @Test
    void ensureInvalidLoginIsRejected() {
        // Arrange
        final PilotRemoteAccessController controller = controller(false);

        // Act
        final RemoteAccessResponse response = controller.handle("LOGIN|UAA|wrong");

        // Assert
        assertTrue(response.isError());
        assertFalse(controller.isAuthenticated());
        assertEquals("Invalid credentials.", response.payload());
    }

    @Test
    void ensureProtectedCommandRequiresLogin() {
        // Arrange
        final FakeOperations operations = new FakeOperations();
        final PilotRemoteAccessController controller = controller(
                new FakeCredentialHandler(true),
                new FakeAuthorizationService(),
                operations
        );

        // Act
        final RemoteAccessResponse response = controller.handle("CONSULT_WEATHER|2027-01-10|LISACC");

        // Assert
        assertTrue(response.isError());
        assertEquals("Remote login is required.", response.payload());
        assertFalse(operations.consultWeatherCalled);
    }

    @Test
    void ensureAuthorizationIsCheckedBeforeProtectedCommand() {
        // Arrange
        final FakeAuthorizationService authz = new FakeAuthorizationService();
        final FakeOperations operations = new FakeOperations();
        final PilotRemoteAccessController controller = controller(new FakeCredentialHandler(true), authz, operations);
        controller.handle("LOGIN|UAA|Password1");
        authz.exceptionToThrow = new IllegalStateException("not authorized");

        // Act
        final RemoteAccessResponse response = controller.handle("CONSULT_WEATHER|2027-01-10|LISACC");

        // Assert
        assertTrue(response.isError());
        assertEquals("not authorized", response.payload());
        assertFalse(operations.consultWeatherCalled);
    }

    @Test
    void ensureConsultWeatherDelegatesToRemoteOperations() {
        // Arrange
        final FakeOperations operations = new FakeOperations();
        final PilotRemoteAccessController controller = controller(
                new FakeCredentialHandler(true),
                new FakeAuthorizationService(),
                operations
        );
        controller.handle("LOGIN|UAA|Password1");

        // Act
        final RemoteAccessResponse response = controller.handle("CONSULT_WEATHER|2027-01-10|LISACC");

        // Assert
        assertEquals("DATA", response.status());
        assertEquals("weather data found", response.payload());
        assertTrue(operations.consultWeatherCalled);
        assertEquals("2027-01-10", operations.weatherDay);
        assertEquals("LISACC", operations.airControlAreaCode);
    }

    @Test
    void ensureCreateFlightPlanDelegatesToRemoteOperations() {
        // Arrange
        final FakeOperations operations = new FakeOperations();
        final PilotRemoteAccessController controller = controller(
                new FakeCredentialHandler(true),
                new FakeAuthorizationService(),
                operations
        );
        controller.handle("LOGIN|UAA|Password1");

        // Act
        final RemoteAccessResponse response = controller.handle("CREATE_FLIGHT_PLAN|FP001|TP123|CS-TXA|UAA|5000|KG");

        // Assert
        assertTrue(response.isOk());
        assertEquals("flight plan created", response.payload());
        assertTrue(operations.createFlightPlanCalled);
        assertEquals("FP001", operations.flightPlanIdentifier);
        assertEquals("TP123", operations.flightDesignator);
        assertEquals("CS-TXA", operations.aircraftRegistration);
        assertEquals("UAA", operations.pilotId);
        assertEquals("5000", operations.fuelAmount);
        assertEquals("KG", operations.fuelUnit);
    }

    @Test
    void ensureInsertWeatherDataDelegatesToRemoteOperations() {
        // Arrange
        final FakeOperations operations = new FakeOperations();
        final PilotRemoteAccessController controller = controller(
                new FakeCredentialHandler(true),
                new FakeAuthorizationService(),
                operations
        );
        controller.handle("LOGIN|UAA|Password1");

        // Act
        final RemoteAccessResponse response = controller.handle("INSERT_WEATHER_DATA|FP001|2027-01-10|LISACC");

        // Assert
        assertTrue(response.isOk());
        assertEquals("weather inserted", response.payload());
        assertTrue(operations.insertWeatherCalled);
        assertEquals("FP001", operations.flightPlanIdentifier);
        assertEquals("2027-01-10", operations.weatherDay);
        assertEquals("LISACC", operations.airControlAreaCode);
    }

    @Test
    void ensureListTestableFlightPlansDelegatesToRemoteOperations() {
        // Arrange
        final FakeOperations operations = new FakeOperations();
        final PilotRemoteAccessController controller = controller(
                new FakeCredentialHandler(true),
                new FakeAuthorizationService(),
                operations
        );
        controller.handle("LOGIN|UAA|Password1");

        // Act
        final RemoteAccessResponse response = controller.handle("LIST_TESTABLE_FLIGHT_PLANS");

        // Assert
        assertEquals("DATA", response.status());
        assertEquals("testable flight plans", response.payload());
        assertTrue(operations.listTestableFlightPlansCalled);
    }

    @Test
    void ensureTestFlightPlanDelegatesToRemoteOperations() {
        // Arrange
        final FakeOperations operations = new FakeOperations();
        final PilotRemoteAccessController controller = controller(
                new FakeCredentialHandler(true),
                new FakeAuthorizationService(),
                operations
        );
        controller.handle("LOGIN|UAA|Password1");

        // Act
        final RemoteAccessResponse response = controller.handle("TEST_FLIGHT_PLAN|FP001");

        // Assert
        assertTrue(response.isOk());
        assertEquals("flight plan tested", response.payload());
        assertTrue(operations.testFlightPlanCalled);
        assertEquals("FP001", operations.flightPlanIdentifier);
    }

    @Test
    void ensureConsultFlightPlanTestReportsDelegatesToRemoteOperations() {
        // Arrange
        final FakeOperations operations = new FakeOperations();
        final PilotRemoteAccessController controller = controller(
                new FakeCredentialHandler(true),
                new FakeAuthorizationService(),
                operations
        );
        controller.handle("LOGIN|UAA|Password1");

        // Act
        final RemoteAccessResponse response = controller.handle("CONSULT_FLIGHT_PLAN_TEST_REPORTS");

        // Assert
        assertEquals("DATA", response.status());
        assertEquals("flight plan test reports", response.payload());
        assertTrue(operations.consultFlightPlanTestReportsCalled);
    }

    @Test
    void ensureInvalidArgumentCountIsRejectedBeforeDelegation() {
        // Arrange
        final FakeOperations operations = new FakeOperations();
        final PilotRemoteAccessController controller = controller(
                new FakeCredentialHandler(true),
                new FakeAuthorizationService(),
                operations
        );
        controller.handle("LOGIN|UAA|Password1");

        // Act
        final RemoteAccessResponse response = controller.handle("INSERT_WEATHER_DATA|FP001|2027-01-10");

        // Assert
        assertTrue(response.isError());
        assertEquals("INSERT_WEATHER_DATA expects 3 argument(s).", response.payload());
        assertFalse(operations.insertWeatherCalled);
    }

    @Test
    void ensureInvalidCreateFlightPlanArgumentCountIsRejectedBeforeDelegation() {
        // Arrange
        final FakeOperations operations = new FakeOperations();
        final PilotRemoteAccessController controller = controller(
                new FakeCredentialHandler(true),
                new FakeAuthorizationService(),
                operations
        );
        controller.handle("LOGIN|UAA|Password1");

        // Act
        final RemoteAccessResponse response = controller.handle("CREATE_FLIGHT_PLAN|FP001|TP123|CS-TXA");

        // Assert
        assertTrue(response.isError());
        assertEquals("CREATE_FLIGHT_PLAN expects 6 argument(s).", response.payload());
        assertFalse(operations.createFlightPlanCalled);
    }

    @Test
    void ensureLogoutClosesRemoteSession() {
        // Arrange
        final PilotRemoteAccessController controller = controller(true);
        controller.handle("LOGIN|UAA|Password1");

        // Act
        final RemoteAccessResponse response = controller.handle("LOGOUT");

        // Assert
        assertTrue(response.isOk());
        assertFalse(controller.isAuthenticated());
        assertNull(controller.username());
    }

    private PilotRemoteAccessController controller(final boolean credentialsAccepted) {
        return controller(new FakeCredentialHandler(credentialsAccepted), new FakeAuthorizationService(), new FakeOperations());
    }

    private PilotRemoteAccessController controller(
            final CredentialHandler credentials,
            final AisafeAuthorizationService authz,
            final PilotRemoteOperations operations) {

        return new PilotRemoteAccessController(credentials, authz, operations);
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

    private static final class FakeOperations implements PilotRemoteOperations {

        private boolean consultWeatherCalled;
        private boolean createFlightPlanCalled;
        private boolean insertWeatherCalled;
        private boolean listTestableFlightPlansCalled;
        private boolean testFlightPlanCalled;
        private boolean consultFlightPlanTestReportsCalled;
        private String flightPlanIdentifier;
        private String flightDesignator;
        private String aircraftRegistration;
        private String pilotId;
        private String fuelAmount;
        private String fuelUnit;
        private String weatherDay;
        private String airControlAreaCode;

        @Override
        public String createFlightPlan(
                final String flightPlanIdentifier,
                final String flightDesignator,
                final String aircraftRegistration,
                final String pilotId,
                final String fuelAmount,
                final String fuelUnit) {

            createFlightPlanCalled = true;
            this.flightPlanIdentifier = flightPlanIdentifier;
            this.flightDesignator = flightDesignator;
            this.aircraftRegistration = aircraftRegistration;
            this.pilotId = pilotId;
            this.fuelAmount = fuelAmount;
            this.fuelUnit = fuelUnit;
            return "flight plan created";
        }

        @Override
        public String consultWeatherData(final String day, final String airControlAreaCode) {
            consultWeatherCalled = true;
            weatherDay = day;
            this.airControlAreaCode = airControlAreaCode;
            return "weather data found";
        }

        @Override
        public String insertWeatherData(
                final String flightPlanIdentifier,
                final String weatherDataDay,
                final String airControlAreaCode) {

            insertWeatherCalled = true;
            this.flightPlanIdentifier = flightPlanIdentifier;
            weatherDay = weatherDataDay;
            this.airControlAreaCode = airControlAreaCode;
            return "weather inserted";
        }

        @Override
        public String listTestableFlightPlans() {
            listTestableFlightPlansCalled = true;
            return "testable flight plans";
        }

        @Override
        public String testFlightPlan(final String flightPlanIdentifier) {
            testFlightPlanCalled = true;
            this.flightPlanIdentifier = flightPlanIdentifier;
            return "flight plan tested";
        }

        @Override
        public String consultFlightPlanTestReports() {
            consultFlightPlanTestReportsCalled = true;
            return "flight plan test reports";
        }
    }
}
