package isep.aisafe.airtransportcompany.domain;

import eapli.framework.domain.model.AggregateRoot;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.io.Serializable;

/**
 * Represents an air transport company registered in the system.
 *
 * It is the aggregate root responsible for keeping the company's identifying
 * information consistent.
 */
@Entity
public class AirTransportCompany implements AggregateRoot<IATACode>, Serializable {

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    @Column(name = "COMPANY_NAME", nullable = false)
    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "COUNTRY", nullable = false))
    private Country country;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "IATA_CODE", nullable = false, unique = true))
    private IATACode iataCode;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ICAO_CODE", nullable = false, unique = true))
    private ICAOCode icaoCode;

    
    protected AirTransportCompany() {
    }

    /**
     * Creates a new air transport company.
     *
     * @param name     the company name
     * @param country  the company country
     * @param iataCode the company IATA code
     * @param icaoCode the company ICAO code
     */
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

        this.name = name.trim().replaceAll("\\s+", " ");
        this.country = country;
        this.iataCode = iataCode;
        this.icaoCode = icaoCode;
    }

    /**
     * Returns the business identity of this aggregate.
     *
     * @return the IATA code
     */
    @Override
    public IATACode identity() {
        return iataCode;
    }

    /**
     * Checks if another object represents the same air transport company.
     *
     * @param other the object to compare with
     * @return true if both objects have the same identity
     */
    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof AirTransportCompany)) {
            return false;
        }

        final AirTransportCompany that = (AirTransportCompany) other;
        return this.identity().equals(that.identity());
    }

    /**
     * Returns the company name.
     *
     * @return the company name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the company country.
     *
     * @return the country
     */
    public Country country() {
        return country;
    }

    /**
     * Returns the company IATA code.
     *
     * @return the IATA code
     */
    public IATACode iataCode() {
        return iataCode;
    }

    /**
     * Returns the company ICAO code.
     *
     * @return the ICAO code
     */
    public ICAOCode icaoCode() {
        return icaoCode;
    }
}