package isep.aisafe;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

    /**
     * Purpose: verify the public application metadata constants.
     * Scenario: access VERSION and COPYRIGHT directly.
     * Expected output: the constants match the AISafe application metadata.
     */
    @Test
    void ensureApplicationConstantsAreAvailable() {
        // Arrange / Act / Assert
        assertEquals("v0.1", Application.VERSION);
        assertEquals("(C) 2026, AISafe Team", Application.COPYRIGHT);
    }

    /**
     * Purpose: verify that application settings are exposed through the application facade.
     * Scenario: call Application.settings().
     * Expected output: a non-null AppSettings instance is returned.
     */
    @Test
    void ensureSettingsAreAvailable() {
        // Arrange / Act / Assert
        assertNotNull(Application.settings());
    }

    /**
     * Purpose: document and verify that Application is a utility-style class.
     * Scenario: inspect and invoke the private constructor through reflection.
     * Expected output: the constructor is private and can create an instance only via reflection.
     */
    @Test
    void ensureUtilityClassHasPrivateConstructor() throws Exception {
        // Arrange
        final Constructor<Application> constructor = Application.class.getDeclaredConstructor();

        // Act
        constructor.setAccessible(true);

        // Assert
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        assertNotNull(constructor.newInstance());
    }
}
