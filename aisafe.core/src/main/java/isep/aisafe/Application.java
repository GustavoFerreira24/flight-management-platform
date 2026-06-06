package isep.aisafe;

/**
 * Application-level facade for AISafe metadata and runtime settings.
 */
public final class Application {

    public static final String VERSION = "v0.1";
    public static final String COPYRIGHT = "(C) 2026, AISafe Team";

    private static final AppSettings SETTINGS = new AppSettings();

    private Application() {
    }

    /**
     * Returns the application settings loaded at startup.
     *
     * @return shared AISafe application settings
     */
    public static AppSettings settings() {
        return SETTINGS;
    }
}
