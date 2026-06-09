package isep.aisafe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Loads AISafe runtime configuration from application.properties.
 *
 * <p>If the properties resource is unavailable, default persistence settings are
 * applied so infrastructure components can still fail in a predictable way.</p>
 */
public class AppSettings {

    private static final String PROPERTIES_RESOURCE = "application.properties";

    private static final String REPOSITORY_FACTORY_KEY = "persistence.repositoryFactory";
    private static final String PERSISTENCE_UNIT_KEY = "persistence.persistenceUnit";
    private static final String SCHEMA_GENERATION_KEY = "jakarta.persistence.schema-generation.database.action";
    private static final String JDBC_DRIVER_KEY = "jakarta.persistence.jdbc.driver";
    private static final String JDBC_URL_KEY = "jakarta.persistence.jdbc.url";
    private static final String JDBC_USER_KEY = "jakarta.persistence.jdbc.user";
    private static final String JDBC_PASSWORD_KEY = "jakarta.persistence.jdbc.password";
    private static final String ECLIPSELINK_LOGGING_KEY = "eclipselink.logging.level";
    private static final String ECLIPSELINK_POOL_INITIAL_KEY = "eclipselink.jdbc.connection_pool.default.initial";
    private static final String ECLIPSELINK_POOL_MIN_KEY = "eclipselink.jdbc.connection_pool.default.min";
    private static final String ECLIPSELINK_POOL_MAX_KEY = "eclipselink.jdbc.connection_pool.default.max";

    private static final String[] EXTENDED_PERSISTENCE_KEYS = {
            SCHEMA_GENERATION_KEY,
            JDBC_DRIVER_KEY,
            JDBC_URL_KEY,
            JDBC_USER_KEY,
            JDBC_PASSWORD_KEY,
            ECLIPSELINK_LOGGING_KEY,
            ECLIPSELINK_POOL_INITIAL_KEY,
            ECLIPSELINK_POOL_MIN_KEY,
            ECLIPSELINK_POOL_MAX_KEY
    };

    private static final Map<String, String> ENVIRONMENT_OVERRIDES = Map.of(
            JDBC_URL_KEY, "AISAFE_DB_URL",
            JDBC_USER_KEY, "AISAFE_DB_USER",
            JDBC_PASSWORD_KEY, "AISAFE_DB_PASSWORD",
            SCHEMA_GENERATION_KEY, "AISAFE_DB_SCHEMA_ACTION"
    );

    private final Properties applicationProperties = new Properties();

    /**
     * Creates settings by loading configured properties or falling back to defaults.
     */
    public AppSettings() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream propertiesStream =
                     this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_RESOURCE)) {

            if (propertiesStream == null) {
                throw new FileNotFoundException(
                        "Property file '" + PROPERTIES_RESOURCE + "' not found in the classpath");
            }

            applicationProperties.load(propertiesStream);
        } catch (IOException e) {
            setDefaultProperties();
        }
    }

    private void setDefaultProperties() {
        applicationProperties.setProperty(REPOSITORY_FACTORY_KEY,
                "isep.aisafe.persistence.impl.jpa.JpaRepositoryFactory");
        applicationProperties.setProperty(PERSISTENCE_UNIT_KEY, "aisafePU");
        applicationProperties.setProperty(SCHEMA_GENERATION_KEY, "create");
        applicationProperties.setProperty(JDBC_DRIVER_KEY, "org.h2.Driver");
        applicationProperties.setProperty(JDBC_URL_KEY, "jdbc:h2:./db/aisafe");
        applicationProperties.setProperty(JDBC_USER_KEY, "sa");
        applicationProperties.setProperty(JDBC_PASSWORD_KEY, "");
        applicationProperties.setProperty(ECLIPSELINK_LOGGING_KEY, "INFO");
        applicationProperties.setProperty(ECLIPSELINK_POOL_INITIAL_KEY, "2");
        applicationProperties.setProperty(ECLIPSELINK_POOL_MIN_KEY, "2");
        applicationProperties.setProperty(ECLIPSELINK_POOL_MAX_KEY, "10");
    }

    /**
     * Returns the configured JPA persistence unit name.
     *
     * @return persistence unit name
     */
    public String persistenceUnitName() {
        return applicationProperties.getProperty(PERSISTENCE_UNIT_KEY);
    }

    /**
     * Returns the fully qualified repository factory class name.
     *
     * @return repository factory class name
     */
    public String repositoryFactory() {
        return applicationProperties.getProperty(REPOSITORY_FACTORY_KEY);
    }

    /**
     * Returns extra properties passed to the persistence provider.
     *
     * @return map containing provider-specific persistence settings
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map extendedPersistenceProperties() {
        final Map ret = new HashMap();
        for (final String key : EXTENDED_PERSISTENCE_KEYS) {
            final String value = configuredValue(key);
            if (value != null) {
                ret.put(key, value);
            }
        }
        return ret;
    }

    private String configuredValue(final String propertyKey) {
        final String environmentKey = ENVIRONMENT_OVERRIDES.get(propertyKey);
        if (environmentKey != null) {
            final String environmentValue = System.getenv(environmentKey);
            if (environmentValue != null && !environmentValue.isBlank()) {
                return environmentValue;
            }
        }
        return applicationProperties.getProperty(propertyKey);
    }
}
