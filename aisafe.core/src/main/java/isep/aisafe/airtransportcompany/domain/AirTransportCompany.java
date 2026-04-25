package isep.aisafe.airtransportcompany.domain;

import eapli.framework.domain.model.AggregateRoot;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.io.Serializable;

@Entity
public class AirTransportCompany implements AggregateRoot<ICAOCode>, Serializable {

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    private String name;

    @Embedded
    private Country country;

    @Embedded
    private IATACode iataCode;

    @Embedded
    private ICAOCode icaoCode;

    protected AirTransportCompany() {
    }

    public AirTransportCompany(
            final String name,
            final Country country,
            final IATACode iataCode,
            final ICAOCode icaoCode) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be empty.");
        }

        if (country == null) {
            throw new IllegalArgumentException("Country cannot be null.");
        }

        if (iataCode == null) {
            throw new IllegalArgumentException("IATA code cannot be null.");
        }

        if (icaoCode == null) {
            throw new IllegalArgumentException("ICAO code cannot be null.");
        }

        this.name = name.trim();
        this.country = country;
        this.iataCode = iataCode;
        this.icaoCode = icaoCode;
    }

    @Override
    public ICAOCode identity() {
        return this.icaoCode;
    }

    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof AirTransportCompany)) {
            return false;
        }

        final AirTransportCompany that = (AirTransportCompany) other;
        return this.identity().equals(that.identity());
    }

    public String name() {
        return name;
    }

    public Country country() {
        return country;
    }

    public IATACode iataCode() {
        return iataCode;
    }

    public ICAOCode icaoCode() {
        return icaoCode;
    }
}