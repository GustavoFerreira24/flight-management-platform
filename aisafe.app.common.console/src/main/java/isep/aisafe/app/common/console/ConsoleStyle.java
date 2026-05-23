package isep.aisafe.app.common.console;

/**
 * Small ANSI styling helper for console applications.
 */
public final class ConsoleStyle {

    private static final boolean COLORS_ENABLED = System.getenv("NO_COLOR") == null
            && !Boolean.getBoolean("aisafe.noColor");

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";
    private static final String CYAN = "\u001B[36m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String WHITE = "\u001B[37m";

    private static final int BOX_WIDTH = 62;
    private static final int CONTENT_WIDTH = BOX_WIDTH - 4;

    private ConsoleStyle() {
    }

    public static void printMenu(final String title, final String... lines) {
        System.out.println();
        printBox(title, lines);
    }

    public static void printSection(final String title) {
        System.out.println();
        System.out.println(accent("-- " + title + " --"));
    }

    public static void printSuccess(final String message) {
        System.out.println(success("OK") + " " + message);
    }

    public static void printWarning(final String message) {
        System.out.println(warning("WARN") + " " + message);
    }

    public static void printError(final String message) {
        System.out.println(error("ERROR") + " " + message);
    }

    public static void printInfo(final String message) {
        System.out.println(muted("INFO") + " " + message);
    }

    public static void printPrompt(final String prompt) {
        System.out.print(accent(prompt));
    }

    public static String accent(final String text) {
        return color(text, CYAN + BOLD);
    }

    public static String success(final String text) {
        return color(text, GREEN + BOLD);
    }

    public static String warning(final String text) {
        return color(text, YELLOW + BOLD);
    }

    public static String error(final String text) {
        return color(text, RED + BOLD);
    }

    public static String muted(final String text) {
        return color(text, DIM + WHITE);
    }

    public static String option(final int number, final String label) {
        return number + " - " + label;
    }

    private static void printBox(final String title, final String... lines) {
        System.out.println(color("╔" + "═".repeat(BOX_WIDTH - 2) + "╗", CYAN));
        System.out.println(color("║ ", CYAN) + center(title, CONTENT_WIDTH) + color(" ║", CYAN));
        System.out.println(color("╠" + "═".repeat(BOX_WIDTH - 2) + "╣", CYAN));

        for (String line : lines) {
            System.out.println(color("║ ", CYAN) + padRight(line, CONTENT_WIDTH) + color(" ║", CYAN));
        }

        System.out.println(color("╚" + "═".repeat(BOX_WIDTH - 2) + "╝", CYAN));
    }

    private static String center(final String text, final int width) {
        final int padding = Math.max(0, width - text.length());
        final int left = padding / 2;
        final int right = padding - left;
        return " ".repeat(left) + color(text, BLUE + BOLD) + " ".repeat(right);
    }

    private static String padRight(final String text, final int width) {
        if (text.length() >= width) {
            return text;
        }

        return text + " ".repeat(width - text.length());
    }

    private static String color(final String text, final String code) {
        if (!COLORS_ENABLED) {
            return text;
        }

        return code + text + RESET;
    }
}
