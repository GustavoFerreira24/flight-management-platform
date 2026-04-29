package isep.aisafe.infrastructure.authz;

import eapli.framework.infrastructure.authz.application.Authenticator;
import eapli.framework.infrastructure.authz.application.UserSession;
import eapli.framework.infrastructure.authz.domain.model.Role;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationCredentialHandlerTest {

    /**
     * Purpose: verify that the production constructor remains available.
     * Scenario: create the credential handler without injecting an authenticator.
     * Expected output: a non-null handler instance is created.
     */
    @Test
    void ensureDefaultConstructorIsAvailable() {
        // Arrange / Act / Assert
        assertNotNull(new AuthenticationCredentialHandler());
    }

    /**
     * Purpose: verify that credential authentication is delegated to the framework authenticator contract.
     * Scenario: authenticate with username and password using a capturing authenticator fake.
     * Expected output: the fake receives the same credentials and the handler returns false for an empty session.
     */
    @Test
    void ensureAuthenticationIsDelegatedToAuthenticator() {
        // Arrange
        final CapturingAuthenticator authenticator = new CapturingAuthenticator();
        final AuthenticationCredentialHandler handler = new AuthenticationCredentialHandler(authenticator);

        // Act
        final boolean authenticated = handler.authWithCredentialData("admin", "Password1");

        // Assert
        assertFalse(authenticated);
        assertEquals("admin", authenticator.identity);
        assertEquals("Password1", authenticator.password);
    }

    private static final class CapturingAuthenticator implements Authenticator {

        private String identity;
        private String password;

        @Override
        public Optional<UserSession> authenticate(
                final String username,
                final String rawPassword,
                final Role... requiredRoles) {
            identity = username;
            password = rawPassword;
            return Optional.empty();
        }
    }
}
