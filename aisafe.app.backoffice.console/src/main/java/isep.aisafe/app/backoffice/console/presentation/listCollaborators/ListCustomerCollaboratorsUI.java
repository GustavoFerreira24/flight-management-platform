package isep.aisafe.app.backoffice.console.presentation.listCollaborators;

import isep.aisafe.application.listcustomercollaborators.ListCustomerCollaboratorsController;

import java.util.List;
import java.util.Scanner;

import isep.aisafe.customercollaborator.domain.CustomerCollaborator;

public class ListCustomerCollaboratorsUI {

    private final ListCustomerCollaboratorsController controller = new ListCustomerCollaboratorsController();
    private final Scanner scanner;

    /**
     * Constructs the UI using the standard system input.
     */
    public ListCustomerCollaboratorsUI() {
        this(new Scanner(System.in));
    }

    /**
     * Constructs the UI using a specific scanner, allowing for easier unit testing.
     *
     * @param scanner the scanner used to read user input.
     */
    public ListCustomerCollaboratorsUI(final Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean show() {
        try {
            System.out.println("\nDo you wish to list:");

            System.out.println("1 - Air Transport Company Customer Collaborators");
            System.out.println("2 - Air Control Area Customer Collaborators     ");

            System.out.print("Option: ");
            final String option = scanner.nextLine();

            System.out.println();

            List<CustomerCollaborator> collaborators;

            if ("1".equals(option)) {

                System.out.print("Enter the IATA code of the Air Transport Company (2 letters): ");
                final String iata = scanner.nextLine();
                collaborators = controller.listForAirTransportCompany(iata);

                System.out.println();

                System.out.println("Air Transport Company Collaborators:");
                for (CustomerCollaborator collaborator : collaborators) {
                    System.out.println("- " + collaborator.firstName() + " " + collaborator.lastName() + " (" + collaborator.email() + ")");
                }

                System.out.println();
                return true;
            }

            if ("2".equals(option)) {
                System.out.print("Enter the code of the Air Control Area Company (2 to 10 digits or letters): ");
                final String areaCode = scanner.nextLine();
                collaborators = controller.listForAirControlArea(areaCode);

                System.out.println();

                System.out.println("Air Control Area Company Collaborators:");
                for (CustomerCollaborator collaborator : collaborators) {
                    System.out.println("- " + collaborator.firstName() + " " + collaborator.lastName() + " (" + collaborator.email() + ")");
                }

                System.out.println();
                return true;
            }

        } catch (Exception e) {
            System.out.println("\nCould not list collaborators: " + e.getMessage());
        }
        return false;
    }




}
