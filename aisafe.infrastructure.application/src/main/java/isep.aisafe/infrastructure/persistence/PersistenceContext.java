package isep.aisafe.infrastructure.persistence;

import isep.aisafe.Application;

import java.lang.reflect.InvocationTargetException;

/**
 * Provides access to the configured repository factory.
 *
 * <p>The factory is created lazily from the class name configured in application
 * settings and then reused for subsequent repository requests.</p>
 */
public final class PersistenceContext {

    private static RepositoryFactory theFactory;

    private PersistenceContext() {
    }

    /**
     * Returns the configured repository factory, creating it on first access.
     *
     * @return repository factory configured for the application
     * @throws IllegalStateException when the configured factory class cannot be loaded
     */
    public static RepositoryFactory repositories() {
        if (theFactory == null) {
            final String factoryClassName = Application.settings().repositoryFactory();

            try {
                theFactory = (RepositoryFactory) Class.forName(factoryClassName)
                        .getDeclaredConstructor()
                        .newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException
                     | IllegalArgumentException | InvocationTargetException
                     | NoSuchMethodException | SecurityException ex) {
                throw new IllegalStateException(
                        "Unable to dynamically load the Repository Factory: " + factoryClassName, ex);
            }
        }

        return theFactory;
    }
}
