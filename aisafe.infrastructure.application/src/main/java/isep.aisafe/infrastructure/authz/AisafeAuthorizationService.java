package isep.aisafe.infrastructure.authz;

import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.application.AuthorizationService;
import eapli.framework.infrastructure.authz.domain.model.Role;

public class AisafeAuthorizationService {

    private final AuthorizationService authorizationService;

    public AisafeAuthorizationService() {
        authorizationService = null;
    }

    protected AisafeAuthorizationService(final AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {
        authorizationService().ensureAuthenticatedUserHasAnyOf(roles);
    }

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
