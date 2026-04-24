package isep.aisafe.infrastructure.authz;

import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.domain.model.Role;

public class AisafeAuthorizationService {

    public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {
        AuthzRegistry.authorizationService().ensureAuthenticatedUserHasAnyOf(roles);
    }

    public void ensureAuthenticatedUser() {
        AuthzRegistry.authorizationService().ensureAuthenticatedUserHasAnyOf();
    }
}