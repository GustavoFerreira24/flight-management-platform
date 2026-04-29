package isep.aisafe.infrastructure.authz;

import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.application.Authenticator;

public class AuthenticationCredentialHandler implements CredentialHandler {

    private final Authenticator authenticator;

    public AuthenticationCredentialHandler() {
        authenticator = null;
    }

    AuthenticationCredentialHandler(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }

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
