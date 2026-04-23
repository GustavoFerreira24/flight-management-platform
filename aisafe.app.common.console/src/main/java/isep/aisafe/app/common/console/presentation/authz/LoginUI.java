package isep.aisafe.app.common.console.presentation.authz;

import java.util.Scanner;
import isep.aisafe.infrastructure.authz.CredentialHandler;

public class LoginUI {

    private final CredentialHandler credentialHandler;

    public LoginUI(final CredentialHandler credentialHandler) {
        this.credentialHandler = credentialHandler;
    }

    public boolean show() {
        final Scanner scanner = new Scanner(System.in);

        System.out.println("Login");
        System.out.print("Username/Email: ");
        final String identity = scanner.nextLine();

        System.out.print("Password: ");
        final String password = scanner.nextLine();

        final boolean success = credentialHandler.authWithCredentialData(identity, password);

        if (success) {
            System.out.println("Login successful.");
        } else {
            System.out.println("Invalid credentials.");
        }

        return success;
    }
}