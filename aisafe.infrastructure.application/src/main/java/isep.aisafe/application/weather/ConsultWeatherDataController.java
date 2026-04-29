package isep.aisafe.application.weather;

import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;

public class ConsultWeatherDataController {

    private final AisafeAuthorizationService authz;

    public ConsultWeatherDataController() {
        this(new AisafeAuthorizationService());
    }

    ConsultWeatherDataController(final AisafeAuthorizationService authz) {
        this.authz = authz;
    }

    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_CONSULTATION);

        System.out.println("Weather data consultation use case authorized.");
    }
}
