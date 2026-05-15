package isep.aisafe.application.listCustomers;

import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.customercollaborator.domain.CustomerCollaborator;
import isep.aisafe.customercollaborator.domain.CustomerCollaboratorRepository;
import isep.aisafe.customercollaborator.domain.CustomerReference;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

import java.util.List;

public class ListCustomerCollaboratorsController {

    private final AisafeAuthorizationService authz;
    private final CustomerCollaboratorRepository collaboratorRepository;
    private final AirTransportCompanyRepository airTransportCompanyRepository;
    private final AirControlAreaRepository airControlAreaRepository;

    public ListCustomerCollaboratorsController() {

        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().customerCollaborators(),
                PersistenceContext.repositories().airTransportCompanies(),
                PersistenceContext.repositories().airControlAreas()
        );

    }

    public ListCustomerCollaboratorsController(
            final AisafeAuthorizationService authz,
            final CustomerCollaboratorRepository collaboratorRepository,
            final AirTransportCompanyRepository airTransportCompanyRepository,
            final AirControlAreaRepository airControlAreaRepository) {

        if (authz == null || collaboratorRepository == null || airTransportCompanyRepository == null || airControlAreaRepository == null) {
            throw new IllegalArgumentException("As dependências não podem ser nulas.");
        }

        this.authz = authz;
        this.collaboratorRepository = collaboratorRepository;
        this.airTransportCompanyRepository = airTransportCompanyRepository;
        this.airControlAreaRepository = airControlAreaRepository;
    }

    public List<CustomerCollaborator> listForAirTransportCompany(String iataCode) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.USER_MANAGEMENT);

        IATACode customerIataCode = IATACode.valueOf(iataCode);
        airTransportCompanyRepository.findByIataCode(customerIataCode).orElseThrow(() -> new IllegalArgumentException("Air transport company not found."));

        CustomerReference reference = CustomerReference.airTransportCompany(customerIataCode.toString());

        return collaboratorRepository.findByCustomerReference(reference).stream().filter(CustomerCollaborator::isActive).toList();
    }

    public List<CustomerCollaborator> listForAirControlArea(String areaCode) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        AirControlAreaCode customerAreaCode = AirControlAreaCode.valueOf(areaCode);
        airControlAreaRepository.findByCode(customerAreaCode).orElseThrow(() -> new IllegalArgumentException("Air control area not found."));

        CustomerReference reference = CustomerReference.airControlArea(areaCode);
        return collaboratorRepository.findByCustomerReference(reference).stream().filter(CustomerCollaborator::isActive).toList();

    }

}
