package isep.aisafe.airport.domain;

import eapli.framework.domain.model.ValueObject;
import jakarta.persistence.*;

@Embeddable
public class GpsCoordinates implements ValueObject {
    private double latitude;
    private double longitude;

    protected GpsCoordinates() {}

    public GpsCoordinates(double lat, double lon) {
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180)
            throw new IllegalArgumentException("Coordenadas inválidas.");
        this.latitude = lat;
        this.longitude = lon;
    }
}
