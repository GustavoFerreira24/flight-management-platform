package isep.aisafe.aircontrolarea.domain;

import eapli.framework.domain.model.ValueObject;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents the geographic boundary of an air control area.
 */
@Embeddable
public class GeographicBoundary implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;

    protected GeographicBoundary() {
    }

    private GeographicBoundary(
            final double minLatitude,
            final double maxLatitude,
            final double minLongitude,
            final double maxLongitude) {

        if (minLatitude < -90 || minLatitude > 90) {
            throw new IllegalArgumentException("Minimum latitude must be between -90 and 90.");
        }

        if (maxLatitude < -90 || maxLatitude > 90) {
            throw new IllegalArgumentException("Maximum latitude must be between -90 and 90.");
        }

        if (minLongitude < -180 || minLongitude > 180) {
            throw new IllegalArgumentException("Minimum longitude must be between -180 and 180.");
        }

        if (maxLongitude < -180 || maxLongitude > 180) {
            throw new IllegalArgumentException("Maximum longitude must be between -180 and 180.");
        }

        if (minLatitude >= maxLatitude) {
            throw new IllegalArgumentException("Minimum latitude must be lower than maximum latitude.");
        }

        if (minLongitude >= maxLongitude) {
            throw new IllegalArgumentException("Minimum longitude must be lower than maximum longitude.");
        }

        this.minLatitude = minLatitude;
        this.maxLatitude = maxLatitude;
        this.minLongitude = minLongitude;
        this.maxLongitude = maxLongitude;
    }

    /**
     * Creates a geographic boundary.
     *
     * @param minLatitude the minimum latitude
     * @param maxLatitude the maximum latitude
     * @param minLongitude the minimum longitude
     * @param maxLongitude the maximum longitude
     * @return a new geographic boundary
     */
    public static GeographicBoundary valueOf(
            final double minLatitude,
            final double maxLatitude,
            final double minLongitude,
            final double maxLongitude) {

        return new GeographicBoundary(minLatitude, maxLatitude, minLongitude, maxLongitude);
    }

    public double minLatitude() {
        return minLatitude;
    }

    public double maxLatitude() {
        return maxLatitude;
    }

    public double minLongitude() {
        return minLongitude;
    }

    public double maxLongitude() {
        return maxLongitude;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof GeographicBoundary)) {
            return false;
        }

        final GeographicBoundary that = (GeographicBoundary) other;
        return Double.compare(that.minLatitude, minLatitude) == 0
                && Double.compare(that.maxLatitude, maxLatitude) == 0
                && Double.compare(that.minLongitude, minLongitude) == 0
                && Double.compare(that.maxLongitude, maxLongitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minLatitude, maxLatitude, minLongitude, maxLongitude);
    }

    @Override
    public String toString() {
        return minLatitude + "," + maxLatitude + "," + minLongitude + "," + maxLongitude;
    }
}
