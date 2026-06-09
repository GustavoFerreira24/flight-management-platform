package isep.aisafe.application.remoteaccess;

import isep.aisafe.CredentialHandler;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.authz.AuthenticationCredentialHandler;

/**
 * Application controller for Weather Person remote access commands.
 */
public class WeatherRemoteAccessController {

    private static final String HELP_TEXT = String.join(
            ";",
            "LOGIN|username|password",
            "LOGOUT",
            "CONSULT_WEATHER|day|airControlAreaCode",
            "REGISTER_WEATHER_DATA|day|airControlAreaCode|providerName|providerType|parameter|value|unit|altitude|latitude|longitude",
            "IMPORT_BULK_WEATHER_DATA|serverFilePath"
    );

    private final CredentialHandler credentialHandler;
    private final AisafeAuthorizationService authz;
    private final WeatherRemoteOperations operations;
    private boolean authenticated;
    private String username;

    public WeatherRemoteAccessController() {
        this(
                new AuthenticationCredentialHandler(),
                new AisafeAuthorizationService(),
                new ControllerWeatherRemoteOperations()
        );
    }

    WeatherRemoteAccessController(
            final CredentialHandler credentialHandler,
            final AisafeAuthorizationService authz,
            final WeatherRemoteOperations operations) {

        if (credentialHandler == null) {
            throw new IllegalArgumentException("Credential handler cannot be null.");
        }
        if (authz == null) {
            throw new IllegalArgumentException("Authorization service cannot be null.");
        }
        if (operations == null) {
            throw new IllegalArgumentException("Remote operations cannot be null.");
        }

        this.credentialHandler = credentialHandler;
        this.authz = authz;
        this.operations = operations;
    }

    /**
     * Handles one remote protocol line.
     *
     * @param line protocol command line
     * @return remote access response
     */
    public RemoteAccessResponse handle(final String line) {
        try {
            final RemoteAccessRequest request = RemoteAccessRequest.parse(line);
            return dispatch(request);
        } catch (RuntimeException e) {
            return RemoteAccessResponse.error(e.getMessage());
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String username() {
        return username;
    }

    private RemoteAccessResponse dispatch(final RemoteAccessRequest request) {
        switch (request.command()) {
            case "LOGIN":
                return login(request);
            case "LOGOUT":
                return logout();
            case "HELP":
                return RemoteAccessResponse.data(HELP_TEXT);
            case "CONSULT_WEATHER":
                requireAuthenticated(request, 2);
                return RemoteAccessResponse.data(operations.consultWeatherData(
                        request.argument(0),
                        request.argument(1)
                ));
            case "REGISTER_WEATHER_DATA":
                requireAuthenticated(request, 10);
                return registerWeatherData(request);
            case "IMPORT_BULK_WEATHER_DATA":
                requireAuthenticated(request, 1);
                return RemoteAccessResponse.ok(operations.importBulkWeatherData(request.argument(0)));
            default:
                return RemoteAccessResponse.error("Unknown remote command: " + request.command());
        }
    }

    private RemoteAccessResponse login(final RemoteAccessRequest request) {
        ensureArgumentCount(request, 2);

        final String candidateUsername = request.argument(0);
        final String password = request.argument(1);

        if (!credentialHandler.authWithCredentialData(candidateUsername, password)) {
            authenticated = false;
            username = null;
            return RemoteAccessResponse.error("Invalid credentials.");
        }

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_MANAGEMENT);

        authenticated = true;
        username = candidateUsername;
        return RemoteAccessResponse.ok("Authenticated as " + username + ".");
    }

    private RemoteAccessResponse logout() {
        authenticated = false;
        username = null;
        return RemoteAccessResponse.ok("Remote session closed.");
    }

    private RemoteAccessResponse registerWeatherData(final RemoteAccessRequest request) {
        return RemoteAccessResponse.ok(operations.registerWeatherData(
                request.argument(0),
                request.argument(1),
                request.argument(2),
                request.argument(3),
                request.argument(4),
                parseDouble(request.argument(5), "value"),
                request.argument(6),
                parseDouble(request.argument(7), "altitude"),
                parseDouble(request.argument(8), "latitude"),
                parseDouble(request.argument(9), "longitude")
        ));
    }

    private void requireAuthenticated(final RemoteAccessRequest request, final int expectedArguments) {
        ensureArgumentCount(request, expectedArguments);

        if (!authenticated) {
            throw new IllegalStateException("Remote login is required.");
        }

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_MANAGEMENT);
    }

    private void ensureArgumentCount(final RemoteAccessRequest request, final int expectedArguments) {
        if (request.argumentCount() != expectedArguments) {
            throw new IllegalArgumentException(
                    request.command() + " expects " + expectedArguments + " argument(s)."
            );
        }
    }

    private double parseDouble(final String value, final String fieldName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
    }
}
