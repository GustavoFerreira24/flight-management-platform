package isep.aisafe.airport.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.domain.model.DomainEntities;
import isep.aisafe.aircontrolarea.domain.AirControlArea;

import isep.aisafe.airtransportcompany.domain.Country;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Represents an airport registered in the system.
 *
 * An airport is identified by its ICAO code and must be associated with
 * exactly one air control area.
 */
@Entity
public class Airport implements AggregateRoot<AirportICAOCode>, Serializable {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "ICAO_CODE", nullable = false))
    private AirportICAOCode icao;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "IATA_CODE", nullable = false, unique = true))
    private AirportIATACode iata;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Country country;

    @Column(nullable = false)
    private String town;

    @Embedded
    private GpsCoordinates coordinates;

    @Embedded
    private Elevation elevation;

    @ManyToOne(optional = false)
    private AirControlArea airControlArea;

    /**
     * Required by ORM.
     */
    protected Airport() {
    }

    /**
     * Creates a new airport.
     *
     * @param icao the airport ICAO code
     * @param iata the airport IATA code
     * @param name the airport name
     * @param country the airport country
     * @param town the airport town
     * @param coords the airport coordinates
     * @param elev the airport elevation
     * @param area the air control area
     */
    public Airport(
            final AirportICAOCode icao,
            final AirportIATACode iata,
            final String name,
            final Country country,
            final String town,
            final GpsCoordinates coords,
            final Elevation elev,
            final AirControlArea area) {

        if (icao == null) {
            throw new IllegalArgumentException("Airport ICAO code cannot be null.");
        }

        if (iata == null) {
            throw new IllegalArgumentException("Airport IATA code cannot be null.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Airport name cannot be empty.");
        }

        if (country == null) {
            throw new IllegalArgumentException("Airport country cannot be null.");
        }

        if (town == null || town.trim().isEmpty()) {
            throw new IllegalArgumentException("Airport town cannot be empty.");
        }

        if (coords == null) {
            throw new IllegalArgumentException("Airport coordinates cannot be null.");
        }

        if (elev == null) {
            throw new IllegalArgumentException("Airport elevation cannot be null.");
        }

        if (area == null) {
            throw new IllegalArgumentException("Air control area is mandatory.");
        }

        this.icao = icao;
        this.iata = iata;
        this.name = name.trim().replaceAll("\\s+", " ");
        this.country = country;
        this.town = town.trim().replaceAll("\\s+", " ");
        this.coordinates = coords;
        this.elevation = elev;
        this.airControlArea = area;
    }

    @Override
    public boolean sameAs(final Object other) {
        return DomainEntities.areEqual(this, other);
    }

    @Override
    public AirportICAOCode identity() {
        return this.icao;
    }


    /**
     * Returns icao code.
     *
     * @return icao code
     */
    public AirportICAOCode icaoCode() {
        return icao;
    }

    /**
     * Returns iata code.
     *
     * @return iata code
     */
    public AirportIATACode iataCode() {
        return iata;
    }

    /**
     * Returns airport name
     *
     * @return airport name
     */
    public String name() {
        return name;
    }

    /**
     * Returns airport country
     *
     * @return airport country
     */
    public Country country() {
        return country;
    }

    /**
     * Returns airport town
     *
     * @return airport town
     */
    public String town() {
        return town;
    }

    /**
     * Returns airport coordinates
     *
     * @return airport coordinates
     */
    public GpsCoordinates coordinates() {
        return coordinates;
    }

    /**
     * Returns airport elevation
     *
     * @return airport elevation
     */
    public Elevation elevation() {
        return elevation;
    }

    /**
     * Returns airport air ccontrol area
     *
     * @return airport air ccontrol area
     */
    public AirControlArea airControlArea() {
        return airControlArea;
    }
}