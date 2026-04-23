package isep.aisafe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AppSettings {

    private static final String PROPERTIES_RESOURCE = "application.properties";

    private static final String REPOSITORY_FACTORY_KEY = "persistence.repositoryFactory";
    private static final String PERSISTENCE_UNIT_KEY = "persistence.persistenceUnit";
    private static final String SCHEMA_GENERATION_KEY = "jakarta.persistence.schema-generation.database.action";

    private final Properties applicationProperties = new Properties();

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
                "pt.isep.aisafe.persistence.impl.jpa.JpaRepositoryFactory");
        applicationProperties.setProperty(PERSISTENCE_UNIT_KEY, "aisafePU");
        applicationProperties.setProperty(SCHEMA_GENERATION_KEY, "drop-and-create");
    }

    public String persistenceUnitName() {
        return applicationProperties.getProperty(PERSISTENCE_UNIT_KEY);
    }

    public String repositoryFactory() {
        return applicationProperties.getProperty(REPOSITORY_FACTORY_KEY);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map extendedPersistenceProperties() {
        final Map ret = new HashMap();
        ret.put(SCHEMA_GENERATION_KEY, applicationProperties.getProperty(SCHEMA_GENERATION_KEY));
        return ret;
    }
}