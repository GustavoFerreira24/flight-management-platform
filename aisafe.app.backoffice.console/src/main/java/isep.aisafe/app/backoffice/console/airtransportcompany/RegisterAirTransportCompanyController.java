package isep.aisafe.app.backoffice.console.airtransportcompany;

import isep.aisafe.airtransportcompany.domain.*;

public class RegisterAirTransportCompanyController {

    private final AirTransportCompanyRepository repository;

    public RegisterAirTransportCompanyController(
            final AirTransportCompanyRepository repository) {
        this.repository = repository;
    }

    public AirTransportCompany registerCompany(
            final String name,
            final String country,
            final String iata,
            final String icao) {

        final Country companyCountry = Country.valueOf(country);
        final IATACode iataCode = IATACode.valueOf(iata);
        final ICAOCode icaoCode = ICAOCode.valueOf(icao);

        if (repository.findByIataCode(iataCode).isPresent()) {
            throw new IllegalArgumentException("IATA code already exists");
        }

        if (repository.findByIcaoCode(icaoCode).isPresent()) {
            throw new IllegalArgumentException("ICAO code already exists");
        }

        if (repository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Company name already exists");
        }

        final AirTransportCompany company =
                new AirTransportCompany(name, companyCountry, iataCode, icaoCode);

        return repository.save(company);
    }
}