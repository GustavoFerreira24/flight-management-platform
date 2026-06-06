package isep.aisafe.app.backoffice.console.presentation.report;

import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.application.report.GenerateMonthlyReportController;

import java.util.Scanner;

/**
 * User interface for generating monthly reports.
 */
public class GenerateMonthlyReportUI {

    private final GenerateMonthlyReportController controller = new GenerateMonthlyReportController();
    private final Scanner scanner;

    /**
     * Creates the UI with a default scanner.
     */
    public GenerateMonthlyReportUI() {
        this(new Scanner(System.in));
    }

    /**
     * Creates the UI with a custom scanner.
     *
     * @param scanner scanner used to read input
     */
    public GenerateMonthlyReportUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the monthly report generation form.
     */
    public void show() {
        System.out.println();
        ConsoleStyle.printSection("Generate Monthly Report");

        final int year = readInt("Report year: ");
        final int month = readInt("Report month (1-12): ");

        final String report = controller.generateMonthlyReportText(year, month);
        System.out.println();
        System.out.println(report);
    }

    private int readInt(final String prompt) {
        while (true) {
            ConsoleStyle.printPrompt(prompt);
            final String value = scanner.nextLine().trim();

            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
                ConsoleStyle.printWarning("Enter a valid integer value.");
            }
        }
    }
}
