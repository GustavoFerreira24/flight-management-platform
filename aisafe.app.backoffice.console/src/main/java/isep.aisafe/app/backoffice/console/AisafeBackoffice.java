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
import isep.aisafe.app.backoffice.console.presentation.flightmanagement.CreateFlightPlanUI;
import isep.aisafe.app.backoffice.console.presentation.flightroute.RegisterFlightRouteUI;
import isep.aisafe.app.backoffice.console.presentation.pilot.ManagePilotsUI;
import isep.aisafe.app.backoffice.console.presentation.weather.ConsultWeatherDataUI;
import isep.aisafe.app.backoffice.console.presentation.weather.ImportBulkWeatherDataUI;
import isep.aisafe.app.backoffice.console.presentation.listCollaborators.ListCustomerCollaboratorsUI;
import isep.aisafe.app.backoffice.console.presentation.usermanagement.RegisterBackofficeUserUI;
import isep.aisafe.app.backoffice.console.presentation.weather.RegisterWeatherDataUI;
import isep.aisafe.app.common.console.AisafeBaseApplication;
import isep.aisafe.app.common.console.ConsoleStyle;
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

        ConsoleStyle.printInfo("Exiting application.");
    }

    private boolean showAccessOptions(final Scanner scanner) {
        ConsoleStyle.printMenu(
                "AISafe Console Login",
                ConsoleStyle.option(1, "Login"),
                ConsoleStyle.option(2, "Exit")
        );
        ConsoleStyle.printPrompt("Option: ");

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

        ConsoleStyle.printWarning("Unknown option.");
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

        if (authz.authenticatedUserHasAnyOf(AisafePermissions.FLIGHT_PLAN_MANAGEMENT)) {
            while (showFlightPlanManagementOptions(scanner)) {
                // Keep the authenticated flight-plan management session open until logout.
            }
            return;
        }

        if (authz.authenticatedUserHasAnyOf(AisafePermissions.WEATHER_DATA_CONSULTATION)) {
            while (showWeatherConsultationOptions(scanner)) {
                // Keep the authenticated weather consultation session open until logout.
            }
            return;
        }

        ConsoleStyle.printWarning("Authenticated user has no console menu available.");
    }

    private boolean showAdminOptions(final Scanner scanner) {
        ConsoleStyle.printMenu(
                "Admin Management Console",
                ConsoleStyle.option(1, "Register Backoffice User"),
                ConsoleStyle.option(2, "Logout")
        );
        ConsoleStyle.printPrompt("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            executeMenuAction(() -> new RegisterBackofficeUserUI(scanner).show());
            return true;
        }

        if ("2".equals(option)) {
            ConsoleStyle.printInfo("Returning to login menu.");
            return false;
        }

        ConsoleStyle.printWarning("Unknown option.");
        return true;
    }

    private boolean showBackofficeOptions(final Scanner scanner) {
        ConsoleStyle.printMenu(
                "Backoffice Management Console",
                ConsoleStyle.option(1, "Register Aircraft Model                        (US55)"),
                ConsoleStyle.option(2, "Register Engine Model                          (US56)"),
                ConsoleStyle.option(3, "Register Air Transport Company                 (US60)"),
                ConsoleStyle.option(4, "Register Air Control Area                      (US50)"),
                ConsoleStyle.option(5, "Register Airport                               (US52)"),
                ConsoleStyle.option(6, "Register Customer Collaborator                 (US61)"),
                ConsoleStyle.option(7, "List Collaborators                             (US62)"),
                ConsoleStyle.option(8, "Logout")
        );
        ConsoleStyle.printPrompt("Option: ");

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
            ConsoleStyle.printInfo("Returning to login menu.");
            return false;
        }

        ConsoleStyle.printWarning("Unknown option.");
        return true;
    }

    private boolean showAirCompanyOptions(final Scanner scanner) {
        ConsoleStyle.printMenu(
                "Air Company Management Console",
                ConsoleStyle.option(1, "Register Aircraft                              (US70)"),
                ConsoleStyle.option(2, "Decommission Aircraft                          (US71)"),
                ConsoleStyle.option(3, "List Aircraft Fleet                            (US72)"),
                ConsoleStyle.option(4, "Register Flight Route                          (US73)"),
                ConsoleStyle.option(5, "Manage Pilots                             (US75-US77)"),
                ConsoleStyle.option(6, "Logout")
        );
        ConsoleStyle.printPrompt("Option: ");

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
            executeMenuAction(() -> new RegisterFlightRouteUI(scanner).show());
            return true;
        }

        if ("5".equals(option)) {
            executeMenuAction(() -> new ManagePilotsUI(scanner).show());
            return true;
        }

        if ("6".equals(option)) {
            ConsoleStyle.printInfo("Returning to login menu.");
            return false;
        }

        ConsoleStyle.printWarning("Unknown option.");
        return true;
    }

    private boolean showWeatherPersonOptions(final Scanner scanner) {
        ConsoleStyle.printMenu(
                "Weather Management Console",
                ConsoleStyle.option(1, "Register Weather Data                          (US41)"),
                ConsoleStyle.option(2, "Import Bulk Weather Data                       (US42)"),
                ConsoleStyle.option(3, "Consult Weather Data                           (US43)"),
                ConsoleStyle.option(4, "Logout")
        );
        ConsoleStyle.printPrompt("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            executeMenuAction(() -> new RegisterWeatherDataUI(scanner).show());
            return true;
        }

        if ("2".equals(option)) {
            executeMenuAction(() -> new ImportBulkWeatherDataUI(scanner).show());
            return true;
        }

        if ("3".equals(option)) {
            executeMenuAction(() -> new ConsultWeatherDataUI(scanner).show());
            return true;
        }

        if ("4".equals(option)) {
            ConsoleStyle.printInfo("Returning to login menu.");
            return false;
        }

        ConsoleStyle.printWarning("Unknown option.");
        return true;
    }

    private boolean showFlightPlanManagementOptions(final Scanner scanner) {
        ConsoleStyle.printMenu(
                "Flight Plan Management Console",
                ConsoleStyle.option(1, "Create Flight Plan                            (US80)"),
                ConsoleStyle.option(2, "Consult Weather Data                          (US43)"),
                ConsoleStyle.option(3, "Logout")
        );
        ConsoleStyle.printPrompt("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            executeMenuAction(() -> new CreateFlightPlanUI(scanner).show());
            return true;
        }

        if ("2".equals(option)) {
            executeMenuAction(() -> new ConsultWeatherDataUI(scanner).show());
            return true;
        }

        if ("3".equals(option)) {
            ConsoleStyle.printInfo("Returning to login menu.");
            return false;
        }

        ConsoleStyle.printWarning("Unknown option.");
        return true;
    }

    private boolean showWeatherConsultationOptions(final Scanner scanner) {
        ConsoleStyle.printMenu(
                "Weather Consultation Console",
                ConsoleStyle.option(1, "Consult Weather Data                           (US43)"),
                ConsoleStyle.option(2, "Logout")
        );
        ConsoleStyle.printPrompt("Option: ");

        final String option = scanner.nextLine();

        if ("1".equals(option)) {
            executeMenuAction(() -> new ConsultWeatherDataUI(scanner).show());
            return true;
        }

        if ("2".equals(option)) {
            ConsoleStyle.printInfo("Returning to login menu.");
            return false;
        }

        ConsoleStyle.printWarning("Unknown option.");
        return true;
    }

    private void executeMenuAction(final Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException e) {
            ConsoleStyle.printError("Invalid input: " + e.getMessage());
        } catch (RuntimeException e) {
            ConsoleStyle.printError("Operation failed: " + e.getMessage());
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
