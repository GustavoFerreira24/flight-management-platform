package isep.aisafe.app.backoffice.console;

import isep.aisafe.app.backoffice.console.airtransportcompany.RegisterAirTransportCompanyUI;
import isep.aisafe.app.common.console.AisafeBaseApplication;
import isep.aisafe.app.common.console.presentation.authz.LoginUI;
import isep.aisafe.infrastructure.authz.AuthenticationCredentialHandler;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.usermanagement.domain.AisafePasswordPolicy;
import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.domain.model.PlainTextEncoder;
import isep.aisafe.app.backoffice.console.presentation.aircraftmodel.RegisterAircraftModelUI;
import isep.aisafe.app.backoffice.console.presentation.enginemodel.RegisterEngineModelUI;
import isep.aisafe.app.backoffice.console.presentation.usermanagement.RegisterBackofficeUserUI;

import java.util.Scanner;

public final class AisafeBackoffice extends AisafeBaseApplication {

    private AisafeBackoffice() {
    }

    public static void main(final String[] args) {
        new AisafeBackoffice().run(args);
    }

    @Override
    protected void doMain(final String[] args) {

        AisafeBootstrapper.bootstrap();

        final Scanner scanner = new Scanner(System.in);
        final LoginUI loginUI = new LoginUI(new AuthenticationCredentialHandler(), scanner);

        final boolean success = loginUI.show();

        if (!success) {
            System.out.println("Exiting application.");
            return;
        }

        showMainOptions(scanner);

        System.out.println("Authenticated user can now access protected functionalities.");
    }

    private void showMainOptions(final Scanner scanner) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║               Backoffice Management Console                ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1 - Register Backoffice User                               ║ ");
        System.out.println("║ 2- Register Aircraft Model                         (US55)  ║ ");
        System.out.println("║ 3 - Register Engine Model                          (US56)  ║ ");
        System.out.println("║ 4 - Register Air Transport Company                 (US60)  ║ ");
        System.out.println("║ 5 - Register Airport                               (US52)  ║ ");
        System.out.println("║ 6 - Exit                                                   ║ ");
        System.out.println("╚════════════════════════════════════════════════════════════╝ ");
        System.out.print("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            new RegisterBackofficeUserUI(scanner).show();
            return;
        }

        if ("2".equals(option)) {
            new RegisterAircraftModelUI(scanner).show();
            return;
        }

        if ("3".equals(option)) {
            new RegisterEngineModelUI(scanner).show();
            return;
        }

        if ("4".equals(option)) {
            new RegisterAirTransportCompanyUI(scanner).show();
            return;
        }

        System.out.println("Unknown option.");
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
