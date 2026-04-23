package isep.aisafe.application.weather;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

public class ManageWeatherDataController {

    private final AisafeAuthorizationService authz = new AisafeAuthorizationService();

    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_MANAGEMENT);

        System.out.println("Weather data management use case authorized.");
    }
}