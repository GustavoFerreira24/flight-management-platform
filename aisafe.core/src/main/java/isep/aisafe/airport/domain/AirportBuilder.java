package isep.aisafe.airport.domain;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.IATACode;
import isep.aisafe.ICAOCode;
import eapli.framework.domain.model.DomainFactory;

/**
 * Builder/Factory to create Airport instances without exposing
 * construction complexity to the Controller.
 */
public class AirportBuilder implements DomainFactory<Airport> {
    private ICAOCode icao;
    private IATACode iata;
    private String name;
    private String town;
    private GpsCoordinates coords;
    private Elevation elev;
    private AirControlArea area;

    /**
     * Sets the main textual identity data.
     */
    public AirportBuilder withData(String icao, String iata, String name, String town) {
        this.icao = new ICAOCode(icao);
        this.iata = new IATACode(iata);
        this.name = name;
        this.town = town;
        return this;
    }

    /**
     * Sets geographic data.
     */
    public AirportBuilder withLocation(double lat, double lon, double elevation) {
        this.coords = new GpsCoordinates(lat, lon);
        this.elev = new Elevation(elevation);
        return this;
    }

    /**
     * Assigns the required Air Control Area.
     */
    public AirportBuilder inArea(AirControlArea area) {
        this.area = area;
        return this;
    }

    /**
     * Finalizes the creation of the Airport entity.
     */
    @Override
    public Airport build() {
        return new Airport(icao, iata, name, town, coords, elev, area);
    }
}