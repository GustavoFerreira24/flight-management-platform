package isep.aisafe.infrastructure.persistence;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceContextTest {

    /**
     * Purpose: verify that repository factory loading fails explicitly when configuration is invalid.
     * Scenario: call repositories using the default test settings, whose factory class is not on the test classpath.
     * Expected output: an IllegalStateException is thrown instead of silently returning null.
     */
    @Test
    void ensureRepositoriesFailsFastWhenConfiguredFactoryCannotBeLoaded() {
        // Arrange / Act / Assert
        assertThrows(IllegalStateException.class, PersistenceContext::repositories);
    }

    /**
     * Purpose: document and verify that PersistenceContext is a utility-style class.
     * Scenario: inspect and invoke the private constructor through reflection.
     * Expected output: the constructor is private and can create an instance only via reflection.
     */
    @Test
    void ensureUtilityClassHasPrivateConstructor() throws Exception {
        // Arrange
        final Constructor<PersistenceContext> constructor = PersistenceContext.class.getDeclaredConstructor();

        // Act
        constructor.setAccessible(true);

        // Assert
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        assertNotNull(constructor.newInstance());
    }
}
