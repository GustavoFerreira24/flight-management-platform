package isep.aisafe.infrastructure.authz;

import isep.aisafe.usermanagement.domain.AisafeRoles;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AisafePermissionsTest {

    @Test
    void ensureUserManagementAllowsAdminAndBackofficeOperator() {
        assertContains(AisafePermissions.USER_MANAGEMENT, AisafeRoles.ADMIN);
        assertContains(AisafePermissions.USER_MANAGEMENT, AisafeRoles.BACKOFFICE_OPERATOR);
    }

    @Test
    void ensureWeatherManagementAllowsOnlyWeatherPerson() {
        assertContains(AisafePermissions.WEATHER_DATA_MANAGEMENT, AisafeRoles.WEATHER_PERSON);
        assertDoesNotContain(AisafePermissions.WEATHER_DATA_MANAGEMENT, AisafeRoles.ADMIN);
    }

    @Test
    void ensureWeatherConsultationAllowsPilotAndFlightControlOperator() {
        assertContains(AisafePermissions.WEATHER_DATA_CONSULTATION, AisafeRoles.PILOT);
        assertContains(AisafePermissions.WEATHER_DATA_CONSULTATION, AisafeRoles.FLIGHT_CONTROL_OPERATOR);
    }

    @Test
    void ensureFlightControlAllowsOnlyFlightControlOperator() {
        assertContains(AisafePermissions.FLIGHT_CONTROL, AisafeRoles.FLIGHT_CONTROL_OPERATOR);
        assertDoesNotContain(AisafePermissions.FLIGHT_CONTROL, AisafeRoles.ADMIN);
    }

    private void assertContains(final Object[] roles, final Object expectedRole) {
        assertTrue(Arrays.asList(roles).contains(expectedRole));
    }

    private void assertDoesNotContain(final Object[] roles, final Object unexpectedRole) {
        assertFalse(Arrays.asList(roles).contains(unexpectedRole));
    }
}