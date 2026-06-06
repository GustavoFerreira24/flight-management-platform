package isep.aisafe;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AppSettingsTest {

    /**
     * Purpose: verify the fallback configuration used when application.properties is not available in tests.
     * Scenario: instantiate AppSettings in the test classpath.
     * Expected output: default persistence unit and repository factory values are loaded.
     */
    @Test
    void ensureDefaultSettingsAreLoadedWhenPropertiesFileIsUnavailable() {
        // Arrange / Act
        final AppSettings settings = new AppSettings();

        // Assert
        assertEquals("aisafePU", settings.persistenceUnitName());
        assertEquals("isep.aisafe.persistence.impl.jpa.JpaRepositoryFactory", settings.repositoryFactory());
    }

    /**
     * Purpose: verify the extended persistence properties exposed to JPA setup.
     * Scenario: request extendedPersistenceProperties from default AppSettings.
     * Expected output: schema generation action is present with value drop-and-create.
     */
    @Test
    void ensureExtendedPersistencePropertiesIncludeSchemaGenerationAction() {
        // Arrange
        final AppSettings settings = new AppSettings();

        // Act
        final Map properties = settings.extendedPersistenceProperties();

        // Assert
        assertEquals("drop-and-create",
                properties.get("jakarta.persistence.schema-generation.database.action"));
    }
}
