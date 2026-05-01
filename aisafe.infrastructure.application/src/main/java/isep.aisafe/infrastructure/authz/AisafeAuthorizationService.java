package isep.aisafe.infrastructure.authz;

import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.application.AuthorizationService;
import eapli.framework.infrastructure.authz.domain.model.Role;

/**
 * AISafe facade over the framework authorization service.
 *
 * <p>The class keeps application code independent from direct registry access and
 * provides a small seam for tests to inject a controlled authorization service.</p>
 */
public class AisafeAuthorizationService {

    private final AuthorizationService authorizationService;

    /**
     * Creates a service that resolves the framework authorization service from AuthzRegistry.
     */
    public AisafeAuthorizationService() {
        authorizationService = null;
    }

    /**
     * Creates a service backed by a specific framework authorization service.
     *
     * @param authorizationService authorization service to delegate to
     */
    protected AisafeAuthorizationService(final AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    /**
     * Requires the current authenticated user to have at least one of the given roles.
     *
     * @param roles roles accepted for the action being protected
     */
    public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {
        authorizationService().ensureAuthenticatedUserHasAnyOf(roles);
    }

    /**
     * Requires that any user is currently authenticated, regardless of role.
     */
    public void ensureAuthenticatedUser() {
        authorizationService().ensureAuthenticatedUserHasAnyOf();
    }

    private AuthorizationService authorizationService() {
        if (authorizationService != null) {
            return authorizationService;
        }
        return AuthzRegistry.authorizationService();
    }
}
