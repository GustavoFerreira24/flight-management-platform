package isep.aisafe.airtransportcompany.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;

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

        Preconditions.ensure(name != null && !name.trim().isEmpty(), "Company name cannot be empty.");
        Preconditions.nonNull(country, "Country cannot be null.");
        Preconditions.nonNull(iataCode, "IATA code cannot be null.");
        Preconditions.nonNull(icaoCode, "ICAO code cannot be null.");

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
     * Checks whether this company has the given IATA code.
     *
     * @param code the IATA code to compare
     * @return true if the code identifies this company
     */
    public boolean hasIataCode(final IATACode code) {
        return iataCode.equals(code);
    }

    /**
     * Checks whether the given text starts with this company's IATA code.
     *
     * @param value text to check
     * @return true if the text starts with the company's IATA code
     */
    public boolean startsWithIataCode(final String value) {
        return value != null && value.startsWith(iataCode.toString());
    }

    /**
     * Returns the company ICAO code.
     *
     * @return the ICAO code
     */
    public ICAOCode icaoCode() {
        return icaoCode;
    }

    /**
     * Checks whether this company has the given ICAO code.
     *
     * @param code the ICAO code to compare
     * @return true if the code identifies this company
     */
    public boolean hasIcaoCode(final ICAOCode code) {
        return icaoCode.equals(code);
    }
}
