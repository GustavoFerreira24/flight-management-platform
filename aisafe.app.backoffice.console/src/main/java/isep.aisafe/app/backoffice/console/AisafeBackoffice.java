package isep.aisafe.app.backoffice.console;

import isep.aisafe.app.backoffice.console.presentation.aircontrolarea.RegisterAirControlAreaUI;
import isep.aisafe.app.backoffice.console.presentation.aircraft.DecommissionAircraftUI;
import isep.aisafe.app.backoffice.console.presentation.aircraft.ListAircraftFleetUI;
import isep.aisafe.app.backoffice.console.presentation.aircraft.RegisterAircraftUI;
import isep.aisafe.app.backoffice.console.presentation.aircraftmodel.RegisterAircraftModelUI;
import isep.aisafe.app.backoffice.console.presentation.airport.RegisterAirportUI;
import isep.aisafe.app.backoffice.console.presentation.airtransportcompany.RegisterAirTransportCompanyUI;
import isep.aisafe.app.backoffice.console.presentation.customercollaborator.RegisterCustomerCollaboratorUI;
import isep.aisafe.app.backoffice.console.presentation.enginemodel.RegisterEngineModelUI;
import isep.aisafe.app.backoffice.console.presentation.listCollaborators.ListCustomerCollaboratorsUI;
import isep.aisafe.app.backoffice.console.presentation.usermanagement.RegisterBackofficeUserUI;
import isep.aisafe.app.backoffice.console.presentation.weather.RegisterWeatherDataUI;
import isep.aisafe.app.common.console.AisafeBaseApplication;
import isep.aisafe.app.common.console.presentation.authz.LoginUI;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.authz.AuthenticationCredentialHandler;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.usermanagement.domain.AisafePasswordPolicy;
import isep.aisafe.usermanagement.domain.AisafeRoles;
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

        while (showAccessOptions(scanner)) {
            // Return here after each authenticated area logs out.
        }

        System.out.println("Exiting application.");
    }

    private boolean showAccessOptions(final Scanner scanner) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    AISafe Console Login                    ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1 - Login                                                  ║");
        System.out.println("║ 2 - Exit                                                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.print("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            if (login(scanner)) {
                showAuthenticatedUserOptions(scanner);
            }
            return true;
        }

        if ("2".equals(option)) {
            return false;
        }

        System.out.println("Unknown option.");
        return true;
    }

    private boolean login(final Scanner scanner) {
        System.out.println();

        final LoginUI loginUI = new LoginUI(new AuthenticationCredentialHandler(), scanner);
        return loginUI.show();
    }

    private void showAuthenticatedUserOptions(final Scanner scanner) {
        final AisafeAuthorizationService authz = new AisafeAuthorizationService();

        if (authz.authenticatedUserHasAnyOf(AisafeRoles.ADMIN)) {
            while (showAdminOptions(scanner)) {
                // Keep the authenticated admin session open until logout.
            }
            return;
        }

        if (authz.authenticatedUserHasAnyOf(AisafeRoles.BACKOFFICE_OPERATOR)) {
            while (showBackofficeOptions(scanner)) {
                // Keep the authenticated backoffice session open until logout.
            }
            return;
        }

        if (authz.authenticatedUserHasAnyOf(AisafePermissions.AIR_COMPANY_CONFIGURATION)) {
            while (showAirCompanyOptions(scanner)) {
                // Keep the authenticated air-company session open until logout.
            }
            return;
        }

        if (authz.authenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_MANAGEMENT)) {
            while (showWeatherPersonOptions(scanner)) {
                // Keep the authenticated weather person session open until logout.
            }
            return;
        }

        System.out.println("Authenticated user has no console menu available.");
    }

    private boolean showAdminOptions(final Scanner scanner) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    Admin Management Console                ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1 - Register Backoffice User                               ║");
        System.out.println("║ 2 - Logout                                                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.print("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            executeMenuAction(() -> new RegisterBackofficeUserUI(scanner).show());
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
        System.out.println("║ 6 - Register Customer Collaborator                 (US61)  ║");
        System.out.println("║ 7 - List Collaborators                             (US62)  ║");
        System.out.println("║ 8 - Logout                                                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.print("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            executeMenuAction(() -> new RegisterAircraftModelUI(scanner).show());
            return true;
        }

        if ("2".equals(option)) {
            executeMenuAction(() -> new RegisterEngineModelUI(scanner).show());
            return true;
        }

        if ("3".equals(option)) {
            executeMenuAction(() -> new RegisterAirTransportCompanyUI(scanner).show());
            return true;
        }

        if ("4".equals(option)) {
            executeMenuAction(() -> new RegisterAirControlAreaUI(scanner).show());
            return true;
        }

        if ("5".equals(option)) {
            executeMenuAction(() -> new RegisterAirportUI(scanner).show());
            return true;
        }

        if ("6".equals(option)) {
            executeMenuAction(() -> new RegisterCustomerCollaboratorUI(scanner).show());
            return true;
        }

        if ("7".equals(option)) {
            executeMenuAction(() -> new ListCustomerCollaboratorsUI(scanner).show());
            return true;
        }

        if ("8".equals(option)) {
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
        System.out.println("║ 3 - List Aircraft Fleet                            (US72)  ║");
        System.out.println("║ 4 - Logout                                                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.print("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            executeMenuAction(() -> new RegisterAircraftUI(scanner).show());
            return true;
        }

        if ("2".equals(option)) {
            executeMenuAction(() -> new DecommissionAircraftUI(scanner).show());
            return true;
        }

        if ("3".equals(option)) {
            executeMenuAction(() -> new ListAircraftFleetUI(scanner).show());
            return true;
        }

        if ("4".equals(option)) {
            System.out.println("Returning to login menu.");
            return false;
        }

        System.out.println("Unknown option.");
        return true;
    }

    private boolean showWeatherPersonOptions(final Scanner scanner) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              Weather Management Console                   ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1 - Register Weather Data                          (US41)  ║");
        System.out.println("║ 2 - Logout                                                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.print("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            executeMenuAction(() -> new RegisterWeatherDataUI(scanner).show());
            return true;
        }

        if ("2".equals(option)) {
            System.out.println("Returning to login menu.");
            return false;
        }

        System.out.println("Unknown option.");
        return true;
    }

    private void executeMenuAction(final Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
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
