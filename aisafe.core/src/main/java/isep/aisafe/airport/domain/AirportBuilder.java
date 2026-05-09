package isep.aisafe.airport.domain;

import eapli.framework.domain.model.DomainFactory;
import isep.aisafe.aircontrolarea.domain.AirControlArea;

/**
 * Builder/factory to create Airport instances without exposing
 * construction complexity to the controller.
 */
public class AirportBuilder implements DomainFactory<Airport> {

    private AirportICAOCode icao;
    private AirportIATACode iata;
    private String name;
    private String town;
    private GpsCoordinates coords;
    private Elevation elev;
    private AirControlArea area;

    /**
     * Sets the airport identification data.
     *
     * @param icao the airport ICAO code
     * @param iata the airport IATA code
     * @param name the airport name
     * @param town the airport town
     * @return this builder
     */
    public AirportBuilder withData(
            final String icao,
            final String iata,
            final String name,
            final String town) {

        this.icao = AirportICAOCode.valueOf(icao);
        this.iata = AirportIATACode.valueOf(iata);
        this.name = name;
        this.town = town;
        return this;
    }

    /**
     * Sets the airport location data.
     *
     * @param lat the latitude
     * @param lon the longitude
     * @param elevation the elevation in meters
     * @return this builder
     */
    public AirportBuilder withLocation(
            final double lat,
            final double lon,
            final double elevation) {

        this.coords = new GpsCoordinates(lat, lon);
        this.elev = new Elevation(elevation);
        return this;
    }

    /**
     * Assigns the airport to an air control area.
     *
     * @param area the air control area
     * @return this builder
     */
    public AirportBuilder inArea(final AirControlArea area) {
        this.area = area;
        return this;
    }

    /**
     * Creates the airport.
     *
     * @return the new airport
     */
    @Override
    public Airport build() {
        return new Airport(icao, iata, name, town, coords, elev, area);
    }
}