package isep.aisafe.airport.domain;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.IATACode;
import isep.aisafe.ICAOCode;
import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.domain.model.DomainEntities;

import jakarta.persistence.*;

@Entity
public class Airport implements AggregateRoot<ICAOCode> {
    @EmbeddedId
    private ICAOCode icao;

    @Column(unique = true, nullable = false)
    private IATACode iata;

    private String name;
    private String town;

    @Embedded
    private GpsCoordinates coordinates;

    @Embedded
    private Elevation elevation;

    @ManyToOne(optional = false)
    private AirControlArea airControlArea;

    protected Airport() {}

    public Airport(ICAOCode icao, IATACode iata, String name, String town, GpsCoordinates coords, Elevation elev, AirControlArea area) {

        if (icao.toString().length() != 4)
            throw new IllegalArgumentException("Airport ICAO code must be exactly 4 letters.");
        if (iata.toString().length() != 3)
            throw new IllegalArgumentException("Airport IATA code must be exactly 3 letters.");
        if (area == null) throw new IllegalArgumentException("Área é obrigatória.");

        this.icao = icao;
        this.iata = iata;
        this.name = name;
        this.town = town;
        this.coordinates = coords;
        this.elevation = elev;
        this.airControlArea = area;
    }

    @Override
    public boolean sameAs(Object other) { return DomainEntities.areEqual(this, other); }

    @Override
    public ICAOCode identity() { return this.icao; }
}