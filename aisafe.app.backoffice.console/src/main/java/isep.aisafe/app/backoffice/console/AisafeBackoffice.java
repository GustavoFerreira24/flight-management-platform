package isep.aisafe.app.backoffice.console;

import isep.aisafe.app.backoffice.console.presentation.aircontrolarea.RegisterAirControlAreaUI;
import isep.aisafe.app.backoffice.console.presentation.aircraft.DecommissionAircraftUI;
import isep.aisafe.app.backoffice.console.presentation.aircraft.RegisterAircraftUI;
import isep.aisafe.app.backoffice.console.presentation.aircraftmodel.RegisterAircraftModelUI;
import isep.aisafe.app.backoffice.console.presentation.airtransportcompany.RegisterAirTransportCompanyUI;
import isep.aisafe.app.backoffice.console.presentation.enginemodel.RegisterEngineModelUI;
import isep.aisafe.app.backoffice.console.presentation.usermanagement.RegisterBackofficeUserUI;
import isep.aisafe.app.common.console.AisafeBaseApplication;
import isep.aisafe.app.common.console.presentation.authz.LoginUI;
import isep.aisafe.infrastructure.authz.AuthenticationCredentialHandler;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.usermanagement.domain.AisafePasswordPolicy;
import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.domain.model.PlainTextEncoder;

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

        while (showMainOptions(scanner)) {
            // Keep the authenticated session open until the user explicitly exits.
        }

        System.out.println("Exiting authenticated session.");
    }

    private boolean showMainOptions(final Scanner scanner) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║               Backoffice Management Console                ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1 - Register Backoffice User                               ║");
        System.out.println("║ 2 - Register Aircraft Model                        (US55)  ║");
        System.out.println("║ 3 - Register Engine Model                          (US56)  ║");
        System.out.println("║ 4 - Register Air Transport Company                 (US60)  ║");
        System.out.println("║ 5 - Register Air Control Area                      (US50)  ║");
        System.out.println("║ 6 - Register Aircraft                              (US70)  ║");
        System.out.println("║ 7 - Decommission Aircraft                         (US71)   ║");
        System.out.println("║ 8 - Register Airport                               (US52)  ║");
        System.out.println("║ 9 - Exit                                                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.print("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            new RegisterBackofficeUserUI(scanner).show();
            return true;
        }

        if ("2".equals(option)) {
            new RegisterAircraftModelUI(scanner).show();
            return true;
        }

        if ("3".equals(option)) {
            new RegisterEngineModelUI(scanner).show();
            return true;
        }

        if ("4".equals(option)) {
            new RegisterAirTransportCompanyUI(scanner).show();
            return true;
        }

        if ("5".equals(option)) {
            new RegisterAirControlAreaUI(scanner).show();
            return true;
        }

        if ("6".equals(option)) {
            new RegisterAircraftUI(scanner).show();
            return true;
        }

        if ("7".equals(option)) {
            new DecommissionAircraftUI(scanner).show();
            return true;
        }

        if ("8".equals(option)) {
            System.out.println("Register Airport option is not connected yet.");
            return true;
        }

        if ("9".equals(option)) {
            System.out.println("Exiting application.");
            return false;
        }

        System.out.println("Unknown option.");
        return true;
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
