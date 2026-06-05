package isep.aisafe.application.remoteaccess;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.authz.AuthenticationCredentialHandler;
import isep.aisafe.CredentialHandler;

/**
 * Application controller for Air Transport Company remote access commands.
 */
public class AirCompanyRemoteAccessController {

    private static final String HELP_TEXT = String.join(
            ";",
            "LOGIN|username|password",
            "LOGOUT",
            "LIST_FLEET",
            "REGISTER_AIRCRAFT|registration|country|status|crewSize|economySeats|businessSeats|firstClassSeats|aircraftModelName|aircraftModelManufacturer|engineModelName|engineModelManufacturer",
            "DECOMMISSION_AIRCRAFT|registration",
            "CREATE_ROUTE|routeDesignator|departureAirportIcao|destinationAirportIcao"
    );

    private final CredentialHandler credentialHandler;
    private final AisafeAuthorizationService authz;
    private final AirCompanyRemoteOperations operations;
    private boolean authenticated;
    private String username;

    public AirCompanyRemoteAccessController() {
        this(
                new AuthenticationCredentialHandler(),
                new AisafeAuthorizationService(),
                new ControllerAirCompanyRemoteOperations()
        );
    }

    AirCompanyRemoteAccessController(
            final CredentialHandler credentialHandler,
            final AisafeAuthorizationService authz,
            final AirCompanyRemoteOperations operations) {

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
            case "LIST_FLEET":
                requireAuthenticated(request, 0);
                return RemoteAccessResponse.data(operations.listAircraftFleet());
            case "REGISTER_AIRCRAFT":
                requireAuthenticated(request, 11);
                return registerAircraft(request);
            case "DECOMMISSION_AIRCRAFT":
                requireAuthenticated(request, 1);
                return RemoteAccessResponse.ok(operations.decommissionAircraft(request.argument(0)));
            case "CREATE_ROUTE":
                requireAuthenticated(request, 3);
                return RemoteAccessResponse.ok(operations.createFlightRoute(
                        request.argument(0),
                        request.argument(1),
                        request.argument(2)
                ));
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

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);

        authenticated = true;
        username = candidateUsername;
        return RemoteAccessResponse.ok("Authenticated as " + username + ".");
    }

    private RemoteAccessResponse logout() {
        authenticated = false;
        username = null;
        return RemoteAccessResponse.ok("Remote session closed.");
    }

    private RemoteAccessResponse registerAircraft(final RemoteAccessRequest request) {
        return RemoteAccessResponse.ok(operations.registerAircraft(
                request.argument(0),
                request.argument(1),
                request.argument(2),
                parseInteger(request.argument(3), "crewSize"),
                parseInteger(request.argument(4), "economySeats"),
                parseInteger(request.argument(5), "businessSeats"),
                parseInteger(request.argument(6), "firstClassSeats"),
                request.argument(7),
                request.argument(8),
                request.argument(9),
                request.argument(10)
        ));
    }

    private void requireAuthenticated(final RemoteAccessRequest request, final int expectedArguments) {
        ensureArgumentCount(request, expectedArguments);

        if (!authenticated) {
            throw new IllegalStateException("Remote login is required.");
        }

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION);
    }

    private void ensureArgumentCount(final RemoteAccessRequest request, final int expectedArguments) {
        if (request.argumentCount() != expectedArguments) {
            throw new IllegalArgumentException(
                    request.command() + " expects " + expectedArguments + " argument(s)."
            );
        }
    }

    private int parseInteger(final String value, final String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be an integer.");
        }
    }
}
