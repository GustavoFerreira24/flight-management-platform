package isep.aisafe.infrastructure.authz;

import isep.aisafe.usermanagement.domain.AisafeRoles;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class AisafePermissionsTest {

    @Test
    void ensureUserManagementPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.ADMIN, AisafeRoles.BACKOFFICE_OPERATOR },
                AisafePermissions.USER_MANAGEMENT
        );
    }

    @Test
    void ensureUserRegistrationPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.ADMIN },
                AisafePermissions.USER_REGISTRATION
        );
    }

    @Test
    void ensureBackofficeConfigurationPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.BACKOFFICE_OPERATOR },
                AisafePermissions.BACKOFFICE_CONFIGURATION
        );
    }

    @Test
    void ensureAirCompanyConfigurationPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR },
                AisafePermissions.AIR_COMPANY_CONFIGURATION
        );
    }

    @Test
    void ensureFlightPlanManagementPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.PILOT, AisafeRoles.FLIGHT_CONTROL_OPERATOR },
                AisafePermissions.FLIGHT_PLAN_MANAGEMENT
        );
    }

    @Test
    void ensureFlightControlPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.FLIGHT_CONTROL_OPERATOR },
                AisafePermissions.FLIGHT_CONTROL
        );
    }

    @Test
    void ensureWeatherDataManagementPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.WEATHER_PERSON },
                AisafePermissions.WEATHER_DATA_MANAGEMENT
        );
    }

    @Test
    void ensureWeatherDataConsultationPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] {
                        AisafeRoles.WEATHER_PERSON,
                        AisafeRoles.PILOT,
                        AisafeRoles.FLIGHT_CONTROL_OPERATOR
                },
                AisafePermissions.WEATHER_DATA_CONSULTATION
        );
    }

    @Test
    void ensureUtilityClassHasPrivateConstructor() throws Exception {
        // Arrange
        final Constructor<AisafePermissions> constructor =
                AisafePermissions.class.getDeclaredConstructor();

        // Act
        constructor.setAccessible(true);

        // Assert
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }
}
