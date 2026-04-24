package isep.aisafe;

public final class Application {

    public static final String VERSION = "v0.1";
    public static final String COPYRIGHT = "(C) 2026, AISafe Team";

    private static final AppSettings SETTINGS = new AppSettings();

    private Application() {
    }

    public static AppSettings settings() {
        return SETTINGS;
    }
}