package isep.aisafe.application.aircompany;

import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.AirTransportCompanyRepository;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

/**
 * Controller for air company configuration use cases.
 */
public class AirCompanyConfigurationController {

    private final AisafeAuthorizationService authz;
    private final AirTransportCompanyRepository airTransportCompanyRepository;

    /**
     * Creates the controller with the default authorization service and repository.
     */
    public AirCompanyConfigurationController() {
        this(new AisafeAuthorizationService(), PersistenceContext.repositories().airTransportCompanies());
    }

    /**
     * Creates the controller with a custom repository.
     * This constructor is mainly useful for tests.
     *
     * @param airTransportCompanyRepository the air transport company repository
     */
    public AirCompanyConfigurationController(
            final AirTransportCompanyRepository airTransportCompanyRepository) {
        this(new AisafeAuthorizationService(), airTransportCompanyRepository);
    }

    AirCompanyConfigurationController(
            final AisafeAuthorizationService authz,
            final AirTransportCompanyRepository airTransportCompanyRepository) {

        if (authz == null) {
            throw new IllegalArgumentException("Authorization service cannot be null.");
        }

        if (airTransportCompanyRepository == null) {
            throw new IllegalArgumentException("Air transport company repository cannot be null.");
        }

        this.authz = authz;
        this.airTransportCompanyRepository = airTransportCompanyRepository;
    }

    /**
     * Authorizes access to the air company configuration area.
     */
    public void execute() {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);
    }

    /**
     * Registers a new air transport company.
     *
     * @param name the company name
     * @param country the company country
     * @param iataCode the company IATA code
     * @param icaoCode the company ICAO code
     * @return the registered air transport company
     */
    public AirTransportCompany registerAirTransportCompany(
            final String name,
            final String country,
            final String iataCode,
            final String icaoCode) {

        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        final Country companyCountry = Country.valueOf(country);
        final IATACode companyIataCode = IATACode.valueOf(iataCode);
        final ICAOCode companyIcaoCode = ICAOCode.valueOf(icaoCode);

        if (airTransportCompanyRepository.findByIataCode(companyIataCode).isPresent()) {
            throw new IllegalArgumentException("Air transport company IATA code already exists.");
        }

        if (airTransportCompanyRepository.findByIcaoCode(companyIcaoCode).isPresent()) {
            throw new IllegalArgumentException("Air transport company ICAO code already exists.");
        }

        final AirTransportCompany airTransportCompany = new AirTransportCompany(
                name,
                companyCountry,
                companyIataCode,
                companyIcaoCode
        );

        return airTransportCompanyRepository.save(airTransportCompany);
    }
}
