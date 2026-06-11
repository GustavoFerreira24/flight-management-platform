package isep.aisafe.support;

import eapli.framework.infrastructure.authz.application.AuthorizationService;
import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;

/**
 * Test double used to capture authorization requests without relying on AuthzRegistry global state.
 */
public class CapturingAuthorizationService extends AisafeAuthorizationService {

    private Role[] requestedRoles;
    private int calls;
    private RuntimeException exceptionToThrow;
    private String authenticatedUsername = "ABC";
    private String authenticatedEmail = "pilot@example.com";

    public CapturingAuthorizationService() {
        super(new StubAuthorizationService());
    }

    @Override
    public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {
        calls++;
        requestedRoles = roles;
        if (exceptionToThrow != null) {
            throw exceptionToThrow;
        }
    }

    public Role[] requestedRoles() {
        return requestedRoles;
    }

    public int calls() {
        return calls;
    }

    public void throwOnAuthorization(final RuntimeException exceptionToThrow) {
        this.exceptionToThrow = exceptionToThrow;
    }

    @Override
    public String authenticatedUsername() {
        return authenticatedUsername;
    }

    public void authenticatedUsername(final String authenticatedUsername) {
        this.authenticatedUsername = authenticatedUsername;
    }

    @Override
    public String authenticatedEmail() {
        return authenticatedEmail;
    }

    public void authenticatedEmail(final String authenticatedEmail) {
        this.authenticatedEmail = authenticatedEmail;
    }

    private static final class StubAuthorizationService extends AuthorizationService {

        private StubAuthorizationService() {
            super();
        }
    }
}