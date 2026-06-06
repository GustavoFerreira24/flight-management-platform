package isep.aisafe.infrastructure.authz;

import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.application.Authenticator;

import java.util.Objects;

/**
 * Credential handler that authenticates users through the EAPLI authorization framework.
 */
public class AuthenticationCredentialHandler implements isep.aisafe.CredentialHandler {

    private final Authenticator authenticator;

    /**
     * Creates a handler that resolves the authenticator from AuthzRegistry.
     */
    public AuthenticationCredentialHandler() {
        authenticator = null;
    }

    AuthenticationCredentialHandler(final Authenticator authenticator) {
        this.authenticator = Objects.requireNonNull(authenticator, "authenticator must not be null");
    }

    /**
     * Authenticates the supplied credentials and reports whether a session was created.
     *
     * @param identity username entered by the user
     * @param password raw password entered by the user
     * @return true when the framework authenticates the user successfully
     */
    @Override
    public boolean authWithCredentialData(final String identity, final String password) {
        return authenticator().authenticate(identity, password).isPresent();
    }

    private Authenticator authenticator() {
        if (authenticator != null) {
            return authenticator;
        }
        return AuthzRegistry.authenticationService();
    }
}
