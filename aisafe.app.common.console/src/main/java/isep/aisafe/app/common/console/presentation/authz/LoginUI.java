package isep.aisafe.app.common.console.presentation.authz;

import java.util.Scanner;
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
        System.out.println("Login");
        final String identity = askRequiredText("Username/Email", "Username/email is required.");
        final String password = askRequiredText("Password", "Password is required.");

        final boolean success;

        try {
            success = credentialHandler.authWithCredentialData(identity, password);
        } catch (RuntimeException e) {
            System.out.println("Login failed: " + e.getMessage());
            return false;
        }

        if (success) {
            System.out.println("Login successful.");
        } else {
            System.out.println("Invalid credentials.");
        }

        return success;
    }

    private String askRequiredText(final String label, final String errorMessage) {
        while (true) {
            System.out.print(label + ": ");
            final String value = scanner.nextLine();

            if (value != null && !value.isBlank()) {
                return value.trim();
            }

            System.out.println(errorMessage);
        }
    }
}
