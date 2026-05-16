package isep.aisafe.airport.domain;

import eapli.framework.domain.model.ValueObject;
import jakarta.persistence.*;

import java.util.Locale;

@Embeddable
public class Elevation implements ValueObject {
    private double meters;

    protected Elevation() {}

    public Elevation(double meters) {

        if (meters < 0) {
            throw new IllegalArgumentException("Altitude cannot be below sea level (in other words, negative).");
        }
        this.meters = meters;
    }

    public double meters() {
        return meters;
    }

    @Override
    public String toString() {
        // Usa Locale.US para garantir que sai "114.0 m" e não "114,0 m"
        return String.format(Locale.US, "%.1f m", meters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Elevation)) return false;
        Elevation that = (Elevation) o;
        return Double.compare(that.meters, meters) == 0;
    }
}