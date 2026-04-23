package isep.aisafe.app.backoffice.console;

import isep.aisafe.app.common.console.AisafeBaseApplication;
import isep.aisafe.app.common.console.presentation.authz.LoginUI;
import isep.aisafe.infrastructure.authz.AuthenticationCredentialHandler;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.usermanagement.domain.AisafePasswordPolicy;
import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.domain.model.PlainTextEncoder;


public final class AisafeBackoffice extends AisafeBaseApplication {

    private AisafeBackoffice() {
    }

    public static void main(final String[] args) {
        new AisafeBackoffice().run(args);
    }

    @Override
    protected void doMain(final String[] args) {

        AisafeBootstrapper.bootstrap();

        final LoginUI loginUI = new LoginUI(new AuthenticationCredentialHandler());

        final boolean success = loginUI.show();

        if (!success) {
            System.out.println("Exiting application.");
            return;
        }

        System.out.println("Authenticated user can now access protected functionalities.");
    }

    @Override
    protected String appTitle() {
        return "AISafe Back Office";
    }

    @Override
    protected String appGoodbye() {
        return "AISafe Back Office";
    }

    @Override
    protected void configureAuthz() {
        AuthzRegistry.configure(
                PersistenceContext.repositories().users(),
                new AisafePasswordPolicy(),
                new PlainTextEncoder()
        );
    }
}