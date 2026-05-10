package isep.aisafe.flightplan.importer.dsl;

public final class DslCoordinate {

    private final double latitude;
    private final double longitude;

    public DslCoordinate(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }
}
