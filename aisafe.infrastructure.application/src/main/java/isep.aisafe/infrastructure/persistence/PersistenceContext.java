package isep.aisafe.infrastructure.persistence;

import isep.aisafe.Application;

import java.lang.reflect.InvocationTargetException;

public final class PersistenceContext {

    private static RepositoryFactory theFactory;

    private PersistenceContext() {
    }

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