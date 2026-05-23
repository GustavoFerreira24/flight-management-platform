package isep.aisafe.app.common.console.presentation.authz;

import java.util.Scanner;
import isep.aisafe.app.common.console.ConsoleStyle;
import isep.aisafe.infrastructure.authz.CredentialHandler;

public class LoginUI {

    private final CredentialHandler credentialHandler;
    private final Scanner scanner;

    public LoginUI(final CredentialHandler credentialHandler) {
        this(credentialHandler, new Scanner(System.in));
    }

    public LoginUI(final CredentialHandler credentialHandler, final Scanner scanner) {
        this.credentialHandler = credentialHandler;
        this.scanner = scanner;
    }

    public boolean show() {
        ConsoleStyle.printSection("Login");
        final String identity = askRequiredText("Username/Email", "Username/email is required.");
        final String password = askRequiredText("Password", "Password is required.");

        final boolean success;

        try {
            success = credentialHandler.authWithCredentialData(identity, password);
        } catch (RuntimeException e) {
            ConsoleStyle.printError("Login failed: " + e.getMessage());
            return false;
        }

        if (success) {
            ConsoleStyle.printSuccess("Login successful.");
        } else {
            ConsoleStyle.printError("Invalid credentials.");
        }

        return success;
    }

    private String askRequiredText(final String label, final String errorMessage) {
        while (true) {
            ConsoleStyle.printPrompt(label + ": ");
            final String value = scanner.nextLine();

            if (value != null && !value.isBlank()) {
                return value.trim();
            }

            ConsoleStyle.printWarning(errorMessage);
        }
    }
}
