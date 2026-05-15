package isep.aisafe.app.backoffice.console;

import isep.aisafe.app.backoffice.console.presentation.aircontrolarea.RegisterAirControlAreaUI;
import isep.aisafe.app.backoffice.console.presentation.aircraft.DecommissionAircraftUI;
import isep.aisafe.app.backoffice.console.presentation.aircraft.RegisterAircraftUI;
import isep.aisafe.app.backoffice.console.presentation.aircraftmodel.RegisterAircraftModelUI;
import isep.aisafe.app.backoffice.console.presentation.airport.RegisterAirportUI;
import isep.aisafe.app.backoffice.console.presentation.airtransportcompany.RegisterAirTransportCompanyUI;
import isep.aisafe.app.backoffice.console.presentation.enginemodel.RegisterEngineModelUI;
import isep.aisafe.app.backoffice.console.presentation.usermanagement.RegisterBackofficeUserUI;
import isep.aisafe.app.common.console.AisafeBaseApplication;
import isep.aisafe.app.common.console.presentation.authz.LoginUI;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.authz.AuthenticationCredentialHandler;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.usermanagement.domain.AisafePasswordPolicy;
import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.domain.model.PlainTextEncoder;
import eapli.framework.infrastructure.authz.domain.model.Role;

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

        while (showLoginOptions(scanner)) {
            // Return here after each authenticated user logs out.
        }

        System.out.println("Exiting application.");
    }

    private boolean showLoginOptions(final Scanner scanner) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                      AISafe Console                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1 - Login                                                  ║");
        System.out.println("║ 2 - Exit                                                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.print("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            loginAndShowAuthorizedMenu(scanner);
            return true;
        }

        if ("2".equals(option)) {
            return false;
        }

        System.out.println("Unknown option.");
        return true;
    }

    private void loginAndShowAuthorizedMenu(final Scanner scanner) {
        System.out.println();

        final LoginUI loginUI = new LoginUI(new AuthenticationCredentialHandler(), scanner);

        if (!loginUI.show()) {
            return;
        }

        final AisafeAuthorizationService authz = new AisafeAuthorizationService();
        System.out.println("Logged in as: " + authz.authenticatedUsername());

        if (hasPermission(AisafePermissions.USER_REGISTRATION)) {
            while (showAdminOptions(scanner)) {
                // Keep the authenticated admin session open until logout.
            }
            return;
        }

        if (hasPermission(AisafePermissions.BACKOFFICE_CONFIGURATION)) {
            while (showBackofficeOptions(scanner)) {
                // Keep the authenticated backoffice session open until logout.
            }
            return;
        }

        if (hasPermission(AisafePermissions.AIR_COMPANY_CONFIGURATION)) {
            while (showAirCompanyOptions(scanner)) {
                // Keep the authenticated air-company session open until logout.
            }
            return;
        }

        System.out.println("Authenticated user has no console menu available.");
    }

    private boolean hasPermission(final Role... roles) {
        return AuthzRegistry.authorizationService().session()
                .map(session -> session.authenticatedUser().hasAny(roles))
                .orElse(false);
    }

    private boolean showAdminOptions(final Scanner scanner) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                  Admin Management Console                  ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1 - Register Backoffice User                               ║");
        System.out.println("║ 2 - Logout                                                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.print("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            new RegisterBackofficeUserUI(scanner).show();
            return true;
        }

        if ("2".equals(option)) {
            System.out.println("Returning to login menu.");
            return false;
        }

        System.out.println("Unknown option.");
        return true;
    }

    private boolean showBackofficeOptions(final Scanner scanner) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║               Backoffice Management Console                ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1 - Register Aircraft Model                        (US55)  ║");
        System.out.println("║ 2 - Register Engine Model                          (US56)  ║");
        System.out.println("║ 3 - Register Air Transport Company                 (US60)  ║");
        System.out.println("║ 4 - Register Air Control Area                      (US50)  ║");
        System.out.println("║ 5 - Register Airport                               (US52)  ║");
        System.out.println("║ 6 - Logout                                                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.print("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            new RegisterAircraftModelUI(scanner).show();
            return true;
        }

        if ("2".equals(option)) {
            new RegisterEngineModelUI(scanner).show();
            return true;
        }

        if ("3".equals(option)) {
            new RegisterAirTransportCompanyUI(scanner).show();
            return true;
        }

        if ("4".equals(option)) {
            new RegisterAirControlAreaUI(scanner).show();
            return true;
        }

        if ("5".equals(option)) {
            new RegisterAirportUI(scanner).show();
            return true;
        }

        if ("6".equals(option)) {
            System.out.println("Returning to login menu.");
            return false;
        }

        System.out.println("Unknown option.");
        return true;
    }

    private boolean showAirCompanyOptions(final Scanner scanner) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              Air Company Management Console               ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1 - Register Aircraft                              (US70)  ║");
        System.out.println("║ 2 - Decommission Aircraft                          (US71)  ║");
        System.out.println("║ 3 - Logout                                                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.print("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            new RegisterAircraftUI(scanner).show();
            return true;
        }

        if ("2".equals(option)) {
            new DecommissionAircraftUI(scanner).show();
            return true;
        }

        if ("3".equals(option)) {
            System.out.println("Returning to login menu.");
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
