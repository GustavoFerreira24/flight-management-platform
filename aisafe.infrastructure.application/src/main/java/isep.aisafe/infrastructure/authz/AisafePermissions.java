package isep.aisafe.infrastructure.authz;

import isep.aisafe.usermanagement.domain.AisafeRoles;
import eapli.framework.infrastructure.authz.domain.model.Role;

public final class AisafePermissions {

    private AisafePermissions() {
    }

    public static final Role[] USER_MANAGEMENT =
            { AisafeRoles.ADMIN };

    public static final Role[] BACKOFFICE_CONFIGURATION =
            { AisafeRoles.ADMIN, AisafeRoles.BACKOFFICE_OPERATOR };

    public static final Role[] WEATHER_MANAGEMENT =
            { AisafeRoles.WEATHER_PERSON };

    public static final Role[] FLIGHT_CONTROL =
            { AisafeRoles.FLIGHT_CONTROL_OPERATOR };

    public static final Role[] FLIGHT_MANAGEMENT =
            { AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR, AisafeRoles.PILOT };
}