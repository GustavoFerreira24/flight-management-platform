package isep.aisafe.application.weather;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

/**
 * Controller for weather data consultation actions.
 */
public class ConsultWeatherDataController {

    private final AisafeAuthorizationService authz;

    /**
     * Creates the controller with the default AISafe authorization service.
     */
    public ConsultWeatherDataController() {
        this(new AisafeAuthorizationService());
    }

    ConsultWeatherDataController(final AisafeAuthorizationService authz) {
        this.authz = authz;
    }

    /**
     * Authorizes access to weather data consultation functionality.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_CONSULTATION);
        // TODO implement weather data consultation behavior when the feature is developed.
    }
}
