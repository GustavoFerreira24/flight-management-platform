package isep.aisafe.application.weather;

import java.util.Objects;

/**
 * Data transfer object representing one weather measurement read from an import source.
 */
public final class ImportedWeatherMeasurement {

    private final int sourceLine;
    private final String day;
    private final String areaCode;
    private final String providerName;
    private final String providerType;
    private final String parameter;
    private final double value;
    private final String unit;
    private final double altitude;
    private final double latitude;
    private final double longitude;

    /**
     * Creates an imported weather measurement record.
     *
     * @param sourceLine original line or record number in the import source
     * @param day weather data day
     * @param areaCode air control area code
     * @param providerName weather provider name
     * @param providerType weather provider type
     * @param parameter weather parameter
     * @param value measured value
     * @param unit measurement unit
     * @param altitude altitude where the value applies
     * @param latitude latitude where the value applies
     * @param longitude longitude where the value applies
     */
    public ImportedWeatherMeasurement(
            final int sourceLine,
            final String day,
            final String areaCode,
            final String providerName,
            final String providerType,
            final String parameter,
            final double value,
            final String unit,
            final double altitude,
            final double latitude,
            final double longitude) {

        if (sourceLine < 1) {
            throw new IllegalArgumentException("Source line must be positive.");
        }

        this.sourceLine = sourceLine;
        this.day = Objects.requireNonNull(day, "day cannot be null");
        this.areaCode = Objects.requireNonNull(areaCode, "areaCode cannot be null");
        this.providerName = Objects.requireNonNull(providerName, "providerName cannot be null");
        this.providerType = Objects.requireNonNull(providerType, "providerType cannot be null");
        this.parameter = Objects.requireNonNull(parameter, "parameter cannot be null");
        this.value = value;
        this.unit = Objects.requireNonNull(unit, "unit cannot be null");
        this.altitude = altitude;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @return original line or record number in the import source
     */
    public int sourceLine() {
        return sourceLine;
    }

    /**
     * @return weather data day
     */
    public String day() {
        return day;
    }

    /**
     * @return air control area code
     */
    public String areaCode() {
        return areaCode;
    }

    /**
     * @return weather provider name
     */
    public String providerName() {
        return providerName;
    }

    /**
     * @return weather provider type
     */
    public String providerType() {
        return providerType;
    }

    /**
     * @return weather parameter
     */
    public String parameter() {
        return parameter;
    }

    /**
     * @return measured value
     */
    public double value() {
        return value;
    }

    /**
     * @return measurement unit
     */
    public String unit() {
        return unit;
    }

    /**
     * @return altitude where the value applies
     */
    public double altitude() {
        return altitude;
    }

    /**
     * @return latitude where the value applies
     */
    public double latitude() {
        return latitude;
    }

    /**
     * @return longitude where the value applies
     */
    public double longitude() {
        return longitude;
    }
}
