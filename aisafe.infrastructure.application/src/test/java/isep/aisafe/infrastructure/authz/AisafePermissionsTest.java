package isep.aisafe.infrastructure.authz;

import isep.aisafe.usermanagement.domain.AisafeRoles;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class AisafePermissionsTest {

    /**
     * Purpose: verify user management permission roles.
     * Scenario: inspect the permission constant.
     * Expected output: Admin and Backoffice Operator are allowed.
     */
    @Test
    void ensureUserManagementPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.ADMIN, AisafeRoles.BACKOFFICE_OPERATOR },
                AisafePermissions.USER_MANAGEMENT
        );
    }

    /**
     * Purpose: verify user registration permission roles.
     * Scenario: inspect the permission constant.
     * Expected output: only Admin is allowed.
     */
    @Test
    void ensureUserRegistrationPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.ADMIN },
                AisafePermissions.USER_REGISTRATION
        );
    }

    /**
     * Purpose: verify backoffice configuration permission roles.
     * Scenario: inspect the permission constant.
     * Expected output: only Backoffice Operator is allowed.
     */
    @Test
    void ensureBackofficeConfigurationPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.BACKOFFICE_OPERATOR },
                AisafePermissions.BACKOFFICE_CONFIGURATION
        );
    }

    /**
     * Purpose: verify air company configuration permission roles.
     * Scenario: inspect the permission constant.
     * Expected output: only Air Transport Company Collaborator is allowed.
     */
    @Test
    void ensureAirCompanyConfigurationPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR },
                AisafePermissions.AIR_COMPANY_CONFIGURATION
        );
    }

    /**
     * Purpose: verify flight plan management permission roles.
     * Scenario: inspect the permission constant.
     * Expected output: only Pilot is allowed.
     */
    @Test
    void ensureFlightPlanManagementPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.PILOT },
                AisafePermissions.FLIGHT_PLAN_MANAGEMENT
        );
    }

    /**
     * Purpose: verify flight plan weather data insertion permission roles.
     * Scenario: inspect the permission constant used by US082.
     * Expected output: only Pilot is allowed.
     */
    @Test
    void ensureFlightPlanWeatherDataInsertionPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.PILOT },
                AisafePermissions.FLIGHT_PLAN_WEATHER_DATA_INSERTION
        );
    }

    /**
     * Purpose: verify flight control permission roles.
     * Scenario: inspect the permission constant.
     * Expected output: only Flight Control Operator is allowed.
     */
    @Test
    void ensureFlightControlPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.FLIGHT_CONTROL_OPERATOR },
                AisafePermissions.FLIGHT_CONTROL
        );
    }

    /**
     * Purpose: verify weather data management permission roles.
     * Scenario: inspect the permission constant.
     * Expected output: only Weather Person is allowed.
     */
    @Test
    void ensureWeatherDataManagementPermissionsAreCorrect() {
        // Arrange / Act / Assert
        assertArrayEquals(
                new Object[] { AisafeRoles.WEATHER_PERSON },
                AisafePermissions.WEATHER_DATA_MANAGEMENT
        );
    }

    /**
     * Purpose: verify weather data consultation permission roles.
     * Scenario: inspect the permission constant used by US043.
     * Expected output: Weather Person, Pilot and Flight Control Operator are allowed.
     */
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

    /**
     * Purpose: verify utility class construction guard.
     * Scenario: inspect the private constructor through reflection.
     * Expected output: the constructor is private.
     */
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
