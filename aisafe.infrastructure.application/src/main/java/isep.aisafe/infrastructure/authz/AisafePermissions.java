package isep.aisafe.infrastructure.authz;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.usermanagement.domain.AisafeRoles;

/**
 * Groups the roles allowed to execute each protected AISafe application action.
 *
 * <p>Controllers use these constants instead of hardcoding authorization rules, which
 * keeps permissions explicit and easier to review.</p>
 */
public final class AisafePermissions {

    private AisafePermissions() {
    }

    public static final Role[] USER_MANAGEMENT =
            { AisafeRoles.ADMIN, AisafeRoles.BACKOFFICE_OPERATOR };

    public static final Role[] BACKOFFICE_CONFIGURATION =
            { AisafeRoles.BACKOFFICE_OPERATOR };

    public static final Role[] AIR_COMPANY_CONFIGURATION =
            { AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR };

    public static final Role[] FLIGHT_PLAN_MANAGEMENT =
            { AisafeRoles.PILOT };

    public static final Role[] FLIGHT_PLAN_WEATHER_DATA_INSERTION =
            { AisafeRoles.PILOT };

    public static final Role[] FLIGHT_CONTROL =
            { AisafeRoles.FLIGHT_CONTROL_OPERATOR };

    public static final Role[] WEATHER_DATA_MANAGEMENT =
            { AisafeRoles.WEATHER_PERSON };

    public static final Role[] WEATHER_DATA_CONSULTATION =
            { AisafeRoles.WEATHER_PERSON, AisafeRoles.PILOT, AisafeRoles.FLIGHT_CONTROL_OPERATOR };

    public static final Role[] USER_REGISTRATION =
            { AisafeRoles.ADMIN };
}
