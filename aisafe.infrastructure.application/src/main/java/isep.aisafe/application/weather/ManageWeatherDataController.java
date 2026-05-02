package isep.aisafe.application.weather;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

/**
 * Controller for weather data management actions.
 */
public class ManageWeatherDataController {

    private final AisafeAuthorizationService authz;

    /**
     * Creates the controller with the default AISafe authorization service.
     */
    public ManageWeatherDataController() {
        this(new AisafeAuthorizationService());
    }

    ManageWeatherDataController(final AisafeAuthorizationService authz) {
        this.authz = authz;
    }

    /**
     * Authorizes access to weather data management functionality.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_MANAGEMENT);
        // TODO implement weather data management behavior when the feature is developed.
    }
}
