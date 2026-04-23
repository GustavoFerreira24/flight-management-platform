package isep.aisafe.infrastructure.authz;

import eapli.framework.infrastructure.authz.application.AuthzRegistry;

public class AuthenticationCredentialHandler implements CredentialHandler {

    @Override
    public boolean authWithCredentialData(final String identity, final String password) {
        return AuthzRegistry.authenticationService().authenticate(identity, password).isPresent();
    }
}