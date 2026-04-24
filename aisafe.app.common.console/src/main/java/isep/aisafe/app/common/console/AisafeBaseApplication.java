package isep.aisafe.app.common.console;

import isep.aisafe.Application;

public abstract class AisafeBaseApplication {

    protected static final String SEPARATOR_HR = "============================================";

    public void run(final String[] args) {
        configure();

        printHeader();

        try {
            doMain(args);
            printFooter();
        } catch (final Exception e) {
            System.out.println("Unexpected error. Please check the logs.");
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    protected void configure() {
        configureAuthz();
    }

    protected void printHeader() {
        System.out.println(SEPARATOR_HR);
        System.out.println(appTitle() + " " + Application.VERSION);
        System.out.println(Application.COPYRIGHT);
        System.out.println(SEPARATOR_HR);
    }

    protected void printFooter() {
        System.out.println();
        System.out.println(SEPARATOR_HR);
        System.out.println(appGoodbye());
        System.out.println(SEPARATOR_HR);
    }

    protected abstract void configureAuthz();

    protected abstract void doMain(final String[] args);

    protected abstract String appTitle();

    protected abstract String appGoodbye();
}