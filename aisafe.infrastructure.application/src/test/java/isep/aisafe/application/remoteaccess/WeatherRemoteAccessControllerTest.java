package isep.aisafe.application.remoteaccess;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.CredentialHandler;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeatherRemoteAccessControllerTest {

    @Test
    void ensureHelpDoesNotRequireLogin() {
        final WeatherRemoteAccessController controller = controller(true);

        final RemoteAccessResponse response = controller.handle("HELP");

        assertEquals("DATA", response.status());
        assertTrue(response.payload().contains("LOGIN"));
        assertTrue(response.payload().contains("CONSULT_WEATHER"));
        assertTrue(response.payload().contains("REGISTER_WEATHER_DATA"));
        assertTrue(response.payload().contains("IMPORT_BULK_WEATHER_DATA"));
    }

    @Test
    void ensureValidLoginOpensRemoteSession() {
        final FakeCredentialHandler credentials = new FakeCredentialHandler(true);
        final FakeAuthorizationService authz = new FakeAuthorizationService();
        final WeatherRemoteAccessController controller = controller(credentials, authz, new FakeOperations());

        final RemoteAccessResponse response = controller.handle("LOGIN|weather@aisafe.com|Password1");

        assertTrue(response.isOk());
        assertTrue(controller.isAuthenticated());
        assertEquals("weather@aisafe.com", controller.username());
        assertArrayEquals(AisafePermissions.WEATHER_DATA_MANAGEMENT, authz.requestedRoles);
        assertEquals("weather@aisafe.com", credentials.identity);
        assertEquals("Password1", credentials.password);
    }

    @Test
    void ensureInvalidLoginIsRejected() {
        final WeatherRemoteAccessController controller = controller(false);

        final RemoteAccessResponse response = controller.handle("LOGIN|weather@aisafe.com|wrong");

        assertTrue(response.isError());
        assertFalse(controller.isAuthenticated());
        assertEquals("Invalid credentials.", response.payload());
    }

    @Test
    void ensureProtectedCommandRequiresLogin() {
        final FakeOperations operations = new FakeOperations();
        final WeatherRemoteAccessController controller = controller(
                new FakeCredentialHandler(true),
                new FakeAuthorizationService(),
                operations
        );

        final RemoteAccessResponse response = controller.handle("CONSULT_WEATHER|2027-01-10|LISACC");

        assertTrue(response.isError());
        assertEquals("Remote login is required.", response.payload());
        assertFalse(operations.consultWeatherCalled);
    }

    @Test
    void ensureAuthorizationIsCheckedBeforeProtectedCommand() {
        final FakeAuthorizationService authz = new FakeAuthorizationService();
        final FakeOperations operations = new FakeOperations();
        final WeatherRemoteAccessController controller = controller(new FakeCredentialHandler(true), authz, operations);

        controller.handle("LOGIN|weather@aisafe.com|Password1");
        authz.exceptionToThrow = new IllegalStateException("not authorized");

        final RemoteAccessResponse response = controller.handle("CONSULT_WEATHER|2027-01-10|LISACC");

        assertTrue(response.isError());
        assertEquals("not authorized", response.payload());
        assertFalse(operations.consultWeatherCalled);
    }

    @Test
    void ensureConsultWeatherDelegatesToRemoteOperations() {
        final FakeOperations operations = new FakeOperations();
        final WeatherRemoteAccessController controller = authenticatedController(operations);

        final RemoteAccessResponse response = controller.handle("CONSULT_WEATHER|2027-01-10|LISACC");

        assertEquals("DATA", response.status());
        assertEquals("weather data found", response.payload());
        assertTrue(operations.consultWeatherCalled);
        assertEquals("2027-01-10", operations.weatherDay);
        assertEquals("LISACC", operations.airControlAreaCode);
    }

    @Test
    void ensureRegisterWeatherDataDelegatesToRemoteOperations() {
        final FakeOperations operations = new FakeOperations();
        final WeatherRemoteAccessController controller = authenticatedController(operations);

        final RemoteAccessResponse response = controller.handle(
                "REGISTER_WEATHER_DATA|2027-01-10|LISACC|METAR|STATION|TEMPERATURE|17.5|C|120|38.7|-9.1"
        );

        assertTrue(response.isOk());
        assertEquals("weather registered", response.payload());
        assertTrue(operations.registerWeatherCalled);
        assertEquals("2027-01-10", operations.weatherDay);
        assertEquals("LISACC", operations.airControlAreaCode);
        assertEquals("METAR", operations.providerName);
        assertEquals("STATION", operations.providerType);
        assertEquals("TEMPERATURE", operations.parameter);
        assertEquals(17.5, operations.value);
        assertEquals("C", operations.unit);
        assertEquals(120, operations.altitude);
        assertEquals(38.7, operations.latitude);
        assertEquals(-9.1, operations.longitude);
    }

    @Test
    void ensureImportBulkWeatherDataDelegatesToRemoteOperations() {
        final FakeOperations operations = new FakeOperations();
        final WeatherRemoteAccessController controller = authenticatedController(operations);

        final RemoteAccessResponse response = controller.handle("IMPORT_BULK_WEATHER_DATA|C:/tmp/weather.csv");

        assertTrue(response.isOk());
        assertEquals("bulk imported", response.payload());
        assertTrue(operations.importBulkCalled);
        assertEquals("C:/tmp/weather.csv", operations.serverFilePath);
    }

    @Test
    void ensureInvalidNumberArgumentIsRejectedBeforeDelegation() {
        final FakeOperations operations = new FakeOperations();
        final WeatherRemoteAccessController controller = authenticatedController(operations);

        final RemoteAccessResponse response = controller.handle(
                "REGISTER_WEATHER_DATA|2027-01-10|LISACC|METAR|STATION|TEMPERATURE|seventeen|C|120|38.7|-9.1"
        );

        assertTrue(response.isError());
        assertEquals("value must be a number.", response.payload());
        assertFalse(operations.registerWeatherCalled);
    }

    @Test
    void ensureInvalidArgumentCountIsRejectedBeforeDelegation() {
        final FakeOperations operations = new FakeOperations();
        final WeatherRemoteAccessController controller = authenticatedController(operations);

        final RemoteAccessResponse response = controller.handle(
                "REGISTER_WEATHER_DATA|2027-01-10|LISACC|METAR|STATION|TEMPERATURE|17.5|C|120|38.7"
        );

        assertTrue(response.isError());
        assertEquals("REGISTER_WEATHER_DATA expects 10 argument(s).", response.payload());
        assertFalse(operations.registerWeatherCalled);
    }

    @Test
    void ensureLogoutClosesRemoteSession() {
        final WeatherRemoteAccessController controller = controller(true);

        controller.handle("LOGIN|weather@aisafe.com|Password1");
        final RemoteAccessResponse response = controller.handle("LOGOUT");

        assertTrue(response.isOk());
        assertFalse(controller.isAuthenticated());
        assertNull(controller.username());
    }

    private WeatherRemoteAccessController authenticatedController(final FakeOperations operations) {
        final WeatherRemoteAccessController controller = controller(
                new FakeCredentialHandler(true),
                new FakeAuthorizationService(),
                operations
        );

        controller.handle("LOGIN|weather@aisafe.com|Password1");
        return controller;
    }

    private WeatherRemoteAccessController controller(final boolean credentialsAccepted) {
        return controller(new FakeCredentialHandler(credentialsAccepted), new FakeAuthorizationService(), new FakeOperations());
    }

    private WeatherRemoteAccessController controller(
            final CredentialHandler credentials,
            final AisafeAuthorizationService authz,
            final WeatherRemoteOperations operations) {

        return new WeatherRemoteAccessController(credentials, authz, operations);
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

    private static final class FakeOperations implements WeatherRemoteOperations {

        private boolean registerWeatherCalled;
        private boolean importBulkCalled;
        private boolean consultWeatherCalled;
        private String weatherDay;
        private String airControlAreaCode;
        private String providerName;
        private String providerType;
        private String parameter;
        private double value;
        private String unit;
        private double altitude;
        private double latitude;
        private double longitude;
        private String serverFilePath;

        @Override
        public String registerWeatherData(
                final String day,
                final String areaCode,
                final String providerName,
                final String providerType,
                final String parameter,
                final double value,
                final String unit,
                final double altitude,
                final double latitude,
                final double longitude) {

            registerWeatherCalled = true;
            weatherDay = day;
            airControlAreaCode = areaCode;
            this.providerName = providerName;
            this.providerType = providerType;
            this.parameter = parameter;
            this.value = value;
            this.unit = unit;
            this.altitude = altitude;
            this.latitude = latitude;
            this.longitude = longitude;
            return "weather registered";
        }

        @Override
        public String importBulkWeatherData(final String serverFilePath) {
            importBulkCalled = true;
            this.serverFilePath = serverFilePath;
            return "bulk imported";
        }

        @Override
        public String consultWeatherData(final String day, final String airControlAreaCode) {
            consultWeatherCalled = true;
            weatherDay = day;
            this.airControlAreaCode = airControlAreaCode;
            return "weather data found";
        }
    }
}
