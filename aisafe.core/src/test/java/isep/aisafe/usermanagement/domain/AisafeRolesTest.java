package isep.aisafe.usermanagement.domain;

import eapli.framework.infrastructure.authz.domain.model.Role;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class AisafeRolesTest {

    @Test
    void ensureRolesHaveExpectedValues() {
        // Arrange / Act / Assert
        assertEquals(Role.valueOf("POWER_USER"), AisafeRoles.POWER_USER);
        assertEquals(Role.valueOf("ADMIN"), AisafeRoles.ADMIN);
        assertEquals(Role.valueOf("BACKOFFICE_OPERATOR"), AisafeRoles.BACKOFFICE_OPERATOR);
        assertEquals(Role.valueOf("AIR_TRANSPORT_COMPANY_COLLABORATOR"),
                AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR);
        assertEquals(Role.valueOf("PILOT"), AisafeRoles.PILOT);
        assertEquals(Role.valueOf("FLIGHT_CONTROL_OPERATOR"), AisafeRoles.FLIGHT_CONTROL_OPERATOR);
        assertEquals(Role.valueOf("WEATHER_PERSON"), AisafeRoles.WEATHER_PERSON);
    }

    @Test
    void ensureUtilityClassHasPrivateConstructor() throws Exception {
        // Arrange
        final Constructor<AisafeRoles> constructor =
                AisafeRoles.class.getDeclaredConstructor();

        // Act
        constructor.setAccessible(true);

        // Assert
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        try {
            constructor.newInstance();
        } catch (Exception e) {
            fail("Constructor should not throw exception");
        }
    }
}