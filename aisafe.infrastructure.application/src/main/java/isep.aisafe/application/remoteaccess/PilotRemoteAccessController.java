package isep.aisafe.application.remoteaccess;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.CredentialHandler;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AuthenticationCredentialHandler;
import isep.aisafe.usermanagement.domain.AisafeRoles;

/**
 * Application controller for Pilot remote access commands.
 */
public class PilotRemoteAccessController {

    private static final Role[] PILOT_ACCESS = { AisafeRoles.PILOT };

    private static final String HELP_TEXT = String.join(
            ";",
            "LOGIN|username|password",
            "LOGOUT",
            "CREATE_FLIGHT_PLAN|flightPlanIdentifier|flightDesignator|aircraftRegistration|pilotId|fuelAmount|fuelUnit",
            "CONSULT_WEATHER|day|airControlAreaCode",
            "INSERT_WEATHER_DATA|flightPlanIdentifier|weatherDataDay|airControlAreaCode",
            "LIST_TESTABLE_FLIGHT_PLANS",
            "TEST_FLIGHT_PLAN|flightPlanIdentifier",
            "CONSULT_FLIGHT_PLAN_TEST_REPORTS"
    );

    private final CredentialHandler credentialHandler;
    private final AisafeAuthorizationService authz;
    private final PilotRemoteOperations operations;
    private boolean authenticated;
    private String username;

    public PilotRemoteAccessController() {
        this(
                new AuthenticationCredentialHandler(),
                new AisafeAuthorizationService(),
                new ControllerPilotRemoteOperations()
        );
    }

    PilotRemoteAccessController(
            final CredentialHandler credentialHandler,
            final AisafeAuthorizationService authz,
            final PilotRemoteOperations operations) {

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
            case "CREATE_FLIGHT_PLAN":
                requireAuthenticated(request, 6);
                return RemoteAccessResponse.ok(operations.createFlightPlan(
                        request.argument(0),
                        request.argument(1),
                        request.argument(2),
                        request.argument(3),
                        request.argument(4),
                        request.argument(5)
                ));
            case "CONSULT_WEATHER":
                requireAuthenticated(request, 2);
                return RemoteAccessResponse.data(operations.consultWeatherData(
                        request.argument(0),
                        request.argument(1)
                ));
            case "INSERT_WEATHER_DATA":
                requireAuthenticated(request, 3);
                return RemoteAccessResponse.ok(operations.insertWeatherData(
                        request.argument(0),
                        request.argument(1),
                        request.argument(2)
                ));
            case "LIST_TESTABLE_FLIGHT_PLANS":
                requireAuthenticated(request, 0);
                return RemoteAccessResponse.data(operations.listTestableFlightPlans());
            case "TEST_FLIGHT_PLAN":
                requireAuthenticated(request, 1);
                return RemoteAccessResponse.ok(operations.testFlightPlan(request.argument(0)));
            case "CONSULT_FLIGHT_PLAN_TEST_REPORTS":
                requireAuthenticated(request, 0);
                return RemoteAccessResponse.data(operations.consultFlightPlanTestReports());
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

        authz.ensureAuthenticatedUserHasAnyOf(PILOT_ACCESS);

        authenticated = true;
        username = candidateUsername;
        return RemoteAccessResponse.ok("Authenticated as " + username + ".");
    }

    private RemoteAccessResponse logout() {
        authenticated = false;
        username = null;
        return RemoteAccessResponse.ok("Remote session closed.");
    }

    private void requireAuthenticated(final RemoteAccessRequest request, final int expectedArguments) {
        ensureArgumentCount(request, expectedArguments);

        if (!authenticated) {
            throw new IllegalStateException("Remote login is required.");
        }

        authz.ensureAuthenticatedUserHasAnyOf(PILOT_ACCESS);
    }

    private void ensureArgumentCount(final RemoteAccessRequest request, final int expectedArguments) {
        if (request.argumentCount() != expectedArguments) {
            throw new IllegalArgumentException(
                    request.command() + " expects " + expectedArguments + " argument(s)."
            );
        }
    }
}
